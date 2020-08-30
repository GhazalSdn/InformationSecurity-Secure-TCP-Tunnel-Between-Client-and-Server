
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
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
    private generateSessionKey gensessionkey;
    private static final String initVector = "encryptionIntVec";
    private PublicKey publicKey = null;
    private SecretKeySpec secretKey;
    private String str;
    private Mac mac = Mac.getInstance("HmacSHA256");
    private Boolean betFile = false;
    int counter = 0;

    public Server(int port) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {

        this.gensessionkey = new generateSessionKey();
        ServerSocket servsock = new ServerSocket(port);
        connectionSocket = servsock.accept();
        in = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
        out = new DataOutputStream(connectionSocket.getOutputStream());


    }

    public void getMasterKey(String name) throws IOException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException {
        this.readPublicKey("RSA/publicKey" + name);

    }

    public void readPublicKey(String fileName) throws IOException {
        byte[] pubKey;
        String p_key = "";
        Path path = Paths.get(fileName);
        pubKey = Files.readAllBytes(path);
        p_key = Base64.getEncoder().encodeToString(pubKey);
        try {
            System.out.println(p_key);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(p_key.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }


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


    public void sendMac(byte[] bytes) throws InvalidKeyException, IOException {
        mac.init(this.secretKey);
        byte[] macResult = mac.doFinal(bytes);
        out.writeUTF("mac" + Base64.getEncoder().encodeToString(macResult));


    }

    ///////////////////////////////////////////////

    public void sendFile(String fileName, int i) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        System.out.println(fileName);
        File myFile = new File(fileName);
        Path Path = Paths.get(myFile.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(Path);
        out.writeUTF("filename" + fileName);
        out.writeUTF("" + bytes.length);
        System.out.println("" + bytes.length);
        FileInputStream fis = new FileInputStream(myFile);
        byte[] buffer = new byte[4096];
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher cipher2 = Cipher.getInstance("AES/CTR/PKCS5Padding");
        byte[] encryptedFile = null;
        cipher2.init(Cipher.ENCRYPT_MODE, this.secretKey, iv);

        while (fis.read(buffer) > 0) {
            if (flag == false) {
                this.sendSessionKey();
                flag = true;
            }

            cipher2.init(Cipher.ENCRYPT_MODE, this.secretKey, iv);
            encryptedFile = cipher2.doFinal(buffer);
            String stringifyFile = Base64.getEncoder().encodeToString(encryptedFile);
            out.writeUTF("new packet" + stringifyFile);
            counter++;
            System.out.println(counter + " section");
            System.out.println("file" + stringifyFile);
            if (i == 2) {
                this.sendMac(encryptedFile);
                if (this.fromClient().equals("Error")) {
                    if (flag == false) {
                        this.sendSessionKey();
                        flag = true;
                    }
                    cipher2.init(Cipher.ENCRYPT_MODE, this.secretKey, iv);
                    encryptedFile = cipher2.doFinal(buffer);
                    stringifyFile = Base64.getEncoder().encodeToString(encryptedFile);
                    out.writeUTF("new packet" + stringifyFile);
                    this.sendMac(buffer);
                }
            }else{
                this.sendMac(buffer);
            }



        }

        fis.close();
        out.writeUTF("end");

//        if(test == 1){
//            this.sendMac(encryptedFile);
//        }
//        else {
//            this.sendMac(bytes);
//        }
    }


    ///////////////////////////////////////////////////////////
    public void newSessionKey() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        // IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] sessionkey = gensessionkey.getSessionKey();
        encryptedSessionkey = encryptCipher.doFinal(sessionkey);
        this.secretKey = new SecretKeySpec(sessionkey, "AES");
        flag = true;
    }

    public void newSessionKeyPeriodic() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        flag = false;
        System.out.println("generating new sessionkey");
//        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher sessioncipher = Cipher.getInstance("RSA");
        sessioncipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
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


}
