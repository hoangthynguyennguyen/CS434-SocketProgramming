package server;

import client.Main;
import utils.Question;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainServer extends Thread {
    public static int numOfPlayers=2;
    public static int numQuestions=4;
    private static int port=8000;
    public static ArrayList<Socket> socketArrayList;
    public static ArrayList<String> names;
    public static ArrayList<Integer> points;
    public static ArrayList<Boolean> correctAnswer;
    public static ArrayList<Integer> wrongAnswer;
    public static ArrayList<Question> listQuestions;
    private static Question question;
    private static ServerSocket serverSocket;
    private static Random random;
    private static Socket socket;


    public static void main(String[] args) throws IOException {
        MainServer.socketArrayList= new ArrayList<>();
        MainServer.names= new ArrayList<>();
        MainServer.random= new Random();
        MainServer.socket= new Socket();
        MainServer.loadData();
        System.out.println("Running...");

        if (MainServer.serverSocket==null){
            MainServer.serverSocket= new ServerSocket(MainServer.port);
            System.out.println("Server is running on port "+port);
        }

        while (true) {
                MainServer.socket=MainServer.serverSocket.accept();
                System.out.println("Client " + MainServer.socket);
                MainServer.socketArrayList.add(MainServer.socket);

                DataInputStream dataInputStream = new DataInputStream(MainServer.socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(MainServer.socket.getOutputStream());

                // get name
                String name = dataInputStream.readUTF();
                System.out.println(name + " is registered");
                MainServer.names.add(name);

                if (MainServer.socketArrayList.size() == numOfPlayers) break;
        }

            MainServer.points = new ArrayList<>();
            MainServer.wrongAnswer = new ArrayList<>();
            for (int i = 0; i < MainServer.numOfPlayers; i++) {
                MainServer.points.add(0);
                MainServer.wrongAnswer.add(0);
//                DataOutputStream dataOutputStream= new DataOutputStream(MainServer.socket.getOutputStream());
//                dataOutputStream.writeUTF("Score "+MainServer.points.get(i));
            }
            System.out.println(MainServer.points);
            MainServer.writeToClient();

            MainServer.correctAnswer = new ArrayList<>();

            for (int i = 0; i < MainServer.numOfPlayers; ++i) {
                MainServer.correctAnswer.add(false);
            }


            for (Socket item : MainServer.socketArrayList) {
                HandleAnswer handleAnswer = new HandleAnswer(item, MainServer.question.getKeyword());
                handleAnswer.start();
            }

            MainServer.handleGame();
    }

    public static void writeToClient() throws IOException {
            int randomNum=  MainServer.random.nextInt(MainServer.listQuestions.size());
            MainServer.question= MainServer.listQuestions.get(randomNum);

            DataOutputStream dataOutputStream= null;
            for (Socket item : MainServer.socketArrayList) {
                System.out.println(MainServer.question.getDescription());
                dataOutputStream = new DataOutputStream(item.getOutputStream());
                dataOutputStream.writeUTF("Question: " + MainServer.question.getDescription());
            }
    }

    public static void loadData() {
        MainServer.listQuestions = new ArrayList<Question>();
        String key, des;
        try {
            Scanner fin = new Scanner(Paths.get("database.txt"));
            while (fin.hasNextLine()) {
                key = fin.nextLine();
                des = fin.nextLine();
                Question ques = new Question(key, des);
                MainServer.listQuestions.add(ques);
            }
            fin.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void handleGame(){
       handleNobodyAnswerCorrect();
       handleClientCorrect();
    }

    // Nobody answer correct
    public static void handleNobodyAnswerCorrect(){
            System.out.println("Wrong all");
            for (int i=0; i< MainServer.socketArrayList.size(); i++){
                MainServer.wrongAnswer.set(i, MainServer.wrongAnswer.get(i)+1);
                if (MainServer.points.get(i)>1){
                    int val= MainServer.points.get(i);
                    val=val-1;
                    MainServer.points.set(i, val);
                }
            }
    }

    private static void handleClientCorrect() {
        int wrongAnsN = 0;
        for (int i=0; i<MainServer.socketArrayList.size(); ++i) {
            if (MainServer.correctAnswer.get(i)) {
                int val = MainServer.points.get(i);
                System.out.println(val);
                val++;
                MainServer.points.set(i, val);
            }
            else if (!MainServer.correctAnswer.get(i)) {
                if (MainServer.points.get(i) > 1) {
                    int val = MainServer.points.get(i);
                    System.out.println(val);
                    val--;
                    MainServer.points.set(i, val);
                }

                wrongAnsN += 1;
            }
        }
        if (wrongAnsN == 0) wrongAnsN = 2;
//        System.out.println(MainServer.points);
    }
}

class HandleAnswer extends Thread {
    private Socket socket;
    public static String answer;


    public HandleAnswer(Socket socket, String answer) {
        this.socket = socket;
        this.answer = answer;
    }


    @Override
    public void run() {
        try {
            int socketIndex = MainServer.socketArrayList.indexOf(socket);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            String characterOfClient = dataInputStream.readUTF();
            String keywordOfClient = dataInputStream.readUTF();

            try {
                System.out.println("Character of " + MainServer.names.get(socketIndex) + " :" + characterOfClient);
                System.out.println("Keyword of " + MainServer.names.get(socketIndex) + " :" + keywordOfClient);

                if (HandleAnswer.answer.contains(characterOfClient.substring(0,1))){
                    int val= MainServer.points.get(socketIndex);
                    val++;
                    MainServer.points.set(socketIndex, val);
                    MainServer.correctAnswer.set(socketIndex, true);
                    MainServer.wrongAnswer.set(socketIndex, 0);
                }
                if (keywordOfClient.equals(HandleAnswer.answer)) {
                    int val= MainServer.points.get(socketIndex);
                    val+=5;
                    MainServer.points.set(socketIndex, val);
                    MainServer.correctAnswer.set(socketIndex, true);
                    MainServer.wrongAnswer.set(socketIndex, 0);
                } else {
                    MainServer.wrongAnswer.set(socketIndex, MainServer.wrongAnswer.get(socketIndex) + 1);
                }

                if (MainServer.correctAnswer.get(socketIndex)){
                    System.out.println(MainServer.names.get(socketIndex) + " -> has correct answer and score "+ MainServer.points.get(socketIndex));
                }
                
                if (MainServer.wrongAnswer.get(socketIndex)>=1){
                    System.out.println(MainServer.names.get(socketIndex)+ " -> has wrong answer "+ MainServer.wrongAnswer.get(socketIndex));
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("End connection ---> Bye...");
            }
        }
    }
}
