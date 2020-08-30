import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Ghazal Sadeghian
 */

class MasterKeyGenerate {
    private Random myRand;
    private int keyLength;
    char map[];
    String str;

    public MasterKeyGenerate() {
        this.myRand = new Random();
        this.keyLength = 16;
        this.str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        this.map = str.toCharArray();


    }

    public byte[] getBytes() throws UnsupportedEncodingException {
        String key = "";
        for (int i = 0; i < keyLength; i++) {

            key += map[myRand.nextInt(62)];
        }
        return key.getBytes("UTF-8");

    }

}

public class MainMasterKeyGenerate {
    public static void main(String[] args) throws IOException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        Scanner userInputScanner = new Scanner(System.in);
        System.out.println("Please Enter your username: " + "\n");
        String username = userInputScanner.next();
        MasterKeyGenerate masterkey = new MasterKeyGenerate();
        byte[] mk = masterkey.getBytes();
        EncryptMasterKeyFile encryptServer = new EncryptMasterKeyFile(mk, "serverpass123456");
        byte[] encryptedmk1 = encryptServer.getEncryptedMasterFile();
        EncryptMasterKeyFile encryptClient = new EncryptMasterKeyFile(mk, "clientpass123456");
        byte[] encryptedmk2 = encryptClient.getEncryptedMasterFile();
        try {

            File file = new File(username + "masterkey.txt");
            File file2 = new File("Client/" + username + "masterKey.txt");
            // Initialize a pointer
            // in file using OutputStream
            OutputStream
                    os
                    = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(encryptedmk1);
            OutputStream
                    os2
                    = new FileOutputStream(file2);

            // Starts writing the bytes in it
            os2.write(encryptedmk2);

            // Close the file
            os.close();
            os2.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}










