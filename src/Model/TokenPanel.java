package Model;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

public class TokenPanel extends JPanel implements Observer{
	private int WIDTH = 60;
	private int HEIGHT = 60;
	private static final int GAP = 8;
	private static final int STROKE = 3;// stroke width /2
	BoardPiece[][] tokens;
	private int x = GAP;
	private int y = GAP;
	private int mouseX;
	private int mouseY;
	SwordAndShieldGame game;
	Player player;
	BoardPiece clickedPiece = null;
	BoardPiece pieceToPlay;
	GameFrame run;
	boolean animateCreation = false;
	List<BoardPiece> clickedPieceRotations = new ArrayList<BoardPiece>();
	String create = "create";
	private int alpha = 0;
	Color currentPlayerColor;
	int scaleToken;
	int tokenSize;

	public TokenPanel(SwordAndShieldGame game, Player player, GameFrame run) {
		this.game = game;
		this.player = player;
		this.tokens = player.getTokens();
		this.run = run;
		this.setMinimumSize(new Dimension(100,220));
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				if(clickedPiece==null) {
					clicked();
					if(!clickedPiece.getCol().equals(run.currentPlayer.getName())) {
						clickedPiece = null;
						return;
					}
					getRotations();
				}else {
					playToken();
				}
			}
		});

	}

	public void playToken() {
		for(int i = 0; i < clickedPieceRotations.size(); i++) {
			if(mouseX >=x && mouseX <= x+WIDTH && mouseY >= y && mouseY <= y+HEIGHT) {
				pieceToPlay = clickedPieceRotations.get(i);
				String letter = pieceToPlay.getName();
				int rotation = i*90;
				create = "create " + letter + " " + rotation;
				System.out.println(pieceToPlay.toString());
				clickedPieceRotations.clear();
				//run.addClearPanel();
				game.createToken(player, create);         //<-------------Creates token
				clickedPiece = null;
				break;
			}
			x += GAP;
			x += WIDTH;
		}
		x = GAP;
		if(pieceToPlay!=null) {
			System.out.println("create is : ");
			System.out.println(create);
			create = "";
		}
	}



	public void displayClickedRotations(Graphics2D g) {
		for(int i = 0; i < clickedPieceRotations.size(); i++) {
			g.setColor(Color.BLACK);
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

	public void getRotations() {
		clickedPieceRotations.clear();
		if(clickedPiece!=null)
		System.out.println("Now in here");{
			//0
			BoardPiece one = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(), clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece);
			//90
			BoardPiece two = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(), clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece);
			//180
			BoardPiece three = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(), clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece);
			//270
			BoardPiece four = new BoardPiece(clickedPiece.getName(), clickedPiece.getNorth(), clickedPiece.getEast(), clickedPiece.getSouth(), clickedPiece.getWest(), clickedPiece.getCol());
			game.rotator(clickedPiece); // back to original
			clickedPieceRotations.addAll(Arrays.asList(one,two,three,four));
			animateCreation = true;
		}
	}

	public String translateRotation(BoardPiece bp, int rot) {
		String letter = bp.getName();
		String rotation = ""+rot;
		System.out.println("rotate " + letter + " " + rotation);
		return "rotate " + letter + " " + rotation;
	}

	public void clicked() {
		for (int row = 0; row < tokens.length; row++) {
			for (int col = 0; col < tokens[0].length; col++) {
				if(tokens[row][col] instanceof BoardPiece) {
					if(mouseX >=x && mouseX <= x+WIDTH && mouseY >= y && mouseY <= y+HEIGHT) {
						clickedPiece = (BoardPiece)tokens[row][col];
						System.out.println("Found " + clickedPiece.getName());
					} // TODO - else null and set put break in and reset x and y
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
		Graphics2D _g = (Graphics2D) g;

		if(!clickedPieceRotations.isEmpty()) {
			if(animateCreation) {
				applyAnimation(_g);
			}else {
				displayClickedRotations(_g);
			}
		}else {
			//double ratio = (double) this.getWidth()/ this.getHeight();
			//_g.scale(ratio, ratio);
			drawBoard(_g);

		}
	}

	public void applyAnimation(Graphics2D g) {
		for(int i = 0; i < clickedPieceRotations.size(); i++) {
			g.setColor(new Color(0,0,0,alpha));
			g.fillRect(x, y, WIDTH, WIDTH);
			if (player.getName().equals("yellow")) {
				currentPlayerColor = new Color(255,255,0,alpha);
			} else {
				currentPlayerColor = new Color(0,255,0,alpha);
			}
			g.setColor(currentPlayerColor);
			g.fillOval(x, y, WIDTH, HEIGHT);
			g.setColor(new Color(255,0,0,alpha));
			g.setStroke(new BasicStroke(6));
			drawToken(g,clickedPieceRotations.get(i));
			x += GAP;
			x += WIDTH;
		}
		if(alpha < 255) {
			alpha+=5;
			x = GAP;
		}
		else {
			animateCreation = false;
			alpha = 0;
			x = GAP;
		}
	}

	double RoundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
    return Double.valueOf(df2.format(val));
}

	public void drawBoard(Graphics2D g) {

		WIDTH = Math.min(getWidth(), getHeight())/7 - Math.min(getWidth(), getHeight())/60;
		HEIGHT = Math.min(getWidth(), getHeight())/7 - Math.min(getWidth(), getHeight())/60;

		if (player.getName().equals("yellow")) {}
		if (player.getName().equals("green")) {}
		for (int row = 0; row < tokens.length; row++) {
			for (int col = 0; col < tokens[0].length; col++) {
				if (tokens[row][col] instanceof BoardPiece) {
					g.setColor(Color.BLACK);
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
				}
				else {
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



}
