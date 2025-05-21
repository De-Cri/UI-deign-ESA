package com.esa.moviestar.home;
import com.esa.moviestar.Database.AccountDao;
import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.components.ScrollViewSkin;
import com.esa.moviestar.movie_view.FilmCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;


import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SearchController {
    @FXML
    public Button searchlabel;
    @FXML
    public Line separatorline;
    @FXML
    private Button findout;
    @FXML
    private Button genre;
    @FXML
    private Button filmSeries;
    @FXML
    private FlowPane filmseriesRaccomendations;
    @FXML
    private Pane pnl_separator1;
    @FXML
    private FlowPane raccomendations;
    //private db
    private String searchText;

    private HeaderController headerController;

    private Utente p;

    private ContentDao dbSearch;

    public ResourceBundle resourcebundlesearch;

    private List<Node> tryraccomendationlist;
    List<Content> suggestedContent;
    List<Content> content;
    private List<Node> trylist;

    public void initialize(){
        //prompt da fare come netflix
        //separatorline.setStroke(getLinearGradient((Color)foreColor));
    }
    public void set_headercontroller(HeaderController h, Utente u, ResourceBundle bundle) throws IOException {
        this.headerController = h;
        this.p = u;
        this.resourcebundlesearch = bundle;
        String searchText= headerController.getTbxSearch().getText();
        if (dbSearch == null) {
            dbSearch = new ContentDao();
        }

        content = dbSearch.take_film_tvseries(searchText, p);
        suggestedContent = dbSearch.take_reccomendations(searchText, p);
        trylist = createFilmNodes(content, false);
        tryraccomendationlist = createFilmNodes(suggestedContent, false);
        reccomendedList();
        raccomendedSeriesFilms();
    }

    public void reccomendedList(){
        if (!headerController.getTbxSearch().getText().isEmpty()){

            for(int i = 0; (i < tryraccomendationlist.size()); i++){

                Button dynamicButton = new Button(suggestedContent.get(i).getTitle());
                dynamicButton.getStyleClass().addAll("register-text-raccomendations-mid");
                raccomendations.getChildren().add(dynamicButton);
            }
        }
    }
    public void raccomendedSeriesFilms(){
        if (!headerController.getTbxSearch().getText().isEmpty()){

            for(int i = 0;i<trylist.size() ; i++){

                Node dynamicContent = trylist.get(i);
                filmseriesRaccomendations.getChildren().add(dynamicContent);
            }
        }
    }
    public List<Node> createFilmNodes(List<Content> contentList,boolean isVertical) throws IOException {
        List<Node> nodes= new Vector<>();
        for (Content content: contentList) {
            FXMLLoader fxmlLoader= new FXMLLoader(isVertical? Objects.requireNonNull(getClass().getResource("/com/esa/moviestar/movie_view/FilmCard_Vertical.fxml")):Objects.requireNonNull(getClass().getResource("/com/esa/moviestar/movie_view/FilmCard_Horizontal.fxml")),resourcebundlesearch);
            Node n = fxmlLoader.load();
            FilmCardController filmCardController = fxmlLoader.getController();
            filmCardController.setContent(content);
            //n.setOnMouseClicked(e->cardClicked(filmCardController.getCardId()));
            nodes.add(n);
        }
        return nodes; //nodes
    }
}