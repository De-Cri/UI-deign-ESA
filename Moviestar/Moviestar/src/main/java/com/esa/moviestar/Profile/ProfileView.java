package com.esa.moviestar.Profile;

import com.esa.moviestar.Database.AccountDao;
import com.esa.moviestar.Database.UtenteDao;
import com.esa.moviestar.home.MainPagesController;
import com.esa.moviestar.model.Account;
import com.esa.moviestar.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;


public class ProfileView {

    @FXML   // Collegamento alla label del testo
    Label testo;

    @FXML   // HBox per la griglia che contiene gli utenti
    HBox griglia;

    @FXML   // StackPane che contiene l'intera schermata
    StackPane ContenitorePadre;

    @FXML   // Label per eventuali messaggi di errore o avviso
    Label warningText;


    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");


    private Account account;
    public void setAccount(Account account) {
        this.account = account;
        System.out.println("Email passata alla schermata creazione profilo: " + account.getEmail());
        caricaUtenti();
    }

    // Metodo di inizializzazione che viene eseguito subito all'avvio
    public void initialize() {
        testo.setText("Chi vuole guardare Moviestar ?");  // Impostazione del testo della label iniziale
        griglia.setSpacing(40);  // Impostazione della spaziatura tra gli elementi nella griglia


    }

    private void caricaUtenti() {
            griglia.getChildren().clear(); // Pulisci sempre la griglia prima di ricaricare
            UtenteDao dao = new UtenteDao();
            List<Utente> utenti = dao.recuperaTuttiGliUtenti(account.getEmail());

            for (Utente utente : utenti) {
                VBox box = new VBox();
                box.setSpacing(10);
                box.setPadding(new Insets(10));
                box.setAlignment(Pos.CENTER);

                Label name = new Label(utente.getNome());
                name.getStyleClass().addAll("on-secondary", "bold-text", "large-text");

                Group icon = new Group(IconSVG.takeElement(utente.getIDIcona()));
                icon.setScaleY(8);
                icon.setScaleX(8);

                StackPane iconBox = new StackPane(icon);
                StackPane.setAlignment(icon, Pos.CENTER);
                iconBox.setMinSize(185, 185);

                box.setOnMouseEntered(event -> {
                    icon.setScaleX(8.2);
                    icon.setScaleY(8.2);
                    name.getStyleClass().remove("on-secondary");
                    name.getStyleClass().addAll("on-white","bold-text", "large-text");
                });

                box.setOnMouseExited(event -> {
                    icon.setScaleX(8);
                    icon.setScaleY(8);
                    name.getStyleClass().remove("on-white");
                    name.getStyleClass().addAll("on-secondary", "bold-text", "large-text");
                });

                StackPane modifica = new StackPane();
                modifica.setPrefWidth(100);
                SVGPath pencilModify = new SVGPath();
                pencilModify.setContent(resourceBundle.getString("pencil"));
                pencilModify.setScaleY(0.5);
                pencilModify.setScaleX(0.5);
                pencilModify.setStyle("-fx-fill: #E6E3DC;");


                pencilModify.setOnMouseEntered(event -> {
                    pencilModify.setStyle("-fx-fill: #F0ECFD;");
                });

                pencilModify.setOnMouseExited(event -> {
                    pencilModify.setStyle("-fx-fill: #E6E3DC;");
                });


                modifica.getChildren().add(pencilModify);
                icon.setOnMouseClicked(e -> paginaHome(utente));
                modifica.setOnMouseClicked(e -> paginaModifica(utente));

                box.getChildren().addAll(iconBox, name, modifica);
                griglia.getChildren().add(box);
            }
            //creazione e settaggio del bottone aggiungi
            if (utenti.size() < 4) {
                StackPane creazione = new StackPane();
                creazione.setMinSize(100,100);
                creazione.setTranslateY(-20);
                creazione.setStyle("-fx-background-color: #333333;" +
                        "-fx-background-radius: 24px;" +
                        "-fx-border-radius: 24px;");

                SVGPath crossAggiungi = new SVGPath();
                crossAggiungi.setContent(resourceBundle.getString("plusButton"));
                crossAggiungi.setScaleX(1.8);
                crossAggiungi.setScaleY(1.8);
                crossAggiungi.setStyle("-fx-fill: #F0ECFD;");

                // Aggiungi al pane
                creazione.getChildren().add(crossAggiungi);


                VBox creazioneUtente = new VBox();
                Label plusText = new Label();
                plusText.setText("Aggiungi");
                plusText.setTranslateY(-18);
                plusText.getStyleClass().addAll("on-primary", "bold-text", "large-text");
                creazioneUtente.getChildren().addAll(creazione,plusText);
                creazioneUtente.setSpacing(20);

                creazione.setOnMouseEntered(event -> {
                    crossAggiungi.setStyle("-fx-fill: #121212;");
                });

                creazione.setOnMouseExited(event -> {
                    crossAggiungi.setStyle("-fx-fill: #F0ECFD;");
                });

                griglia.getChildren().add(creazioneUtente);
                creazioneUtente.setAlignment(Pos.CENTER);
                creazione.setOnMouseClicked(e -> paginaCreazioneUtente());
            }
    }


