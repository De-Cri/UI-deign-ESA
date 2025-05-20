package com.esa.moviestar.Settings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ResourceBundle;

public class SettingsViewController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Label impostazioni;
    @FXML
    private Label account;
    @FXML
    private Label cronologia;
    @FXML
    private Label privacy;
    @FXML
    private Label accessibilità;
    @FXML
    private Label about;

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");

    public void initialize(){

        //caricamento della pagina degli account
        account.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/Settings_FXML/account-setting-view.fxml"),resourceBundle);
                Parent account_view = loader.load();

                contentArea.getChildren().setAll(account_view);

            } catch (IOException e) {
               System.err.println("non è possibile caricare la pagina impostazioni : account");
            }
        });

        //caricamento della pagina della cronologia
        cronologia.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/Settings_FXML/cronologia-setting-view.fxml"),resourceBundle);
                Parent cronologia_view = loader.load();

                contentArea.getChildren().setAll(cronologia_view);

            } catch (IOException e) {
                System.err.println("non è possibile caricare la pagina impostazioni : cronologia");
            }
        });

        //caricamento della pagina di privacy
        privacy.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/Settings_FXML/privacy-setting-view.fxml"),resourceBundle);
                Parent privacy_view = loader.load();

                contentArea.getChildren().setAll(privacy_view);

            } catch (IOException e) {
                System.err.println("non è possibile caricare la pagina impostazioni : privacy");
            }
        });

        //caricamento della pagina di accessibilita
        accessibilità.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/Settings_FXML/accessibilità-setting-view.fxml"),resourceBundle);
                Parent accessibilita_view = loader.load();

                contentArea.getChildren().setAll(accessibilita_view);

            } catch (IOException e) {
                System.err.println("non è possibile caricare la pagina impostazioni : accessibilita");
            }
        });

        //caricamento della pagina About
        about.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/Settings_FXML/about-setting-view.fxml"),resourceBundle);
                Parent about_view = loader.load();

                contentArea.getChildren().setAll(about_view);

            } catch (IOException e) {
                System.err.println("non è possibile caricare la pagina impostazioni : about");
            }
        });

        //carica di default la pagina account
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/Settings_FXML/account-setting-view.fxml"),resourceBundle);
            Parent account_view = loader.load();
            contentArea.getChildren().setAll(account_view);
        } catch (IOException e) {
            System.err.println("non è possibile caricare la pagina impostazioni di default: account");
        }
    }
}

