/**
 * @author Ghazal Sadeghian
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;

public class PairGenerator {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public PairGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        Scanner userInputScanner = new Scanner(System.in);
        System.out.println("Please Enter your username: " + "\n");
        String username = userInputScanner.next();
        PairGenerator keyPairGenerator = new PairGenerator();
        keyPairGenerator.writeToFile("RSA/publicKey"+username, keyPairGenerator.getPublicKey().getEncoded());
        keyPairGenerator.writeToFile("Client/RSAKEY/privateKey"+username, keyPairGenerator.getPrivateKey().getEncoded());
        System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded()));
        System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded()));
    }
}