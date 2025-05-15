package com.esa.moviestar.home;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Profile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;


import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class MainPagesController {
    @FXML
    AnchorPane body;
    @FXML
    private StackPane headerContainer;
    private final ResourceBundle resourceBundle =ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");
    private HeaderController headerController;
    private Node homeBody;
    private Node currentScene;
    private Node s;
    private Node searchNode;
    private HomeController homeBodyController;
    private List<Content> c;

    public void  initialize()  {
        loadHeader();
        homeBody =loadDynamicBody("home.fxml");
        currentScene = homeBody;
        Profile p = new Profile(1,"eden","FF1C2A0B3D4E1F00112233445566778009",6);
        c = new ArrayList<>(Arrays.asList(
                        new Content(1L, "SPiderman", "un ragazzo viene punto da un ragno", "1h 57m", 4.6, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 88935L, "2012, 05, 28", false, new ArrayList<>(Arrays.asList(1, 15))), // action, superheros
                        new Content(2L, "Avengers: Endgame", "I Vendicatori rimasti cercano di annullare le azioni di Thanos.", "3h 1m", 4.8, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 157896L, "2019, 04, 24", true, new ArrayList<>(Arrays.asList(1, 14, 15))), // action, sci-fy, superheros
                        new Content(3L, "Il Cavaliere Oscuro", "Batman affronta il Joker.", "2h 32m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 123456L, "2008, 07, 23", true, new ArrayList<>(Arrays.asList(1, 6, 16))), // action, crime, thriller
                        new Content(4L, "Pulp Fiction", "Storie intrecciate di criminali a Los Angeles.", "2h 34m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 98765L, "1994, 10, 14", true, new ArrayList<>(Arrays.asList(6, 8))), // crime, drama
                        new Content(5L, "Ritorno al Futuro", "Un adolescente viaggia indietro nel tempo.", "1h 56m", 4.8, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 76543L, "1985, 07, 03", true, new ArrayList<>(Arrays.asList(5, 9, 14))), // comedy, fantasy, sci-fy
                        new Content(6L, "Your Name.", "Due studenti delle superiori scoprono di scambiarsi i corpi magicamente e periodicamente.", "1h 46m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 65432L, "2016, 08, 26", true, new ArrayList<>(Arrays.asList(3, 9, 13))), // anime, fantasy, romantic
                        new Content(7L, "Parasite", "Una famiglia povera si infiltra nella ricca casa dei Park, uno per uno.", "2h 12m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 54321L, "2019, 05, 30", true, new ArrayList<>(Arrays.asList(6, 8, 16))), // crime, drama, thriller
                        new Content(8L, "The Shawshank Redemption", "Un uomo innocente è condannato per l'omicidio di sua moglie e trascorre quasi due decenni in prigione dove fa amicizia con un altro detenuto.", "2h 22m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 43210L, "1994, 09, 23", true, new ArrayList<>(Arrays.asList(8))), // drama
                        new Content(9L, "Spider-Man: Into the Spider-Verse", "Miles Morales diventa il nuovo Spider-Man e incontra altri Spider-Eroi provenienti da dimensioni parallele.", "1h 57m", 4.8, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 32109L, "2018, 12, 14", true, new ArrayList<>(Arrays.asList(1, 2, 15))), // action, animation, superheros
                        new Content(10L, "Forrest Gump", "La vita epica di un uomo semplice attraverso decenni di eventi storici.", "2h 22m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 21098L, "1994, 07, 06", true, new ArrayList<>(Arrays.asList(5, 8, 10))), // comedy, drama, history
                        new Content(11L, "Toy Story", "Un gruppo di giocattoli prende vita quando il loro proprietario non è in giro.", "1h 21m", 4.6, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 11122L, "1995, 11, 22", true, new ArrayList<>(Arrays.asList(2, 5, 9))), // animation, comedy, fantasy
                        new Content(12L, "Spirited Away", "Una bambina si ritrova nel mondo degli spiriti.", "2h 5m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 9876L, "2001, 07, 20", true, new ArrayList<>(Arrays.asList(2, 3, 9))), // animation, anime, fantasy
                        new Content(13L, "The Dark Knight Rises", "Batman torna per affrontare Bane.", "2h 44m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 87654L, "2012, 07, 20", true, new ArrayList<>(Arrays.asList(1, 16))), // action, thriller
                        new Content(14L, "Inception", "Un ladro che ruba segreti entrando nei sogni delle persone riceve l'incarico di impiantare un'idea nella mente di un amministratore delegato.", "2h 28m", 4.8, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 7654L, "2010, 07, 16", true, new ArrayList<>(Arrays.asList(1, 14, 16))), // action, sci-fy, thriller
                        new Content(15L, "Good Will Hunting", "Un giovane prodigio della matematica, con problemi con la legge, accetta di seguire una terapia.", "2h 6m", 4.7, "https://placehold.co/600x400?text=Good+Will+Hunting", 654321L, "1997, 12, 05", true, new ArrayList<>(Arrays.asList(8))), // drama
                        new Content(16L, "The Lion King", "Un giovane leone destinato a diventare re viene manipolato dallo zio per fuggire dal suo regno.", "1h 28m", 4.5, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 5432L, "1994, 06, 24", true, new ArrayList<>(Arrays.asList(2, 8))), // animation, drama
                        new Content(17L, "Seven Samurai", "Un villaggio di contadini assolda sette samurai per proteggerli dai banditi.", "3h 27m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 4321L, "1954, 04, 26", true, new ArrayList<>(Arrays.asList(1, 8))), // action, drama
                        new Content(18L, "Interstellar", "Un gruppo di esploratori viaggia attraverso un wormhole nello spazio alla ricerca di una nuova casa per l'umanità.", "2h 49m", 4.8, "https://placehold.co/600x400?text=Interstellar", 3210L, "2014, 11, 07", true, new ArrayList<>(Arrays.asList(9, 14))), // fantasy, sci-fy
                        new Content(19L, "The Godfather", "La saga di una famiglia criminale italoamericana.", "2h 55m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 2109L, "1972, 03, 24", true, new ArrayList<>(Arrays.asList(6, 8))), // crime, drama
                        new Content(20L, "Back to the Future Part II", "Marty McFly e Doc Brown viaggiano nel futuro.", "1h 48m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 10987L, "1989, 11, 22", true, new ArrayList<>(Arrays.asList(5, 14))), // comedy, sci-fy
                        new Content(21L, "The Matrix", "Un hacker scopre che la sua realtà è una simulazione creata da macchine.", "2h 16m", 4.8, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 987654L, "1999, 03, 31", true, new ArrayList<>(Arrays.asList(1, 14))), // action, sci-fy
                        new Content(22L, "Fight Club", "Un impiegato insoddisfatto forma un club di combattimento segreto con un venditore di sapone carismatico.", "2h 19m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 876543L, "1999, 10, 15", true, new ArrayList<>(Arrays.asList(8, 16))), // drama, thriller
                        new Content(23L, "Pirati dei Caraibi: La maledizione della prima luna", "Un fabbro si allea con un eccentrico pirata per salvare la donna che ama dal suo equipaggio maledetto.", "2h 23m", 4.6, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 765432L, "2003, 07, 09", true, new ArrayList<>(Arrays.asList(1, 9))), // action, fantasy
                        new Content(24L, "The Silence of the Lambs", "Una giovane agente dell'FBI cerca l'aiuto di un assassino psicopatico imprigionato per catturare un altro serial killer.", "1h 58m", 4.8, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 65432L, "1991, 02, 14", true, new ArrayList<>(Arrays.asList(16))), // thriller
                        new Content(25L, "Schindler's List", "Nella Polonia occupata dai tedeschi durante la Seconda Guerra Mondiale, Oskar Schindler, un industriale, cerca di salvare la sua forza lavoro ebrea dalla persecuzione nazista.", "3h 15m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 543210L, "1993, 12, 15", true, new ArrayList<>(Arrays.asList(8, 10, 17))), // drama, history, war
                        new Content(26L, "The Lord of the Rings: The Fellowship of the Ring", "Un hobbit della Contea e otto compagni intraprendono una ricerca per distruggere l'Unico Anello e sconfiggere il Signore Oscuro Sauron.", "2h 58m", 4.9, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 432109L, "2001, 12, 19", true, new ArrayList<>(Arrays.asList(1, 9))), // action, fantasy
                        new Content(27L, "Eternal Sunshine of the Spotless Mind", "Una coppia si sottopone a una procedura medica per cancellare i ricordi l'uno dell'altra dopo una brutta rottura.", "1h 48m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 321098L, "2004, 03, 19", true, new ArrayList<>(Arrays.asList(8, 9, 14))), // drama, fantasy, sci-fy
                        new Content(28L, "The Departed", "La vita di due talpe, una nella polizia statale del Massachusetts e l'altra nella mafia irlandese, si incrociano.", "2h 31m", 4.8, "https://placehold.co/600x400?text=The+Departed", 210987L, "2006, 10, 06", true, new ArrayList<>(Arrays.asList(6, 8, 16))), // crime, drama, thriller
                        new Content(29L, "Gladiator", "Un generale romano viene tradito e la sua famiglia assassinata da un imperatore ambizioso.", "2h 35m", 4.7, "https://placehold.co/600x400?text=Gladiator", 109876L, "2000, 05, 05", true, new ArrayList<>(Arrays.asList(1, 10))), // action, history
                        new Content(30L, "The Prestige", "Due prestigiatori rivali nell'Inghilterra di fine Ottocento si impegnano in una battaglia di trucchi in continua escalation che ha conseguenze tragiche.", "2h 10m", 4.8, "https://placehold.co/600x400?text=The+Prestige", 98765L, "2006, 10, 20", true, new ArrayList<>(Arrays.asList(8, 16))), // drama, thriller
                        new Content(31L, "Memento", "Un uomo affetto da amnesia anterograda cerca l'assassino di sua moglie usando appunti e tatuaggi.", "1h 53m", 4.7, "https://placehold.co/600x400?text=Memento", 87654L, "2000, 05, 25", true, new ArrayList<>(Arrays.asList(16))), // thriller
                        new Content(32L, "Raiders of the Lost Ark", "L'archeologo Indiana Jones viene assunto dal governo degli Stati Uniti per trovare l'Arca dell'Alleanza prima dei nazisti.", "1h 55m", 4.8, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 76543L, "1981, 06, 12", true, new ArrayList<>(Arrays.asList(1, 10))), // action, history
                        new Content(33L, "No Country for Old Men", "Un cacciatore si imbatte in un accordo di droga andato male e in due uomini che seguono i soldi.", "2h 2m", 4.7, "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece", 65432L, "2007, 11, 09", true, new ArrayList<>(Arrays.asList(6, 16, 18)))

                ));


        try {

            homeBodyController.setRecommendations(p,c);
            body.getChildren().clear();
            body.getChildren().add(homeBody);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //s=loadDynamicBody("seconda.fxml");
//        Node home =loadDynamicBody("home.fxml");
//        Node home =loadDynamicBody("home.fxml");
    }

    private void loadHeader(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("header.fxml"), resourceBundle);
            Node headerNode = loader.load();
            headerContainer.getChildren().add(headerNode);
            headerController = loader.getController();
            headerController.homeButton.setOnMouseClicked(e -> {
                if (homeBody == null)
                    return;
                if (body.getChildren().contains(homeBody))
                    return;
                currentScene = homeBody;
                body.getChildren().clear();
                body.getChildren().add(homeBody);

            });
            headerController.filmButton.setOnMouseClicked(e -> {
                if (body.getChildren().contains(s))
                    return;
                if (s == null)
                    s = loadDynamicBody("seconda.fxml");
                currentScene = s;
                body.getChildren().clear();
                body.getChildren().add(s);
            });
            headerController.seriesButton.setOnMouseClicked(e -> {
                if (homeBody == null)
                    return;
                if (body.getChildren().contains(homeBody))
                    return;
                //currentScene = series;
                body.getChildren().clear();
                body.getChildren().add(homeBody);
            });

            headerController.getTbxSearch().textProperty().addListener((observableValue, oldV,  newV)
                    -> {
                if (newV.isEmpty()) {
                    try {
                        body.getChildren().clear();
                        body.getChildren().add(currentScene);

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    return ;
                }
                try {
                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("search.fxml"));
                    Node search = loader1.load();
                    AnchorPane.setBottomAnchor(search, 0.0);
                    AnchorPane.setTopAnchor(search, 0.0);
                    AnchorPane.setLeftAnchor(search, 0.0);
                    AnchorPane.setRightAnchor(search, 0.0);
                    body.getChildren().clear();
                    ((SearchController) loader1.getController()).set_headercontroller(headerController);
                    ((SearchController) loader1.getController()).set_trylist(homeBodyController.createFilmNodes(c,false));
                    body.getChildren().add(search);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            });
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Node loadDynamicBody(String bodySource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(bodySource));
            Node body = loader.load();
            AnchorPane.setBottomAnchor(body,0.0);
            AnchorPane.setTopAnchor(body,0.0);
            AnchorPane.setLeftAnchor(body,0.0);
            AnchorPane.setRightAnchor(body,0.0);
            if (loader.getController() instanceof  HomeController)
                homeBodyController = loader.getController();//messo solo per provare
            //Object ugo = loader.getController();//

            return body;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}