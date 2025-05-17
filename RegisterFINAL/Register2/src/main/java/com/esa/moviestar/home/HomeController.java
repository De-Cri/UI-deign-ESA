package com.esa.moviestar.home;

import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.movie_view.FilmCardController;
import com.esa.moviestar.movie_view.FilmTagChips;
import com.esa.moviestar.movie_view.WindowCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HomeController {
    @FXML
    private VBox body;
    @FXML
    private ScrollPane root;
    @FXML
    private VBox scrollViewContainer;
    private  final  String pathWindowCard = "/com/esa/moviestar/movie_view/WindowCard.fxml";
    private final  String pathCardVertical = "/com/esa/moviestar/movie_view/FilmCard_Vertical.fxml";
    private final  String pathCardHorizontal = "/com/esa/moviestar/movie_view/FilmCard_Horizontal.fxml";
    private final ResourceBundle resourceBundle =ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");

    //ui color
    private final Color foreColor = Color.rgb(240, 236, 253);
    private final Color backgroundColor = Color.rgb(16, 16, 16);

    /**
     * Set all recommendation lists for a user profile
     */
    public void setRecommendations(Utente user,  MainPagesController mainPagesController) {
        List<List<Content>> contentList= new ContentDao().getHomePageContents(user);
        try {
            List<Node> carouselList= new Vector<>();
            for (Content c:contentList.getFirst()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(pathWindowCard),resourceBundle);
                Node body = loader.load();
                WindowCardController windowCardController  = loader.getController();
                windowCardController.setContent(c);
               // windowCardController.getPlayButton().setOnMouseClicked(e->cardClicked(windowCardController.getCardId()));
                windowCardController.getInfoButton().setOnMouseClicked(e->mainPagesController.cardClicked(windowCardController.getCardId()));
                carouselList.add(body);
            }
            Carousel carousel = new Carousel();
            carousel.getItems().addAll(carouselList);
            carousel.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/esa/moviestar/styles/Carousel.css")).toExternalForm());
            body.getChildren().add(1, carousel);

            ScrollView top10Scroll = new ScrollView("Scelti per te", Color.TRANSPARENT, foreColor, backgroundColor);
            top10Scroll.setContent(createFilmNodes(mainPagesController, contentList.get(1), false));


            ScrollView latest10Scroll = new ScrollView("Novità", Color.rgb(228, 193, 42), backgroundColor, null, 32.0);
            latest10Scroll.setContent(createFilmNodes(mainPagesController, contentList.get(2), true));


            ScrollView favouriteCategoryScroll = new ScrollView("La tua categoria preferita", Color.TRANSPARENT, foreColor, backgroundColor);
            favouriteCategoryScroll.setContent(createFilmNodes(mainPagesController, contentList.get(3), false));




            ScrollView similarToLastWatchedScroll = new ScrollView("Similar at "+contentList.get(4).getFirst().getTitle()+" :", Color.TRANSPARENT, foreColor, backgroundColor);
            similarToLastWatchedScroll.setContent(createFilmNodes(mainPagesController, contentList.get(5), false));


            ScrollView recommendSeriesScroll = new ScrollView("Series that you may like:", Color.TRANSPARENT, foreColor, backgroundColor);
            recommendSeriesScroll.setContent(createFilmNodes(mainPagesController, contentList.get(7), true));



            ScrollView bottom7Scroll = new ScrollView("New Experiences:", Color.TRANSPARENT, foreColor, backgroundColor);
            bottom7Scroll.setContent(createFilmNodes(mainPagesController, contentList.get(8), false));


            scrollViewContainer.getChildren().addAll(top10Scroll, latest10Scroll, favouriteCategoryScroll, recommendSeriesScroll, bottom7Scroll);

            if (!contentList.get(4).isEmpty()) {
                ScrollView watchedScroll =new ScrollView("Continue to watch:", Color.TRANSPARENT, foreColor, backgroundColor, 32.0);
                watchedScroll.setContent(createFilmNodes(mainPagesController, contentList.get(4), true));
                scrollViewContainer.getChildren().add(3,watchedScroll);
            }

            if (!contentList.get(6).isEmpty()) {
                ScrollView favouriteScroll = new ScrollView("Favourites:", Color.rgb(155, 155, 155), backgroundColor, null, 32.0);
                favouriteScroll.setContent(createFilmNodes(mainPagesController, contentList.get(6), true));
                scrollViewContainer.getChildren().add(5,favouriteScroll);
            }
        }
        catch (IOException e){
            System.err.println("HomeController: Failed to load recommendations \n Error:"+e.getMessage());
        }
    }

    public List<Node> createFilmNodes(MainPagesController mainPagesController,List<Content> contentList,boolean isVertical) throws IOException {
        List<Node> nodes= new Vector<>();
        for (Content content: contentList) {
            FXMLLoader fxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource(isVertical?pathCardVertical:pathCardHorizontal)),resourceBundle);
            Node n = fxmlLoader.load();
            FilmCardController filmCardController = fxmlLoader.getController();
            filmCardController.setContent(content);
            n.setOnMouseClicked(e->mainPagesController.cardClicked(filmCardController.getCardId()));
            nodes.add(n);
        }
        return nodes;
    }

}