    //passaggio alla pagina Home
    private void paginaHome(Utente user) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/home/main.fxml"),resourceBundle);
            Parent homeContent = loader.load();

            MainPagesController mainPagesController = loader.getController();
            mainPagesController.first_load(user,account);

            Scene currentScene = ContenitorePadre.getScene();
            Scene newScene = new Scene(homeContent, currentScene.getWidth(), currentScene.getHeight());

            // Ottieni lo Stage corrente e imposta la nuova scena
            Stage stage = (Stage) ContenitorePadre.getScene().getWindow();
            stage.setScene(newScene);
        }catch(IOException e){
            warningText.setText("Errore durante il caricamento della pagina home: " + e.getMessage());  // Gestione errore
            System.err.println("ProfileView : Errore caricamento pagina home"+e.getMessage());
        }
    }

    //  passaggio alla pagina di modifica
    private void paginaModifica(Utente user) {
        if (griglia.getChildren().size() > 1) {  // Verifica che ci sia almeno un utente nella griglia
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/modify-profile-view.fxml"),resourceBundle);  // Carica il FXML per la modifica
                Parent modifyContent = loader.load();  // Carica la vista della pagina

                ModifyProfileController modifyProfileController = loader.getController();
                modifyProfileController.setAccount(account);
                modifyProfileController.setUtente(user);
                modifyProfileController.setOrigine(ModifyProfileController.Origine.PROFILI);

                //Ottieni la scena corrente
                Scene currentScene = ContenitorePadre.getScene();

                // Crea una nuova scena con il nuovo contenuto
                Scene newScene = new Scene(modifyContent, currentScene.getWidth(), currentScene.getHeight());

                // Ottieni lo Stage corrente e imposta la nuova scena
                Stage stage = (Stage) ContenitorePadre.getScene().getWindow();
                stage.setScene(newScene);
            } catch (IOException e) {
                warningText.setText("Errore durante il caricamento della pagina di modifica: " + e.getMessage());  // Gestione errore
                System.err.println("ProfileView : Errore caricamento pagina di modifica"+e.getMessage());
            }
        } else {
            warningText.setText("Nessun elemento selezionato da modificare.");  // Se non c'è nessun utente, mostra avviso
        }
    }

    //  passaggio alla pagina di creazione utente
    private void paginaCreazioneUtente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/create-profile-view.fxml"),resourceBundle);  // Carica il FXML per la modifica
            Parent createContent = loader.load();  // Carica la vista della pagina

            CreateProfileController createProfileController = loader.getController();
            createProfileController.setAccount(account);

            //Ottieni la scena corrente
            Scene currentScene = ContenitorePadre.getScene();

            // Crea una nuova scena con il nuovo contenuto
            Scene newScene = new Scene(createContent, currentScene.getWidth(), currentScene.getHeight());

            // Ottieni lo Stage corrente e imposta la nuova scena
            Stage stage = (Stage) ContenitorePadre.getScene().getWindow();
            stage.setScene(newScene);
        } catch (IOException e) {
            warningText.setText("Errore durante il caricamento della pagina di creazione: " + e.getMessage());  // Gestione errore
            System.err.println("ProfileView : Errore caricamento pagina di creazione"+e.getMessage());
        }
    }

}
