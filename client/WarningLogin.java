package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class
WarningLogin {
    @FXML
    private Button warningCloseButton;

    @FXML
    private void confirmOnAction(){
         RegisterController.setWarningStage((Stage) warningCloseButton.getScene().getWindow());
         RegisterController.getWarningStage().close();


    }

}
