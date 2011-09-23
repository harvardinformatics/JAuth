import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.util.regex.Pattern;
import java.util.zip.*;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.datatransfer.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.sql.*;

import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class AuthenticatorGUI extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

  public int lastx;
  public int lasty;

  public JLabel        codeField     = new JLabel("--- ---");
  public JLabel        copyLabel     = new JLabel(" Copy");
  public JLabel        progressLabel = new JLabel("");
  public Image         image;

  public String        secret;
  public Mac           mac;
  public PasscodeGenerator pcg;

  public AuthenticatorGUI(String secret) {
    try {

      // Do the magic with the secret key
      final byte[] keybytes = Base32String.decode(secret);

      mac = Mac.getInstance("HMACSHA1");
      mac.init(new SecretKeySpec(keybytes,""));

      pcg = new PasscodeGenerator(mac);

      // Read the background image
      image = ImageIO.read(new File("lcd3.png"));
      
    } catch (Exception e) {
      e.printStackTrace();
    }

    componentInit();
  }

  private void componentInit() {

    FormLayout layout = new FormLayout("20px,fill:pref:grow,4dlu,pref",  // Cols
				       "10px,40px,1px,10px,10px");       // Rows
    
    setLayout(layout);
    setBackground(Color.white);

    CellConstraints cc = new CellConstraints();

    try {
      Font javaFont = Font.createFont( Font.TRUETYPE_FONT,new File("digital.ttf"));

      codeField.setFont(javaFont.deriveFont(28f));
      copyLabel.setFont(javaFont.deriveFont(16f));
      progressLabel.setFont(javaFont.deriveFont(16f));
    } catch (Exception e) {
      e.printStackTrace();
    }

    codeField.setPreferredSize(new Dimension(100,30));
    copyLabel.setPreferredSize(new Dimension(60,30));
    copyLabel.addMouseListener(this);


    // Show textfield with number
    add(codeField,             cc.xy(2,2));    // 2nd col 2nd row
 
    // Show copy button
    add(copyLabel,            cc.xy(4,2));     // 4th col 2nd row

    // Show timer countdown
    add(progressLabel,         cc.xywh(2,4,3,1)); // 2nd col 4th row spans 3 cols


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
  public void mouseClicked(MouseEvent evt) { }
  public void mouseDragged(MouseEvent evt) {}
  public void mousePressed(MouseEvent evt) { }
  public void mouseMoved(MouseEvent evt) { }
  
  public void mouseReleased(MouseEvent evt) {

    if (evt.getSource() == copyLabel) {
      String tmp = codeField.getText();

      tmp = tmp.substring(0,3) + tmp.substring(4);

      StringSelection ss = new StringSelection(tmp);

      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

    }

  }

  public void actionPerformed(ActionEvent e){
    
    if (e.getSource() instanceof Counter) {

      try {
	String prevcode = codeField.getText();
	String tmp      = pcg.generateTimeoutCode();

	String newcode = tmp.substring(0,3) + " " + tmp.substring(3,6);
	int    remain  = (int)(System.currentTimeMillis()%30000/2000);

	if (!newcode.equals(prevcode)){ 
	  codeField.setText(newcode);
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
  public static void main(String[] args) {
    Dimension dim  = Toolkit.getDefaultToolkit().getScreenSize();
    AuthenticatorGUI gui = new AuthenticatorGUI("POGWOG");
    
    AuthenticatorFrame    jf   = new AuthenticatorFrame();
    gui.addMouseMotionListener(jf);
    gui.addMouseListener(jf);
    jf.setUndecorated(true);
    jf.add(gui);
    jf.setDefaultCloseOperation(2);
    jf.pack();
    
    jf.setLocation(dim.width  / 2 - jf.getSize().width / 2,
		   dim.height / 2 - jf.getSize().height / 2);
    
    jf.setVisible(true);
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
  public void mouseClicked(MouseEvent evt) { }
  public void mouseReleased(MouseEvent evt) { }
  public void mouseMoved(MouseEvent evt) { }

}



