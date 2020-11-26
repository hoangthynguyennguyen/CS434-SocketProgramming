//package client;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//public class Main extends Application {
//    Scene scene1, scene2, scene3, scene4, scene5;
//    static Stage stage;
//
//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        Main.stage= primaryStage;
//        Parent root = FXMLLoader.load(getClass().getResource("WheelOfFortune.fxml"));
//        Parent rootRegister = FXMLLoader.load(getClass().getResource("register.fxml"));
//        Parent rootRules = FXMLLoader.load(getClass().getResource("Rules.fxml"));
//        Parent rootRuleCont = FXMLLoader.load(getClass().getResource("Rulecont.fxml"));
//        Parent rootGame = FXMLLoader.load(getClass().getResource("Server.fxml"));
//        stage.setTitle("Wheel Of Fortune");
//
//        // Layout Menu
//        scene1 = new Scene(root, 600, 400);
//
//        // Layout Register
//        scene2 = new Scene(rootRegister, 600, 400);
//
//        // Layout Rules
//        scene3 = new Scene(rootRules, 600, 400);
//
//        // Layout RuleCont
//        scene4 = new Scene(rootRuleCont, 600, 400);
//
//        // Layout RuleCont
//        scene5 = new Scene(rootGame, 600, 400);
//
//        stage.setScene(scene1);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
//
//
//

package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application{
    private double x,y;
     public static void main (String[] args){
         launch(args);
     }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader= new FXMLLoader(getClass().getResource("rootScreen.fxml"));
        StackPane rootStackPane  = loader.load();
        Scene scene = new Scene(rootStackPane);
        primaryStage.setScene(scene);
        primaryStage.setHeight(400);
        primaryStage.setWidth(600);
        primaryStage.initStyle(StageStyle.UNDECORATED);

        try{
            System.out.println("Success!!!");
        }
        catch(Exception e){
            System.out.println("Failed!!!");
        }


        rootStackPane.setOnMousePressed(mouseEvent -> {
            x= mouseEvent.getSceneX();
            y=mouseEvent.getSceneY();
        });
        rootStackPane.setOnMouseDragged(mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() - x);
            primaryStage.setY(mouseEvent.getScreenY() - y);
        });
        primaryStage.show();

    }


}


