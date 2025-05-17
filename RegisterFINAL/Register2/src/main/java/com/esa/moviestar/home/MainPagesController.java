package com.esa.moviestar.home;

import com.esa.moviestar.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;


import java.io.IOException;
import java.util.*;

public class MainPagesController {

    @FXML
    AnchorPane body;
    @FXML
    private StackPane headerContainer;

    //Var
    private final ResourceBundle resourceBundle =ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");
    private record Data(Node node, Object controller){}
    private Utente user;

    //Pages
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
            headerController.setUpPopUpMenu(user);
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
                    ((FilterController) filter_film.controller).loadWithFilter(user,false);
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
                    ((FilterController) filter_series.controller).loadWithFilter(user,true);
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
                    System.err.println("MainPagesController: tbxSearchListener error \nError:"+e.getMessage());
                }
                return;
            }
            Data search = loadDynamicBody("search.fxml");
            if (search != null) {
                try{
                ((SearchController) search.controller).set_headercontroller((HeaderController)header.controller,user);
                body.getChildren().clear();
                body.getChildren().add(search.node);}
                catch (Exception e){
                    System.err.println("MainPagesController: tbxSearchListener error \nError:"+e.getMessage());
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
            System.err.println("Main Controller: Failed to load body: "+ bodySource+"\nError:"+e.getMessage());
        }
        return null;
    }
    public void cardClicked(int id){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("com/esa/moviestar/movie_view/filmInterface.fxml"),resourceBundle);
            Node body = loader.load();
            this.body.getChildren().add(body);
            AnchorPane.setBottomAnchor(body,0.0);
            AnchorPane.setTopAnchor(body,0.0);
            AnchorPane.setLeftAnchor(body,0.0);
            AnchorPane.setRightAnchor(body,0.0);

        } catch (IOException e) {
            System.err.println("Main Controller: Failed to load body: card \nError:"+e.getMessage());
        }
    }
}