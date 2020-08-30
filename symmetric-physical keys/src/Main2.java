import javax.crypto.Cipher;
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
        System.out.println("size = " + Firstsize);
        byte[] firstbytes = new byte[Firstsize];
        for (int k = 0; k < Firstsize; k++) {
            firstbytes[k] = client.bytesfromServer();
        }
        IvParameterSpec firstiv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        File masterkeyFile = new File("Client/" + username + "masterkey.txt");
        Path masterkeyPath = Paths.get(masterkeyFile.getAbsolutePath());
        byte[] encodedMasterkey = Files.readAllBytes(masterkeyPath);

        byte[] mypass = "clientpass123456".getBytes("utf-8");
        Cipher cipherr = Cipher.getInstance("AES/CTR/PKCS5Padding");
        SecretKeySpec passFormasterKey = new SecretKeySpec(mypass, "AES");
        IvParameterSpec ivv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        cipherr.init(Cipher.DECRYPT_MODE, passFormasterKey, ivv);
        byte[] decryptedMasterkey = cipherr.doFinal(encodedMasterkey);

        SecretKey masterKey = new SecretKeySpec(decryptedMasterkey, "AES");


        //SecretKey masterKey = new SecretKeySpec(encodedMasterkey, "AES");
        Cipher firstcipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        firstcipher.init(Cipher.DECRYPT_MODE, masterKey, firstiv);
        byte[] decryptedFirstsessionkey = firstcipher.doFinal(firstbytes);


        secretKey = new SecretKeySpec(decryptedFirstsessionkey, "AES");
        cipher2.init(Cipher.DECRYPT_MODE, secretKey, firstiv);


        String recv = client.fromServer();
        while (!(recv.equals("end"))) {
            if (recv.equals("newSessionKey")) {
                String s = client.fromServer();

                int size = Integer.parseInt(s);
                System.out.println("size = " + size);
                byte[] bytes = new byte[size];
                for (int k = 0; k < size; k++) {
                    bytes[k] = client.bytesfromServer();

                }

                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                byte[] decryptedsessionkey = cipher2.doFinal(bytes);


                secretKey = new SecretKeySpec(decryptedsessionkey, "AES");
                cipher2.init(Cipher.DECRYPT_MODE, secretKey, iv);
                System.out.println("new sessionKey recieved !");
                recv = client.fromServer();
            } else {
                System.out.println("fileName = " + recv);
                String s2 = client.fromServer();
                System.out.println("received");
                int filesize = Integer.parseInt(s2);
                System.out.println("size = " + filesize);
                byte[] filebytes = new byte[filesize];
                for (int l = 0; l < filesize; l++) {
                    filebytes[l] = client.bytesfromServer();

                }
                byte[] decryptedFile = cipher2.doFinal(filebytes);

                try {

                    FileOutputStream fos = new FileOutputStream("Client/" + recv);
                    fos.write(decryptedFile);
                    recv = client.fromServer();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }


        File myFile = new File("Client/" + username + "masterkey.txt");
        Path Path = Paths.get(myFile.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(Path);
        EncryptMasterKeyFile encryptedmk = new EncryptMasterKeyFile(bytes, "clientpass123456");
        byte[] encryptednewmaster = encryptedmk.getEncryptedMasterFile();
        OutputStream
                os
                = new FileOutputStream(myFile);
        os.write(encryptednewmaster);


        System.out.println("new masterkey received");
        System.out.println("bye");


    }

}
