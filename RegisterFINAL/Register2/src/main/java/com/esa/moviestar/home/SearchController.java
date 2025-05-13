package com.esa.moviestar.home;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


import java.io.IOException;
public class SearchController {
    @FXML
    private Pane pnl_separator1;
    private HeaderController headerController;

    public void initialize(){

    }
    public void set_headercontroller(HeaderController h){
        headerController = h;
    }

}
