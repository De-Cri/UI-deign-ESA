package com.esa.moviestar.home;

import com.esa.moviestar.model.Profile;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class HeaderController {
    private PopupMenu popupMenu;
    private final ResourceBundle resources = ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");

    @FXML
    private StackPane rootHeader;
    @FXML
    private HBox titleImageContainer;

    @FXML
    StackPane homeButton;

    @FXML
    HBox filmButton;

    @FXML
    HBox seriesButton;

    @FXML
    HBox searchButton;

    @FXML
    private TextField tbxSearch;

    private Node currentActive;
    @FXML
    private StackPane profileImage;

    @FXML
    public void initialize() {
        currentActive = homeButton;
        currentActive.getStyleClass().remove("surface-transparent");
        currentActive.getStyleClass().add("primary");
        tbxSearch.setPromptText("Titoli, persone, generi");
        profileImage.setOnMouseClicked(e -> popupMenu.show(profileImage));
        // da spostare
        homeButton.setOnMouseReleased(e -> activeButton(homeButton));
        filmButton.setOnMouseReleased(e -> activeButton(filmButton));
        seriesButton.setOnMouseReleased(e -> activeButton(seriesButton));
        searchButton.setOnMouseReleased(e -> {
        });

        rootHeader.widthProperty().addListener((observable, oldValue, newValue) -> {
            titleImageContainer.setVisible(!(newValue.doubleValue() < 720));
        });
        List<Profile> l = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Profile n = new Profile(i, "Giacomino", "", i);
            l.add(n);
        }
        setupPopupMenu(l);
    }
    public TextField getTbxSearch(){
        return tbxSearch;
    }

    /**
     * Sets the visual style for the active button and resets the previous one.
     *
     * @param button The Node (StackPane or HBox) that was clicked.
     */
    public void activeButton(Node button) {
        // If the clicked button is already the active one, do nothing
        if (button == currentActive) {
            return;
        }

        // Deactivate the previously active button
        if (currentActive != null) {
            currentActive.getStyleClass().remove("primary");
            // Ensure the inactive style is present
            if (!currentActive.getStyleClass().contains("surface-transparent")) {
                currentActive.getStyleClass().add("surface-transparent");
            }
        }

        // Activate the new button
        currentActive = button;
        if (currentActive != null) {
            currentActive.getStyleClass().remove("surface-transparent"); // *** Important: Remove inactive style ***
            // Ensure the active style is present
            if (!currentActive.getStyleClass().contains("primary")) {
                currentActive.getStyleClass().add("primary");
            }
            // If the activated element is the search HBox containing the TextField,
            // you might want to give focus to the TextField itself.
            if (currentActive == searchButton) {
                // Add a small delay to ensure focus happens after potential layout changes
                javafx.application.Platform.runLater(() -> tbxSearch.requestFocus());
            }
        }
    }


    private void setupPopupMenu(List<Profile> content) {
        // Create the popup menu - no stage needed
        popupMenu = new PopupMenu();

        // Add menu items
        HBox settingsItem = new HBox() {{
            setMinHeight(40.0);
            setAlignment(Pos.CENTER_LEFT);
            getStyleClass().addAll("small-item", "chips", "surface-transparent");
        }};
        SVGPath profileIcon = new SVGPath() {{
            setContent(resources.getString("user"));
            getStyleClass().add("on-primary");
        }};
        Text text = new Text("My Account") {{
            getStyleClass().addAll("medium-text", "on-primary");
        }};
        settingsItem.getChildren().addAll(profileIcon, text);
        settingsItem.setOnMouseClicked(e -> settingsClick());

        popupMenu.addItem(settingsItem);
        popupMenu.addSeparator();
        for (Profile i : content) {
            createProfileItem(i.getID(), i.getName(), i.getIcon());
        }
        popupMenu.addSeparator();
        HBox emailButton = new HBox() {{
            setMinHeight(40.0);
            setAlignment(Pos.CENTER);
            getStyleClass().addAll("small-item", "surface-dim-border", "chips", "surface-transparent");
        }};
        SVGPath emailIcon = new SVGPath() {{
            setContent(resources.getString("email"));
            getStyleClass().add("on-primary");
        }};
        Text emailText = new Text("Change Email") {{
            getStyleClass().addAll("medium-text", "on-primary");
        }};
        emailButton.setOnMouseClicked(e -> emailClick());
        emailButton.getChildren().addAll(emailIcon, emailText);
        popupMenu.addItem(emailButton);

    }

    private void createProfileItem(int id, String name, Group profileIcon) {
        profileIcon.setScaleX(1.3);
        profileIcon.setScaleY(1.3);
        HBox item = new HBox() {{
            setMinHeight(40.0);
            setAlignment(Pos.CENTER_LEFT);
            getStyleClass().addAll("small-item", "chips", "surface-transparent");
        }};
        Text text = new Text(name) {{
            getStyleClass().addAll("medium-text", "on-primary");
        }};
        item.getChildren().addAll(profileIcon, text);
        // Item click handling moved to controller via callback
        item.setOnMouseClicked(e -> profileClick(id));
        popupMenu.addItem(item);

    }

    private void profileClick(int itemId) {
        // Handle menu selections based on ID
        System.out.println("selected profile n:" + itemId);
    }

    private void settingsClick() {
        System.out.println("settings button clicked");
        // Handle email change logic here
    }

    private void emailClick() {
        System.out.println("Change email button clicked");
        // Handle email change logic here
    }

    public void setProfileIcon(Group icon) {
        profileImage.getChildren().clear();
        icon.setScaleX(2.286);
        icon.setScaleY(2.286);
        profileImage.getChildren().add(icon);
    }
}