

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Ghazal Sadeghian
 */
public class Client {

    String str;
    int i;
    byte b;
    Socket clientSocket;
    Scanner userInputScanner;
    DataInputStream inFromServer;
    DataOutputStream outToServer;

    public Client() {
        try {

            clientSocket = new Socket("", 8080);
            System.out.println("connected");
            userInputScanner = new Scanner(System.in);
            inFromServer = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            outToServer = new DataOutputStream(clientSocket.getOutputStream());

        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

    public void sendServer(String str) {
        try {
            outToServer.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendServerBytes(byte[] b) {
        try {
            outToServer.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String fromServer() {
        try {
            str = inFromServer.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public byte bytesfromServer() {
        try {
            b = inFromServer.readByte();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }

}


