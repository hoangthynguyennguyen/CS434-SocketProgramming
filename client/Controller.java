package client;

import java.io.IOException;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {
    String[] sceneNames = {"WheelOFortune.fxml", "register.fxml", "Rules.fxml", "Rulecont.fxml", "Server.fxml"};

    // -----------------------------------------------------------------------------------
    private void goToSceneIndicator(int nextScene, ActionEvent event) throws IOException {
        String sceneName = sceneNames[nextScene];
        Parent root = FXMLLoader.load(getClass().getResource(sceneName));
        Scene scene = new Scene(root, 600, 400);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(scene);
    }

    // -----------------------------------------------------------------------------------
    @FXML
    private void goToMenuScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(0, event);
    }

    @FXML
    private void goToRegisterScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(1, event);
    }

    @FXML
    private void goToRulesScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(2, event);
    }

    @FXML
    private void goToRuleContScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(3, event);
    }

    @FXML
    private void goToGameScene(ActionEvent event) throws IOException {
        event.consume();
        goToSceneIndicator(4, event);
    }

    @FXML
    private void quitGame() {
        Platform.exit();
        System.exit(0);
    }
}

