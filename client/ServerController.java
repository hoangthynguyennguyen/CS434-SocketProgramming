package client;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import java.io.IOException;

public class ServerController {
    private RootController rootController;

    @FXML
    public void setRootController(RootController rootController) {

        this.rootController = rootController;
    }
    @FXML
    public void LoadGame(ActionEvent event) throws IOException {
        rootController.loadGameScreen();
    }

}
