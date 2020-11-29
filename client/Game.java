package client;

import javafx.application.Platform;
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

public class Game extends AnchorPane implements Initializable {
    public PlayerConnector playerConnector;

    @FXML
    private Text question, clientName, currentScrore;

    @FXML
    private TextField inputCharacter, inputKeyword;

    @FXML
    private Button submitBtn;

    Scene scene;

    public Game(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Server.fxml"));
//        loader.setRoot(this);
//        loader.setController(this);
//        scene = new Scene(this, 600, 400);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initPlayer(String host, int port, String clientName) {
        playerConnector = new PlayerConnector(clientName, host, port);
//        this.clientName.setText(clientName);
    }

    public void initGame(){
        submitBtn.setOnAction(action->{
            String character=inputCharacter.getText();
            String keyword= inputKeyword.getText();
            playerConnector.submitAnswer(character, keyword);
        });
    }

    public boolean connectToServer(){
        return playerConnector.connectToServer();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Game Initialized");
    }

    public class PlayerConnector implements Runnable {
        Socket socket;
        Thread thread;
        DataInputStream inputStream;
        DataOutputStream outputStream;

        String userName;
        String host;
        int port;

        String score;
        String questionServer;


        public PlayerConnector(String clientName, String host, int port) {
            this.host = host;
            this.port = port;
            this.userName = clientName;
        }

        public boolean connectToServer() {
            try {
                System.out.println(userName);
                socket = new Socket(host, port);
                System.out.println("client is connected to " + host + " port " + port + "...");
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                outputStream.writeUTF(userName);
                String info = this.inputStream.readUTF();

                thread = new Thread(this);
                thread.start();

                String status = info.split(" ")[0];

                if (status.equals("successful")) {
                    System.out.println(userName + " logged in successfully");
                    return true;
                }

                System.out.println("Logged in failed");
                return false;
            } catch (UnknownHostException e) {
                System.out.println("Logged in failed");
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                System.out.println("Logged in failed");
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateScore() {
            try {
                score = this.inputStream.readUTF();
                System.out.println(score);
                return true;
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateQuestion() {
            try {
                questionServer = this.inputStream.readUTF();
                System.out.println(questionServer);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public String getCurrentScore() {
            return score;
        }

        public String getCurrentQuestion() {
            return questionServer;
        }

        public void submitAnswer(String character, String keyword) {
            try {
                this.outputStream.writeUTF(character);
                this.outputStream.writeUTF(keyword);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (thread != null) {
                System.out.println("Run...");
                if (updateScore() && updateQuestion()) {
                    Platform.runLater(() -> {
                        inputCharacter.clear();
                        inputKeyword.clear();
                        currentScrore.setText(getCurrentScore());
                        question.setText(getCurrentQuestion());
                    });
                }
            }
        }
    }
}
