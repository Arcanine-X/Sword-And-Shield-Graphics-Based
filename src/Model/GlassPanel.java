package Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GlassPanel extends JPanel {
	private SwordAndShieldGame game;
	private GameFrame run;
	private TokenPanel tokenPanelY;
	private TokenPanel tokenPanelG;
	private BoardPanel boardPanel;
	private JPanel buttonPanel;
	private static final int STROKE = 3;
	private int WIDTH = 60;
	private int HEIGHT = 60;
	private int destinationX;
	private int destinationY;
	private int greenDestination;
	int x, y;
	int drawX, drawY;
	boolean doOnce = false;
	private static final int DIVIDER = 10;

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

		if (run.currentPlayer.getName().equals("yellow")) {
			if (tokenPanelY.timeToFly && run.currentPlayer.getName().equals("yellow")) {
				if (!doOnce) {
					x = tokenPanelG.getWidth() + DIVIDER + boardPanel.getWidth() + DIVIDER + 8
							+ (tokenPanelY.toFlyRot / 90) * tokenPanelY.WIDTH + (tokenPanelY.toFlyRot / 90) * 8;
					y = buttonPanel.getHeight() + 8;
					destinationX = tokenPanelG.getWidth() + DIVIDER + (7 * boardPanel.WIDTH);
					destinationY = buttonPanel.getHeight() + DIVIDER + (7 * boardPanel.HEIGHT);
					drawX = x;
					drawY = y;
					doOnce = true;
					while (destinationX % 8 != 0) {
						destinationX++;
					}
					while (destinationY % 4 != 0) {
						destinationY++;
					}
				}
				if (drawX > destinationX) {
					drawX -= 8;
				}
				if (drawY < destinationY) {
					drawY += 4;
				}
				if (!(drawY >= destinationY) || !(drawX <= destinationX)) {
					animateYellow((Graphics2D) g);
				} else {
					tokenPanelY.timeToFly = false;
					doOnce = false;
					tokenPanelY.createToken();
				}
			}
		}
		if (run.currentPlayer.getName().equals("green")) {
			if (tokenPanelG.timeToFly && run.currentPlayer.getName().equals("green")) {
				if (!doOnce) {
					x = 8 + (tokenPanelG.toFlyRot / 90) * tokenPanelG.WIDTH + (tokenPanelG.toFlyRot / 90) * 8;
					y = buttonPanel.getHeight() + 8;
					destinationX = tokenPanelG.getWidth() + DIVIDER + (2 * boardPanel.WIDTH);
					destinationY = buttonPanel.getHeight() + DIVIDER + (2 * boardPanel.HEIGHT);
					drawX = x;
					drawY = y;
					doOnce = true;
					while (destinationX % 8 != 0) {
						destinationX++;
					}
					while (destinationY % 4 != 0) {
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
					animateGreen((Graphics2D) g);
				} else {
					tokenPanelG.timeToFly = false;
					doOnce = false;
					tokenPanelG.createToken();
				}
			}
		}
	}

	public void animateGreen(Graphics2D g) {
		g.setColor(Color.green);
		g.fillOval(drawX, drawY, WIDTH - 5, HEIGHT - 5);
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(6));
		drawToken(g, tokenPanelG.toFly, drawX, drawY);
		g.setStroke(new BasicStroke(1));
	}

	public void animateYellow(Graphics2D g) {
		g.setColor(Color.yellow);
		g.fillOval(drawX, drawY, WIDTH - 5, HEIGHT - 5);
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(6));
		drawToken(g, tokenPanelY.toFly, drawX, drawY);
		g.setStroke(new BasicStroke(1));
	}

	@Override
	public Dimension preferredSize() {
		return new Dimension(500, 500);
	}

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
