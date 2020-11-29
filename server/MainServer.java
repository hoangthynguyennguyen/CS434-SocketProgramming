package server;

import utils.Question;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class MainServer extends Thread {
    private static int port=8000;
    private static int numQuestions=3;
    private static int players=2;
    private static int numRooms=1;
    private static ServerSocket serverSocket;
    private int roomId;
    private Socket[] clients= new Socket[players];
    private HandleClient[] handleClients= new HandleClient[players];
    public ArrayList<Question> listQuestion;


    public MainServer(int roomId) throws IOException {
        this.roomId = roomId;
        loadData();

        if (serverSocket==null){
            serverSocket= new ServerSocket(port);
        }
        System.out.println("Room "+roomId+ " is running on port" +port);
    }

    public void createConnection() throws IOException {
        for (int i=0; i< players; i++){
            clients[i]= (serverSocket.accept());
        }
    }

    public void createConnectionForPlayers() throws IOException {
        for (int i=0; i<players; i++){
            handleClients[i]= new HandleClient(clients[i], roomId, players, numQuestions, numRooms);
        }
    }

    public void registerGame() throws IOException {
        for (int i=0; i< players; i++){
            handleClients[i].register();
        }
    }

    public void generateQuestions(){
        HandleClient.generateQuestion(this.roomId, this.listQuestion);
    }

    public void sendQuestionToAllPlayersInRoom(){
        for (int i=0; i<players; i++){
            handleClients[i].start();
        }
    }

    public void waitPlayersAnswer() throws InterruptedException {
        for (int i=0; i<players; i++){
            handleClients[i].join();
        }
    }

    public void closeSever() throws IOException {
        serverSocket.close();
    }

    private void loadData() {
        this.listQuestion = new ArrayList<Question>();
        String key, des;
        try {
            Scanner fin = new Scanner(Paths.get("database.txt"));
            while (fin.hasNextLine()) {
                key = fin.nextLine();
                des = fin.nextLine();
                Question ques = new Question(key, des);
                this.listQuestion.add(ques);
            }
            fin.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public void run() {
        try {
            for (int questionIndex = 0; questionIndex < numQuestions; questionIndex ++) {
                createConnectionForPlayers();
                registerGame();
                generateQuestions();
                sendQuestionToAllPlayersInRoom();
                waitPlayersAnswer();
            }

            HandleClient.clearRegisteredNames(this.roomId);
            for (int i=0; i<players; i++) {
                clients[i].close();
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MainServer[] servers= new MainServer[numRooms];

        while (true){ for (int i=0; i< numRooms; i++){
                if (servers[i] == null || !servers[i].isAlive()) {
                    servers[i] = new MainServer(i);
                    servers[i].createConnection();
                    servers[i].start();
                }
            }
        }
    }
}
