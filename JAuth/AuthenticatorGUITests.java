package JAuth;

import static org.junit.Assert.*;

import java.awt.Font;
import java.awt.Image;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.junit.*;


public class AuthenticatorGUITests {
	
	public Image image = null;
    public Font  font  = null;
    
    @Before
    public void makeAGui() {
    	 try {
    	      InputStream fontStream  = AuthenticatorGUI.class.getResourceAsStream("fonts/digital.ttf");
    	      InputStream imagestream = AuthenticatorGUI.class.getResourceAsStream("logo/lcd3.png");

    	      //secret  = (String)Variables.getInstallerVariable("secret");

    	      font        = Font.createFont( Font.TRUETYPE_FONT,fontStream ); 
    	      image       = ImageIO.read(imagestream);


    	    } catch (Exception e) {
    	      e.printStackTrace();
    	      System.exit(0);
    	    }
    }
	
	@Test
	public void providersIsEqualToSecrets() {
   	 	AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		assertEquals("Unequal number of secrets and providers", aGui.secrets.size(),aGui.providers.size());
	}
	@Test
	public void editWindowIsCorrectSize() {
   	 	AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit(); 
		assertEquals("Incorect number of Frames in table", aGui.table.getContentPane().getComponents().length, (aGui.rows+1)*2);
	}
	@Test
	public void editCreatesCorrectSizeBoxesArray() {
   	 	AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit();
		assertEquals("Incorect number of boxes", aGui.boxes.size(),aGui.rows*2-2);
	}
	@Test
	public void addRowIncreasesRows() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit();
		int tempRows = aGui.rows;
		aGui.addRow();
		assertEquals("Rows did not increase correctly", aGui.rows, tempRows+1);
	}
	@Test
	public void addRowIncreasesTableSize() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit();
		int tempTable = aGui.table.getContentPane().getComponents().length;
		aGui.addRow();
		assertEquals("Table did not add row", tempTable +2, aGui.table.getContentPane().getComponents().length);
	}
	@Test
	public void addRowIncreasesBoxesSize() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit();
		int tempBoxes = aGui.boxes.size();
		aGui.addRow();
		assertEquals("Boxes did not add row", tempBoxes +2, aGui.boxes.size());
	}
	@Test
	public void deleteRowDecreasesRows() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit();
		int tempRows = aGui.rows;
		aGui.deleteRow();
		assertEquals("Rows did not decrease correctly", tempRows -1, aGui.rows);
	}
	@Test
	public void delteRowDecreasesTableSize() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit();
		int tempTable = aGui.table.getContentPane().getComponents().length;
		aGui.deleteRow();
		assertEquals("Table did not delete row", tempTable -2, aGui.table.getContentPane().getComponents().length);
	}
	@Test
	public void deleteRowDecreasesBoxesSize() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.edit();
		int tempBoxes = aGui.boxes.size();
		aGui.deleteRow();
		assertEquals("Boxes did not delete row", tempBoxes -2, aGui.boxes.size());
	}
	@Test
	public void updateNameUpdatesNameButton() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		for(int i = 0; i < 2; i ++) {
			aGui.providers.add("Hi"+i);
		}
		for(int i = 2; i < 4; i++) {
			aGui.secrets.add("Hi"+i);
		}
		aGui.updateName(0);
		String tempName = aGui.providers.get(aGui.placeInList +1);
		aGui.updateName(1);
		assertEquals("NameButton did not update properly", tempName, aGui.nameButton.getText().trim());
	}
	@Test 
	public void editPasswordCheckOpensPasswordWindow() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.editPasswordCheck();
		assertTrue("Window not opened", aGui.frame.isVisible());
	}
	@Test
	public void editPasswordCheckAddsAllComponentsToFrame() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.editPasswordCheck();
		assertEquals("Incorrect number of components in window", aGui.frame.getContentPane().getComponents().length, 3);
	}
	@Test
	public void arrayFillFillsProperly() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.arrayFill("2+1234+RCFAS*One+");
		assertEquals("Wrong number of rows", aGui.rows,2);
		assertEquals("Wrong password", aGui.password, "1234");
		assertEquals("Wrong provider", aGui.providers.get(0).trim(),"RCFAS");
		assertEquals("Wrong secret", aGui.secrets.get(0).trim(), "One");
	}
	@Test
	public void setPasswordCreatesCorrectComponents() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image, font);
		aGui.setPassword();
		assertEquals("Incorect number of components", aGui.firstFrame.getContentPane().getComponents().length,3);
	}
	@Test
	public void editWindowDisplaysInCorrectOrder() {
		AuthenticatorGUI aGui = new AuthenticatorGUI(image,font);
		aGui.edit();
		for(int i = 0; i < (aGui.rows-1)*2; i++) {
			if(i%2 != 1 || i == 0) {
				assertEquals("Incorect provider", aGui.boxes.get(i).getText(),aGui.providers.get(i/2));
			}
			else {
				assertEquals("Incorect secret", aGui.boxes.get(i).getText(),aGui.secrets.get(i/2));
			}
		}
	}
	

}
