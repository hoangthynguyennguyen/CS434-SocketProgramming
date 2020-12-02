package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class RootController {
    @FXML
    private StackPane rootStackPane;

    public void initialize() throws IOException {
        loadMenuScreen();
    }
    @FXML
    public void loadMenuScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WheelOfFortune.fxml"));
        Pane pane = loader.load();
        MenuController menuController = loader.getController();
        menuController.setRootController(this);
        setScreen(pane);
    }
    @FXML
    public void getRuleContScene() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Rulecont.fxml"));
        Pane pane = loader.load();
        HowToPlayController howToPlayController = loader.getController();
        howToPlayController.setRootController(this);
        setScreen(pane);
    }

    @FXML
    public void loadGameScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Server.fxml"));
        Pane pane = loader.load();
        Game serverController = loader.getController();
        serverController.setRootController(this);
        setScreen(pane);
    }

    public void setOpacityForScreen(double opacity){
        rootStackPane.setOpacity(opacity);
    }

    public void setScreen(Pane pane) {
        rootStackPane.getChildren().clear();
        rootStackPane.getChildren().add(pane);
    }
}
