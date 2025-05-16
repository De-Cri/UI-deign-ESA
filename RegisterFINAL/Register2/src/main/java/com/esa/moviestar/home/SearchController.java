package com.esa.moviestar.home;
import com.esa.moviestar.Database.AccountDao;
import com.esa.moviestar.model.Content;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

    private List<Node> trylist;
    public void initialize(){
        //prompt da fare come netflix

    }
    public void set_headercontroller(HeaderController h){
        this.headerController = h;
        String searchText= headerController.getTbxSearch().getText();
        reccomendedList();
    }
    public void set_trylist(List<Node> contentTry){
        this.trylist = contentTry;
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

            /*ArrayList<String> prefSet = s.search(searchText).size())*/;

            /*prefSet.size()
            for (Node dynamicNode : trylist) {
                prefSet
                filmseriesRaccomendations.getChildren().add(dynamicNode);
            }*/
            for(int i = 0; (i < 10 /*prefSet.size()*/); i++){
                /*prefSet*/
                Button dynamicButton = new Button("/*prefSet[i]*/");
                dynamicButton.getStyleClass().add("medium-item");
                raccomendations.getChildren().add(dynamicButton);
            }
        }
    }
}