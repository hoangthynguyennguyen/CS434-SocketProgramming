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
    private static Random random;


    public static void main(String[] args) throws IOException {
        MainServer.socketArrayList= new ArrayList<>();
        MainServer.names= new ArrayList<>();
        MainServer.loadData();
        System.out.println("Running...");

        ServerSocket serverSocket= new ServerSocket(MainServer.port);
        while (true) {
            Socket socket = serverSocket.accept();
            MainServer.socketArrayList.add(socket);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // get name
            String name = dataInputStream.readUTF();
            dataOutputStream.writeUTF(name + "is registered");
            MainServer.names.add(name);

            if (MainServer.socketArrayList.size() == numOfPlayers) break;
        }

        MainServer.points= new ArrayList<>();
        MainServer.wrongAnsCount=new ArrayList<>();
        for (int i=0; i< MainServer.numOfPlayers; i++){
            MainServer.points.add(1);
            MainServer.wrongAnsCount.add(0);
        }
        MainServer.writeToClient();
        MainServer.firstSocket = new ArrayList<>();
        MainServer.receivedAnsCorrect = new ArrayList<>();

        for (int i=0; i< MainServer.numOfPlayers; ++i) {
            MainServer.receivedAnsCorrect.add(false);
        }


        for (Socket item : MainServer.socketArrayList) {
            ReadAnswer readAnswer = new ReadAnswer(item, MainServer.question.getKeyword());
            readAnswer.start();

        }
    }

    public static void writeToClient() throws IOException {
        while (true) {
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
            if (endGame) break;

            int randomNum=  MainServer.random.nextInt(MainServer.listQuestions.size());
            MainServer.question= MainServer.listQuestions.get(randomNum);

            DataOutputStream dataOutputStream= null;
            for (Socket item : MainServer.socketArrayList) {
                dataOutputStream = new DataOutputStream(item.getOutputStream());
                dataOutputStream.writeUTF("Question: " + MainServer.question);
            }
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

                if (resultOfClient == ReadAnswer.answer && MainServer.firstSocket.isEmpty()) {
                    MainServer.receivedAnsCorrect.set(socketIndex, true);
                    MainServer.firstSocket.add(socket);
                    MainServer.wrongAnsCount.set(socketIndex, 0);
                } else if (resultOfClient == ReadAnswer.answer) {
                    MainServer.receivedAnsCorrect.set(socketIndex, true);
                    MainServer.wrongAnsCount.set(socketIndex, 0);
                } else {
                    MainServer.wrongAnsCount.set(socketIndex, MainServer.wrongAnsCount.get(socketIndex) + 1);
                }

                System.out.println(MainServer.names.get(socketIndex) + ": receivedAnsCorrect: " + MainServer.receivedAnsCorrect.get(socketIndex));
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
