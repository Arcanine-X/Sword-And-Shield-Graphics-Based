package Model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class ClearPanel extends JPanel{

	SwordAndShieldGame game;
	GameFrame run;

	public ClearPanel(SwordAndShieldGame game, GameFrame run) {
		this.game = game;
		this.run = run;
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D _g = (Graphics2D)g;
		_g.setColor(Color.orange);
		g.fillRect(300, 400, 600, 600);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(run.getWidth(), run.getHeight());
	}



}
