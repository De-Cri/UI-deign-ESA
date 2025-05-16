package com.esa.moviestar.Profile;

import java.io.IOException;

import com.esa.moviestar.Login.AnimationUtils;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ModifyProfileController {
    @FXML
    GridPane pageContainer;
    @FXML
    VBox ContenitorePaginaModifica;
    @FXML
    VBox elementContainer;
    @FXML
    Group defaultImagine;
    @FXML
    Label creationTitle;
    @FXML
    TextField textName;
    @FXML
    HBox imageScroll1;
    @FXML
    HBox imageScroll2;
    @FXML
    HBox imageScroll3;
    @FXML
    HBox imageScroll4;
    @FXML
    VBox scrollContainer;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;
    @FXML
    Label warningText;
    @FXML
    private Label errorText;
    @FXML
    private VBox imageContainer;

    private Group originalProfileImage;
    private int codImmagineCorrente;
    private String email;
    public void setEmail(String email) {
        this.email = email;
        System.out.println("Email passata alla schermata creazione profilo: " + email);
    }

    public void initialize() {

// DA MODIFICARE IN MODO CHE FACCIA LE MODIFICHE
        errorText.setText("");

        codImmagineCorrente=0;
        defaultImagine.getChildren().add(IconSVG.takeElement(codImmagineCorrente));
        defaultImagine.setScaleX(10);
        defaultImagine.setScaleY(10);

        creationTitle.setText("Modifica il nome utente:"); //label per sopra il textfield per farci capire che stiamo creando un nuovo utente

        textName.setPromptText("Nome");// text field dove inserire il nome, (con all'interno trasparente la scritta "inserisci nome")

        //metto lo stile per ogni scroll di immagini

        scrollContainer.setSpacing(90);

        saveButton.setText("Salva"); //setting del bottone di salvataggio

        cancelButton.setText("Annulla"); //setting del bottone di annullamento

        cancelButton.setOnMouseClicked(e -> {//Se cliccato è un evento irreversibile
            textName.setText(""); //elimina la stringa che scrivo da input se non mi piace
            // elementContainer.getChildren().set(0, originalProfileImage); // ripristina  l'immagine originale

        });
        saveButton.setOnMouseClicked(event -> {  //Se clicco sul bottone di salvataggio / dovrà poi ritornare alla pagina di scelta dei profili con il profilo creato
            String name = textName.getText();
            String gusto = "0";
            int immagine=codImmagineCorrente;

            if (!textName.getText().isEmpty()) {  //se ho messo un nome nel textfield e l'ho salvato allora ritorno alla pagina principale dei profili / oppure potrei far direttamente loggare / (modifiche da fare : controllare che abbia scelto anche un immagine, oppure se non l'ha scelta dare quella di default)
                //questo metodo cosi fa ritornare alla pagina dei profili, aggiungere poi il fatto che io abbia creato il panel nuovo con tutte le modifiche
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/profile-view.fxml"));
                    Parent profileContent = loader.load();

                    ProfileView profileController = loader.getController();
                    profileController.setEmail(email);

                    // Ottieni la scena corrente
                    Scene currentScene = ContenitorePaginaModifica.getScene();

                    // Crea una nuova scena con il nuovo contenuto
                    Scene newScene = new Scene(profileContent, currentScene.getWidth(), currentScene.getHeight());

                    // Ottieni lo Stage corrente e imposta la nuova scena
                    Stage stage = (Stage) ContenitorePaginaModifica.getScene().getWindow();
                    stage.setScene(newScene);

                } catch (IOException e) {
                    warningText.setText("Errore durante il caricamento della pagina di visualizzazione dei profili: " + e.getMessage());
                    System.err.println("ModifyProfileController : Errore caricamento pagina di visualizzazione dei profili"+e.getMessage());
                }
                System.out.println("Ritorni alla pagina dei profili");
            } else {
                errorText.setText("Nessun nome inserito"); //se non ho inserito nessun nome mi da errore perchè per forza va settato un nome , oppure potrei dare il nome di default tipo utente 1
                AnimationUtils.shake(errorText);
            }



        });


        for (int i = 0; i <= 16; i++) {  // Aggiungi le 16 icone (da 1 a 16)
            Group g = new Group();
            g.setScaleX(3.8);
            g.setScaleY(3.8);

            // Usa IconSVG.takeElement(i + 1) per ottenere tutte le icone
            g.getChildren().add(IconSVG.takeElement(i));  // Aggiungi l'elemento SVG al gruppo g

            // Aggiungi il gruppo all'HBox, distribuendo le icone tra imageScroll1, imageScroll2, imageScroll3
            if (i <= 3) {
                imageScroll1.getChildren().add(g);  // Aggiungi il gruppo a imageScroll1
            } else if (i <= 7) {
                imageScroll2.getChildren().add(g);  // Aggiungi il gruppo a imageScroll2
            } else if (i<=11){
                imageScroll3.getChildren().add(g);  // Aggiungi il gruppo a imageScroll3
            }else if (i<=15) {
                imageScroll4.getChildren().add(g);
            }
        }
        imageScroll1.setSpacing(120);
        imageScroll2.setSpacing(120);
        imageScroll3.setSpacing(120);
        imageScroll4.setSpacing(120);
        // Inizializza tutti gli HBox di immagini
        setupImageProfile(imageScroll1);
        setupImageProfile(imageScroll2);
        setupImageProfile(imageScroll3);
        setupImageProfile(imageScroll4);
    }

    private void setupImageProfile(HBox imageScroll) {
        // Cicla attraverso tutti gli elementi dell'HBox (le immagini)
        for (int i = 0; i < imageScroll.getChildren().size(); i++) {
            // prendo un'immagine che ce all'interno dello scrollImage
            Node scrollImage = imageScroll.getChildren().get(i);

            // ogni volta che clicco un immagine all'interno di un imageScroll allora succede qualcosa
            scrollImage.setOnMouseClicked(event -> {
                // Copia l'immagine SVG dal gruppo selezionato
                Group originalGroup = (Group) scrollImage;

                // Crea un nuovo gruppo che contiene l'immagine SVG
                Group clonedGroup = IconSVG.copyGroup(originalGroup);  // Crea una copia del gruppo con l'immagine

                // Aggiungi il clone al container principale
                defaultImagine.getChildren().clear();  // Rimuovi la precedente immagine
                defaultImagine.getChildren().addFirst(clonedGroup);  // Aggiungi la nuova immagine

                // Salva il riferimento dell'immagine selezionata
                originalProfileImage = clonedGroup;  // Salva il clone come immagine originale

                for(int j = 0 ; j < 4 ; j++){
                    HBox c = (HBox) imageContainer.getChildren().get(j);
                    if (c.getChildren().contains(originalGroup)){
                        codImmagineCorrente = c.getChildren().indexOf(originalGroup)+j*4;
                    }
                }
            });

        }
    }
}
