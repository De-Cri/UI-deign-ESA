package com.esa.moviestar.Profile;

import com.esa.moviestar.Database.UtenteDao;
import com.esa.moviestar.model.Utente;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class HelloController {

    @FXML   // Collegamento alla label del testo
    Label testo;

    @FXML   // HBox per la griglia che contiene gli utenti
    HBox griglia;

    @FXML   // StackPane che contiene l'intera schermata
    StackPane ContenitorePadre;

    @FXML   // Label per eventuali messaggi di errore o avviso
    Label warningText;

    private int codUtente;
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
        try {
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
                modifica.setOnAction(e -> paginaModifica(utente.getNome(), "colore"));

                box.getChildren().addAll(iconBox, name, modifica);
                griglia.getChildren().add(box);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            warningText.setText("Errore nel recupero degli utenti: " + e.getMessage());
        }
    }

    // Metodo che si attiva quando l'utente clicca sul pannello "Aggiungi"
    private void tastoAggiungi() {
        if (griglia.getChildren().size() < 4) {  // Se ci sono meno di 4 elementi, posso aggiungerne uno nuovo
            paginaCreazioneUtente();  // Carica la pagina di creazione
            griglia.getChildren().add(recuperoCreazioneDellUtente());  // Aggiungi un nuovo utente alla griglia
        } else if (griglia.getChildren().size() == 4) {  // Se la griglia ha già 4 elementi
            griglia.getChildren().remove(0);  // Rimuovi il primo utente della griglia (per far spazio al nuovo)
            griglia.getChildren().add(recuperoCreazioneDellUtente());  // Aggiungi il nuovo utente
        }
    }

    // Metodo per simulare il passaggio alla pagina Home
    private void paginaHome() {
        System.out.println("SEI NELLA PAGINA HOME ");  // Stampa un messaggio per debugging
    }

    // Metodo che gestisce il passaggio alla pagina di modifica
    private void paginaModifica(String nome, String colore) {
        if (griglia.getChildren().size() > 1) {  // Verifica che ci sia almeno un utente nella griglia
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("modify-profile-view.fxml"));  // Carica il FXML per la modifica
                Parent modifyContent = loader.load();  // Carica la vista della pagina
                ContenitorePadre.getChildren().clear();  // Svuota il contenitore principale
                ContenitorePadre.getChildren().add(modifyContent);  // Aggiungi la nuova vista al contenitore principale
            } catch (IOException e) {
                warningText.setText("Errore durante il caricamento della pagina di modifica: " + e.getMessage());  // Gestione errore
                e.printStackTrace();  // Stampa il trace dell'errore
            }
        } else {
            warningText.setText("Nessun elemento selezionato da modificare.");  // Se non c'è nessun utente, mostra avviso
        }
    }

    // Metodo che simula il passaggio alla pagina di creazione utente
    private void paginaCreazioneUtente() {
        System.out.println("SEI NELLA PAGINA DI CREAZIONE");  // Stampa un messaggio per il debug
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("create-profile-view.fxml"));  // Carica il FXML per la modifica
            Parent modifyContent = loader.load();  // Carica la vista della pagina
            ContenitorePadre.getChildren().clear();  // Svuota il contenitore principale
            ContenitorePadre.getChildren().add(modifyContent);  // Aggiungi la nuova vista al contenitore principale
        } catch (IOException e) {
            warningText.setText("Errore durante il caricamento della pagina di modifica: " + e.getMessage());  // Gestione errore
            e.printStackTrace();  // Stampa il trace dell'errore
        }
    }

    // Metodo che crea e restituisce un nuovo utente con il nome e l'immagine specificati
    private VBox recuperoCreazioneDellUtente() {
        VBox box = new VBox();
        box.setSpacing(10);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);  // Centro gli elementi

        try {
            UtenteDao dao = new UtenteDao();
            codUtente = dao.recuperoCodiceUtente(email);

            if (codUtente != -1) {
                Utente a = dao.recuperoUtente(email, codUtente);

                Label name = new Label(a.getNome());
                Group icon = new Group(IconSVG.takeElement(a.getIDIcona()));

                Button modifica = new Button("Modifica");
                modifica.setPrefWidth(100);

                // Aggiungi azioni
                icon.setOnMouseClicked(e -> paginaHome());
                modifica.setOnAction(e -> paginaModifica(a.getNome(), "colore"));  // puoi sostituire "colore" se serve

                box.getChildren().addAll(icon, name, modifica);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return box;
    }

}
