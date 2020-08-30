import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ghazal Sadeghian
 */

public class Main1 {
    public static void main(String[] args) throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        Server serv = new Server(8080);
        String username = serv.fromClient();
        serv.getMasterKey(username);
        serv.newSessionKey();
        serv.sendFirstSessionKey();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    serv.newSessionKeyPeriodic();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
            }
        }, 30, 100);

//        serv.sendFile("Files/testing.txt");

        serv.sendFile("Files/HW3_theory.pdf", 2);
        String str = serv.fromClient();
        if (str.equals("Error")) {
            System.out.println("error recieved");
            String fileN = serv.fromClient();
            System.out.println("error file is:  "+fileN);
            serv.sendFile(fileN, 2);

        }
        serv.sendFile("Files/1234.txt", 1);
        String str2 = serv.fromClient();
        if (str2.equals("Error")) {
            System.out.println("error recieved");
            String fileN = serv.fromClient();
            System.out.println("error file is:  "+fileN);
            serv.sendFile(fileN, 2);

        }
        serv.sendFile("Files/multimedia.jpg", 1);
        String str3 = serv.fromClient();
        if (str3.equals("Error")) {
            System.out.println("error recieved");
            String fileN = serv.fromClient();
            System.out.println("error file is:  "+fileN);
            serv.sendFile(fileN, 2);

        }
        serv.sendFile("Files/FirstProject-Part1.pdf", 2);

        String str4 = serv.fromClient();
        if (str4.equals("Error")) {
            System.out.println("error recieved");
            String fileN = serv.fromClient();
            System.out.println("error file is:  "+fileN);
            serv.sendFile(fileN, 2);

        }
        serv.sendString("end");

        timer.cancel();
    }


}
