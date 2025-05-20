package com.esa.moviestar.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BufferAnimation extends StackPane {

    private final Rectangle largeSquare;
    private final Rectangle smallSquare;
    private final Timeline animation;

    /**
     * Creates a new CubeSpinner with default size
     */
    public BufferAnimation() {
        this(70);
    }

    /**
     * Creates a new CubeSpinner with the specified size
     * @param size The size of the spinner in pixels
     */
    public BufferAnimation(double size) {
        // Configure the container
        setPrefSize(size, size);
        setMaxSize(size, size);
        setMinSize(size, size);

        // Create the background container for centering
        Pane container = new Pane();
        container.setPrefSize(size, size);

        // Create large square (outer cube)
        largeSquare = new Rectangle(size, size);
        largeSquare.setFill(Color.rgb(115, 65, 190));
        largeSquare.setArcWidth(32);
        largeSquare.setArcHeight(32);
        largeSquare.setLayoutX(0);
        largeSquare.setLayoutY(0);

        // Create small square (inner cube)
        double smallSize = size * 0.5;
        smallSquare = new Rectangle(smallSize, smallSize);
        smallSquare.setFill(Color.rgb(31,31,31));
        smallSquare.setArcWidth(24);
        smallSquare.setArcHeight(24);
        smallSquare.setLayoutX((size - smallSize) / 2);
        smallSquare.setLayoutY((size - smallSize) / 2);

        // Add the shapes to the container
        container.getChildren().addAll(largeSquare, smallSquare);
        getChildren().add(container);

        // Create the animation
        animation = createAnimation();

        // Start the animation as soon as the component is created
        animation.play();
    }

    /**
     * Creates the rotation animation for both squares
     * @return Timeline animation
     */
    private Timeline createAnimation() {
        Duration duration = Duration.seconds(2);
        Timeline timeline = new Timeline();

        // Large square animation
        KeyValue kv1Start = new KeyValue(largeSquare.rotateProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kv1Mid = new KeyValue(largeSquare.rotateProperty(), 180, Interpolator.EASE_BOTH);
        KeyValue kv1End = new KeyValue(largeSquare.rotateProperty(), 180, Interpolator.EASE_BOTH);

        // Small square animation (reverse)
        KeyValue kv2Start = new KeyValue(smallSquare.rotateProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kv2Mid = new KeyValue(smallSquare.rotateProperty(), -180, Interpolator.EASE_BOTH);
        KeyValue kv2End = new KeyValue(smallSquare.rotateProperty(), -180, Interpolator.EASE_BOTH);

        // Create keyframes
        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv1Start, kv2Start);
        KeyFrame kf1 = new KeyFrame(duration.divide(2), kv1Mid, kv2Mid);
        KeyFrame kf2 = new KeyFrame(duration, kv1End, kv2End);

        // Add keyframes to timeline
        timeline.getKeyFrames().addAll(kf0, kf1, kf2);
        timeline.setCycleCount(Timeline.INDEFINITE);

        return timeline;
    }

    /**
     * Starts the spinner animation
     */
    public void startAnimation() {
        animation.play();
    }

    /**
     * Stops the spinner animation
     */
    public void stopAnimation() {
        animation.pause();
    }
}
