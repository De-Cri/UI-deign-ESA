package com.esa.moviestar.home;

import com.esa.moviestar.Database.ContentDao;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.movie_view.FilmTagChips;
import javafx.fxml.FXML;
import javafx.scene.layout.Background;
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
;

    //ui color
    private final Color foreColor = Color.rgb(240, 236, 253);
    private final Color backgroundColor = Color.rgb(15, 15, 15);

    public  void initialize(){

    }

    public void loadWithFilter(Utente user,boolean isFilm) {
        PopupMenu popupMenu = new PopupMenu(192,4);
        for(int i=0; i<18; i++){
            FilmTagChips filmTagChips = new FilmTagChips(i);
            filmTagChips.setOnMouseClicked(e->{
                ContentDao contentDao = new ContentDao();
             //   setContent(user, contentDao.getFilterPageContentsWithTag(user,isFilm,filmTagChips.getCategory()));
            });
            popupMenu.addItem(i/5, new FilmTagChips(i));
        }
        filterButton.setOnMouseClicked(e->popupMenu.show(filterButton));
        //setContent(user,new ContentDao().getFilterPageContents(user,isFilm));
    }

    public void setContent(Utente user,List<Content> contentList) {
        try {
            scrollViewContainer.getChildren().clear();

       } catch (Exception e) {
            System.err.println("FilterController: Failed to load recommendations \n Error:" + e.getMessage());
        }
    }


}
