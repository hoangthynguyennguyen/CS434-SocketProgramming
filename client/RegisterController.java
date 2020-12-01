package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class RegisterController {
    private RootController rootController;


    @FXML
    private TextField nameField;
    @FXML
    private Button submitButton;
    @FXML
    HBox waiting;

    private static Stage warningStage;
    private boolean checkWarning = false;
    private boolean isConnected;


    public static Stage getWarningStage() {
        return warningStage;
    }

    public static void setWarningStage(Stage warningStage) {
        RegisterController.warningStage = warningStage;
    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) throws IOException {
        Window owner = submitButton.getScene().getWindow();
        isConnected=false;
        CountDownLatch latch = new CountDownLatch(1);

        // another options is user choose a duplicate nickname

        if (nameField.getText().isEmpty()) {
            AlertHelper(Alert.AlertType.ERROR, owner, "Form error", "Name is required");
            return;
        }
        //Maximum 10 characters, at least one uppercase letter,
        // one lowercase letter, one number and one special character
        if (nameField.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[_])[A-Za-z\\d_]{1,10}$")) {
            if (!checkWarning) {
                checkWarning = true;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("WarningLogin.fxml"));
                Scene scene = new Scene(loader.load());
                warningStage = new Stage();
                warningStage.setScene(scene);
                warningStage.initStyle(StageStyle.UNDECORATED);
//                warningStage.initModality(Modality.APPLICATION_MODAL);
                warningStage.showAndWait();
            }


            rootController.setOpacityForScreen(0.6);
            waiting.setVisible(true);
            String name= nameField.getText();

            new Thread(()->{
                Main.gameScene.initPlayer("127.0.0.1", 8000, name);
                isConnected= Main.gameScene.connectToServer();
                latch.countDown();
            }).start();

            new Thread(()->{
                try{
                    latch.await();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    if (isConnected) {
                        rootController.setOpacityForScreen(1);
                        waiting.setVisible(false);
                        Main.gameScene.initGame();
                        try {
                            rootController.loadGameScreen();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("not connected");
                    }
                });
            }).start();
        } else {
            AlertHelper(Alert.AlertType.ERROR, owner, "Form error", "NameField is required");
            return;
        }


    }

    private void AlertHelper(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();

    }


    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }


}
