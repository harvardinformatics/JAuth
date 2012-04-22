import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;

import javax.swing.*;
import java.awt.datatransfer.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.imageio.ImageIO;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

//import com.install4j.api.launcher.Variables;

//import com.apple.eawt.*;

public final class AuthenticatorGUI extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

  public JLabel            codeField     = new JLabel("--- ---");
  public JLabel            copyLabel     = new JLabel(" Copy");
  public JLabel            nextLabel     = new JLabel(" next");
  public JLabel            progressLabel = new JLabel("");
  public JLabel            closeLabel    = new JLabel("x");

  public Image             image;

  public InputStream       fontStream;

  public String            secret;
  public Mac               mac;
  public PasscodeGenerator pcg;
  public Font              font;

  public boolean           shownextcode = false;

  public String            currcode;
  public String            nextcode;

  public Color             darkred = new Color(150,0,0);

  public AuthenticatorGUI(Image image,Font font) {

    try {
      this.font  = font;
      this.image = image;

    } catch (Exception e) {
      e.printStackTrace();
    }

    componentInit();
  }
   
  public AuthenticatorGUI(String secret,Image image, Font font) {
    this(image,font);

    this.setSecret(secret);
  }

  public void setSecret(String secret) {
    try {
      this.secret = secret;
      
      // Do the magic with the secret key
      final byte[] keybytes = Base32String.decode(secret);
      
      mac = Mac.getInstance("HMACSHA1");
      mac.init(new SecretKeySpec(keybytes,""));
      
      pcg = new PasscodeGenerator(mac);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void componentInit() {

    FormLayout layout = new FormLayout("17px,fill:pref:grow,1dlu,40px,10px",  // Cols
				       "10px,2px,14px,14px,2px,5px,17px");      // Rows
    
    setLayout(layout);
    setBackground(Color.white);

    CellConstraints cc = new CellConstraints();

    try {

      // Load the LCD font

      Font font32 = this.font.deriveFont(32f);
      Font font16 = this.font.deriveFont(16f);
      Font font20 = this.font.deriveFont(20f);
      Font font13 = this.font.deriveFont(13f);
      Font font12 = new Font("Monospace",Font.BOLD,10);

      codeField.setFont(font32);
      copyLabel.setFont(font13);
      nextLabel.setFont(font13);
      progressLabel.setFont(font20);
      closeLabel.setFont(font12);

      Color c = new Color(150,150,150);
      closeLabel.setForeground(c);
      //closeLabel.setBorder(BorderFactory.createLineBorder(Color.red));


    } catch (Exception e) {
      e.printStackTrace();
    }

    codeField.setPreferredSize(new Dimension(100,30));
    copyLabel.setPreferredSize(new Dimension(60,30));
    nextLabel.setPreferredSize(new Dimension(60,30));
    copyLabel.addMouseListener(this);
    nextLabel.addMouseListener(this);

    closeLabel.setPreferredSize(new Dimension(10,10));
    closeLabel.addMouseListener(this);
    // Show textfield with number
    add(codeField,             cc.xywh(2,3,1,2));       // 2nd col 3rd row
 
    // Show copy button
    add(copyLabel,             cc.xy(4,3));       // 4th col 3rd row

    add(nextLabel,             cc.xy(4,4));       // 4th col 4th row

    // Show timer countdown
    add(progressLabel,         cc.xywh(2,6,3,1)); // 2nd col 6th row spans 3 cols

    add(closeLabel,            cc.xy(5,1));       //

    // Start the counter thread - fires an event every two seconds

    Counter cd = new Counter();
    cd.addActionListener(this);
    cd.start();
  }

  public void paintComponent(Graphics g) {
    Image tmpimage = image.getScaledInstance(this.getSize().width,this.getSize().height, Image.SCALE_DEFAULT);   
    g.drawImage(tmpimage, 0, 0, null);
    
  }


  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited (MouseEvent evt) { }
  public void mouseClicked(MouseEvent evt) {
    if (evt.getSource() == closeLabel) {
      System.exit(0);
    }
  }
  public void mouseDragged(MouseEvent evt) { }
  public void mousePressed(MouseEvent evt) { 
    // Copies the code to the clipboard when the copy label is clicked

    if (evt.getSource() == copyLabel) {

      String tmp = codeField.getText();

      tmp = tmp.substring(0,3) + tmp.substring(4);

      StringSelection ss = new StringSelection(tmp);

      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

      copyLabel.setForeground(this.darkred);

    } else if (evt.getSource() == nextLabel) {

      this.shownextcode = !this.shownextcode;

      if (this.shownextcode) {
	nextLabel.setForeground(this.darkred);
      } else {
	nextLabel.setForeground(Color.black);
      }
      this.currcode = null;
      
    }

  }      
  public void mouseMoved  (MouseEvent evt) { }
  public void mouseReleased(MouseEvent evt) { 

    try {

      Thread.currentThread().sleep(1000);

      copyLabel.setForeground(Color.black);

    } catch (InterruptedException ie){
      System.out.println("Thread interrupted");
    }
  }

  public String getNewCode() {
    try {
      if (this.shownextcode) {
	return pcg.generateNextTimeoutCode();
      } else {
	return pcg.generateTimeoutCode();
      }
    } catch (java.security.GeneralSecurityException ex) {
    }
    return "";
  }
   
  public String getCurrentCode() {
    return this.currcode;
  }
    
  public void actionPerformed(ActionEvent e){
      
    if (e.getSource() instanceof Counter) {

      try {
	String currcode = this.getCurrentCode();
	String newcode  = this.getNewCode();
	String tmp      = newcode;

	if (this.shownextcode) {

	  codeField.setForeground(this.darkred);
	  nextLabel.setForeground(this.darkred);
	} else {

	  codeField.setForeground(Color.black);
	  nextLabel.setForeground(Color.black);

	}


	String newcodestr  = tmp.substring(0,3) + " " + tmp.substring(3,6);

	int    remain  = (int)(System.currentTimeMillis()%30000/2000);

	if (currcode == null || !newcode.equals(currcode)){ 
	    
	  codeField.setText(newcodestr);
	  this.currcode = newcode;
	  //new Application().setDockIconBadge(tmp);
	  int i = 0;
	  String s = "";
	  while (i <= 15-remain) {
	    s += "-";
	    i++;
	  }
	  progressLabel.setText(s);


	}
      } catch (Exception ex) {
	ex.printStackTrace();
      }
      String val = progressLabel.getText();

      int len = val.length();
      len--;

      if (len <  0) {
	len = 15;
      };

      String s = "";
      int i = 0;
      while (i < len) {
	s += "-";
	i++;
      }
      progressLabel.setText(s);
    }
  }

  public static String getSecret(String[] args) {

    try {
      // Gets the secret from a number of places.
      
      String homedir    = System.getProperty("user.home");
      
      // Command line first
      
      if (args.length > 0  && args[0].indexOf("-secret=") == 0) {
	return args[0].substring(8);
      }
      
      if (args.length > 0) {
	String secretfile                  = args[0];
	
	byte[]               buffer = new byte[(int) new File(secretfile).length()];
	BufferedInputStream  f      = new BufferedInputStream(new FileInputStream(secretfile));

	f.read(buffer);
	return new String(buffer);
      }
      
      // Jar file next
      
      // Read the .JAuth.rc file
      if (new File(homedir + File.separator + ".JAuth.rc").exists()) {
	String secretfile = homedir + File.separator + ".JAuth.rc";

	FileInputStream fstream = new FileInputStream(secretfile);
	DataInputStream in      = new DataInputStream(fstream);
	BufferedReader  br      = new BufferedReader(new InputStreamReader(in));
	String          strLine;

	while ((strLine = br.readLine()) != null)   {
	  if (strLine.indexOf("secret=") == 0) {
	    return strLine.substring(7);
	  }
	}
	in.close();
      }
      
      if (new File(homedir + File.separator + ".google_authenticator").exists()) {
	
	String secretfile = homedir + File.separator + ".google_authenticator";

	byte[]              buffer = new byte[(int) new File(secretfile).length()];
	BufferedInputStream f      = new BufferedInputStream(new FileInputStream(secretfile));

	f.read(buffer);
	return new String(buffer);

      } 
      
      //JOptionPane.showMessageDialog(null, "Installer secret is " + secret);
      
      JOptionPane.showMessageDialog(null, "Error reading secret string.");
      System.exit(0);
      
    } catch (Exception e) {

      JOptionPane.showMessageDialog(null, "Error reading secret string.");
      e.printStackTrace();
      System.exit(0);

    }
    return "";
  }
  public static void main(String[] args) {
    String secret     = "";
    String secretfile = "";

    Image image = null;
    Font  font  = null;
    Image icon  = null;

    try {
      InputStream fontStream  = AuthenticatorGUI.class.getResourceAsStream("digital.ttf");
      InputStream imagestream = AuthenticatorGUI.class.getResourceAsStream("lcd3.png");
      InputStream iconstream  = AuthenticatorGUI.class.getResourceAsStream("icon.png");

      //secret  = (String)Variables.getInstallerVariable("secret");

      font        = Font.createFont( Font.TRUETYPE_FONT,fontStream ); 
      image       = ImageIO.read(imagestream);
      icon        = ImageIO.read(iconstream);

      secret      = AuthenticatorGUI.getSecret(args);

      JOptionPane.showMessageDialog(null, "Secret is " + secret);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Error reading secret string. This should be contained in [" + secretfile + "]", "JAuth Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
      System.exit(0);
    }
    try {
      Dimension          dim  = Toolkit.getDefaultToolkit().getScreenSize();
      AuthenticatorGUI   gui  = new AuthenticatorGUI(secret,image,font);
      AuthenticatorFrame jf   = new AuthenticatorFrame();
      
      gui.addMouseMotionListener(jf);
      gui.addMouseListener(jf);
      //new Application().setDockIconImage(image);

      jf.setIconImage(icon);
      jf.setUndecorated(true);
      jf.add(gui);
      jf.setDefaultCloseOperation(2);
      jf.pack();
      
      jf.setSize(175,60);
      jf.setLocation(dim.width  - jf.getSize().width -50,30);
      
      
      jf.setVisible(true);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Error creating GUI","JAuth Error", JOptionPane.ERROR_MESSAGE);
      System.exit(0);
    }
  }
  
}

class Counter extends Thread {
  public ActionListener l;
  public int    time = 0;

  public void addActionListener(ActionListener l) {
    this.l = l;
  }
  
  public void run() {
    while (true) {
      try {
	Thread.sleep(2000);
	time+= 2000;

	l.actionPerformed(new ActionEvent((Object)this,time,String.valueOf(time)));
      } catch (InterruptedException e) {
	e.printStackTrace();
      }
    }
  }
}

class AuthenticatorFrame extends JFrame implements MouseListener, MouseMotionListener {
  int positionx;
  int positiony;

  int x1;
  int y1;
  
  int x2;
  int y2;

  public void mouseDragged(MouseEvent evt) {
    this.positionx = evt.getXOnScreen();
    this.positiony = evt.getYOnScreen();
         
    if (this.positionx > this.x1){

      this.x2 = this.positionx - this.x1;
      this.setLocation(this.getX() + this.x2, this.getY());
      
    } else if(this.positionx < this.x1){

      this.x2 =  this.x1 - this.positionx;
      this.setLocation(this.getX() - this.x2, this.getY());

    }

    if (this.positiony > this.y1) {

      this.y2 = this.positiony - this.y1;
      this.setLocation(this.getX(), this.getY() + this.y2);

    } else if(this.positiony < this.y1) {

      this.y2 =  this.y1 - this.positiony;
      this.setLocation(this.getX(), this.getY() - this.y2);

    }

    this.x1 = this.positionx;
    this.y1 = this.positiony;

  }
  public void mousePressed(MouseEvent evt) { 
    this.x1 = evt.getXOnScreen();
    this.y1 = evt.getYOnScreen();
  }

  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited (MouseEvent evt) { }
  public void mouseClicked(MouseEvent evt) {
  }
  public void mouseReleased(MouseEvent evt) { }
  public void mouseMoved(MouseEvent evt) { }

}



