package com.esa.moviestar.Profile;

import com.esa.moviestar.Database.UtenteDao;
import com.esa.moviestar.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
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


    private String email;
    public void setEmail(String email) {
        this.email = email;
        System.out.println("Email passata alla schermata creazione profilo: " + email);
        caricaUtenti();
    }

    // Metodo di inizializzazione che viene eseguito subito all'avvio
    public void initialize() {
        testo.setText("Chi vuole guardare Moviestar ?");  // Impostazione del testo della label iniziale
        griglia.setSpacing(40);  // Impostazione della spaziatura tra gli elementi nella griglia


    }

    private void caricaUtenti() {
            UtenteDao dao = new UtenteDao();
            List<Utente> utenti = dao.recuperaTuttiGliUtenti(email);

            if (utenti.size() < 4) {
                Pane creazione = new Pane();
                creazione.setPrefHeight(100);
                creazione.setPrefWidth(100);
                creazione.setStyle("-fx-background-color:yellow;" +
                        "-fx-background-radius:30;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: black; " +
                        "-fx-border-radius: 30;");

                VBox creazioneUtente = new VBox();
                Label nomeCreazione = new Label("Aggiungi");
                nomeCreazione.setStyle("-fx-font-size: 20px;" +
                        "-fx-font-family: Arial;" +
                        "-fx-text-fill: white;");
                creazioneUtente.getChildren().addAll(creazione, nomeCreazione);
                griglia.getChildren().add(creazioneUtente);
                creazioneUtente.setAlignment(Pos.CENTER);
                creazione.setOnMouseClicked(e -> tastoAggiungi());
            }

            for (Utente utente : utenti) {
                VBox box = new VBox();
                box.setSpacing(10);
                box.setPadding(new Insets(10));
                box.setAlignment(Pos.CENTER);

                Label name = new Label(utente.getNome());
                name.getStyleClass().addAll("on-primary", "bold-text", "large-text");

                Group icon = new Group(IconSVG.takeElement(utente.getIDIcona()));
                icon.setScaleY(5);
                icon.setScaleX(5);

                StackPane iconBox = new StackPane(icon);
                StackPane.setAlignment(icon, Pos.CENTER);
                iconBox.setMinSize(100, 100);

                box.setOnMouseEntered(event -> {
                    icon.setScaleX(6);
                    icon.setScaleY(6);
                    name.getStyleClass().addAll("bold-text", "large-text");
                });

                box.setOnMouseExited(event -> {
                    icon.setScaleX(4);
                    icon.setScaleY(4);
                    name.getStyleClass().addAll("on-primary", "bold-text", "large-text");
                });

                Button modifica = new Button("Modifica");
                modifica.setPrefWidth(100);
                icon.setOnMouseClicked(e -> paginaHome());
                modifica.setOnAction(e -> paginaModifica());

                box.getChildren().addAll(iconBox, name, modifica);
                griglia.getChildren().add(box);
            }


    }

    // Metodo che si attiva quando l'utente clicca sul pannello "Aggiungi"
    private void tastoAggiungi() {
        if (griglia.getChildren().size() < 4) {  // Se ci sono meno di 4 elementi, posso aggiungerne uno nuovo
            paginaCreazioneUtente();  // Carica la pagina di creazione
            griglia.getChildren().add(recuperoCreazioneDellUtente());  // Aggiungi un nuovo utente alla griglia
        } else if (griglia.getChildren().size() == 4) {  // Se la griglia ha già 4 elementi
            griglia.getChildren().removeFirst();  // Rimuovi il primo utente della griglia (per far spazio al nuovo)
            griglia.getChildren().add(recuperoCreazioneDellUtente());  // Aggiungi il nuovo utente
        }
    }

    // Metodo per simulare il passaggio alla pagina Home
    private void paginaHome() {
        System.out.println("SEI NELLA PAGINA HOME ");  // Stampa un messaggio per debugging
    }

    // Metodo che gestisce il passaggio alla pagina di modifica
    private void paginaModifica() {
        if (griglia.getChildren().size() > 1) {  // Verifica che ci sia almeno un utente nella griglia
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/modify-profile-view.fxml"),resourceBundle);  // Carica il FXML per la modifica
                Parent modifyContent = loader.load();  // Carica la vista della pagina

                ModifyProfileController modifyProfileController = loader.getController();
                modifyProfileController.setEmail(email);

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

    // Metodo che simula il passaggio alla pagina di creazione utente
    private void paginaCreazioneUtente() {
        System.out.println("SEI NELLA PAGINA DI CREAZIONE");  // Stampa un messaggio per il debug
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/create-profile-view.fxml"),resourceBundle);  // Carica il FXML per la modifica
            Parent createContent = loader.load();  // Carica la vista della pagina

            CreateProfileController createProfileController = loader.getController();
            createProfileController.setEmail(email);

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

    // Metodo che crea e restituisce un nuovo utente con il nome e l'immagine specificati
    private VBox recuperoCreazioneDellUtente() {
        VBox box = new VBox();
        box.setSpacing(10);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);  // Centro gli elementi

            UtenteDao dao = new UtenteDao();

            int codUtente = dao.recuperoCodiceUtente(email);

            if (codUtente != -1) {
                Utente a = dao.recuperoUtente(email, codUtente);

                Label name = new Label(a.getNome());
                Group icon = new Group(IconSVG.takeElement(a.getIDIcona()));

                Button modifica = new Button("Modifica");
                modifica.setPrefWidth(100);

                // Aggiungi azioni
                icon.setOnMouseClicked(e -> paginaHome());
                modifica.setOnAction(e -> paginaModifica());  // puoi sostituire "colore" se serve

                box.getChildren().addAll(icon, name, modifica);
            }
            return box;
    }

}
