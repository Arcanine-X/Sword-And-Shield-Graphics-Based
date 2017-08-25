package Model;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class ButtonPanel extends JPanel{
	SwordAndShieldGame game;
	public ButtonPanel(SwordAndShieldGame game) {
		this.game = game;

	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(700,100);
	}



}
