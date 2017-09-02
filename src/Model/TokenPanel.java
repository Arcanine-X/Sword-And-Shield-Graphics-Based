package Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

import Controller.TokenController;
/**
 * This class represents the two token panels. It holds each of the players tokens,
 * and is responsible for the player being able to choose and pick a token, to play
 * on the board.
 * @author Chin Patel
 *
 */
public class TokenPanel extends JPanel implements Observer {
	private static final Color TOKEN_SQUARE = new Color(179, 218, 255);
	private static final int GAP = 8;
	private static final int STROKE = 3;// stroke width /2
	private int WIDTH = 60;
	private int HEIGHT = 60;
	private int x = GAP, y = GAP;
	private int mouseX, mouseY;
	private int toAnimateAcrossRotation;
	private int alpha = 0;
	private boolean animateCreation = false;
	private boolean animateAcross = false;
	private BoardPiece[][] tokens;
	private BoardPiece clickedPiece = null;
	private BoardPiece pieceToPlay;
	private BoardPiece toAnimateAcross;
	private SwordAndShieldGame game;
	private Player player;
	private GameFrame run;
	private List<BoardPiece> clickedPieceRotations = new ArrayList<BoardPiece>();
	private String create = "create";
	private Color currentPlayerColor;
	private TokenController tokenController;

	public TokenPanel(SwordAndShieldGame game, Player player, GameFrame run) {
		this.game = game;
		this.player = player;
		this.tokens = player.getTokens();
		this.run = run;
		tokenController = new TokenController(game, run, this);
		this.addMouseListener(tokenController);
		this.setMinimumSize(new Dimension(100, 220));
	}

	/**
	 * Creates a token. This method is called from the glassPanel class after it has finished
	 * animating the board pieces across panels.
	 */
	public void createToken() {
		game.createToken(player, create);
		clickedPiece = null;
		if (game.getBoard().checkForReaction()) {
			run.setBoardReactionsTrue();
			run.getButtonPanel().getPass().setEnabled(false);
		} else {
			run.setBoardReactionsFalse();
			run.getButtonPanel().getPass().setEnabled(true);
		}
		if (pieceToPlay != null) {
			create = "";
		}
	}

	/**
	 * Sets the animation to fly across the panels to true, and plays the token if a token is clicked on.
	 * If the area clicked on isn't a token, it will allow the player to re choose.
	 */
	public void playToken() {
		for (int i = 0; i < clickedPieceRotations.size(); i++) {
			if (mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT) {
				pieceToPlay = clickedPieceRotations.get(i);
				String letter = pieceToPlay.getName();
				int rotation = i * 90;
				create = "create " + letter + " " + rotation;
				clickedPieceRotations.clear();
				toAnimateAcrossRotation = rotation;
				toAnimateAcross = pieceToPlay;
				if(run.getCurrentPlayer().getName().equals("yellow") && run.yellowCreationSpotValid()) {
					animateAcross = true;
				}else if(run.getCurrentPlayer().getName().equals("green") && run.greenCreationSpotValid()) {
					animateAcross = true;
				}else {
					clickedPieceRotations.clear();
					clickedPiece = null;
				}
				break;
			}else {
				if(mouseX > ((WIDTH * 4) + (4 * GAP)) || mouseY > ((HEIGHT) + (GAP ))) {
					x = GAP;
					clickedPieceRotations.clear();
					clickedPiece = null;
				}
			}
			x += GAP;
			x += WIDTH;
		}
		x = GAP;
	}


