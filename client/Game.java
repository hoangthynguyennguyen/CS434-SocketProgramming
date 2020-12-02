package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Game {
    private RootController rootController;
    public static Thread thread;
    @FXML
    private Text question, clientName, currentScrore;

    @FXML
    private TextField inputCharacter, inputKeyword;

    @FXML
    private Button submitBtn;

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    public Game(){
        thread=new Thread(()->{
            try {
//                    String scoreFromServer= Main.dataInputStream.readUTF();
                    String questionFromServer = Main.dataInputStream.readUTF();
                    String statusQuestion = questionFromServer.split(" ")[0];
//                    String statusScore = scoreFromServer.split(" ")[0];
                    System.out.println("Question sends from server "+ questionFromServer);
//                    System.out.println("Score sends from server "+ scoreFromServer);

                    if (statusQuestion.equals("Question:")){
                        this.clientName.setText(Main.clientName);
                        this.question.setText(questionFromServer.split(" ")[1]);
                    }
//                    else if (statusScore.equals("Score")){
//                        System.out.println(scoreFromServer.split(" ")[1]);
//                    }
            }catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    public void handleSubmitAnswer(ActionEvent actionEvent) throws IOException {
        String character= inputCharacter.getText();
        String keyword= inputKeyword.getText();
        System.out.println("Character from client: "+ character);
        System.out.println("Keyword from client: "+ keyword);
        Main.dataOutputStream.writeUTF(character);
        Main.dataOutputStream.writeUTF(keyword);
    }

}
