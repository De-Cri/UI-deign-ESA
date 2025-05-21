package com.esa.moviestar.Settings;

import com.esa.moviestar.Database.AccountDao;
import com.esa.moviestar.Database.UtenteDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class AccountSettingController {
    @FXML
    private StackPane AccountContentSetting;
    @FXML
    private Button modifyUserButton;
    @FXML
    private Button modifyPasswordButton;
    @FXML
    private Button deleteAccountButton;

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("com.esa.moviestar.images.svg-paths.general-svg");

    public void initialize(){
        modifyUser();
        deleteAccount("prova");
        updatePassword();

    }

    public void deleteAccount(String email){
        deleteAccountButton.setOnMouseClicked(event -> {
            AccountDao accountDao = new AccountDao();
            boolean deleteSuccess = accountDao.rimuoviAccount(email);
            if(deleteSuccess){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/hello-view.fxml"), resourceBundle);
                    Parent accessContent = loader.load();

                    Scene currentScene = AccountContentSetting.getScene();

                    Scene newScene = new Scene(accessContent, currentScene.getWidth(), currentScene.getHeight());

                    Stage stage = (Stage) AccountContentSetting.getScene().getWindow();
                    stage.setScene(newScene);

                }catch (IOException e){
                    System.err.println("AccountSettingController: Errore caricamento pagina di accesso dell'account"+e.getMessage());
                }
            }
        });
    }



    public void modifyUser(){
        modifyUserButton.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/modify-profile-view.fxml"),resourceBundle);
                Parent modifyContent = loader.load();

                Scene currentScene = AccountContentSetting.getScene();

                Scene newScene = new Scene(modifyContent, currentScene.getWidth(), currentScene.getHeight());

                Stage stage = (Stage) AccountContentSetting.getScene().getWindow();
                stage.setScene(newScene);

            } catch (IOException e) {
                System.err.println("AccountSettingController: Errore caricamento pagina di modifica dell'utente"+e.getMessage());
            }
        });
    }

    public void updatePassword(){
        modifyPasswordButton.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esa/moviestar/update-password-view.fxml"),resourceBundle);
                Parent updateContent = loader.load();

                Scene currentScene = AccountContentSetting.getScene();

                Scene newScene = new Scene(updateContent, currentScene.getWidth(), currentScene.getHeight());

                Stage stage = (Stage) AccountContentSetting.getScene().getWindow();
                stage.setScene(newScene);

            } catch (IOException e) {
                System.err.println("AccountSettingController: Errore caricamento pagina di aggiornamento della password"+e.getMessage());
            }
        });
    }
}
