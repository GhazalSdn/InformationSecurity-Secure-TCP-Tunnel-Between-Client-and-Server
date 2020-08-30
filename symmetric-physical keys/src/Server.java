
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

/**
 * @author Ghazal Sadeghian
 */

public class Server {

    Boolean flag;
    byte[] encryptedSessionkey;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket connectionSocket;
    //private MasterkeyGenerator generateMastrekey;
    //private MasterKeyGenerate mkgen;
    private SecretKey masterKey;
    private static final String initVector = "encryptionIntVec";
    private generateSessionKey gensessionkey;
    private SecretKeySpec secretKey;
    private String str;

    public Server(int port) throws IOException, NoSuchAlgorithmException {

        this.gensessionkey = new generateSessionKey();
        ServerSocket servsock = new ServerSocket(port);
        connectionSocket = servsock.accept();
        in = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
        out = new DataOutputStream(connectionSocket.getOutputStream());


    }


    public void getMasterKey(String name) throws IOException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException {
        File masterkeyFile = new File(name + "masterkey.txt");
        Path masterkeyPath = Paths.get(masterkeyFile.getAbsolutePath());
        byte[] encodedMasterkey = Files.readAllBytes(masterkeyPath);
        byte[] mypass = "serverpass123456".getBytes("utf-8");
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        SecretKeySpec passFormasterKey = new SecretKeySpec(mypass, "AES");
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, passFormasterKey, iv);
        byte[] decryptedFile = cipher.doFinal(encodedMasterkey);

        masterKey = new SecretKeySpec(decryptedFile, "AES");

    }


    //    }
    public String fromClient() {
        try {
            str = in.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void sendFile(String fileName) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        if (flag == false) {
            this.sendSessionKey();
            flag = true;
        }
        System.out.println(fileName);
        File myFile = new File(fileName);
        Path Path = Paths.get(myFile.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(Path);

        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher cipher2 = Cipher.getInstance("AES/CTR/PKCS5Padding");
        cipher2.init(Cipher.ENCRYPT_MODE, this.secretKey, iv);
        byte[] encryptedFile = cipher2.doFinal(bytes);
        for (int m = 0; m < bytes.length; m++) {
            System.out.println(bytes[m]);
        }

        out.writeUTF(fileName);
        out.writeUTF("" + bytes.length);
        out.write(encryptedFile);
    }


    public void newSessionKey() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.masterKey, iv);
        byte[] sessionkey = gensessionkey.getSessionKey();
        encryptedSessionkey = cipher.doFinal(sessionkey);
        this.secretKey = new SecretKeySpec(sessionkey, "AES");
        flag = true;
    }

    public void newSessionKeyPeriodic() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        flag = false;
        System.out.println("generating new sessionkey");
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher sessioncipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        sessioncipher.init(Cipher.ENCRYPT_MODE, this.secretKey, iv);
        byte[] sessionkey = gensessionkey.getSessionKey();
        encryptedSessionkey = sessioncipher.doFinal(sessionkey);
        this.secretKey = new SecretKeySpec(sessionkey, "AES");
    }

    public void sendSessionKey() throws IOException {
        out.writeUTF("newSessionKey");
        out.writeUTF("" + encryptedSessionkey.length);
        out.write(encryptedSessionkey);
    }

    public void sendFirstSessionKey() throws IOException {
        out.writeUTF("" + encryptedSessionkey.length);
        out.write(encryptedSessionkey);
    }


    public void sendString(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNewMasterKey(String name) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {

        byte[] newMasterKey = gensessionkey.getSessionKey();

        try {

            File file = new File(name + "masterkey.txt");

            OutputStream
                    os
                    = new FileOutputStream(file);
            os.write(newMasterKey);

            // Close the file
            os.close();

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        this.sendFile(name + "masterkey.txt");
        EncryptMasterKeyFile encryptedmk = new EncryptMasterKeyFile(newMasterKey, "serverpass123456");
        byte[] encryptednewmaster = encryptedmk.getEncryptedMasterFile();
        try {

            File file2 = new File(name + "masterkey.txt");

            OutputStream
                    os2
                    = new FileOutputStream(file2);
            os2.write(encryptednewmaster);

            // Close the file
            os2.close();

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        System.out.println("new MasterKey sent to client !");



    }
}
