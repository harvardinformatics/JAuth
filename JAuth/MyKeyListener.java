package JAuth;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.JTextComponent;

public class MyKeyListener implements KeyListener {
	public AuthenticatorGUI aGui;
	private final Set<Character> pressed = new HashSet<Character>();

	public MyKeyListener(AuthenticatorGUI aGui) {
		this.aGui = aGui;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		pressed.add(e.getKeyChar());
		if (aGui.editWindow.isVisible()) {
			if (pressed.contains('c') && pressed.contains('￿')) {
				StringSelection ss = new StringSelection(
						((JTextComponent) aGui.editWindow.getFocusOwner()).getSelectedText());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			}
			if (pressed.contains('c') && pressed.contains('￿')) {
				StringSelection ss = new StringSelection(
						((JTextComponent) aGui.editWindow.getFocusOwner()).getSelectedText());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			}
			if (pressed.contains('v') && pressed.contains('￿')) {
				try {
					((JTextComponent) aGui.editWindow.getFocusOwner()).setText(
							(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (aGui.firstFrame.isVisible()) {
				if (!aGui.newPass.getText().equals("") && !aGui.newPass.getText().equals(" ")) {
					aGui.password = aGui.newPass.getText();
					aGui.firstFrame.dispose();
					aGui.setVisible(true);
					aGui.showEditWindow();
				}
			} else if (aGui.editWindow.isVisible()) {
				aGui.save();
				aGui.editWindow.dispose();
			} else if (aGui.frame.isVisible()) {
				String passTry = aGui.pass.getText();
				if (passTry.equals(aGui.password)) {
					aGui.checkPass = true;
					aGui.frame.dispose();
					aGui.showEditWindow();
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressed.remove(e.getKeyChar());
	}

}
