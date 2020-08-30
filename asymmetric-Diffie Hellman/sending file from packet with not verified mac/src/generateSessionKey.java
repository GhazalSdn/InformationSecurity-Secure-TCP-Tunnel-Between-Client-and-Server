import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Ghazal Sadeghian
 */

public class generateSessionKey {
    private Random myRand;
    private int keyLength;
    char map[];
    String str;

    public generateSessionKey() {
        this.myRand = new Random();
        this.keyLength = 16;
        this.str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        this.map = str.toCharArray();


    }

    public byte[] getSessionKey() throws UnsupportedEncodingException {
        String key = "";
        for (int i = 0; i < keyLength; i++) {

            key += map[myRand.nextInt(62)];
        }
        return key.getBytes("UTF-8");

    }



}





