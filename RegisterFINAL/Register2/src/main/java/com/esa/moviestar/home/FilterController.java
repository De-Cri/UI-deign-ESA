package com.esa.moviestar.home;

import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.movie_view.FilmTagChips;
import javafx.fxml.FXML;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class FilterController {
    @FXML
    VBox scrollViewContainer;
    @FXML
    HBox filterButton;
    public  void initialize(){
        PopupMenu popupMenu = new PopupMenu(192,4);
        for(int i=0; i<18; i++){
            popupMenu.addItem((int)(i/5), new FilmTagChips(i));
        }
        filterButton.setOnMouseClicked(e->popupMenu.show(filterButton));
    }
    public void setContent(Utente u, List<Content> c){

        u.getGusti();

    }

    public void loadWithFilter(int i) {

    }
}
