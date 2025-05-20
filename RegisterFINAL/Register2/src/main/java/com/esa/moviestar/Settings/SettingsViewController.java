package com.esa.moviestar.Settings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class SettingsViewController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Label impostazioni;
    @FXML
    private Button account;
    @FXML
    private Button cronologia;
    @FXML
    private Button privacy;
    @FXML
    private Button accessibilità;
    @FXML
    private Button about;
    @FXML
    private Button help;


    public void initialize(){

        account.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/Settings_FXML/account-settings-view.fxml"));
                Parent account_view = loader.load();

                contentArea.getChildren().setAll(account_view);

            } catch (IOException e) {
               System.err.println("non è possibile caricare la pagina impostazioni : account");
            }
        });

    }

}
