import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ghazal Sadeghian
 */

public class Main1 {
    public static void main(String[] args) throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
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
        }, 0, 1000);

        serv.sendFile("testing.txt");
        serv.sendFile("multimedia.jpg");
        serv.sendFile("1234.txt");
        serv.sendFile("HW3_theory.pdf");
        serv.sendFile("FirstProject-Part1.pdf");
        serv.sendFile("index.html");
        serv.sendNewMasterKey(username);
        serv.sendString("end");

        timer.cancel();
    }


}
