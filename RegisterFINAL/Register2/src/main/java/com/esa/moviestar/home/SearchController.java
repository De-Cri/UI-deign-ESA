package com.esa.moviestar.home;
import com.esa.moviestar.Database.AccountDao;
import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.movie_view.FilmCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;


import java.io.IOException;
import java.util.*;

public class SearchController {
    @FXML
    private Label findout;
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

    private List<Node> trylist;

    public  ResourceBundle resourcebundlesearch;


    public void initialize(){
        //prompt da fare come netflix

    }
    public void set_headercontroller(HeaderController h, Utente u, ResourceBundle bundle) throws IOException {
        this.headerController = h;
        this.p = u;
        this.resourcebundlesearch = bundle;
        String searchText= headerController.getTbxSearch().getText();
        if (dbSearch == null) {
            dbSearch = new ContentDao();
        }
        reccomendedList();
        List<Content> content = dbSearch.take_film_tvseries(searchText, p);
        trylist = createFilmNodes(content, false);
        raccomendedSeriesFilms();
    }
    public void reccomendedList(){
        if (!headerController.getTbxSearch().getText().isEmpty()){

            /*ArrayList<String> prefSet = s.search(searchText).size())*/;

            for(int i = 0; (i < 10 /*prefSet.size()*/); i++){
                /*prefSet*/
                Button dynamicButton = new Button("/*prefSet[i]*/");
                dynamicButton.getStyleClass().add("medium-item");
                raccomendations.getChildren().add(dynamicButton);
            }
        }
    }
    public void raccomendedSeriesFilms(){
        if (!headerController.getTbxSearch().getText().isEmpty()){

            for(int i = 0;i<trylist.size() ; i++){
                Node dynamicButton = trylist.get(i);
                dynamicButton.getStyleClass().add("medium-item");
                raccomendations.getChildren().add(dynamicButton);
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