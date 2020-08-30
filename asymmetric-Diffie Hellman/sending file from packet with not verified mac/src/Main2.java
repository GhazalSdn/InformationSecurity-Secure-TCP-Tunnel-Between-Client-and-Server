import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

/**
 * @author Ghazal Sadeghian
 */

public class Main2 {
    public static void main(String[] argv) throws Exception {
        Client client = new Client();
        Cipher cipher2 = Cipher.getInstance("AES/CTR/PKCS5Padding");
        SecretKeySpec secretKey;
        ;
        Scanner userInputScanner = new Scanner(System.in);
        ;
        System.out.println("Please Enter your username: " + "\n");
        String username = userInputScanner.next();
        client.sendServer(username);
        String initVector = "encryptionIntVec";
        String firstsessionSize = client.fromServer();
        System.out.println("first sessionkey received");
        int Firstsize = Integer.parseInt(firstsessionSize);
        byte[] firstbytes = new byte[Firstsize];
        for (int k = 0; k < Firstsize; k++) {
            firstbytes[k] = client.bytesfromServer();
        }
        IvParameterSpec firstiv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        PrivateKey privateKey = client.readPrivateKey("Client/RSAKEY/privateKey" + username);


        //SecretKey masterKey = new SecretKeySpec(encodedMasterkey, "AES");
        Cipher firstcipher = Cipher.getInstance("RSA");
        firstcipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedFirstsessionkey = firstcipher.doFinal(firstbytes);
        int counter = 0;
        FileOutputStream fos = null ;
        secretKey = new SecretKeySpec(decryptedFirstsessionkey, "AES");
        cipher2.init(Cipher.DECRYPT_MODE, secretKey, firstiv);
        String recv = client.fromServer();
        if(recv.startsWith("filename")){
            recv = recv.replace("filename","");
            fos = new FileOutputStream("Client/"+recv);
        }
        recv = client.fromServer();
        System.out.println("file size:" + recv);
        int filesize = Integer.parseInt(recv);
        int remaining = filesize;
        recv = client.fromServer();


        while (!(recv.equals("end"))) {
            if (recv.startsWith("newSessionKey")) {
                String s = client.fromServer();
                System.out.println("new sessionkey received");
                int size = Integer.parseInt(s);
                System.out.println("size = " + size);
                byte[] bytes = new byte[size];
                for (int k = 0; k < size; k++) {
                    bytes[k] = client.bytesfromServer();

                }

                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                byte[] decryptedsessionkey = firstcipher.doFinal(bytes);


                secretKey = new SecretKeySpec(decryptedsessionkey, "AES");
                cipher2.init(Cipher.DECRYPT_MODE, secretKey, iv);
                System.out.println("new sessionKey recieved !");
                recv = client.fromServer();
            } else if (recv.startsWith("new packet")) {

                byte[] decryptedFile;

                String packetFile = recv.replace("new packet", "");

                byte[] buffer = Base64.getDecoder().decode(packetFile);
                decryptedFile = cipher2.doFinal(buffer);
                remaining -= decryptedFile.length;
                System.out.println("remaining:" + remaining);
                counter++;
                System.out.println(counter + " section");

                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(secretKey);
                String macRecieved = client.fromServer();
                byte[] macBytes = null;
                if (macRecieved.startsWith("mac")) {
                    macRecieved = macRecieved.replace("mac", "");
                    macBytes = Base64.getDecoder().decode(macRecieved);
                }
                byte[] macResult = mac.doFinal(decryptedFile);
                if (Arrays.equals(macBytes, macResult)) {
                    System.out.println("mac verified!!!");
                    fos.write(decryptedFile);
                    client.sendServer("ok");


                } else {
                    System.out.println("error sent");
                    client.sendServer("Error");

                }


                recv = client.fromServer();

            } else {
                break;
            }
        }
        fos.close();


    }


}


