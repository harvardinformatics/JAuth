package JAuth;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultStyledDocument;

import java.awt.datatransfer.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.imageio.ImageIO;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.lang.reflect.*;

import javax.swing.text.DocumentFilter;
//import com.portal.app.comp.DocumentSizeFilter;


//import com.install4j.api.launcher.Variables;

//import com.apple.eawt.*;

public final class AuthenticatorGUI extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

  public JLabel            codeField     = new JLabel("--- ---");
  public JLabel            copyLabel     = new JLabel(" Copy");
  public JLabel            nextLabel     = new JLabel(" next");
  public JLabel            progressLabel = new JLabel("");
  public JLabel            closeLabel    = new JLabel("x");
  public JLabel		   	   nameButton    = new JLabel("");
  public JButton		   editButton 	 = new JButton("Edit");
  public JButton 		   enterButton	 = new JButton("Enter");
  public JPasswordField	   pass			 = new JPasswordField(4);
  public JFrame			   frame		 = new JFrame();
  public JButton		   addButton	 = new JButton("+");
  public JButton 		   minusButton   = new JButton("-");
  public JButton 		   saveButton	 = new JButton("Save");
  public JFrame			   table		 = new JFrame("Edit Providers");
  public JPasswordField	   newPass 		 = new JPasswordField(4);
  public JButton		   enterButton2  = new JButton("Enter");
  public JFrame 		   firstFrame	 = new JFrame();
  public ArrayList<JTextField> boxes 		 = new ArrayList(0);
  public JLabel 		   nextButton    = new JLabel(">");
 
  public Image             image;

  public InputStream       fontStream;

  public String            secret;
  public String			   password = "";
  public boolean		   checkPass;
  public int 			   rows;
  
  public ArrayList<String> secrets = new ArrayList(0);
  public ArrayList<String> providers = new ArrayList(0);
  public int			   placeInList = 0;
  
  public Mac               mac;
  public PasscodeGenerator pcg;
  public Font              font;

  public boolean           shownextcode = false;

  public String            currcode;
  public String            nextcode;

  public Color             darkred = new Color(150,0,0);
  
  private byte[]		   iv = {(byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, };	
  private KeyStore 		   keyStore;
  private PasswordProtection keyPassword = new PasswordProtection("pw-secret".toCharArray());;

  public AuthenticatorGUI() {
	  rows = 1;
	  saveEncrypt();
  }
  public AuthenticatorGUI(Image image,Font font) {
		try {
			saveDecrypt();
			setSecret(secrets.get(placeInList));
			if (password == null) {
				setPassword();
				this.setVisible(false);
			}
			this.font = font;
			this.image = image;

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setSecret(secrets.get(placeInList));
		if (providers.size() != 0 || !providers.get(placeInList).trim().equals("")) {
			nameButton.setText("  " + providers.get(placeInList)); // update provider
		} else {
			nameButton.setText("  Edit Secrets");
		} // displays current provider
		if (nameButton.getText().trim().equals("")) {
			nameButton.setText("Edit Secrets");
		}
		componentInit();
	}
   

  public void setSecret(String secret) {
    try {
      this.secret = secret;
      if(!secret.trim().equals("")){
    	  // Do the magic with the secret key
    	  final byte[] keybytes = Base32String.decode(secret);
    	  
    	  mac = Mac.getInstance("HMACSHA1");
    	  mac.init(new SecretKeySpec(keybytes,""));
      
    	  pcg = new PasscodeGenerator(mac);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  public void setPassword() {
	  FormLayout layout = new FormLayout("fill:pref:grow","fill:pref:grow,fill:pref:grow,fill:pref:grow");
	  firstFrame.setLayout(layout);
	  JLabel title = new JLabel("Create Password");
	  CellConstraints cc = new CellConstraints();
	  firstFrame.add(title, cc.xy(1,1));
	  firstFrame.add(newPass, cc.xy(1,2));
	  firstFrame.add(enterButton2, cc.xy(1,3));
	  enterButton2.addMouseListener(this);
	  
	  Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	  int w = firstFrame.getSize().width;
      int h = firstFrame.getSize().height;
      int x = (dim.width-w);
      int y = (0);
      firstFrame.setLocation(x-250,y);
      
      firstFrame.setSize(200,100);
      firstFrame.setMaximumSize(new Dimension(200,100));
      firstFrame.setMinimumSize(new Dimension(200,100));
      firstFrame.setAlwaysOnTop(true);
      firstFrame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
	  firstFrame.setVisible(true);
  }
  
  public void edit() {
	  rows = providers.size() +1;
	  GridLayout layout = new GridLayout(0,2);
	  
	  table.getContentPane().removeAll(); //reset table 
	  saveButton.removeMouseListener(this);
	  addButton.removeMouseListener(this);
	  minusButton.removeMouseListener(this);
	  
	  table.setLayout(layout);
	  
	  JLabel providerLabel = new JLabel("             Providers");
	  JLabel secretLabel = new JLabel("             Secrets");
	  table.add(providerLabel);
	  table.add(secretLabel);
	  int temp = rows;
	  for(int i = 0; i < rows-1; i++) {
		  DefaultStyledDocument doc = new DefaultStyledDocument();
	      doc.setDocumentFilter(new DocumentSizeFilter(23));
		  JTextField name = new JTextField(providers.get(i));
		  JTextField secret = new JTextField(secrets.get(i));
		  if(!name.getText().equals("")) {
			  table.add(name);
			  table.add(secret);
			  boxes.add(i*2,name);
			  boxes.add(i*2+1,secret);
			  name.setDocument(doc);
			  name.setText(providers.get(i));
		  }
		  else {
			  temp--;
		  }	
	  }
	  rows = temp;
	  //removes duplicates from boxes array
	  while(boxes.size() > (rows-1)*2){
		  boxes.remove(boxes.size()-1);
	  }
		
		  
	  JPanel bottom = new JPanel();
	  bottom.setLayout(new FlowLayout(FlowLayout.CENTER));
	  
	    
	  bottom.add(addButton);
	  addButton.addMouseListener(this);
	  bottom.add(minusButton);
	  minusButton.addMouseListener(this);
	  table.add(bottom);
	  table.add(saveButton);
	  saveButton.addMouseListener(this);
	  
	  Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	  int w = frame.getSize().width;
      int h = frame.getSize().height;
      int x = (dim.width-w)/2;
      int y = (dim.height-h)/2;
      
      if(rows == 1) {
    	  table.setSize(320,90);
      }
      else {
    	  
      }
      table.setSize(320,50*(rows+1));
      table.setMaximumSize(new Dimension(320,50*(rows+1)));
      table.setMinimumSize(new Dimension(320,50*(rows+1)));
      table.setLocation(x,y);
	  table.setVisible(true);
	  
	  
	  
  } 
  public void addRow() {
	  for(int i = 0; i < 2; i++) {
		  JTextField empty = new JTextField("");
		  table.add(empty,(rows*2));
		  boxes.add((rows-1)*2,empty);
		  if(i != 0) {
			  DefaultStyledDocument doc = new DefaultStyledDocument();
		      doc.setDocumentFilter(new DocumentSizeFilter(23));
		      empty.setDocument(doc);
		  }
	  }
	  rows++;
	  if(rows == 1) {
	      table.setMaximumSize(new Dimension(320,90));
	      table.setMinimumSize(new Dimension(320,90));
    	  table.setSize(320,90);
      }
      else {
          table.setMaximumSize(new Dimension(320,50*(rows+1)));
          table.setMinimumSize(new Dimension(320,50*(rows+1)));
    	  table.setSize(320,50*(rows+1));
      }
	  table.repaint();
  }
  public void deleteRow() {
	  if(rows != 1)
	  {
		  table.remove(boxes.remove(boxes.size()-1));
		  table.remove(boxes.remove(boxes.size()-1));
		  rows--;
		  if(rows == 1) {
		      table.setMaximumSize(new Dimension(320,90));
		      table.setMinimumSize(new Dimension(320,90));
	    	  table.setSize(320,90);
	      }
	      else {
	          table.setMaximumSize(new Dimension(320,50*(rows+1)));
	          table.setMinimumSize(new Dimension(320,50*(rows+1)));
	    	  table.setSize(320,50*(rows+1));
	      }
	  }
	  while(providers.size() > rows-1 && secrets.size() > rows-1) {
	      providers.remove(providers.size()-1);
		  secrets.remove(secrets.size()-1);
	  }		  
	  table.repaint();
	  if(placeInList > providers.size()) {
		  placeInList = 0;
	  }
  }
  public void save() {
	 for(int i = 0; i < (rows-1)*2; i++) {
		 if(i%2 != 1 || i == 0) {
			 if(providers.size() <= i/2) {
				 if(boxes.get(i).getText().equals("")) {
					 providers.add("");
				 } else {
					 providers.add(boxes.get(i).getText());
				 }
			 } else {
				 if(boxes.get(i).getText().equals(null)) {
					 providers.set(i/2,"");
				 } else {
					 providers.set(i/2,boxes.get(i).getText());
				 }
			 }
		 }
		 if(secrets.size() <= i/2) {
			 secrets.add(boxes.get(i).getText());
		 } else {
			 secrets.set(i/2, boxes.get(i).getText());
		 }
	 }
	 for(int i = 0; i < rows-1; i++) {
		 if(providers.get(i).trim().equals("")) {
			 providers.remove(i);
			 secrets.remove(i);
			 boxes.remove(i*2);
			 boxes.remove(i*2);
			 rows--;
		 }
	 }
	 updateName(0);
	 saveEncrypt();
  }
 //updates nameButton and codeFeild
  public void updateName(int x) {
	if(placeInList < secrets.size()-1) {
  		placeInList+=x;
  	}
  	else { 
  		if(x == 1) {
  			placeInList = 0;
  		}
  	}
  	if(providers.size() == 0 || providers.get(placeInList).trim().equals("")){  
  		nameButton.setText("  Add Secrets");
  	}
  	else { 
  		nameButton.setText("  " + providers.get(placeInList)); //update provider
  	}
  	if(secrets.size() == 0 || secrets.get(placeInList).trim().equals("")) {
  		this.setSecret("");
  		codeField.setText("--- ---");
  	}
  	else {    		
  		this.setSecret(secrets.get(placeInList)); //update secret respectively
  	}
  }
  //opens a window to enter password 
  public void editPasswordCheck() {

	  if(!table.isVisible() && !frame.isVisible()) {
		  frame.getContentPane().removeAll();
		  pass.setText("");
		  frame.setTitle("Enter PIN");
		  JLabel title = new JLabel("      Enter PIN");
		  title.setHorizontalTextPosition(SwingConstants.CENTER);
		  enterButton.addMouseListener(this);
		  frame.setLayout(new FormLayout("40px,115px,40px","fill:pref:grow,fill:pref:grow,fill:pref:grow"));
		  CellConstraints cc = new CellConstraints();
		  frame.setSize(new Dimension(190,100));
//		  pass.
		  title.setPreferredSize(new Dimension(100,20));
		  frame.add(title, cc.xy(2,1));
		  frame.add(pass, cc.xy(2,2));
		  frame.add(enterButton, cc.xy(2,3));
	  
		  Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		  int w = frame.getSize().width;
		  int h = frame.getSize().height;
		  int x = (dim.width-w)/2;
		  int y = (dim.height-h)/2;
      
		  frame.setLocation(x,y);
		  frame.setVisible(true);
	  }
  }

  
  //fills the arrays with the stored values of providers and secrets
  public void arrayFill(String decrypted) {

	  ArrayList<String> lines = new ArrayList(0);

	  rows = Integer.parseInt(decrypted.substring(0,decrypted.indexOf("+")));
	  decrypted = decrypted.substring(decrypted.indexOf("+")+1);
	  for(int i = 0; i < rows; i++) {
		  lines.add(decrypted.substring(0,decrypted.indexOf("+")));
		  decrypted = decrypted.substring(decrypted.indexOf("+")+1);
	  }

	  if(lines.size() != 0) {
		  password = lines.get(0);
	  }
	  if(password != null && password.equals("")) {
		  setPassword();
	  }
	  for(int i = 1; i < lines.size(); i++) {
		  providers.add(lines.get(i).substring(0, lines.get(i).indexOf("*")));
		  secrets.add(lines.get(i).substring(lines.get(i).indexOf("*")+1));
	  }
	  if(providers.size() == 0) {
		  providers.add(" ");
		  secrets.add(" ");
	  }
  }

  private void componentInit() {

    FormLayout layout = new FormLayout("17px,fill:pref:grow,1dlu,40px,10px",  // Cols
				       "10px,2px,14px,14px,2px,5px,17px,10px,10px");      // Rows
    
    
    setLayout(layout);
    setBackground(Color.white);

    CellConstraints cc = new CellConstraints();

    try {

      // Load the LCD font

      Font font32 = this.font.deriveFont(32f);
      Font font16 = this.font.deriveFont(16f);
      Font font20 = this.font.deriveFont(20f);
      Font font13 = this.font.deriveFont(13f);
      Font font12 = new Font("Monospace",Font.BOLD,8);
      Font font11 = new Font("Monospace", Font.BOLD,8);

      codeField.setFont(font32);
      copyLabel.setFont(font13);
      nextLabel.setFont(font13);
      nameButton.setFont(font11);
      editButton.setFont(font11);
      nextButton.setFont(font11);
      progressLabel.setFont(font20);
      closeLabel.setFont(font12);

      Color c = new Color(150,150,150);
      closeLabel.setForeground(c);
      nameButton.setForeground(c);
      nameButton.setBackground(Color.BLACK);
      editButton.setForeground(c);
      editButton.setBackground(Color.BLACK);
      editButton.setBorder(null);
      nextButton.setForeground(c);
      //closeLabel.setBorder(BorderFactory.createLineBorder(Color.red));


    } catch (Exception e) {
      e.printStackTrace();
    }
    
    codeField.setPreferredSize(new Dimension(100,30));
    copyLabel.setPreferredSize(new Dimension(60,30));
    nextLabel.setPreferredSize(new Dimension(60,30));
    editButton.setPreferredSize(new Dimension(10,10));
    nameButton.setPreferredSize(new Dimension(10,10));
    copyLabel.addMouseListener(this);
    nextLabel.addMouseListener(this);
    editButton.addMouseListener(this);
    nextButton.addMouseListener(this);
    
    nameButton.setAlignmentX(LEFT_ALIGNMENT);

    closeLabel.setPreferredSize(new Dimension(10,10));
    closeLabel.addMouseListener(this);
    // Show textfield with number
    add(codeField,             cc.xywh(2,3,1,2));       // 2nd col 3rd row
 
    // Show copy button, next button, name button, edit button
    add(copyLabel,             cc.xy(4,3));       // 4th col 3rd row

    add(nextLabel,             cc.xy(4,4));       // 4th col 4th row
    
    add(nameButton,		   	   cc.xywh(1,1,2,1));		  // 2st col 1st row
    
    add(editButton,			   cc.xy(4,7));		  // 4th col 1st row

    // Show timer countdown
    add(progressLabel,         cc.xywh(2,6,3,1)); // 2nd col 6th row spans 3 cols

    add(closeLabel,            cc.xy(5,1));       //
    
    add(nextButton,			   cc.xy(4,1));
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
        saveEncrypt();
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
      
    } else if (evt.getSource() == editButton) {
    //allows access to secrets/providers lists if password is entered
    	editPasswordCheck();
    	if(checkPass) {
    		checkPass = false;
    	}
    } else if (evt.getSource() == nextButton) {
    //cycles through providers
    	updateName(1);
    } else if (evt.getSource() == enterButton) {
    	String passTry = pass.getText();
    	if(passTry.equals(password)) {
    		checkPass = true;
			frame.dispose();
			edit();
    	}
    } else if (evt.getSource() == addButton) {
    	addRow();
    } else if (evt.getSource() == saveButton) {
    	save();
    	table.dispose();
    } else if (evt.getSource() == enterButton2) {
    	password = newPass.getText();
    	firstFrame.dispose();
    	this.setVisible(true);
    } else if (evt.getSource() == minusButton) {
    	deleteRow();
    }

  }      
  public void mouseMoved  (MouseEvent evt) { }
  public void mouseReleased(MouseEvent evt) { 

    try {

      if (evt.getSource() == copyLabel) {
	Thread.currentThread().sleep(1000);
	
	copyLabel.setForeground(Color.black);
      }
    } catch (InterruptedException ie){
      System.out.println("Thread interrupted");
    }
  }

  
  public String getNewCode() {
    if(!secret.trim().equals("")) {
    	try {
    		if (this.shownextcode) {
    			return pcg.generateNextTimeoutCode();
    		} else {
    			return pcg.generateTimeoutCode();
    		}
    	} catch (java.security.GeneralSecurityException ex) {
    	}
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
	if(secret.trim().equals("")) {
		tmp = "------";
	}
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

  public static String getSecret(String[] args,Icon icon) {

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

      String secret = (String)JOptionPane.showInputDialog(
							  null,
							  "Enter secret key: ",
							  "Enter secret key",
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  null,
							  "");
 
      String secretfile = homedir + File.separator + ".JAuth.rc";

      try {
	BufferedWriter f = new BufferedWriter(new FileWriter(new File(secretfile)));
	f.write("secret="+secret);
	f.newLine();
	f.close();
	return secret;
      } catch (IOException e) {
	JOptionPane.showMessageDialog(null, "Error writing secret string to [" + secretfile + "]", "JAuth Error", JOptionPane.ERROR_MESSAGE);
	e.printStackTrace();
	System.exit(0);
      }
      
    } catch (Exception e) {

      JOptionPane.showMessageDialog(null, "Error reading secret string.");
      e.printStackTrace();
      System.exit(0);

    }
    return "";
  }
  
  public void fileChange() {
	  try {
		  FileInputStream fis = new FileInputStream("JAuth_Save");
		  BufferedInputStream bis = new BufferedInputStream(fis);
		  
		  byte[] save = new byte[bis.available()];
		  
		  for(int i = 0; i < save.length; i++) {
			  save[i] = (byte) bis.read();
		  }
		  bis.close();
		  
		  FileWriter fw = new FileWriter("JAuth_Save");
		  BufferedWriter bw = new BufferedWriter(fw);
		  
		  String s = new String(save);
		  rows = Integer.parseInt(s.substring(0,s.indexOf("+")));
		  String[] lines = new String[rows+1];
		  for(int i = 0; i < rows+1; i++) {
			  lines[i] = s.substring(0,s.indexOf("+"));
			  s = s.substring(s.indexOf("+")+1);
		  }
		  
		  for(String line: lines) {
			  bw.write(line);
			  bw.newLine();
		  }
		bw.close();  
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
  private void saveWriter() {
	  
	  try {
	    BufferedWriter bf = new BufferedWriter(new FileWriter(new File("JAuth_Save")));
		bf.write(rows+"");
		bf.newLine();
		if(password != null) {
			bf.write(password);
		}
		bf.newLine();
		
		for(int i = 0; i < providers.size(); i++) {
			bf.write(providers.get(i)+"*");
			bf.write(secrets.get(i).trim());
			bf.newLine();
		}
		bf.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
  private String saveReader() {
//	  try {
//		FileReader fr = new FileReader("JAuth_Save");
//		BufferedReader br = new BufferedReader(fr);
//		String read = "";
//		String line = "";
//		while((line = br.readLine()) != null) {
//			read += line + "+";
//		}
//		return read;
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	  return "";
	  String toReturn = "" + rows + "+" + password + "+";
	  for(int i = 0; i < rows -1; i++) {
		  toReturn += providers.get(i) + "*";
		  toReturn += secrets.get(i) + "+";
	  }
	  return toReturn;
  }
  
  private KeyStore createKeyStore(String fileName, String pw) throws Exception {
	  File file = new File(fileName);
	  
	    final KeyStore keyStore = KeyStore.getInstance("JCEKS");
	    if (file.exists()) {
	        keyStore.load(new FileInputStream(file), pw.toCharArray());
	    } else {
	        keyStore.load(null, null);
	        keyStore.store(new FileOutputStream(fileName), pw.toCharArray());
	    }
	 
	    return keyStore;
  }
  public void saveEncrypt() {
	  try{ 
		  KeyGenerator kg = KeyGenerator.getInstance("DESede");
		  SecretKey desKey = kg.generateKey();
		  
		  String ksFile = "JAuth_KS";
		  KeyStore ks = createKeyStore(ksFile, "javaci123");
		  KeyStore.SecretKeyEntry kse = new KeyStore.SecretKeyEntry(desKey);
		  ks.setEntry("MySecretKey", kse, keyPassword);
		  ks.store(new FileOutputStream(ksFile), "javaci123".toCharArray());
		  
		  Cipher desCipher;
          desCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
         
          byte[] text = saveReader().getBytes();
          
          desCipher.init(Cipher.ENCRYPT_MODE, desKey, new IvParameterSpec(iv));
          byte[] textEncrypted = desCipher.doFinal(text);
          
          FileOutputStream fos = new FileOutputStream("JAuth_Save");
          for(int i = 0; i < textEncrypted.length; i++) {
        	  fos.write(textEncrypted[i]);
          }
		  
	  } catch(Exception e) {
		  e.printStackTrace();
	  }
  }
  private void saveDecrypt() {
	try {
		
		FileInputStream fis = new FileInputStream("JAuth_Save");
		byte[] s = new byte[fis.available()];
		for(int i = 0; i < s.length; i++) {
			s[i] = (byte) fis.read();
		}
		fis.close();
		

		String ksFile = "JAuth_KS";
		KeyStore keyStore = createKeyStore(ksFile, "javaci123");
		KeyStore.Entry entry = keyStore.getEntry(keyStore.aliases().nextElement(), keyPassword);
	    SecretKey myDesKey = ((KeyStore.SecretKeyEntry) entry).getSecretKey();

        Cipher desCipher;
        desCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        
        desCipher.init(Cipher.DECRYPT_MODE, myDesKey, new IvParameterSpec(iv));
        byte[] textDecrypted = desCipher.doFinal(s);

        String toSave = new String(textDecrypted);
        arrayFill(toSave);
        
	}
	catch(Exception e) {
		e.printStackTrace();
	}
  }
 public static void main(String[] args) {
	String initPass   = "";
    String secretfile = "";

    Image image = null;
    Font  font  = null;
    Image icon  = null;

    try {
      InputStream fontStream  = AuthenticatorGUI.class.getResourceAsStream("fonts/digital.ttf");
      InputStream imagestream = AuthenticatorGUI.class.getResourceAsStream("logo/lcd3.png");
      InputStream iconstream  = AuthenticatorGUI.class.getResourceAsStream("logo/logo48.png");

      //secret  = (String)Variables.getInstallerVariable("secret");

      font        = Font.createFont( Font.TRUETYPE_FONT,fontStream ); 
      image       = ImageIO.read(imagestream);
      icon        = ImageIO.read(iconstream);

      //initPass      = AuthenticatorGUI.getPassword();

      //JOptionPane.showMessageDialog(null, "Secret is " + secret);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Error reading secret string. This should be contained in [" + secretfile + "]", "JAuth Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
      System.exit(0);
    }
    
    try {

      /*
      if (Class.forName("com.apple.eawt.Application",false,null)!= null) {
	com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
	app.setDockIconImage(icon);
	app.setAboutHandler(new JAuthAboutHandler(icon));
	}*/
      Dimension          dim  = Toolkit.getDefaultToolkit().getScreenSize();
      AuthenticatorGUI gui  = new AuthenticatorGUI(image,font);
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
  

/*
class JAuthAboutHandler implements com.apple.eawt.AboutHandler {
  Image     icon;
  ImageIcon imageicon;

  public JAuthAboutHandler(Image icon) {
    this.icon = icon;
    this.imageicon = new ImageIcon(this.icon);
  }

  public void handleAbout(com.apple.eawt.AppEvent.AboutEvent e) {
    String version  = AuthenticatorGUI.class.getPackage().getImplementationVersion();
    String title    = AuthenticatorGUI.class.getPackage().getImplementationTitle();
    String aboutGreeting = "JAuth OpenAuth desktop client version "+version;
    JOptionPane.showMessageDialog(null,aboutGreeting,"JAuth",JOptionPane.INFORMATION_MESSAGE,imageicon);
  }


}
*/
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




