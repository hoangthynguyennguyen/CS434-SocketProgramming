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
    public static ArrayList<Boolean> receivedAnsCorrect;
    public static ArrayList<Integer> wrongAnsCount;
    public static ArrayList<Socket> firstSocket;
    public static int timeout=8000;
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
        int currentQuestion=0;
//        while (currentQuestion < MainServer.numQuestions) {
            while (true) {
//                Socket socket = MainServer.serverSocket.accept();
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
            MainServer.wrongAnsCount = new ArrayList<>();
            for (int i = 0; i < MainServer.numOfPlayers; i++) {
                MainServer.points.add(0);
                MainServer.wrongAnsCount.add(0);
                DataOutputStream dataOutputStream= new DataOutputStream(MainServer.socket.getOutputStream());
                dataOutputStream.writeUTF("Score "+MainServer.points.get(i));
            }
            System.out.println(MainServer.points);
            MainServer.writeToClient();

            MainServer.firstSocket = new ArrayList<>();
            MainServer.receivedAnsCorrect = new ArrayList<>();

            for (int i = 0; i < MainServer.numOfPlayers; ++i) {
                MainServer.receivedAnsCorrect.add(false);
            }


            for (Socket item : MainServer.socketArrayList) {
                ReadAnswer readAnswer = new ReadAnswer(item, MainServer.question.getKeyword());
                readAnswer.start();
            }

            MainServer.handleGame();
//            currentQuestion++;
//        }
    }

    public static void writeToClient() throws IOException {
            boolean endGame = false;
            int j = 0;
            for (int point : MainServer.points) {
                if (point >= MainServer.numQuestions) {
                    endGame = true;
                    System.out.println("Winner is " + MainServer.names.get(j));
                    break;
                }
                j++;
            }

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
        if (MainServer.firstSocket.isEmpty()){
            System.out.println("Wrong all");
            for (int i=0; i< MainServer.socketArrayList.size(); i++){
                MainServer.wrongAnsCount.set(i, MainServer.wrongAnsCount.get(i)+1);
                if (MainServer.points.get(i)>1){
                    int val= MainServer.points.get(i);
                    val=val-1;
                    MainServer.points.set(i, val);
                }
            }
        }
    }

    private static void handleClientCorrect() {
//        int firstSocketIndex = MainServer.socketArrayList.indexOf(MainServer.firstSocket.get(0));
        int wrongAnsN = 0;
        for (int i=0; i<MainServer.socketArrayList.size(); ++i) {
            if (MainServer.receivedAnsCorrect.get(i)) {
                int val = MainServer.points.get(i);
                System.out.println(val);
                val++;
                MainServer.points.set(i, val);
            }
            else if (!MainServer.receivedAnsCorrect.get(i)) {
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
//        MainServer.points.set(firstSocketIndex, MainServer.points.get(firstSocketIndex) + wrongAnsN);
//        System.out.println(MainServer.points);
    }
}

class ReadAnswer extends Thread {
    private Socket socket;
    public static String answer;


    public ReadAnswer(Socket socket, String answer) {
        this.socket = socket;
        this.answer = answer;
    }


    @Override
    public void run() {
        try {
            int socketIndex = MainServer.socketArrayList.indexOf(socket);
            System.out.println("Socket index is: " + socketIndex);
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            String resultOfClient = dis.readUTF();
            System.out.println(resultOfClient);

            try {
                System.out.println("Ket qua cua " + MainServer.names.get(socketIndex) + " :" + resultOfClient);

                if (resultOfClient.equals(ReadAnswer.answer) && MainServer.firstSocket.isEmpty()) {
                    int val= MainServer.points.get(socketIndex);
                    val++;
                    MainServer.points.set(socketIndex, val);
                    MainServer.receivedAnsCorrect.set(socketIndex, true);
                    MainServer.firstSocket.add(socket);
                    MainServer.wrongAnsCount.set(socketIndex, 0);
                } else if (resultOfClient.equals(ReadAnswer.answer)) {
                    MainServer.receivedAnsCorrect.set(socketIndex, true);
                    MainServer.wrongAnsCount.set(socketIndex, 0);
                } else {
                    MainServer.wrongAnsCount.set(socketIndex, MainServer.wrongAnsCount.get(socketIndex) + 1);
                }

                System.out.println(MainServer.names.get(socketIndex) + ": receivedAnsCorrect: " + MainServer.receivedAnsCorrect.get(socketIndex) + " score"+ MainServer.points.get(socketIndex));
                System.out.println(MainServer.names.get(socketIndex) + ": wrongAnsCount: " + MainServer.wrongAnsCount.get(socketIndex));
            }
            catch (Exception e) {
                MainServer.wrongAnsCount.set(socketIndex, MainServer.wrongAnsCount.get(socketIndex) + 1);
                System.out.println(MainServer.names.get(socketIndex) + ": receivedAnsCorrect: " + MainServer.receivedAnsCorrect.get(socketIndex));
                System.out.println(MainServer.names.get(socketIndex) + ": wrongAnsCount: " + MainServer.wrongAnsCount.get(socketIndex));
            }

        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("End connection");
            }
        }
    }
}
