import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Ghazal Sadeghian
 */

public class EncryptMasterKeyFile {
    private static final String initVector = "encryptionIntVec";
    private SecretKeySpec keyForMAsterkey;
    private byte[] encryptedFile;

    public EncryptMasterKeyFile(byte[] fileBytes, String key) throws IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        byte[] keyBytes = key.getBytes("utf-8");
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        keyForMAsterkey = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keyForMAsterkey, iv);
        encryptedFile = cipher.doFinal(fileBytes);


    }

    public byte[] getEncryptedMasterFile() {
        return encryptedFile;
    }
}
