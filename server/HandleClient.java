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
import java.util.Map.Entry;
import java.util.Random;


public class HandleClient extends  Thread{
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private Socket socket;
    private Player player;
    private int maxNumPlayers;
    private int maxNumQuestions;
    private int roomId;

    private static List<Map<Socket, Player>> socketPlayerMap;			// client (Socket) --> name (String)

    private static Question[] questions;
    private static String operators;
    private static Integer numPlayers;
    private static ArrayList<Question> listQuestions;
    private static Question question;
    private static Random random;

    public HandleClient(Socket socket, int roomId, int maxNumPlayers, int maxNumQuestions, int maxRooms) throws IOException {
        this.socket=socket;
        this.roomId=roomId;
        this.maxNumPlayers=maxNumPlayers;
        this.maxNumQuestions=maxNumQuestions;
        this.dataInputStream= new DataInputStream(socket.getInputStream());
        this.dataOutputStream=new DataOutputStream(socket.getOutputStream());
        HandleClient.random=new Random();

        if (socketPlayerMap == null || operators == null) {
            socketPlayerMap = new ArrayList<>();
            for (int i = 0; i < maxRooms; i++)
                socketPlayerMap.add(new HashMap<>());
            operators = "+-*/%";
        }
        else {
            this.player = socketPlayerMap.get(roomId).get(this.socket);
        }

        if (numPlayers == null) {
            numPlayers = 0;
        }

        if (questions == null) {
            questions = new Question[maxNumQuestions];
        }
    }

    public boolean isRegister(String name){
        for (Entry<Socket, Player> entry: HandleClient.socketPlayerMap.get(roomId).entrySet()) {
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
                name = this.dataInputStream.readUTF();

                // check if name is already registered
                if (!isRegister(name)) {
                    this.player = new Player(name, 0);
                    HandleClient.socketPlayerMap.get(roomId).put(this.socket, this.player);

                    HandleClient.numPlayers ++;
                    this.dataOutputStream.writeUTF("successful "
                            + this.roomId + " "
                            + this.maxNumPlayers + " "
                            + this.maxNumQuestions + " "
                            + numPlayers);
                    break;
                }
                else {
                    this.dataOutputStream.writeUTF("failed");
                }
            }
            while (isRegister(name));
        }
    }

    private boolean test(Question question, String keywordAnswer, String characterAnswer) {

        try {
            String keyword= question.getKeyword();
            String character= question.getDescription();

            return (keyword.equals(keywordAnswer) && character.equals(characterAnswer));
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void generateQuestion(int roomId, ArrayList<Question> listQuestion) {
        HandleClient.listQuestions=listQuestion;
        int randomNum=  HandleClient.random.nextInt(listQuestion.size());
        HandleClient.question=HandleClient.listQuestions.get(randomNum);
    }

    @Override
    public void run() {

        try {
            // send current score and current question from server to client
            this.dataOutputStream.writeUTF(HandleClient.socketPlayerMap.get(roomId).get(this.socket).getScore().toString());
            this.dataOutputStream.writeUTF(HandleClient.question.getDescription());

            // get the answer from the client
            String answer = this.dataInputStream.readUTF();
            System.out.println("answer from " + this.player.getName() + ": " + answer);

            // check result, modify score
            if (this.test(HandleClient.question, answer, answer)) {
                this.player.setScore(this.player.getScore() + 1);
            }
            else {
                this.player.setScore(this.player.getScore() - 1);
            }
            socketPlayerMap.get(roomId).put(this.socket, this.player);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ClientHandler.run() failed! - " + this.player.getName());
        }
    }

    public static void clearRegisteredNames(int roomId) {
        // when the game's over, new players participate the room and set nickname without worrying about their previous duplicated name
        socketPlayerMap.get(roomId).clear();
    }
}
