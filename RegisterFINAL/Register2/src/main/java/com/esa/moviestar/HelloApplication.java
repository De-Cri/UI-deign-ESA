package com.esa.moviestar;

import com.esa.moviestar.home.MainPagesController;
import com.esa.moviestar.model.Utente;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class HelloApplication extends Application{


    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
       // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
       // ((MainPagesController)fxmlLoader.getController()).first_load(new Utente(1,"genoveffo","01FF32763200112233445566778899AABB","prova2@gmail.com"));
        primaryStage.setTitle("Titolo della finestra");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args){
        launch(args);

    }
}