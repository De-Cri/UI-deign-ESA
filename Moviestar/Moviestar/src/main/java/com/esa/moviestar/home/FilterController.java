package com.esa.moviestar.home;

import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.components.ScrollView;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class FilterController {
    @FXML
    VBox scrollViewContainer;
    @FXML
    HBox filterButton;




    public  void initialize(){

    }

    public void loadWithFilter(MainPagesController father, Utente user,boolean isFilm) {
//        PopupMenu popupMenu = new PopupMenu(192,4);
//        for(int i=0; i<18; i++){
//            FilmTagChips filmTagChips = new FilmTagChips(i);
//            filmTagChips.setOnMouseClicked(e->{
//                ContentDao contentDao = new ContentDao();
//                setContent(father,user, contentDao.getFilterPageContentsWithTag(user,isFilm,filmTagChips.getCategory()),isFilm);
//            });
//            popupMenu.addItem(i/5, new FilmTagChips(i));
//        }
//        filterButton.setOnMouseClicked(e->popupMenu.show(filterButton));
        setContent(father,user,isFilm);
    }

    public void setContent(MainPagesController mainPagesController,Utente user, boolean isFilm) {
        try {
            List<List<Content>> contentList = new ContentDao().getFilterPageContents(user,isFilm);
            scrollViewContainer.getChildren().clear();
            ScrollView s1 = new ScrollView(isFilm?"New Releases":"Continue to watch:", Color.TRANSPARENT, mainPagesController.FORE_COLOR, mainPagesController.BACKGROUND_COLOR);
            s1.setContent(mainPagesController.createFilmNodes( contentList.get(0), false));

            ScrollView s2 = new ScrollView(isFilm?"Our selection for you":"New Releases:",  Color.TRANSPARENT, mainPagesController.FORE_COLOR, mainPagesController.BACKGROUND_COLOR);
            s2.setContent(mainPagesController.createFilmNodes( contentList.get(1), false));

            ScrollView s3 = new ScrollView((isFilm?"Film":"Program")+ "to watch", Color.TRANSPARENT, mainPagesController.FORE_COLOR, mainPagesController.BACKGROUND_COLOR);
            s3.setContent(mainPagesController.createFilmNodes( contentList.get(2), false));

            ScrollView s4 = new ScrollView("Explore:", Color.TRANSPARENT, mainPagesController.FORE_COLOR, mainPagesController.BACKGROUND_COLOR);
            s4.setContent(mainPagesController.createFilmNodes( contentList.get(3), false));

            ScrollView s5 = new ScrollView("Maybe you missed:", Color.TRANSPARENT, mainPagesController.FORE_COLOR, mainPagesController.BACKGROUND_COLOR);
            s5.setContent(mainPagesController.createFilmNodes( contentList.get(4), true));

            scrollViewContainer.getChildren().addAll(s1,s2,s3,s4,s5);
        }
        catch (IOException e){
            System.err.println("HomeController: Failed to load recommendations \n Error:"+e.getMessage());
        }
    }


}