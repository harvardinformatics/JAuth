package JAuth;

import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class AuthenticatorCLI {
  Timer timer;

  public static void main(String[] args) {
    System.out.println("\nAuthenticator Started!");
    System.out.println(":----------------------------:--------:");
    System.out.println(":       Code Wait Time       :  Code  :");
    System.out.println(":----------------------------:--------:");
    AuthenticatorCLI main = new AuthenticatorCLI();
    String inFile=args[0];

    try {
      RandomAccessFile raf= new RandomAccessFile(inFile,"r");
      FileReader fileReader = new FileReader(raf.getFD());
      BufferedReader bufReader  = new LineNumberReader(fileReader,65536);
      main.reminder(bufReader.readLine());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void reminder(String secret) {
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimedPin(secret), 0, 1 * 1000);
  }

  int count=1;

  class TimedPin extends TimerTask {
    private String secret;

    public TimedPin (String secret){
      this.secret=secret;
    }

    String previouscode = "";

    public void run() {
      String newout = AuthenticatorCLI.computePin(secret,null);
      if(previouscode.equals(newout)) {
        System.out.print(".");
      } else {
        if(count<=30) {
          for (int i=count+1; i<=30;i++) {
            System.out.print("+");
          }
        }

        System.out.println(": "+ newout + " :");
        count=0;
      }
        previouscode = newout;
        count++;
    }
  }

  public static String computePin(String secret, Long counter) {
    if (secret == null || secret.length() == 0) {
      return "Null or empty secret";
    }

    try {
      final byte[] keyBytes = Base32String.decode(secret);
      Mac mac = Mac.getInstance("HMACSHA1");
      mac.init(new SecretKeySpec(keyBytes, ""));
      PasscodeGenerator pcg = new PasscodeGenerator(mac);
      return pcg.generateTimeoutCode();
    } catch (GeneralSecurityException e) {
      return "General security exception";
    } catch (Base32String.DecodingException e) {
      return "Decoding exception";
    }
  }
}
