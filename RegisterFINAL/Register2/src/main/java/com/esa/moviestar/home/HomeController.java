package com.esa.moviestar.home;

import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;
import com.esa.moviestar.movie_view.FilmCardController;
import com.esa.moviestar.movie_view.WindowCardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HomeController {
    @FXML
    private VBox body;
    @FXML
    private ScrollPane root;
    @FXML
    private VBox scrollViewContainer;
    private  final  String pathWindowCard = "/com/esa/moviestar/movie_view/WindowCard.fxml";
    private final  String pathCardVertical = "/com/esa/moviestar/movie_view/FilmCard_Vertical.fxml";
    private final  String pathCardHorizontal = "/com/esa/moviestar/movie_view/FilmCard_Horizontal.fxml";
    private final ResourceBundle resourceBundle =ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");

    //ui color
    private final Color foreColor = Color.rgb(240, 236, 253);
    private final Color backgroundColor = Color.rgb(15, 15, 15);

    private static final List<String> CATEGORIES = Arrays.asList("action","animation","anime","biography","comedy","crime","documentary","drama","fantasy","history","horror","musical","romantic","sci-fy","superheros","thriller","war","western");

    private static final int LARGE_RECOMMENDATION_LIMIT = 10;
    private static final int SMALL_RECOMMENDATION_LIMIT = 7;

    public void initialize() {}



    /**
     * Set all recommendation lists for a user profile
     */
    public void setRecommendations(Utente user, List<Content> contentList) {
        List<Integer> weights = new ArrayList<>();

        for (int i = 0; i < user.getGusti().length(); i += 2) {
            if (i + 2 <= user.getGusti().length()) {
                try {
                    weights.add(Integer.parseInt(user.getGusti().substring(i, i + 2), 16));
                } catch (NumberFormatException e) {
                    System.err.println("HomeController: the tastes of user can be not valid");
                }
            }
        }
        List<Content> top10 = getTopRecommendations(weights, contentList);
        List<Content> latest10 = getLatestPublished(contentList);
        List<Content> popularByTaste = getPopularByUserTaste(weights, contentList);
        List<Content> similarToLastWatched = getSimilarToLastWatched(user, contentList);
        List<Content> recommendedSeries = getRecommendedSeries(weights, contentList);
        List<Content> bottom7 = getWorstRecommendations(weights, contentList);
        try {
            List<Node> carouselList= new Vector<>();
            for (Content c:popularByTaste) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(pathWindowCard),resourceBundle);
                Node body = loader.load();
                WindowCardController windowCardController  = loader.getController();
                windowCardController.setContent(c);
                windowCardController.getPlayButton().setOnMouseClicked(e->cardClicked(windowCardController.getCardId()));
                windowCardController.getInfoButton().setOnMouseClicked(e->cardClicked(windowCardController.getCardId()));
                carouselList.add(body);
        }
            Carousel carousel = new Carousel();
            carousel.getItems().addAll(carouselList);
            carousel.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/esa/moviestar/styles/Carousel.css")).toExternalForm());
            body.getChildren().add(1, carousel);


            ScrollView top10Scroll = new ScrollView("Scelti per te", Color.TRANSPARENT, foreColor,backgroundColor);
            top10Scroll.setContent(createFilmNodes(top10,false));

            ScrollView latest10Scroll = new ScrollView("Novità", Color.rgb(228,193,42), backgroundColor,null,32.0);
            latest10Scroll.setContent(createFilmNodes(latest10,true));

            ScrollView favouriteCategoryScroll = new ScrollView("La tua categoria preferita", Color.TRANSPARENT, foreColor,backgroundColor);
            favouriteCategoryScroll.setContent(createFilmNodes(popularByTaste,false));

            ScrollView cronologyScroll = new ScrollView("Novità", Color.rgb(115, 65, 190), backgroundColor,null,32.0);
            cronologyScroll.setContent(createFilmNodes(latest10,true));

            ScrollView similarToLastWatchedScroll = new ScrollView("Simili a:"+ user.getUltimoGuardato(), Color.TRANSPARENT, foreColor,backgroundColor);
            similarToLastWatchedScroll.setContent(createFilmNodes(similarToLastWatched,false));


            ScrollView racommendSeriesScroll = new ScrollView("Serie che ti possono piacere", Color.TRANSPARENT, foreColor,backgroundColor);
            racommendSeriesScroll.setContent(createFilmNodes(recommendedSeries,true));

            ScrollView bottom7Scroll = new ScrollView("Nuove Esperienze", Color.TRANSPARENT, foreColor,backgroundColor);
            bottom7Scroll.setContent(createFilmNodes(bottom7,false));


            scrollViewContainer.getChildren().addAll(top10Scroll,latest10Scroll,favouriteCategoryScroll,cronologyScroll,similarToLastWatchedScroll,racommendSeriesScroll,bottom7Scroll);
        }
        catch (IOException e){
            System.err.println("HomeController: Failed to load recommendations \n Error:"+e.getMessage());
        }
    }
    public List<Node> createFilmNodes(List<Content> contentList,boolean isVertical) throws IOException {
        List<Node> nodes= new Vector<>();
        for (Content content: contentList) {
            FXMLLoader fxmlLoader= new FXMLLoader(Objects.requireNonNull(getClass().getResource(isVertical?pathCardVertical:pathCardHorizontal)),resourceBundle);
            Node n = fxmlLoader.load();
            FilmCardController filmCardController = fxmlLoader.getController();
            filmCardController.setContent(content);
            n.setOnMouseClicked(e->cardClicked(filmCardController.getCardId()));
           nodes.add(n);
        }
        return nodes;
    }


        /**
         * Calculate top recommendations based on user taste profile
         */
        public List<Content> getTopRecommendations(List<Integer>weights, List<Content> contentList) {


            return contentList.stream()
                    .sorted((c1, c2) -> {
                        // Calculate relevance scores for each content based on category weights
                        int score1 = calculateRelevanceScore(c1.getCategories(), weights);
                        int score2 = calculateRelevanceScore(c2.getCategories(), weights);

                        // Sort in descending order (highest scores first)
                        return Integer.compare(score2, score1);
                    })
                    .limit(LARGE_RECOMMENDATION_LIMIT)
                    .collect(Collectors.toList());
        }

        public List<Content> getWorstRecommendations(List<Integer>weights, List<Content> contentList) {
            return contentList.stream()
                    .sorted((c1, c2) -> {
                        // Calculate relevance scores for each content based on category weights
                        int score1 = calculateRelevanceScore(c1.getCategories(), weights);
                        int score2 = calculateRelevanceScore(c2.getCategories(), weights);

                        // Sort in ascending order (lowest scores first)
                        return Integer.compare(score1, score2);
                    })
                    .limit(SMALL_RECOMMENDATION_LIMIT)
                    .collect(Collectors.toList());
        }

    /**
     * Calculates a relevance score for content based on user's taste weights
     * @param contentCategories List of category IDs for the content
     * @param tasteWeights List of weights for categories based on user's taste
     * @return The calculated relevance score
     */
    private int calculateRelevanceScore(List<Integer> contentCategories, List<Integer> tasteWeights) {
        if(contentCategories==null)
            return -1;
        int score = 0;
        for (Integer categoryId : contentCategories) {
            // Make sure the category ID is valid for our weights list
            if (categoryId >= 0 && categoryId < tasteWeights.size()) {
                // Add the weight for this category to the total score
                score += tasteWeights.get(categoryId);
            }
        }

        return score;
    }

        /**
         * Get the latest published content
         */
        public List<Content> getLatestPublished(List<Content> contentList)  {
            return contentList.stream()
                    .sorted((c1, c2) -> {
                        return c2.getReleaseDate().compareTo(c1.getReleaseDate());
                    })
                    .limit(LARGE_RECOMMENDATION_LIMIT)
                    .collect(Collectors.toList());
        }

        /**
         * Find content similar to the last watched item
         */
        public List<Content> getSimilarToLastWatched(Utente user, List<Content> contentList) {
            // In un'applicazione reale, otterremmo questo dalla cronologia dell'utente
            int lastWatchedId = user.getUltimoGuardato();

            // Trova l'ultimo contenuto guardato
            Content lastWatched = contentList.stream()
                    .filter(content -> content.getId() == lastWatchedId)
                    .findFirst()
                    .orElse(null);

            if (lastWatched == null || lastWatched.getCategories() == null) {
                return Collections.emptyList();
            }

            // Crea un set per ricerche più veloci
            Set<Integer> lastWatchedCategories = new HashSet<>(lastWatched.getCategories());

            return contentList.stream()
                    .filter(content -> content.getId() != lastWatchedId && content.getCategories() != null) // Escludi lo stesso contenuto e quelli con categorie nulle
                    .map(content -> {
                        // Conta le categorie comuni
                        long commonCategories = content.getCategories().stream()
                                .filter(lastWatchedCategories::contains)
                                .count();
                        return new AbstractMap.SimpleEntry<>((int) commonCategories, content);
                    })
                    .filter(entry -> entry.getKey() > 0) // Includi solo contenuti con almeno una categoria in comune
                    .sorted(Map.Entry.<Integer, Content>comparingByKey().reversed())
                    .limit(SMALL_RECOMMENDATION_LIMIT)
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }

        /**
         * Get recommended TV series based on user taste
         */
        public List<Content> getRecommendedSeries(List<Integer>weights, List<Content> contentList) {
            return contentList.stream()
                    .filter(Content::isSeries) // Only TV series
                    .map(content -> {
                        int score = calculateContentScore(content, weights);
                        return new AbstractMap.SimpleEntry<>(score, content);
                    })
                    .sorted(Map.Entry.<Integer, Content>comparingByKey().reversed())
                    .limit(LARGE_RECOMMENDATION_LIMIT)
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }

        /**
         * Get popular content aligned with user taste profile
         */
        public List<Content> getPopularByUserTaste(List<Integer>weights, List<Content> contentList) {
            int maxWeight = -1;
            int maxWeightIndex = -1;

            for (int i = 0; i < weights.size(); i++) {
                if (weights.get(i) > maxWeight) {
                    maxWeight = weights.get(i);
                    maxWeightIndex = i;
                }
            }
            if (maxWeightIndex < 0 || maxWeightIndex >= CATEGORIES.size()) {
                return Collections.emptyList();
            }

            final Integer favoriteCategory = maxWeightIndex;

            List<Content> filteredContent = contentList.stream()
                    .filter(content -> content.getCategories() != null && content.getCategories().contains(favoriteCategory))
                    .toList();

            if (filteredContent.isEmpty()) {
                return Collections.emptyList();
            }

            long maxClicks = filteredContent.stream()
                    .mapToLong(Content::getClicks)
                    .max()
                    .orElse(1);

            final double alpha = 0.6; // weight of the click
            final double beta = 0.4;  // weight of the category

            return filteredContent.stream()
                    .map(content -> {
                        double normalizedClicks = (double) content.getClicks() / maxClicks;

                        // Calcola il punteggio di compatibilità della categoria, gestendo il caso di categorie nulle
                        double categoryScore = 0.0;
                        if (content.getCategories() != null && !content.getCategories().isEmpty()) {
                            categoryScore = content.getCategories().stream()
                                    .filter(cat -> cat < weights.size() && cat < CATEGORIES.size())
                                    .mapToDouble(cat -> weights.get(cat) / 255.0)
                                    .sum() / content.getCategories().size();
                        }

                        double totalScore = alpha * normalizedClicks + beta * categoryScore;

                        return new AbstractMap.SimpleEntry<>(totalScore, content);
                    })
                    .sorted(Map.Entry.<Double, Content>comparingByKey().reversed())
                    .limit(LARGE_RECOMMENDATION_LIMIT)
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }



        /**
         * Calculate scores for a list of content items based on user taste weights
         */
        private int calculateContentScore(Content content, List<Integer> weights) {
            if (content.getCategories() == null || content.getCategories().isEmpty()) {
                return 0; // O un altro valore predefinito appropriato per indicare un punteggio nullo o minimo
            }

            int score = 0;
            for (Integer categoryId : content.getCategories()) {
                if (categoryId != null && categoryId < weights.size()) {
                    score += weights.get(categoryId);
                }
            }
            return score;
        }

        /**
         * Calculate score for a single content item based on user taste weights
         */



    public void cardClicked(long idFilm){
        System.out.println("hellofromcard"+idFilm);
    }


}
