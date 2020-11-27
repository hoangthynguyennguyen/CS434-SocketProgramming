package client;

import javafx.event.ActionEvent;

import java.io.IOException;

public class HowToPlayController {
    private RootController rootController;

    public void backToMenu(ActionEvent event) throws IOException{
        rootController.loadMenuScreen();
    }
    public void getRuleCont(ActionEvent event) throws IOException{
        rootController.getRuleContScene();
    }
    public void setRootController(RootController rootController) {

        this.rootController = rootController;
    }
}
