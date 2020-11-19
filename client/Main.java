package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Scene scene1, scene2, scene3, scene4, scene5;
    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.stage= primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("WheelOFortune.fxml"));
        Parent rootRegister = FXMLLoader.load(getClass().getResource("register.fxml"));
        Parent rootRules = FXMLLoader.load(getClass().getResource("Rules.fxml"));
        Parent rootRuleCont = FXMLLoader.load(getClass().getResource("Rulecont.fxml"));
        Parent rootGame = FXMLLoader.load(getClass().getResource("Server.fxml"));
        stage.setTitle("Wheel Of Fortune");

        // Layout Menu
        scene1 = new Scene(root, 600, 400);

        // Layout Register
        scene2 = new Scene(rootRegister, 600, 400);

        // Layout Rules
        scene3 = new Scene(rootRules, 600, 400);

        // Layout RuleCont
        scene4 = new Scene(rootRuleCont, 600, 400);

        // Layout RuleCont
        scene5 = new Scene(rootGame, 600, 400);

        stage.setScene(scene1);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
