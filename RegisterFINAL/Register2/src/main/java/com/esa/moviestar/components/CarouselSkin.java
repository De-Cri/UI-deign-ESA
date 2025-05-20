package com.esa.moviestar.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

public class CarouselSkin extends SkinBase<Carousel> {

    // --- Constants ---
    private static final double CENTER_SCALE = 1.5;
    private static final double SIDE_SCALE = 1.0;
    private static final double CARD_OVERLAP_FACTOR = 0.85;
    private static final Duration TRANSITION_DURATION = Duration.millis(500);
    private static final double SLIDE_DISTANCE_FACTOR = 0.65;
    private static final double SCREEN_HEIGHT_PERCENTAGE = 0.6;
    private static final Duration AUTO_ROTATION_INTERVAL = Duration.seconds(10);
    private static final double DEFAULT_SIZE = 16;
    // --- UI Components ---
    private final VBox mainContainer;
    private final HBox cardsContainer;
    private final HBox dotsContainer;
    private final Rectangle clipRect;

    // --- State & Properties ---
    private Timeline animationTimeline;
    private Timeline autoRotationTimeline;
    private final List<Node> cardWrappers;

    private int currentDisplayedIndex = 0;
    private boolean animationInProgress = false;
    private boolean autoRotationEnabled = false;

    // --- Dynamic Calculated Values ---
    private double dynamicSlideDistance = 450;

    /**
     * Constructor for the CarouselSkin.
     * @param control The Carousel control this skin is for.
     */
    public CarouselSkin(Carousel control) {
        super(control);

        // Initialize card wrappers list
        cardWrappers = new ArrayList<>();

        // Container for all cards
        cardsContainer = new HBox();
        cardsContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(cardsContainer,new Insets(150,0,150,0));
        // Container for navigation dots
        dotsContainer = new HBox(DEFAULT_SIZE);
        dotsContainer.setAlignment(Pos.BOTTOM_CENTER);
        dotsContainer.setPadding(new Insets(DEFAULT_SIZE, 0, DEFAULT_SIZE, 0));

        // Region to separate carousel by the main
        Region r = new Region();
        VBox.setVgrow(r, Priority.ALWAYS);
        // Main container with clipping
        mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);

        mainContainer.getChildren().addAll(cardsContainer,r,dotsContainer);


        // Ensure dots are on top
        dotsContainer.setViewOrder(-1);

        // --- Clipping ---
        clipRect = new Rectangle();
        clipRect.widthProperty().bind(mainContainer.widthProperty());
        clipRect.heightProperty().bind(mainContainer.heightProperty());
        mainContainer.setClip(clipRect);

        getChildren().add(mainContainer);

        // Initialize animation timeline
        animationTimeline = new Timeline();
        autoRotationTimeline = createAutoRotationTimeline();

        // --- Listeners ---
        // Listen for changes in the items list
        control.getItems().addListener((ListChangeListener<? super Node>) (c) ->
                Platform.runLater(this::rebuildCarousel));

        // Listen for changes in the selected index
        control.currentIndexProperty().addListener((obs, oldVal, newVal) ->
                handleIndexChange(oldVal, newVal));

        // Listen for scene changes to adjust height
        control.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                adjustHeightToScreenPercentage(control);

