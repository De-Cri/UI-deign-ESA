package com.esa.moviestar.Settings;

import com.esa.moviestar.home.MainPagesController;
import com.esa.moviestar.model.Account;
import com.esa.moviestar.model.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class SettingsViewController {

    @FXML
    private StackPane contentArea;
    @FXML
    private AnchorPane contenitore;
    @FXML
    private Label impostazioni;
    @FXML
    private HBox accountContent;
    @FXML
    private HBox cronologia;
    @FXML
    private HBox privacy;
    @FXML
    private HBox accessibilità;
    @FXML
    private HBox about;
    @FXML
    private Button backToHomeButton;
    @FXML
    private Label userName;
    @FXML
    private Group profileImage;

    private Utente utente;
    private Account account;


    public void setAccount(Account account){
        this.account=account;
        caricaVista("/com/esa/moviestar/Settings_FXML/account-setting-view.fxml");
        System.out.println(account.getEmail());
    }

    public void setUtente(Utente utente){
        this.utente=utente;
    }

    public final ResourceBundle resourceBundle = ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");

    public void initialize() {
        // Gestione ritorno alla home
        backToHomeButton.setOnMouseClicked(event -> {
            // Controllo sicurezza per dati NULL
            if (account == null) {
                System.err.println("Account è NULL, impossibile navigare alla home");
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/home/main.fxml"), resourceBundle);
                Parent backHomeView = loader.load();

                MainPagesController mainPagesController = loader.getController();
                mainPagesController.first_load(utente, account);

                Scene currentScene = contenitore.getScene();
                Scene newScene = new Scene(backHomeView, currentScene.getWidth(), currentScene.getHeight());

                Stage stage = (Stage) currentScene.getWindow();
                stage.setScene(newScene);
            } catch (IOException e) {
                System.err.println("Errore nel tornare alla home");
            }
        });

        // Collegamenti ai pulsanti
        accountContent.setOnMouseClicked(event -> caricaVista("/com/esa/moviestar/Settings_FXML/account-setting-view.fxml"));
        cronologia.setOnMouseClicked(event -> caricaVista("/com/esa/moviestar/Settings_FXML/cronologia-setting-view.fxml"));
        privacy.setOnMouseClicked(event -> caricaVista("/com/esa/moviestar/Settings_FXML/privacy-setting-view.fxml"));
        accessibilità.setOnMouseClicked(event -> caricaVista("/com/esa/moviestar/Settings_FXML/accessibilità-setting-view.fxml"));
        about.setOnMouseClicked(event -> caricaVista("/com/esa/moviestar/Settings_FXML/about-setting-view.fxml"));

        // Caricamento iniziale
    }

    private void caricaVista(String percorsoFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(percorsoFXML), resourceBundle);
            Parent view = loader.load();

            // Passa l'utente solo alla vista account
            if (loader.getController() instanceof AccountSettingController controller) {
                controller.setAccount(account);
                controller.setUtente(utente);
                controller.setContenitore(contenitore);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della vista: " + percorsoFXML);
        }
    }
}