	/**
	 * Displays the 4 rotations when a token is clicked on.
	 * @param g
	 */
	private void displayClickedRotations(Graphics2D g) {
		for (int i = 0; i < clickedPieceRotations.size(); i++) {
			g.setColor(TOKEN_SQUARE);
			g.fillRect(x, y, WIDTH, WIDTH);
			if (player.getName().equals("yellow")) {
				g.setColor(Color.YELLOW);
			} else {
				g.setColor(Color.GREEN);
			}
			g.fillOval(x, y, WIDTH, HEIGHT);
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(6));
			drawToken(g, clickedPieceRotations.get(i));
			x += GAP;
			x += WIDTH;
		}
		x = GAP;

	}
	/**
	 * Creates all the possible reactions and adds them to a list to be drawn.
	 */
	public void getRotations() {
		clickedPieceRotations.clear();
		if (clickedPiece != null) {
			// 0
			BoardPiece one = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(),
					clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece);
			// 90
			BoardPiece two = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(),
					clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece);
			// 180
			BoardPiece three = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(),
					clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece);
			// 270
			BoardPiece four = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(),
					clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece); // back to original
			clickedPieceRotations.addAll(Arrays.asList(one, two, three, four));
			animateCreation = true;
		}
	}

	/**
	 * Checks if the position the player has clicked on is on a token or something else.
	 */
	public void clicked() {
		for (int row = 0; row < tokens.length; row++) {
			for (int col = 0; col < tokens[0].length; col++) {
				if (tokens[row][col] instanceof BoardPiece) {
					if (mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT) {
						clickedPiece = (BoardPiece) tokens[row][col];
					} 
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

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		WIDTH = Math.min(getWidth(), getHeight()) / 7 - Math.min(getWidth(), getHeight()) / 60;
		HEIGHT = Math.min(getWidth(), getHeight()) / 7 - Math.min(getWidth(), getHeight()) / 60;
		Graphics2D _g = (Graphics2D) g;
		GradientPaint blackToGray = new GradientPaint(0, 0, Color.BLACK, 0, this.getHeight(), TOKEN_SQUARE);
		_g.setPaint(blackToGray);
		_g.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (!clickedPieceRotations.isEmpty()) {
			if (animateCreation) {
				applyAnimation(_g);
			} else {
				displayClickedRotations(_g);
			}
		} else {
			drawBoard(_g);
		}
	}
	/**
	 * Applies the fade in animation when a token is clicked. This is done by increasing the alpha value
	 * by a certain amount until a a certain threshold.
	 * @param g
	 */
	private void applyAnimation(Graphics2D g) {
		for (int i = 0; i < clickedPieceRotations.size(); i++) {
			g.setColor(new Color(179, 218, 255, alpha));
			g.fillRect(x, y, WIDTH, WIDTH);
			if (player.getName().equals("yellow")) {
				currentPlayerColor = new Color(255, 255, 0, alpha);
			} else {
				currentPlayerColor = new Color(0, 255, 0, alpha);
			}
			g.setColor(currentPlayerColor);
			g.fillOval(x, y, WIDTH, HEIGHT);
			g.setColor(new Color(255, 0, 0, alpha));
			g.setStroke(new BasicStroke(6));
			drawToken(g, clickedPieceRotations.get(i));
			x += GAP;
			x += WIDTH;
		}
		if (alpha < 250) {
			alpha += 10;
			x = GAP;
		} else {
			animateCreation = false;
			alpha = 0;
			x = GAP;
		}
	}
	/**
	 * Draws the token board by iterating through the players tokens.
	 * @param g
	 */
	private void drawBoard(Graphics2D g) {
		WIDTH = Math.min(getWidth(), getHeight()) / 7 - Math.min(getWidth(), getHeight()) / 60;
		HEIGHT = Math.min(getWidth(), getHeight()) / 7 - Math.min(getWidth(), getHeight()) / 60;
		for (int row = 0; row < tokens.length; row++) {
			for (int col = 0; col < tokens[0].length; col++) {
				if (tokens[row][col] instanceof BoardPiece) {
					g.setColor(TOKEN_SQUARE);
					g.fillRect(x, y, WIDTH, WIDTH);
					if (player.getName().equals("yellow")) {
						g.setColor(Color.YELLOW);
					} else {
						g.setColor(Color.GREEN);
					}
					g.fillOval(x, y, WIDTH, HEIGHT);
					g.setColor(Color.red);
					g.setStroke(new BasicStroke(6));
					BoardPiece piece = (BoardPiece) tokens[row][col];
					drawToken(g, piece);
				} else {
					g.setColor(Color.GRAY);
					g.fillOval(x, y, WIDTH, HEIGHT);
				}
				x += GAP;
				x += WIDTH;
			}
			x = 0;
			x = GAP;
			y += GAP;
			y += HEIGHT;
		}
		y = GAP;
	}

	/**
	 * Draws the the token pieces swords and shields appropriately in the given x and y coordinates. The x
	 * and y coordinates refer to point(0,0) of the board piece token square.
	 * @param g
	 * @param piece --- the piece whose swords and shields are being drawn
	 * @param x --- x coordinate of the board piece square
	 * @param y --- y coordinate of the board piece square
	 */
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
		return new Dimension(400, 480);
	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	public BoardPiece getClickedPiece() {
		return clickedPiece;
	}

	public void setClickedPiece(BoardPiece clickedPiece) {
		this.clickedPiece = clickedPiece;
	}

	public BoardPiece getToAnimateAcross() {
		return toAnimateAcross;
	}

	public int getToAnimateAcrossRotation() {
		return toAnimateAcrossRotation;
	}

	public boolean isAnimateAcross() {
		return animateAcross;
	}

	public void setAnimateAcross(boolean animateAcross) {
		this.animateAcross = animateAcross;
	}

	public int getWIDTH() {
		return WIDTH;
	}



}
