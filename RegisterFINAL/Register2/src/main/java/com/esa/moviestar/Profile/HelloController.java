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
    }

    // Metodo di inizializzazione che viene eseguito subito all'avvio
    public void initialize() {
        testo.setText("Chi vuole guardare Moviestar ?");  // Impostazione del testo della label iniziale
        griglia.setSpacing(40);  // Impostazione della spaziatura tra gli elementi nella griglia


        try {
            UtenteDao dao = new UtenteDao();
            List<Utente> utenti = dao.recuperaTuttiGliUtenti();  // Recupera tutti gli utenti

            // Aggiungi ogni utente alla griglia
            for (Utente utente : utenti) {
                VBox box = new VBox();
                box.setSpacing(10);
                box.setPadding(new Insets(10));
                box.setAlignment(Pos.CENTER);

                // Crea un'etichetta per il nome
                Label name = new Label(utente.getNome());

                // Crea un'icona (modifica questa parte in base a come intendi mostrare l'immagine)
                Group icon = new Group(IconSVG.takeElement(utente.getImmagineProfilo()));

                Button modifica = new Button("Modifica");
                modifica.setPrefWidth(100);
                // Aggiungi azioni per il bottone
                icon.setOnMouseClicked(e -> paginaHome());
                modifica.setOnAction(e -> paginaModifica(utente.getNome(), "colore"));

                box.getChildren().addAll(icon, name, modifica);  // Aggiungi gli elementi alla VBox
                griglia.getChildren().add(box);  // Aggiungi la VBox alla griglia
            }

        } catch (SQLException e) {
            e.printStackTrace();
            warningText.setText("Errore nel recupero degli utenti: " + e.getMessage());  // Mostra eventuali errori
        }


        // Creazione del "pannello di aggiunta" che si visualizzerà come un'area cliccabile
        Pane creazione = new Pane();
        creazione.setPrefHeight(100);  // Altezza del pannello
        creazione.setPrefWidth(100);   // Larghezza del pannello
        creazione.setStyle("-fx-background-color:yellow;" +  // Colore di sfondo del pannello
                "-fx-background-radius:30;" +   // Arrotondamento degli angoli
                "-fx-border-radius: 20;" +      // Border radius per il bordo
                "-fx-border-color: black; " +   // Colore del bordo
                "-fx-border-radius: 30;");      // Altra impostazione di border radius

        // Creazione della VBox che contiene il pannello di aggiunta e la label "Aggiungi"
        VBox creazioneUtente = new VBox();
        Label nomeCreazione = new Label("Aggiungi");  // Etichetta "Aggiungi"
        nomeCreazione.setStyle("-fx-font-size: 20px;" +    // Impostazione del font
                "-fx-font-family: Arial;" +    // Impostazione della famiglia di font
                "-fx-text-fill: white;");      // Colore del testo
        creazioneUtente.getChildren().addAll(creazione, nomeCreazione);  // Aggiungi il pannello e la label alla VBox
        griglia.getChildren().add(creazioneUtente);  // Aggiungi la VBox alla griglia
        creazioneUtente.setAlignment(Pos.CENTER);  // Centra il contenuto all'interno della VBox

        // Aggiungi il listener al pannello di creazione, quando cliccato si esegue tastoAggiungi()
        creazione.setOnMouseClicked(e -> tastoAggiungi());

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
                Group icon = new Group(IconSVG.takeElement(a.getImmagineProfilo()));

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
