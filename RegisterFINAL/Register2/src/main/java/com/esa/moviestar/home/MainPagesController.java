package com.esa.moviestar.home;

import com.esa.moviestar.model.Content;
import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.movie_view.FilmCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.util.*;

public class MainPagesController {

    @FXML
    AnchorPane body;
    @FXML
    private StackPane headerContainer;

    //Var
    public final ResourceBundle resourceBundle =ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");
    public final  String pathWindowCard = "/com/esa/moviestar/movie_view/WindowCard.fxml";
    public final  String pathCardVertical = "/com/esa/moviestar/movie_view/FilmCard_Vertical.fxml";
    public final  String pathCardHorizontal = "/com/esa/moviestar/movie_view/FilmCard_Horizontal.fxml";

    //UI Color
    public final Color foreColor = Color.rgb(240, 236, 253);
    public final Color backgroundColor = Color.rgb(16, 16, 16);

    //Logic
    private record Data(Node node, Object controller){}
    private Utente user;
    private Data header;
    private Data home;
    private Data filter_film;
    private Data filter_series;
    private Data currentScene;


    public void first_load(Utente user){
        this.user = user;
        //load the header and set the Profile Icon
        if(header==null)
            loadHeader();
        ((HeaderController)header.controller).setProfileIcon(user.getIcona());

        //load the home page
        Data home =loadDynamicBody("home.fxml");
        if(home!=null){
            HomeController homeBodyController = (HomeController) home.controller;
            homeBodyController.setRecommendations(user,this);
            currentScene = home;
            body.getChildren().clear();
            body.getChildren().add(home.node);
        }
    }

    private void loadHeader() {
        //load the header
        header = loadDynamicBody("header.fxml");
        headerContainer.getChildren().add(header.node);
        HeaderController headerController = (HeaderController) header.controller();
        if(user!=null)
            headerController.setUpPopUpMenu(this,user);
        //set buttons click to change pages
        headerController.homeButton.setOnMouseClicked(e -> {
            if (currentScene == home)
                return;
            if (home == null) {
                home = loadDynamicBody("home.fxml");
                if (home != null)
                    ((HomeController) home.controller).setRecommendations(user,this);
            }
            currentScene = home;
            body.getChildren().clear();
            body.getChildren().add(home.node);

        });
        headerController.filmButton.setOnMouseClicked(e -> {
            if (currentScene == filter_film)
                return;
            if (filter_film == null) {
                filter_film = loadDynamicBody("filter.fxml");
                if (filter_film != null)
                    ((FilterController) filter_film.controller).loadWithFilter(this,user,false);
            }
            currentScene = filter_film;
            body.getChildren().clear();
            body.getChildren().add(filter_film.node);
        });
        headerController.seriesButton.setOnMouseClicked(e -> {
            if (currentScene == filter_series)
                return;
            if (filter_series == null) {
                filter_series = loadDynamicBody("filter.fxml");
                if (filter_series != null)
                    ((FilterController) filter_series.controller).loadWithFilter(this,user,true);
            }
            currentScene = filter_series;
            body.getChildren().clear();
            body.getChildren().add(filter_series.node);
        });

        headerController.getTbxSearch().textProperty().addListener((observableValue, oldV, newV)
                -> {
            if (newV.isEmpty()) {
                try {
                    body.getChildren().clear();
                    body.getChildren().add(currentScene.node);
                } catch (Exception e) {
                    System.err.println("MainPagesController: tbxSearchListener error \n Error:"+e.getMessage());
                }
                return;
            }
            Data search = loadDynamicBody("search.fxml");
            if (search != null) {
                try{
                    ((SearchController) search.controller).set_headercontroller((HeaderController)header.controller,user, resourceBundle);
                    body.getChildren().clear();
                    body.getChildren().add(search.node);}
                catch (Exception e){
                    System.err.println("MainPagesController: tbxSearchListener error \n Error:"+e.getMessage());
                }
            }
        });
    }


    private Data loadDynamicBody(String bodySource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(bodySource),resourceBundle);
            Node body = loader.load();
            this.body.getChildren().add(body);
            AnchorPane.setBottomAnchor(body,0.0);
            AnchorPane.setTopAnchor(body,0.0);
            AnchorPane.setLeftAnchor(body,0.0);
            AnchorPane.setRightAnchor(body,0.0);
            return new Data(body,(Object) loader.getController());
        } catch (IOException e) {
            System.err.println("Main Controller: Failed to load body: "+ bodySource+"\n Error:"+e.getMessage());
        }
        return null;
    }

    //logic of the children pages
    /**
     * Method that convert a list of contents in a list of Nodes
     * @param contentList list of the Contents to transform in Nodes
     * @param isVertical specify the orientation of the
     * @return List of Nodes
     */
    public List<Node> createFilmNodes(List<Content> contentList, boolean isVertical) throws IOException {
        List<Node> nodes= new Vector<>();
        for (Content content: contentList) {
            FXMLLoader fxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource(isVertical?pathCardVertical:pathCardHorizontal)),resourceBundle);
            Node n = fxmlLoader.load();
            FilmCardController filmCardController = fxmlLoader.getController();
            filmCardController.setContent(content);
            n.setOnMouseClicked(e->cardClicked(filmCardController.getCardId()));
            nodes.add(n);
        }
        return nodes;
    }
    /**
     * Switch the scene with the film interface
     * @param cardId of the Content
     */
    public void cardClicked(int cardId){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("com/esa/moviestar/movie_view/filmInterface.fxml"),resourceBundle);
            Node body = loader.load();
            this.body.getChildren().add(body);

        } catch (IOException e) {
            System.err.println("Main Controller: Failed to load body: card \n Error:"+e.getMessage());
        }
    }
    /**
     * Switch and play the video of the film
     * @param cardId of the Content
     */
    public void cardClickedPlay(int cardId) {

    }
    /**
     * Open the settings of the current user
     */
    public void settingsClick(Utente user){
    }
    /**
     * Dispose all the information and return to login page
     */
    public void emailClick() {

    }
    /**
     * Change the profile with a new one, possibile disposing of resources
     */
    public void profileClick(Utente user) {

    }

}