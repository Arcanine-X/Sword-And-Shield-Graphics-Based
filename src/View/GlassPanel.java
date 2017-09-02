package View;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import Model.BoardPiece;
import Model.SwordAndShieldGame;

/**
 * This class is used to create a glass panel over the existing game frame. It allows for a smooth animation
 * over each panel, and split panel dividers to create the effect of the chosen token flying to creation square.
 * @author Chin Patel
 *
 */
public class GlassPanel extends JPanel {
	private SwordAndShieldGame game;
	private GameFrame run;
	private TokenPanel tokenPanelY;
	private TokenPanel tokenPanelG;
	private BoardPanel boardPanel;
	private JPanel buttonPanel;
	private static final int DIVIDER = 10; // split panel divider width
	private static final int STROKE = 3; // new BasicStroke() / 2
	private int WIDTH = 60;
	private int HEIGHT = 60;
	private int destinationX, destinationY; //destination coordinates of the creation grid
	private int x, y;  // original x and y coordinates of the board piece
	private int drawX, drawY;  // coordinates of the board piece while being animated
	private boolean doOnce = false;

	public GlassPanel(SwordAndShieldGame game, GameFrame run, JPanel buttonPanel, BoardPanel boardPanel,
			TokenPanel tokenPanelY, TokenPanel tokenPanelG) {
		this.game = game;
		this.run = run;
		this.tokenPanelY = tokenPanelY;
		this.tokenPanelG = tokenPanelG;
		this.boardPanel = boardPanel;
		this.buttonPanel = buttonPanel;
		this.setOpaque(false);
		this.setBounds(0, 0, run.returnWidth(), 1000);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (run.getCurrentPlayer().getName().equals("yellow")) {
			if (tokenPanelY.isAnimateAcross() && run.getCurrentPlayer().getName().equals("yellow")) {
				if (!doOnce) {
					x = tokenPanelG.getWidth() + DIVIDER + boardPanel.getWidth() + DIVIDER + 8
							+ (tokenPanelY.getToAnimateAcrossRotation() / 90) * tokenPanelY.getWIDTH() + (tokenPanelY.getToAnimateAcrossRotation() / 90) * 8;
					y = buttonPanel.getHeight() + 8;
					destinationX = tokenPanelG.getWidth() + DIVIDER + (7 * boardPanel.WIDTH);
					destinationY = buttonPanel.getHeight() + DIVIDER + (7 * boardPanel.HEIGHT);
					drawX = x;
					drawY = y;
					doOnce = true;
					while (destinationX % 12 != 0) { // Allows the number to be divisible by 12
						destinationX++;
					}
					while (destinationY % 10 != 0) { // Allows the number to be divisible by 10
						destinationY++;
					}
				}
				if (drawX > destinationX) {
					drawX -= 12;
				}
				if (drawY < destinationY) {
					drawY += 10;
				}
				if (!(drawY >= destinationY) || !(drawX <= destinationX)) {
					animateYellow((Graphics2D) g); //Keep animating until its at the destination
				} else {
					tokenPanelY.setAnimateAcross(false);
					doOnce = false;
					tokenPanelY.createToken();
				}
			}
		}
		if (run.getCurrentPlayer().getName().equals("green")) {
			if (tokenPanelG.isAnimateAcross() && run.getCurrentPlayer().getName().equals("green")) {
				if (!doOnce) {
					x = 8 + (tokenPanelG.getToAnimateAcrossRotation() / 90) * tokenPanelG.getWIDTH() + (tokenPanelG.getToAnimateAcrossRotation() / 90) * 8;
					y = buttonPanel.getHeight() + 8;
					destinationX = tokenPanelG.getWidth() + DIVIDER + (2 * boardPanel.WIDTH);
					destinationY = buttonPanel.getHeight() + DIVIDER + (2 * boardPanel.HEIGHT);
					drawX = x;
					drawY = y;
					doOnce = true;
					while (destinationX % 8 != 0) { // Allows the number to be divisible by 8
						destinationX++;
					}
					while (destinationY % 4 != 0) { // Allows the number to be divisible by 4
						destinationY++;
					}
				}
				if (drawX < destinationX) {
					drawX += 8;
				}
				if (drawY < destinationY) {
					drawY += 4;
				}
				if (!(drawY >= destinationY) || !(drawX >= destinationX)) {
					animateGreen((Graphics2D) g); //Keep animating until its at the destination
				} else {
					tokenPanelG.setAnimateAcross(false);
					doOnce = false;
					tokenPanelG.createToken();
				}
			}
		}
	}

	/**
	 * Animates the green token during its creation phase.
	 * @param g
	 */
	private void animateGreen(Graphics2D g) {
		g.setColor(Color.green);
		g.fillOval(drawX, drawY, WIDTH - 5, HEIGHT - 5);
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(6));
		drawToken(g, tokenPanelG.getToAnimateAcross(), drawX, drawY);
		g.setStroke(new BasicStroke(1));
	}

	/**
	 * Animates the yellow token during its creation phase.
	 * @param g
	 */
	private void animateYellow(Graphics2D g) {
		g.setColor(Color.yellow);
		g.fillOval(drawX, drawY, WIDTH - 5, HEIGHT - 5);
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(6));
		drawToken(g, tokenPanelY.getToAnimateAcross(), drawX, drawY);
		g.setStroke(new BasicStroke(1));
	}

	@Override
	public Dimension preferredSize() {
		return new Dimension(500, 500);
	}

	/**
	 * Draws the the board pieces swords and shields appropriately in the given x and y coordinates. The x
	 * and y coordinates refer to point(0,0) of the board piece token square.
	 * @param g
	 * @param piece --- the piece whose swords and shields are being drawn
	 * @param x --- x coordinate of the board piece square
	 * @param y --- y coordinate of the board piece square
	 */
	private void drawToken(Graphics2D g, BoardPiece piece, int x, int y) {
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
}
