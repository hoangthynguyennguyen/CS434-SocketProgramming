package server;

import utils.Player;
import utils.Question;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleClient extends  Thread{
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private Socket client;
    private Player player;
    private int maxNumPlayers;
    private int maxNumQuestions;
    private int roomId;

    private static List<Map<Socket, Player>> socketPlayerMap;

    private static Question[] questions;
    private static String operators;
    private static Integer numPlayers;

    public HandleClient(Socket client, int roomId, int maxNumPlayers, int maxNumQuestions, int maxNumRooms) throws IOException {

        this.client = client;
        this.roomId = roomId;
        this.inputStream = new DataInputStream(client.getInputStream());
        this.outputStream = new DataOutputStream(client.getOutputStream());
        this.maxNumPlayers = maxNumPlayers;
        this.maxNumQuestions = maxNumQuestions;

        if (socketPlayerMap == null || operators == null) {
            socketPlayerMap = new ArrayList<>();
            for (int i = 0; i < maxNumRooms; i++)
                socketPlayerMap.add(new HashMap<>());
            operators = "+-*/%";
        }
        else {
            this.player = socketPlayerMap.get(roomId).get(this.client);
        }

        if (numPlayers == null) {
            numPlayers = 0;
        }

        if (questions == null) {
            questions = new Question[maxNumRooms];
            for (int i = 0; i < maxNumRooms; i++) {
                questions[i] = new Question();
            }
        }
    }

    private boolean isRegister(String name){
        for (Map.Entry<Socket, Player> entry: HandleClient.socketPlayerMap.get(roomId).entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void register() throws IOException {
        // this is called when a user want to register a name

        if (this.player == null) {

            String name = null;
            do {
                // get name from the client, push it to server queue
                name = this.inputStream.readUTF();

                // check if name is already registered
                if (!isRegister(name)) {
                    this.player = new Player(name, 0);
                    HandleClient.socketPlayerMap.get(roomId).put(this.client, this.player);

                    HandleClient.numPlayers ++;
                    this.outputStream.writeUTF("successful "
                            + this.roomId + " "
                            + this.maxNumPlayers + " "
                            + this.maxNumQuestions + " "
                            + numPlayers);
                    break;
                }
                else {
                    this.outputStream.writeUTF("failed");
                }
            }
            while (isRegister(name));
        }
    }

    public static void generateQuestion(int roomId) {
        HandleClient.questions[roomId].generateRandom();
    }

    public static void clearRegisteredNames(int roomId) {
        socketPlayerMap.get(roomId).clear();
    }

    private boolean test(Question question, String answerString) {

        try {
            Integer answer = Integer.parseInt(answerString);
            Integer expectedAnswer = 0;

            Integer num01 = question.getNumber01();
            Integer num02 = question.getNumber02();
            Integer operatorIndex = question.getOperatorIndex();

            switch (operatorIndex) {
                case 0:
                    expectedAnswer = num01 + num02;
                    break;
                case 1:
                    expectedAnswer = num01 - num02;
                    break;
                case 2:
                    expectedAnswer = num01 * num02;
                    break;
                case 3:
                    expectedAnswer = num01 / num02;
                    break;
                case 4:
                    expectedAnswer = num01 % num02;
                    break;
            }

            System.out.println("expected aswer: " + expectedAnswer);
            return (expectedAnswer.equals(answer));
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {

        try {
            // send current score and current question from server to client
            this.outputStream.writeUTF(HandleClient.socketPlayerMap.get(roomId).get(this.client).getScore().toString());
            this.outputStream.writeUTF(questions[roomId].getNumber01() + " "
                    + operators.charAt(questions[roomId].getOperatorIndex()) + " "
                    + questions[roomId].getNumber02());

            // get the answer from the client
            String answer = this.inputStream.readUTF();
            System.out.println("answer from " + this.player.getName() + ": " + answer);

            // check result, modify score
            if (this.test(questions[roomId], answer)) {

                this.player.setScore(this.player.getScore() + 1);
            }
            else {

                this.player.setScore(this.player.getScore() - 1);
            }
            socketPlayerMap.get(roomId).put(this.client, this.player);

        } catch (IOException e) {

            e.printStackTrace();
            System.out.println("ClientHandler.run() failed! - " + this.player.getName());
        }
    }
}
