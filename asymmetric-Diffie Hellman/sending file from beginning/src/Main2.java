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
//        System.out.println("size = " + Firstsize);
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


        secretKey = new SecretKeySpec(decryptedFirstsessionkey, "AES");
        cipher2.init(Cipher.DECRYPT_MODE, secretKey, firstiv);
        String recv = client.fromServer();
        while (!(recv.equals("end"))) {
            if (recv.equals("newSessionKey")) {
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
            } else {
                System.out.println("fileName = " + recv);
                String filename = recv;
                String s2 = client.fromServer();
                System.out.println("received");
                int filesize = Integer.parseInt(s2);
                System.out.println("size = " + filesize);
                byte[] filebytes = new byte[filesize];
                for (int l = 0; l < filesize; l++) {
                    filebytes[l] = client.bytesfromServer();

                }
                byte[] decryptedFile = cipher2.doFinal(filebytes);
                String macS = client.fromServer();
                System.out.println("mac received");
                int macSize = Integer.parseInt(macS);
                System.out.println("size = " + macSize);
                byte[] macbytes = new byte[macSize];
                for (int g = 0; g < macSize; g++) {
                    macbytes[g] = client.bytesfromServer();

                }
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(secretKey);
                byte[] macResult = mac.doFinal(decryptedFile);
                if (Arrays.equals(macbytes, macResult)) {
                    System.out.println("mac verified!!!");
                    client.sendServer("OK");

                } else {
                    System.out.println("error sent");
                    client.sendServer("Error");
                    System.out.println("error file is:  "+filename);
                    client.sendServer(filename);
                }

                try {

                    FileOutputStream fos = new FileOutputStream("Client/" + recv);
                    fos.write(decryptedFile);
                    recv = client.fromServer();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
//////////////////////////////////////////////////////////////////////////////////////////
//            byte[] decryptedFile;
//            System.out.println("fileName = " + recv);
//            FileOutputStream fos = new FileOutputStream("Client/" + recv);
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//            byte[] buffer = new byte[4096];
//            int read = 0;
//            while((read = client.readBuffer(buffer)) > -1) {
//                if (recv.equals("newSessionKey")) {
//                    String s = client.fromServer();
//                System.out.println("new sessionkey received");
//                int size = Integer.parseInt(s);
//                System.out.println("size = " + size);
//                byte[] bytes = new byte[size];
//                for (int k = 0; k < size; k++) {
//                    bytes[k] = client.bytesfromServer();
//                }
//
//                byte[] decryptedsessionkey = firstcipher.doFinal(bytes);
//                secretKey = new SecretKeySpec(decryptedsessionkey, "AES");
//                cipher2.init(Cipher.DECRYPT_MODE, secretKey, iv);
//                System.out.println("new sessionKey recieved !");
//                continue;
//
//            }
//                decryptedFile = cipher2.doFinal(buffer);
//                fos.write(decryptedFile);
//
//            }
//
//
//        }


//////////////////////////////////////////////////////////////////////////////////////////
//        File myFile = new File("Client/"+username + "masterkey.txt");
//        Path Path = Paths.get(myFile.getAbsolutePath());
//        byte[] bytes = Files.readAllBytes(Path);
//        EncryptMasterKeyFile encryptedmk = new EncryptMasterKeyFile(bytes, "clientpass123456");
//        byte[] encryptednewmaster = encryptedmk.getEncryptedMasterFile();
//        OutputStream
//                os
//                = new FileOutputStream(myFile);
//        os.write(encryptednewmaster);


//        String masterKeySize = client.fromServer();
//        System.out.println("received");
//        int masterkeysize = Integer.parseInt(masterKeySize);
//        System.out.println("size = " + masterkeysize);
//        byte[] masterkeyBytes = new byte[masterkeysize];
//        for (int l = 0; l < masterkeysize; l++) {
//            masterkeyBytes[l] = client.bytesfromServer();
//
//        }
//        byte[] decryptedFile = cipher2.doFinal(masterkeyBytes);
//
//        try {
//
//            FileOutputStream fos2 = new FileOutputStream( "Client/"+username + "masterkey.txt");
//            fos2.write(decryptedFile);
//
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
        System.out.println("bye");


    }
}


