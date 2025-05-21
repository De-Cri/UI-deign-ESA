package com.esa.moviestar.home;
import com.esa.moviestar.Database.AccountDao;
import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.components.ScrollView;
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.control.SkinBase;

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
        if (separatorline != null) {
            separatorline.setStroke(getLinearGradient(javafx.scene.paint.Color.WHITE));
        }
    }

    public LinearGradient getLinearGradient(javafx.scene.paint.Color color) {
        return new LinearGradient(
                0, 0,      // start X,Y (left edge)
                1, 0,      // end X,Y (right edge)
                true,      // proportional
                CycleMethod.NO_CYCLE,
                new Stop(0.0, javafx.scene.paint.Color.TRANSPARENT),
                new Stop(0.5, color),
                new Stop(1.0, javafx.scene.paint.Color.TRANSPARENT)
        );
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
    public LinearGradient getVerticalLinearGradient(javafx.scene.paint.Color color) {
        return new LinearGradient(
                0, 0,      // start X,Y (top edge)
                0, 1,      // end X,Y (bottom edge)
                true,      // proportional
                CycleMethod.NO_CYCLE,
                new Stop(0.0, javafx.scene.paint.Color.TRANSPARENT),
                new Stop(0.5, color),
                new Stop(1.0, javafx.scene.paint.Color.TRANSPARENT)
        );
    }
    // Modifica al metodo reccomendedList() per usare FlowPane esistente
    public void reccomendedList() {
        if (!headerController.getTbxSearch().getText().isEmpty()) {
            // Pulisci il container prima di aggiungere nuovi elementi
            raccomendations.getChildren().clear();

            for(int i = 0; i < tryraccomendationlist.size(); i++) {
                // Crea un HBox per ogni elemento (bottone + separatore)
                HBox itemContainer = new HBox();
                itemContainer.setAlignment(javafx.geometry.Pos.CENTER);
                itemContainer.setSpacing(2);

                // Bottone con titolo del contenuto
                Button dynamicButton = new Button(suggestedContent.get(i).getTitle());
                dynamicButton.getStyleClass().addAll("register-text-raccomendations-mid");

                // Aggiunta del bottone al container
                itemContainer.getChildren().add(dynamicButton);

                // Aggiungi separatore verticale dopo ogni bottone tranne l'ultimo
                if (i < tryraccomendationlist.size() - 1) {
                    Line verticalSeparator = new Line();
                    verticalSeparator.setStartY(0);
                    verticalSeparator.setEndY(30); // Altezza approssimativa del bottone con font 16px
                    verticalSeparator.setStrokeWidth(1.5);
                    verticalSeparator.setStroke(getVerticalLinearGradient(javafx.scene.paint.Color.WHITE));

                    // Margini per il separatore
                    HBox.setMargin(verticalSeparator, new javafx.geometry.Insets(0, 8, 0, 8));

                    // Aggiungi il separatore al container
                    itemContainer.getChildren().add(verticalSeparator);
                }

                // Aggiungi l'intero container al FlowPane
                raccomendations.getChildren().add(itemContainer);
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