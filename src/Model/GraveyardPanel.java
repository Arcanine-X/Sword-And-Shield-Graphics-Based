package Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GraveyardPanel extends JPanel {

	private int WIDTH = 60;
	private int HEIGHT = 60;
	private static final int GAP = 8;
	private static final int STROKE = 3;
	int x = (this.getWidth()/2)-(8*60);
	int y = GAP;
	SwordAndShieldGame game;
	Player player;

	public GraveyardPanel(SwordAndShieldGame game, Player player) {
		this.game = game;
		this.player = player;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D _g = (Graphics2D) g;
		drawGrave(_g);
	}

	public void drawGrave(Graphics2D g) {
		WIDTH = Math.min(getWidth(), getHeight())/10 - Math.min(getWidth(), getHeight())/60;
		HEIGHT = Math.min(getWidth(), getHeight())/10 - Math.min(getWidth(), getHeight())/60;
		BoardPiece[][] graveYard = player.getGraveYard();
		for (int row = 0; row < graveYard.length; row++) {
			for (int col = 0; col < graveYard[0].length; col++) {
				if (graveYard[row][col] instanceof BoardPiece) {
					BoardPiece piece = (BoardPiece) graveYard[row][col];
					g.setColor(Color.BLACK);
					if (piece.getCol().equals("yellow")) {
						g.setColor(Color.YELLOW);
					} else {
						g.setColor(Color.GREEN);
					}
					g.fillOval(x, y, WIDTH, HEIGHT);
					g.setColor(Color.red);
					g.setStroke(new BasicStroke(6));
					drawToken(g, piece);
					g.setStroke(new BasicStroke(0));
				}
				else {
					g.setColor(Color.BLACK);
					g.drawRect(x, y, WIDTH, HEIGHT);
				}
				x += GAP;
				x += WIDTH;
			}
			x = GAP;
			y += GAP;
			y += HEIGHT;
		}
		y = GAP;
	}

	private void drawToken(Graphics2D g, BoardPiece piece) {
		if (piece.getNorth() == 1) {
			g.drawLine(x + WIDTH / 2, y + STROKE, x + WIDTH / 2, y + HEIGHT / 2);
		} else if (piece.getNorth() == 2) {
			g.drawLine(x + STROKE, y + STROKE, x + WIDTH - STROKE, y + STROKE);
		}

		if (piece.getEast() == 1) {
			g.drawLine(x + WIDTH / 2 + STROKE, y + HEIGHT / 2, x + WIDTH - STROKE, y + HEIGHT / 2);
		} else if (piece.getEast() == 2) {
			g.drawLine(x + WIDTH - STROKE, y + STROKE, x + WIDTH - STROKE, y + HEIGHT - STROKE);
		}

		if (piece.getSouth() == 1) {
			g.drawLine(x + WIDTH / 2, y + HEIGHT / 2, x + WIDTH / 2, y + HEIGHT - STROKE);
		} else if (piece.getSouth() == 2) {
			g.drawLine(x + STROKE, y + HEIGHT - STROKE, x + WIDTH - STROKE, y + HEIGHT - STROKE);
		}

		if (piece.getWest() == 1) {
			g.drawLine(x + STROKE, y + HEIGHT / 2, x + WIDTH / 2 - STROKE, y + HEIGHT / 2);
		} else if (piece.getWest() == 2) {
			g.drawLine(x + STROKE, y + STROKE, x + STROKE, y + HEIGHT - STROKE);
		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400,400);
	}

}
