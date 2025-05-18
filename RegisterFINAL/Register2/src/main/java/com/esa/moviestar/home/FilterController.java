package com.esa.moviestar.home;

import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.movie_view.FilmCardController;
import com.esa.moviestar.movie_view.FilmTagChips;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class FilterController {
    @FXML
    VBox scrollViewContainer;
    @FXML
    HBox filterButton;




    public  void initialize(){

    }

    public void loadWithFilter(MainPagesController father, Utente user,boolean isFilm) {
        PopupMenu popupMenu = new PopupMenu(192,4);
        for(int i=0; i<18; i++){
            FilmTagChips filmTagChips = new FilmTagChips(i);
            filmTagChips.setOnMouseClicked(e->{
                ContentDao contentDao = new ContentDao();
                setContent(father,user, contentDao.getFilterPageContentsWithTag(user,isFilm,filmTagChips.getCategory()),isFilm);
            });
            popupMenu.addItem(i/5, new FilmTagChips(i));
        }
        filterButton.setOnMouseClicked(e->popupMenu.show(filterButton));
        setContent(father,user,new ContentDao().getFilterPageContents(user,isFilm),isFilm);
    }

    public void setContent(MainPagesController mainPagesController,Utente user,List<List<Content>> contentList,boolean isFilm) {
        try {
            scrollViewContainer.getChildren().clear();
            ScrollView s1 = new ScrollView(isFilm?"New Releases":"Continue to watch:", Color.TRANSPARENT, mainPagesController.foreColor, mainPagesController.backgroundColor);
            s1.setContent(mainPagesController.createFilmNodes( contentList.get(1), false));

            ScrollView s2 = new ScrollView("",  Color.TRANSPARENT, mainPagesController.foreColor, mainPagesController.backgroundColor);
            s2.setContent(mainPagesController.createFilmNodes( contentList.get(2), false));

            ScrollView s3 = new ScrollView("La tua categoria preferita", Color.TRANSPARENT, mainPagesController.foreColor, mainPagesController.backgroundColor);
            s3.setContent(mainPagesController.createFilmNodes( contentList.get(3), false));

            ScrollView s4 = new ScrollView("Similar at "+contentList.get(4).getFirst().getTitle()+" :", Color.TRANSPARENT, mainPagesController.foreColor, mainPagesController.backgroundColor);
            s4.setContent(mainPagesController.createFilmNodes( contentList.get(5), false));

            ScrollView s5 = new ScrollView("Series that you may like:", Color.TRANSPARENT, mainPagesController.foreColor, mainPagesController.backgroundColor);
            s5.setContent(mainPagesController.createFilmNodes( contentList.get(7), true));

            scrollViewContainer.getChildren().addAll(s1,s2,s3,s4,s5);
        }
        catch (IOException e){
            System.err.println("HomeController: Failed to load recommendations \n Error:"+e.getMessage());
        }
    }


}