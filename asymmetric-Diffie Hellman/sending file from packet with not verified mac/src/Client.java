

import java.net.Socket;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
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
    DataInputStream dis;
    DataOutputStream outToServer;
    PrivateKey privateKey = null;


    public Client() throws IOException {


        clientSocket = new Socket("", 8080);
        System.out.println("connected");
        userInputScanner = new Scanner(System.in);
        inFromServer = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        outToServer = new DataOutputStream(clientSocket.getOutputStream());

    }



    public PrivateKey readPrivateKey(String fileName) throws IOException {
        byte[] privatee;
        String pr_key = "";
        KeyFactory keyFactory = null;
        Path path = Paths.get(fileName);
        privatee = Files.readAllBytes(path);
        pr_key = Base64.getEncoder().encodeToString(privatee);

        System.out.println(pr_key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pr_key.getBytes()));
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;


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


