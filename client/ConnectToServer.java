package client;

import utils.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ConnectToServer {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Player player;
    private String question;
    private int numPlayers=0;
    private int numQuestions=0;
    private Integer roomId=0;

    public ConnectToServer(String IPHost,  int port) throws IOException {
        this.socket=new Socket(IPHost, port);
        this.dataInputStream= new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream= new DataOutputStream(this.socket.getOutputStream());
        System.out.println("Client is connected to "+IPHost+":"+port);
    }

    public boolean registerForClient(String clientName){
        try {
            System.out.println(clientName);
            this.dataOutputStream.writeUTF(clientName);
            String infoClient= this.dataInputStream.readUTF();
            String status= infoClient.split(" ")[0];

            if (status.equals("successful")){
                this.player= new Player(clientName, 0);
                this.roomId= Integer.valueOf(infoClient.split(" ")[1]);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateScore(){
        try {
            String score= this.dataInputStream.readUTF();
            this.player.setScore(Integer.parseInt(score));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer getScore(){
        return this.player.getScore();
    }

    public boolean updateQuestion(){
        try {
            this.question= this.dataInputStream.readUTF();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getQuestion(){
        return this.question;
    }

    public boolean clientAnswer(String answer){
        try {
            this.dataOutputStream.writeUTF(answer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        ConnectToServer connectToServer= new ConnectToServer("localhost", 8000);
        String name= null;
        Scanner scanner= new Scanner(System.in);

        while (name==null || connectToServer.registerForClient(name) == false){
            System.out.println("Enter you name");
            name= scanner.nextLine();
        }

        String answer;
        while (connectToServer.updateQuestion() && connectToServer.updateScore()){
            answer= scanner.nextLine();
            connectToServer.clientAnswer(answer);
        }

        scanner.close();
    }
}
