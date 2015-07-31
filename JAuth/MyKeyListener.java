package JAuth;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyKeyListener implements KeyListener{
	public AuthenticatorGUI aGui;
	
	public MyKeyListener(AuthenticatorGUI aGui) {
		this.aGui = aGui;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(aGui.firstFrame.isVisible()) {
				if(!aGui.newPass.getText().equals("") && !aGui.newPass.getText().equals(" ")) {
					aGui.password = aGui.newPass.getText();
					aGui.firstFrame.dispose();
					aGui.setVisible(true);
					aGui.edit();
	    	}
			}
			else if(aGui.editWindow.isVisible()) {
				aGui.save();
			    aGui.editWindow.dispose();
			}
			else if(aGui.frame.isVisible()) {
				  String passTry = aGui.pass.getText();
				  if(passTry.equals(aGui.password)) {
		    		aGui.checkPass = true;
					aGui.frame.dispose();
					aGui.edit();
		    	}  
			} 
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