                // Listen for window size changes
                Window window = newScene.getWindow();
                if (window != null) {
                    window.heightProperty().addListener((heightObs, heightOld, heightNew) ->
                            adjustHeightToScreenPercentage(control));
                }
            }
        });

        rebuildCarousel();
    }

    /**
     * Starts the automatic rotation of the carousel.
     * The carousel will rotate to the next card every 5 seconds if items are loaded and visible.
     */
    public void start() {
        if (!autoRotationEnabled && getSkinnable().getItems().size() > 1) {
            autoRotationEnabled = true;
            autoRotationTimeline.play();
        }
    }

    /**
     * Stops the automatic rotation of the carousel.
     */
    public void stop() {
        if (autoRotationEnabled) {
            autoRotationEnabled = false;
            autoRotationTimeline.stop();
        }
    }

    /**
     * Creates a timeline for auto-rotation.
     */
    private Timeline createAutoRotationTimeline() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(AUTO_ROTATION_INTERVAL, event -> {
            if (!animationInProgress && getSkinnable().getItems().size() > 1) {
                int nextIndex = normalizeIndex(currentDisplayedIndex + 1, getSkinnable().getItems().size());
                getSkinnable().goTo(nextIndex);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        return timeline;
    }

    /**
     * Adjusts the carousel height to be a percentage of the screen height.
     */
    private void adjustHeightToScreenPercentage(Carousel control) {
        Scene scene = control.getScene();
        if (scene != null) {
            Window window = scene.getWindow();
            if (window != null) {
                double screenHeight = window.getHeight();
                double carouselHeight = screenHeight * SCREEN_HEIGHT_PERCENTAGE;
                control.setPrefHeight(carouselHeight);
                control.setMinHeight(800);
                control.setMaxHeight(carouselHeight * 1.2);
            }
        }
    }

    /**
     * Calculates dynamic layout values based on max card dimensions.
     */
    private void updateDynamicLayoutValues() {
        double width = cardWrappers.stream().mapToDouble(node -> node.getBoundsInLocal().getWidth()).max().orElse(0);
        if (width <= 0) return;
        // Calculate overlap: Negative spacing for HBox
        double dynamicCardOverlap = width * CARD_OVERLAP_FACTOR;
        cardsContainer.setSpacing(-dynamicCardOverlap);

        // Calculate slide distance
        dynamicSlideDistance = width * SLIDE_DISTANCE_FACTOR;
    }

    /**
     * Handles the logic when the current index of the Carousel changes.
     */
    private void handleIndexChange(int oldIndex, int newIndex) {
        if (animationInProgress) {
            return;
        }

        int n = getSkinnable().getItems().size();
        if (n == 0) return;

        // Normalize indices to be within bounds [0, n-1]
        int normalizedOldIndex = normalizeIndex(oldIndex, n);
        int normalizedNewIndex = normalizeIndex(newIndex, n);

        if (normalizedOldIndex == normalizedNewIndex) {
            return;
        }

        // Determine if it's a jump (more than 1 step or initial load)
        boolean isJump = oldIndex == -1 || Math.abs(normalizedOldIndex - normalizedNewIndex) > 1;
        // Handle the wrap-around case as a non-jump animation
        if (Math.abs(normalizedOldIndex - normalizedNewIndex) == n - 1) {
            isJump = false;
        }

        if (isJump) {
            jumpToIndex(normalizedNewIndex, false);
        } else {
            animateToIndex(normalizedNewIndex);
        }
    }

    /**
     * Normalizes an index to be within the bounds of the item list.
     */
    private int normalizeIndex(int index, int itemCount) {
        if (itemCount == 0) return 0;
        return ((index % itemCount) + itemCount) % itemCount;
    }

    /**
     * Rebuilds the entire carousel display (cards and dots).
     */
    private void rebuildCarousel() {
        // Stop any ongoing animation
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationInProgress = false;
        }

        // Clear everything
        cardsContainer.getChildren().clear();
        dotsContainer.getChildren().clear();
        cardWrappers.clear();
        Carousel carousel = getSkinnable();
        int itemCount = carousel.getItems().size();

        if (itemCount == 0) {
            currentDisplayedIndex = -1;
            return;
        }

        // Create card wrappers for each item
        for (int i = 0; i < itemCount; i++) {
            Node item = carousel.getItems().get(i);

            Node cardWrapper = createCardWrapper(item);
            item.viewOrderProperty().bind(cardWrapper.viewOrderProperty());
            cardWrappers.add(cardWrapper);
        }

        // Create navigation dots
        for (int i = 0; i < itemCount; i++) {
            Button dot = createDotButton(i);
            dot.getStyleClass().add("carousel-dot");
            dotsContainer.getChildren().add(dot);
        }

        adjustHeightToScreenPercentage(getSkinnable());
        jumpToIndex(0, true);

    }



    /**
     * Creates a styled wrapper for a carousel item node.
     */
    private StackPane createCardWrapper(Node content) {
        StackPane cardWrapper = new StackPane();
        cardWrapper.getStyleClass().add("carousel-card");
        cardWrapper.getChildren().add(content);

        cardWrapper.setOnMouseClicked(e -> {

            if (!animationInProgress) {
                if (autoRotationEnabled) {
                    autoRotationTimeline.stop();
                    autoRotationTimeline.play();}
                try {
                    if(cardWrapper.getViewOrder()!=0){
                        animateToIndex(cardWrappers.indexOf(cardWrapper));}
                } catch (Exception ex) {
                    System.err.println("Error navigating to card: " + ex.getMessage());
                }
            }
        });

        // Use one combined listener for size changes
        cardWrapper.layoutBoundsProperty().addListener((obs, old, newBounds) -> {
            if ((Math.abs(old.getWidth() - newBounds.getWidth()) > 100 ||
                    Math.abs(old.getHeight() - newBounds.getHeight()) > 100) &&
                    old.getHeight()!=newBounds.getHeight()) {
                updateDynamicLayoutValues();
            }
        });

        return cardWrapper;
    }

    /**
     * Creates a navigation dot button.
     */
    private Button createDotButton(int index) {
        Button dot = new Button();
        dot.getStyleClass().add("carousel-dot");
        dot.setMinSize(DEFAULT_SIZE, DEFAULT_SIZE);
        dot.setPrefSize(DEFAULT_SIZE, DEFAULT_SIZE);
        dot.setMaxSize(DEFAULT_SIZE, DEFAULT_SIZE);
        // Handle click action
        dot.setOnAction(e -> {
            if (!animationInProgress && getSkinnable().getCurrentIndex() != index) {
                try {
                    // Reset auto rotation timer
                    if (autoRotationEnabled) {
                        autoRotationTimeline.stop();
                        autoRotationTimeline.play();
                    }

                    getSkinnable().goTo(index);
                } catch (Exception ex) {
                    System.err.println("Error when clicking dot: " + ex.getMessage());
                }
            }
        });

        return dot;
    }

    /**
     * Immediately jumps the carousel to the target index without animation.
     */
    private void jumpToIndex(int targetIndex, boolean forceRecalculateDynamics) {
        int itemCount = getSkinnable().getItems().size();
        if (itemCount == 0) return;

        if (forceRecalculateDynamics) {
            updateDynamicLayoutValues();
        }

        // Stop any ongoing animations
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationInProgress = false;
        }

        int normalizedIndex = normalizeIndex(targetIndex, itemCount);
        currentDisplayedIndex = normalizedIndex;

        // Determine indices of visible cards
        int prevIndex = normalizeIndex(normalizedIndex - 1, itemCount);
        int nextIndex = normalizeIndex(normalizedIndex + 1, itemCount);

        // Clear the container
        cardsContainer.getChildren().clear();

        // Prepare the list of cards to display
        List<Node> visibleCards = new ArrayList<>();
        Node centerCard = cardWrappers.get(normalizedIndex);
        Node leftCard = (itemCount > 1) ? cardWrappers.get(prevIndex) : null;
        Node rightCard = (itemCount > 1) ? cardWrappers.get(nextIndex) : null;

        // Add cards in visual order (left, center, right)
        if (itemCount == 1) {
            visibleCards.add(centerCard);
        } else if (itemCount == 2) {
            // Decide which is "left" and "right" based on index
            if (normalizedIndex == 0) {
                visibleCards.add(rightCard);
                visibleCards.add(centerCard);
            } else {
                visibleCards.add(leftCard);
                visibleCards.add(centerCard);
            }
        } else { // 3 or more items
            if (leftCard != null) visibleCards.add(leftCard);
            visibleCards.add(centerCard);
            if (rightCard != null) visibleCards.add(rightCard);
        }

        cardsContainer.getChildren().addAll(visibleCards);

        // Set final positions, scales, and styles
        for (Node card : visibleCards) {
            boolean isCenter = (card == centerCard);
            boolean isLeft = (card == leftCard);

            // Calculate target X translation
            double targetTranslateX = 0;
            if (!isCenter) {
                if (itemCount == 2) {
                    targetTranslateX = -dynamicSlideDistance / 1.5;
                } else {
                    targetTranslateX = isLeft ? -dynamicSlideDistance : dynamicSlideDistance;
                }
            }

            // Set position
            card.setTranslateX(targetTranslateX);

            // Set scale
            double scale = isCenter ? CENTER_SCALE : SIDE_SCALE;
            card.setScaleX(scale);
            card.setScaleY(scale);

            // Set visibility
            card.setOpacity(isCenter || itemCount > 1 ? 1 : 0);

            // Set z-order
            card.setViewOrder(isCenter ? 0 : 1);
        }

        // Update dots
        updateDots(normalizedIndex);

        // Ensure the control's index property matches
        if (getSkinnable().getCurrentIndex() != normalizedIndex) {
            getSkinnable().setCurrentIndex(normalizedIndex);
        }
    }

    /**
     * Animates the carousel transition to the target index.
     */
    private void animateToIndex(int targetIndex) {
        boolean goingRight;
        if (currentDisplayedIndex == cardWrappers.size() - 1 && targetIndex == 0) {
            goingRight = true;
        } else if (currentDisplayedIndex == 0 && targetIndex == cardWrappers.size() - 1) {
            goingRight = false;
        } else {
            goingRight = targetIndex > currentDisplayedIndex;
        }
        int itemCount = getSkinnable().getItems().size();
        if (itemCount <= 1) {
            jumpToIndex(targetIndex, false);
            return;
        }
        animationInProgress = true;

        if (animationTimeline != null) {
            animationTimeline.stop();
        }
        animationTimeline = new Timeline();

        // Determine involved cards
        int normalizedCurrentIndex = normalizeIndex(currentDisplayedIndex, itemCount);
        int normalizedTargetIndex = normalizeIndex(targetIndex, itemCount);

        // Current visual state
        Node currentCenterCard = cardWrappers.get(normalizedCurrentIndex);
        Node currentLeftCard =  cardWrappers.get(normalizeIndex(normalizedCurrentIndex - 1, itemCount)) ;
        Node currentRightCard = cardWrappers.get(normalizeIndex(normalizedCurrentIndex + 1, itemCount));

        // Target visual state
        Node targetCenterCard = cardWrappers.get(normalizedTargetIndex);
        Node targetLeftCard =  cardWrappers.get(normalizeIndex(normalizedTargetIndex - 1, itemCount));
        Node targetRightCard = cardWrappers.get(normalizeIndex(normalizedTargetIndex + 1, itemCount));

        // Cards entering/leaving view
        Node enteringCard = goingRight ? targetRightCard : targetLeftCard;
        Node leavingCard = goingRight ? currentLeftCard : currentRightCard;

        // Prepare cards for animation
        List<Node> cardsToAnimate = new ArrayList<>();

        // Add cards involved in the transition
        if (currentLeftCard != null) cardsToAnimate.add(currentLeftCard);
        cardsToAnimate.add(currentCenterCard);

        if (currentRightCard != null && currentRightCard != currentLeftCard) {
            cardsToAnimate.add(currentRightCard);
        }

        // Add the entering card if needed
        if (enteringCard != null && !cardsToAnimate.contains(enteringCard)) {
            cardsToAnimate.add(enteringCard);
        }

        // Ensure target center card is included
        if (!cardsToAnimate.contains(targetCenterCard)) {
            cardsToAnimate.add(targetCenterCard);
        }

        // Clear container and add all involved cards
        cardsContainer.getChildren().clear();
        cardsContainer.getChildren().addAll(cardsToAnimate);

        // Set initial states for animation
        for (Node card : cardsToAnimate) {
            if (card == currentCenterCard) {
                card.setTranslateX(0);
                card.setScaleX(CENTER_SCALE); card.setScaleY(CENTER_SCALE);
                card.setViewOrder(0.1);
                card.setOpacity(1);
            } else if (card == currentLeftCard) {
                card.setTranslateX(-dynamicSlideDistance);
                card.setScaleX(SIDE_SCALE); card.setScaleY(SIDE_SCALE);
                card.setOpacity(1);
            } else if (card == currentRightCard) {
                double initialRightTranslate = itemCount == 2 ?
                        -dynamicSlideDistance / 1.5 : dynamicSlideDistance;
                card.setTranslateX(initialRightTranslate);
                card.setScaleX(SIDE_SCALE); card.setScaleY(SIDE_SCALE);
                card.setOpacity(1);
            } else if (card == enteringCard) {
                card.setTranslateX(goingRight ? dynamicSlideDistance * 1.5 : -dynamicSlideDistance * 2.5);
                card.setScaleX(SIDE_SCALE); card.setScaleY(SIDE_SCALE);
                card.setOpacity(0);
            } else {
                card.setOpacity(0);
                card.setTranslateX(0);
                card.setScaleX(SIDE_SCALE); card.setScaleY(SIDE_SCALE);
            }
        }

        // Create animations (KeyFrames)
        List<KeyFrame> keyFrames = new ArrayList<>();

        for (Node card : cardsToAnimate) {
            // Default target properties
            double targetTranslateX = card.getTranslateX();
            double targetScale = card.getScaleX();
            double targetOpacity = card.getOpacity();

            Interpolator translateInterpolator = Interpolator.EASE_BOTH;
            Interpolator scaleInterpolator = Interpolator.EASE_BOTH;
            Interpolator opacityInterpolator = Interpolator.EASE_BOTH;


            if (card == targetCenterCard) { // Card moving TO center
                targetTranslateX = goingRight ? 0 : dynamicSlideDistance/3;
                targetScale = CENTER_SCALE;
                targetOpacity = 1;
               if(goingRight){
                   card.setViewOrder(0);
               }
            } else if (card == targetLeftCard) { // Card moving TO left side
                targetTranslateX = goingRight ? -dynamicSlideDistance : -dynamicSlideDistance*1.5;
                targetScale = SIDE_SCALE;
                targetOpacity = 1;
                if(!goingRight){
                    card.setViewOrder(2);
                }
            } else if (card == targetRightCard) { // Card moving TO right side
                targetTranslateX = goingRight ? dynamicSlideDistance : dynamicSlideDistance*1.5;
                targetScale = SIDE_SCALE;
                targetOpacity = 1;
                card.setViewOrder(2);

            } else if (card == leavingCard) { // Card LEAVING the view
                targetTranslateX = goingRight ? -dynamicSlideDistance * 1.5 : dynamicSlideDistance * 1.5;
                targetScale = SIDE_SCALE;
                targetOpacity = 0;
                opacityInterpolator = Interpolator.EASE_IN;
                if (!goingRight) {
                    card.setViewOrder(3);
                }
            }

            List<KeyValue> cardKeyValues = new ArrayList<>();

            if (Math.abs(card.getTranslateX() - targetTranslateX) > 0.1) {
                cardKeyValues.add(new KeyValue(card.translateXProperty(), targetTranslateX, translateInterpolator));
            }

            if (Math.abs(card.getScaleX() - targetScale) > 0.01) {
                cardKeyValues.add(new KeyValue(card.scaleXProperty(), targetScale, scaleInterpolator));
                cardKeyValues.add(new KeyValue(card.scaleYProperty(), targetScale, scaleInterpolator));
            }

            if (Math.abs(card.getOpacity() - targetOpacity) > 0.01) {
                cardKeyValues.add(new KeyValue(card.opacityProperty(), targetOpacity, opacityInterpolator));
            }

            if (!cardKeyValues.isEmpty()) {
                keyFrames.add(new KeyFrame(TRANSITION_DURATION, cardKeyValues.toArray(new KeyValue[0])));
            }
        }

        animationTimeline.getKeyFrames().addAll(keyFrames);

        // Animation completion
        animationTimeline.setOnFinished(e -> {
            // Clean up and set final state
            cardsContainer.getChildren().clear();
            List<Node> finalVisibleCards = new ArrayList<>();

            // Add final cards in correct visual order
            if (itemCount == 2) {
                if (targetCenterCard == cardWrappers.get(0)) {
                    finalVisibleCards.add(cardWrappers.get(1));
                } else {
                    finalVisibleCards.add(cardWrappers.getFirst());
                }
                finalVisibleCards.add(targetCenterCard);
            } else {
                if (targetLeftCard != null) finalVisibleCards.add(targetLeftCard);
                finalVisibleCards.add(targetCenterCard);
                if (targetRightCard != null) finalVisibleCards.add(targetRightCard);
            }

            cardsContainer.getChildren().addAll(finalVisibleCards);

            // Set precise final states
            for (Node card : finalVisibleCards) {
                boolean isCenter = (card == targetCenterCard);
                boolean isLeft = (card == targetLeftCard);

                double finalTranslateX = 0;
                double finalScale = SIDE_SCALE;
                double finalViewOrder = 1;

                if (isCenter) {
                    finalScale = CENTER_SCALE;
                    finalViewOrder = 0;
                } else {
                    if (itemCount == 2) {
                        finalTranslateX = -dynamicSlideDistance / 1.5;
                    } else {
                        finalTranslateX = isLeft ? -dynamicSlideDistance : dynamicSlideDistance;
                    }
                }

                // Set all final properties in one go for better performance
                card.setTranslateX(finalTranslateX);
                card.setScaleX(finalScale);
                card.setScaleY(finalScale);
                card.setOpacity(1);
                card.setViewOrder(finalViewOrder);
            }

            // Update state
            currentDisplayedIndex = normalizedTargetIndex;
            animationInProgress = false;

        });

        // Update dots to target index immediately
        updateDots(normalizedTargetIndex);

        // Play the animation
        animationTimeline.play();
    }

    /**
     * Updates the visual state of the navigation dots.
     */
    private void updateDots(int activeIndex) {
        int itemCount = getSkinnable().getItems().size();
        if (itemCount == 0) return;
        int normalizedActiveIndex = normalizeIndex(activeIndex, itemCount);

        for (int i = 0; i < dotsContainer.getChildren().size(); i++) {
            Node node = dotsContainer.getChildren().get(i);
            if (node instanceof Button dot) {

                if(i == normalizedActiveIndex)// is active
                    dot.getStyleClass().add("carousel-dot-active");
                else
                    dot.getStyleClass().remove("carousel-dot-active");

            }
        }
    }
    /**
     * Properly disposes of resources used by the CarouselSkin.
     * This method should be called when the skin is no longer needed to prevent memory leaks.
     */
    @Override
    public void dispose() {


        // Stop and clear all timelines
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline.getKeyFrames().clear();
            animationTimeline = null;
        }

        if (autoRotationTimeline != null) {
            autoRotationTimeline.stop();
            autoRotationTimeline.getKeyFrames().clear();
            autoRotationTimeline = null;
        }

        // Remove all listeners from the control
        Carousel control = getSkinnable();
        if (control != null) {
            // Remove item list listener
            control.getItems().removeListener((ListChangeListener<? super Node>) c -> {});

            // Remove index property listener
            control.currentIndexProperty().removeListener((obs, oldVal, newVal) -> {});

            // Remove scene property listener
            control.sceneProperty().removeListener((obs, oldScene, newScene) -> {});

            // If the control is in a scene, remove window listeners
            if (control.getScene() != null && control.getScene().getWindow() != null) {
                Window window = control.getScene().getWindow();
                window.heightProperty().removeListener((heightObs, heightOld, heightNew) -> {});
            }
        }

        // Remove listeners and event handlers from card wrappers
        for (Node cardWrapper : cardWrappers) {
            if (cardWrapper instanceof StackPane) {
                // Remove layout bounds listener
                cardWrapper.layoutBoundsProperty().removeListener((obs, old, newBounds) -> {});

                // Remove click handler
                cardWrapper.setOnMouseClicked(null);
            }
        }

        // Remove event handlers from dot buttons
        for (Node node : dotsContainer.getChildren()) {
            if (node instanceof Button dot) {
                dot.setOnAction(null);
            }
        }

        // Clear all collections
        cardWrappers.clear();
        cardsContainer.getChildren().clear();
        dotsContainer.getChildren().clear();
        mainContainer.getChildren().clear();
        getChildren().clear();

        // Clear property bindings
        clipRect.widthProperty().unbind();
        clipRect.heightProperty().unbind();

        mainContainer.setClip(null);
        super.dispose();
    }
}