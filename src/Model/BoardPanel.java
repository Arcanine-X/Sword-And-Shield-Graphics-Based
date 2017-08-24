package Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class BoardPanel extends JPanel {
	private static final int WIDTH = 60;
	private static final int HEIGHT = 60;
	private static final int STROKE = 3;
	private int mouseX;
	private int mouseY;
	private int chosenX;
	private int chosenY;
	private int moveY;
	private int moveX;
	private SwordAndShieldGame game;
	private BoardPiece chosenToken;
	private Token[][] board;
	private GameFrame run;
	private boolean moveAnimation = false;
	private String moveDir = "";
	private boolean skip = false;
	private List<BoardPiece> everyBpToAnimate = new ArrayList<BoardPiece>();
	private String letter = "";
	public BoardPanel(SwordAndShieldGame game, GameFrame run) {
		this.game = game;
		this.run = run;
		board = game.getBoard().getBoard();
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				BoardPanel.this.repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				BoardPanel.this.repaint();
				mouseX = e.getX();
				mouseY = e.getY();
				findClickedToken();
				if (chosenToken != null) {
					attemptClickMove();
				}else {
				}


			}
		});

		this.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(run.currentPlayer.getMovesSoFar().contains(chosenToken.getName())){
					return;
				}
				if (key == KeyEvent.VK_UP) {
					if (chosenToken != null) {
						String letter = chosenToken.getName();
						System.out.println("move " + letter + " up");
						moveAnimation = true;
						moveDir = "up";
					}
				} else if (key == KeyEvent.VK_RIGHT) {
					if (chosenToken != null) {
						String letter = chosenToken.getName();
						System.out.println("move " + letter + " right");
						moveAnimation = true;
						moveDir = "right";
					}
				} else if (key == KeyEvent.VK_LEFT) {
					if (chosenToken != null) {
						String letter = chosenToken.getName();
						System.out.println("move " + letter + " left");
						moveAnimation = true;
						moveDir = "left";

					}
				} else if (key == KeyEvent.VK_DOWN) {
					if (chosenToken != null) {
						String letter = chosenToken.getName();
						System.out.println("move " + letter + " down");
						moveAnimation = true;
						moveDir = "down";
					}
				} else {
					System.out.println("invalid key");
				}
				repaint();
			}
		});
	}

	// http://zetcode.com/gfx/java2d/transparency/
	public void attemptClickMove() {
		System.out.println("in attempt to click move");
		if (chosenToken != null) {
			if(run.currentPlayer.getMovesSoFar().contains(chosenToken.getName())){
				return;
			}
			Rectangle moveUp = new Rectangle(chosenX, chosenY, WIDTH, HEIGHT / 4);
			Rectangle moveLeft = new Rectangle(chosenX, chosenY, WIDTH / 4, HEIGHT);
			Rectangle moveRight = new Rectangle(chosenX + (WIDTH / 4) * 3, chosenY, WIDTH / 4, HEIGHT);
			Rectangle moveDown = new Rectangle(chosenX, chosenY + (HEIGHT / 4) * 3, WIDTH, HEIGHT / 4);
			if (moveUp.contains(mouseX, mouseY)) {
				String letter = chosenToken.getName();
				System.out.println("move " + letter + " up");
				moveAnimation = true;
				moveDir = "up";
			} else if (moveRight.contains(mouseX, mouseY)) {
				String letter = chosenToken.getName();
				System.out.println("move " + letter + " right");
				moveAnimation = true;
				moveDir = "right";
			} else if (moveDown.contains(mouseX, mouseY)) {
				String letter = chosenToken.getName();
				System.out.println("move " + letter + " down");
				moveAnimation = true;
				moveDir = "down";
			} else if (moveLeft.contains(mouseX, mouseY)) {
				String letter = chosenToken.getName();
				System.out.println("move " + letter + " left");
				moveAnimation = true;
				moveDir = "left";
			} else {
			}
		}
	}

	public void highlightSelectedToken(Graphics2D g) {
		if (chosenToken != null) {
			g.setColor(Color.BLUE.darker());
			g.setColor(new Color(0,0,255,80));
			g.setStroke(new BasicStroke(6));
			g.fillRect(chosenX, chosenY, WIDTH, HEIGHT);
			//g.drawRect(chosenX - STROKE, chosenY - STROKE, WIDTH + STROKE + 3, HEIGHT + STROKE + 3);
			//Draws bounding boxes
//			g.setColor(Color.CYAN);
//			g.drawRect(chosenX, chosenY, WIDTH / 4, HEIGHT);
//			g.setColor(Color.PINK);
//			g.drawRect(chosenX, chosenY, WIDTH, HEIGHT / 4);
//			g.setColor(Color.ORANGE);
//			g.drawRect(chosenX + (WIDTH / 4) * 3, chosenY, WIDTH / 4, HEIGHT);
//			g.setColor(Color.magenta);
//			g.drawRect(chosenX, chosenY + (HEIGHT / 4) * 3, WIDTH, HEIGHT / 4);
//			g.setStroke(new BasicStroke(0));
		}
	}

	public void findClickedToken() {
		System.out.println("In here");
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if (board[row][col] instanceof BoardPiece && board[row][col] != null) {
					if ((mouseX >= col * WIDTH) && (mouseX <= col * WIDTH + WIDTH) && (mouseY >= row * HEIGHT)
							&& (mouseY <= row * HEIGHT + WIDTH)) {
						System.out.println("Found Tokens");
						chosenToken = (BoardPiece) board[row][col];
						chosenX = moveX = col * WIDTH;
						chosenY = moveY = row * HEIGHT;
						System.out.println("Setting chosen token");
						System.out.println(chosenToken.toString());
						if (run.currentPlayer.getName().equals("yellow") && chosenToken.getCol().equals("yellow")) {
							return;
						}
						if (run.currentPlayer.getName().equals("green") && chosenToken.getCol().equals("green")) {
							return;
						}
						chosenToken = null;
						continue;
					} else {
						System.out.println("in setting null");
						chosenToken = null;
					}
				}
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D _g = (Graphics2D) g;
		drawBoard(_g);
		//highlightSelectedToken(_g);
		displayInfo(_g);
		if (moveAnimation) {
			applyMoveAnimation(_g);
		}
		else {
			drawBoard(_g);
			highlightSelectedToken(_g);
			displayInfo(_g);
		}
	}

	public int getRow(int value) {
		return value/WIDTH;
	}

	public int getCol(int value) {
		return value/HEIGHT;
	}

	public void applyMoveAnimation(Graphics2D g) {
		/*
		 * Moving up works by counting number of tiles above it that need to be pushed aswell
		 * From create a list containing all the those board pieces by doing some maths and finding the the rows and columns
		 * Update those tokens x and y, and their destanations (ie how much they need to be moved)
		 * Keep animating untill theyve arrived to their destination
		 * Once they have arrived set of boolean to turn of move animation, and actually move the piece to create the illusion
		 */
		if (moveDir.equals("up") && run.currentPlayer.getName().equals("yellow")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				int piecesToAnimate = run.currentPlayer.upCounter(chosenToken, game.getBoard());
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destY = chosenY - 60;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY - (i*HEIGHT));
					int col = getCol(chosenX);
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
					System.out.println("=====================================adding : ======================================");
					System.out.println(bp.toString());
					bp.moveX = chosenX;
					bp.moveY = chosenY - (i * HEIGHT);
					bp.destY = chosenY -((i+1) * HEIGHT);
					everyBpToAnimate.add(bp);
				}
				System.out.println("skip is " + skip);
				skip = true;
			}else {
				for(BoardPiece bp : everyBpToAnimate) {
					System.out.println("every piece is : ");
					System.out.println(bp.toString());
					System.out.println("deestY is " + bp.destY);
					System.out.println("moveY is " + bp.moveY);

				}
				hope(g, everyBpToAnimate);
			}
		}else if(moveDir.equals("down") && run.currentPlayer.getName().equals("yellow")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				int piecesToAnimate = run.currentPlayer.downCounter(chosenToken, game.getBoard());
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destY = chosenY + 60;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY + (i*HEIGHT));
					int col = getCol(chosenX);
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
					System.out.println("=====================================adding : ======================================");
					System.out.println(bp.toString());
					bp.moveX = chosenX;
					bp.moveY = chosenY + (i * HEIGHT);
					bp.destY = chosenY +((i+1) * HEIGHT);
					everyBpToAnimate.add(bp);
				}
				System.out.println("skip is " + skip);
				skip = true;
			}else {
				for(BoardPiece bp : everyBpToAnimate) {
					System.out.println("every piece is : ");
					System.out.println(bp.toString());
					System.out.println("deestY is " + bp.destY);
					System.out.println("moveY is " + bp.moveY);
				}
				hope2(g, everyBpToAnimate);
			}
		}else if(moveDir.equals("right") && run.currentPlayer.getName().equals("yellow")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				int piecesToAnimate = run.currentPlayer.rightCounter(chosenToken, game.getBoard());
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destX = chosenX + 60;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY);
					int col = getCol(chosenX + (i*WIDTH));
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
					System.out.println("=====================================adding : ======================================");
					System.out.println(bp.toString());
					bp.moveX = chosenX + (i*WIDTH);
					bp.moveY = chosenY;
					bp.destX = chosenX +((i+1) * WIDTH);
					everyBpToAnimate.add(bp);
				}
				System.out.println("skip is " + skip);
				skip = true;
			}else {
				for(BoardPiece bp : everyBpToAnimate) {
					System.out.println("every piece is : ");
					System.out.println(bp.toString());
					System.out.println("deestX is " + bp.destX);
					System.out.println("moveX is " + bp.moveX);
				}
				hope3(g, everyBpToAnimate);
			}
		}
		else if(moveDir.equals("left") && run.currentPlayer.getName().equals("yellow")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				int piecesToAnimate = run.currentPlayer.leftCounter(chosenToken, game.getBoard());
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destX = chosenX - 60;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY);
					int col = getCol(chosenX - (i*WIDTH));
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
					System.out.println("=====================================adding : ======================================");
					System.out.println(bp.toString());
					bp.moveX = chosenX - (i*WIDTH);
					bp.moveY = chosenY;
					bp.destX = chosenX - ((i+1) * WIDTH);
					everyBpToAnimate.add(bp);
				}
				System.out.println("skip is " + skip);
				skip = true;
			}else {
				for(BoardPiece bp : everyBpToAnimate) {
					System.out.println("every piece is : ");
					System.out.println(bp.toString());
					System.out.println("deestX is " + bp.destX);
					System.out.println("moveX is " + bp.moveX);
				}
				hope4(g, everyBpToAnimate);
			}
		}

	}

	public void hope4(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}
			if(bp!=null) {
				System.out.println(bp.getName() + " " + bp.moveX + " " + bp.destX);
			}
			g.setColor(Color.DARK_GRAY);
			g.fillRect(bp.moveX, moveY, WIDTH, WIDTH);
			g.setColor(Color.YELLOW);
			g.fillOval(bp.moveX, moveY, WIDTH, HEIGHT);
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(6));
			drawToken(g, bp, bp.moveX, bp.moveY);
			g.setStroke(new BasicStroke(0));
			if (bp.moveX > bp.destX) {
				bp.moveX -= 2;
			}
			else {
				moveAnimation = false;
				skip = false;
				if(chosenToken!=null) {
					letter = chosenToken.getName();
					game.moveToken(run.currentPlayer, "move " + letter + " left");
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			everyBpToAnimate.clear();
		}
	}

	public void hope3(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}
			if(bp!=null) {
				System.out.println(bp.getName() + " " + bp.moveX + " " + bp.destX);
			}
			g.setColor(Color.DARK_GRAY);
			g.fillRect(bp.moveX, moveY, WIDTH, WIDTH);
			g.setColor(Color.YELLOW);
			g.fillOval(bp.moveX, moveY, WIDTH, HEIGHT);
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(6));
			drawToken(g, bp, bp.moveX, bp.moveY);
			g.setStroke(new BasicStroke(0));
			if (bp.moveX < bp.destX) {
				bp.moveX += 2;
			}
			else {
				moveAnimation = false;
				skip = false;
				if(chosenToken!=null) {
					letter = chosenToken.getName();
					game.moveToken(run.currentPlayer, "move " + letter + " right");
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			everyBpToAnimate.clear();
		}
	}

	public void hope2(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}
			if(bp!=null) {
				System.out.println(bp.getName() + " " + bp.moveY + " " + bp.destY);
			}
			g.setColor(Color.DARK_GRAY);
			g.fillRect(moveX, bp.moveY, WIDTH, WIDTH);
			g.setColor(Color.YELLOW);
			g.fillOval(moveX, bp.moveY, WIDTH, HEIGHT);
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(6));
			drawToken(g, bp, bp.moveX, bp.moveY);
			g.setStroke(new BasicStroke(0));
			if (bp.moveY < bp.destY) {
				bp.moveY += 2;
			}
			else {
				moveAnimation = false;
				skip = false;
				if(chosenToken!=null) {
					letter = chosenToken.getName();
					game.moveToken(run.currentPlayer, "move " + letter + " down");
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			everyBpToAnimate.clear();
		}
	}

	public void hope(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}
			if(bp!=null) {
				System.out.println(bp.getName() + " " + bp.moveY + " " + bp.destY);
			}
			g.setColor(Color.DARK_GRAY);
			g.fillRect(moveX, bp.moveY, WIDTH, WIDTH);
			g.setColor(Color.YELLOW);
			g.fillOval(moveX, bp.moveY, WIDTH, HEIGHT);
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(6));
			drawToken(g, bp, bp.moveX, bp.moveY);
			g.setStroke(new BasicStroke(0));
			if (bp.moveY > bp.destY) {
				bp.moveY -= 2;
			}
			else {
				moveAnimation = false;
				skip = false;
				if(chosenToken!=null) {
					letter = chosenToken.getName();
					game.moveToken(run.currentPlayer, "move " + letter + " up");
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			everyBpToAnimate.clear();
		}
	}


	public void displayInfo(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Serif", Font.BOLD, 12));
		g.drawString("It is " + run.currentPlayer.getName() + "'s turn", 50, 620);
		if (game.getBoard().getUndoStack().size() > 1) {
			g.drawString("Rotate, Move, Undo or Pass ", 50, 640);
		} else {
			g.drawString("Create a token or Pass", 50, 640);
		}
	}

	public void drawBoard(Graphics2D g) {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board.length; col++) {
				if (board[row][col] instanceof InvalidSquare) {
					g.setColor(Color.GRAY);
					g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
				} else if (row == 2 && col == 2) {
					if (board[row][col] instanceof BoardPiece) {
						g.setColor(Color.DARK_GRAY);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
						g.setColor(Color.GREEN);
						g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(6));
						BoardPiece temp = (BoardPiece) board[row][col];
						drawToken(g, (BoardPiece) board[row][col], col * WIDTH, row * HEIGHT);
						g.setStroke(new BasicStroke(0));
					} else {
						g.setColor(new Color(144, 238, 144));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					}
				} else if (row == 7 && col == 7) {
					if (board[row][col] instanceof BoardPiece) {
						if((((BoardPiece)board[row][col]).equals(chosenToken)&&moveAnimation)) {
							continue;
						}
						g.setColor(Color.DARK_GRAY);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
						g.setColor(Color.YELLOW);
						g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(6));
						BoardPiece temp = (BoardPiece) board[row][col];
						drawToken(g, (BoardPiece) board[row][col], col * WIDTH, row * HEIGHT);

						g.setStroke(new BasicStroke(0));
					} else {
						g.setColor(new Color(255, 250, 205));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					}
				} else if (board[row][col] instanceof BoardPiece) {
					g.setColor(Color.DARK_GRAY);
					g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
					BoardPiece temp = (BoardPiece) board[row][col];
					if (temp.getCol().equals("yellow")) {
						g.setColor(Color.YELLOW);
					} else {
						g.setColor(Color.GREEN);
					}
					g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					g.setColor(Color.red);
					g.setStroke(new BasicStroke(6));
					drawToken(g, (BoardPiece) board[row][col], col * WIDTH, row * HEIGHT);
					g.setStroke(new BasicStroke(0));
					g.setColor(Color.ORANGE);
					g.setFont(new Font("Serif", Font.BOLD, 12));
					if (game.getYellow().getMovesSoFar().contains(temp.getName())) {
						g.drawString("M", col * WIDTH, row * HEIGHT + 10);
					}
					if (game.getGreen().getMovesSoFar().contains(temp.getName())) {
						g.drawString("M", col * WIDTH, row * HEIGHT + 10);
					}
				}

				else if (board[row][col] instanceof Player) {
					Player p = (Player) board[row][col];
					if (p.getName().equals("yellow")) {
						g.setColor(Color.YELLOW);
						g.fillOval(col * WIDTH + 5, row * HEIGHT + 5, WIDTH - 10, HEIGHT - 10);
						g.setColor(Color.black);
						g.drawOval(col * WIDTH + 5, row * HEIGHT + 5, WIDTH - 10, HEIGHT - 10);
						g.drawArc(col * WIDTH + 10, row * HEIGHT, WIDTH - 20, HEIGHT - 10, 0, -180);
					} else {
						g.setColor(Color.GREEN);
						g.fillOval(col * WIDTH + 5, row * HEIGHT + 5, WIDTH - 10, HEIGHT - 10);
						g.setColor(Color.black);
						g.drawOval(col * WIDTH + 5, row * HEIGHT + 5, WIDTH - 10, HEIGHT - 10);
						g.drawArc(col * WIDTH + 10, row * HEIGHT, WIDTH - 20, HEIGHT - 10, 0, -180);
					}
					g.setColor(Color.BLACK);
					g.fillOval(col * WIDTH + WIDTH / 4, row * HEIGHT + HEIGHT / 4, WIDTH / 5, HEIGHT / 5);
					g.fillOval(col * WIDTH + WIDTH / 2, row * HEIGHT + HEIGHT / 4, WIDTH / 5, HEIGHT / 5);
				} else {
					if ((row + col) % 2 != 1) {
						g.setColor(Color.WHITE);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					} else {
						g.setColor(Color.BLACK);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					}
				}
			}
		}
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

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 640);
	}

}
