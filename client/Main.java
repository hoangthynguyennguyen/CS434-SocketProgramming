

package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Main extends Application {
    public static String clientName;
    public static DataInputStream dataInputStream;
    public static DataOutputStream dataOutputStream;
    public static int PORT=8000;
    public static Socket socket;
    public  static Game gameScene;
    private double x, y;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rootScreen.fxml"));
        StackPane rootStackPane = loader.load();
        Scene scene = new Scene(rootStackPane);
        primaryStage.setScene(scene);
        primaryStage.setHeight(400);
        primaryStage.setWidth(600);
        gameScene=new Game();


        try {
            System.out.println("Success!!!");
        } catch (Exception e) {
            System.out.println("Failed!!!");
        }


        rootStackPane.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        rootStackPane.setOnMouseDragged(mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() - x);
            primaryStage.setY(mouseEvent.getScreenY() - y);
        });
        primaryStage.show();

    }


}


