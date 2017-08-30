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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
	public int WIDTH = 60;
	public int HEIGHT = 60;
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
	private boolean rotationAnimation = false;
	private String moveDir = "";
	private boolean skip = false;
	private List<BoardPiece> everyBpToAnimate = new ArrayList<BoardPiece>();
	private String letter = "";
	private boolean disappearAnimation = false;
	private boolean disappearSkip = false;
	private int disappearCol, disapppearRow;
	private BoardPiece disappearPiece;
	private int alpha = 0;
	private boolean singleMove = false;
	int piecesToAnimate;
	int mouseClicks;
	int rotationCount = 0;
	BoardPiece hugeToken;
	public boolean reactionMoveAnimation = false;
	public boolean reactions = false;
	List<Reaction> reactionOptions = new ArrayList<Reaction>();
	List<Rectangle> reactionBoundingBoxes = new ArrayList<Rectangle>();
	List<BoardPiece> hugeTokenRotations = new ArrayList<BoardPiece>();
	List<BoardPiece> reactionMoves = new ArrayList<BoardPiece>();
	boolean reactionMoveSkip = false;
	boolean reactionDisappearSkip = false;
	List<BoardPiece> toDisappear = new ArrayList<BoardPiece>();
	Pair reactionPiece;
	public boolean doOnce = false;
	boolean reactionDisappearAnimation = false;
	public List<BoardPiece> aList = new ArrayList<BoardPiece>();
	Pair pairToDisappear;
	public BoardPiece reactionDisappear;
	public boolean activateAnimation = false;
	String animationDir = "";
	BoardPiece SDisappearPiece;
	public boolean SWEDisappear;
	public boolean pairDisappear;
	Pair reactionPair;
	int number;
	int horiNumber;
	public BoardPanel(SwordAndShieldGame game, GameFrame run) {
		this.game = game;
		this.run = run;
		board = game.getBoard().getBoard();
		this.setMinimumSize(new Dimension(200,200));

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
				if(!reactions) {
					findClickedToken();
				}
				System.out.println(mouseClicks);
				if (chosenToken != null && mouseClicks >=2 && !rotationAnimation && reactions == false) {
					attemptRotation();
					attemptClickMove();
				}else if(reactions){
					findChosenReaction();
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
				if(chosenToken == null) {
					return;
				}
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

	public Pair findPair(BoardPiece one, BoardPiece two, Player player) {
		for(Pair p : game.getBoard().getReactions()) {
			if(player!=null) {
				if(p.getOne().equals(one) && p.getPlayer().getName().equals(player.getName())) {
					return p;
				}
			}else if(two!=null) {
				if(p.getOne().equals(one) && p.getTwo().equals(two)) {
					return p;
				}
			}else {

			}

		}
		return null;
	}

	public void findChosenReaction() {

		for(Reaction r : reactionOptions) {
			Pair p;
			if(r.player!=null) {
				p = findPair(r.one, null, r.player);
			}else {
				p = findPair(r.one, r.two, null);
			}
			if(r.rect.contains(mouseX, mouseY)) {
				doReaction(p);
				break;
			}
		}
	}

	public void doReaction(Pair p) {
		if(p.getDir().equals("hori")) {
			reactionPiece = p;
			//game.horizontalReaction(run.currentPlayer, p);
			tryReactionAnimation(p);
		}
		else {
			reactionPiece = p;
			//game.verticalReaction(run.currentPlayer, p);
			//applyReactionMoveAnimation(p);
			System.out.println("in do reaction");
			tryReactionAnimation(p);
		}
		checkForWinner();
		if(game.getBoard().checkForReaction()) {
			run.pass.setEnabled(false);
			run.setBoardReactionsTrue();
		}else {
			run.setBoardReactionsFalse();
			run.pass.setEnabled(true);
		}
		p = null;
		chosenToken = null;
	}

	public void tryReactionAnimation(Pair p) {
		System.out.println(p.getDir());
		int howManyToAnimate;
		if(p.getDir().equals("vert")) {
			animationDir = game.getDirectionOfAnimation(run.currentPlayer, p);
			if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("up")) {
				howManyToAnimate = game.verticalReactionAnimation(run.currentPlayer, p);
				System.out.println("bloop " + howManyToAnimate);
				if(howManyToAnimate == 0) {
					System.out.println(p.getOne().toString());
					System.out.println(p.getTwo().toString());
				}
				else if(howManyToAnimate == -1) {
					//needs to disappear
				}else if(howManyToAnimate == -2) {
					game.verticalReaction(run.currentPlayer, p);
				}else {
					for(int i = howManyToAnimate; i >= 0; i --) {
						int row = getRow(p.getTwo().yLoc - (i*HEIGHT));
						int col = getCol(p.getTwo().xLoc);
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc - HEIGHT;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						if(bp.equals(p.getTwo())) {
							continue;
						}
						if((row!=0) && !(col == 0 && row == 2) && !(col==1 && row == 2)) {
							aList.add(bp);
						}else {
							reactionDisappear = bp;
							playDisappearSound();
						}
						activateAnimation = true;
					}
				}
			}
			else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("down")) {
				System.out.println("in down animation for reactions");
				howManyToAnimate = game.verticalReactionAnimation(run.currentPlayer, p);
				System.out.println("bloop " + howManyToAnimate);
				if(howManyToAnimate == 0) {
					System.out.println(p.getOne().toString());
					System.out.println(p.getTwo().toString());
				}else if(howManyToAnimate == -1) {
					//needs to disappear
				}else if(howManyToAnimate == -2) {
					game.verticalReaction(run.currentPlayer, p);
				}
				else {
					System.out.println("in else?");
					for(int i = 0; i <= howManyToAnimate; i ++) {
						System.out.println("in for loooooooooooooooooop???");
						int row = getRow(p.getOne().yLoc + (i*HEIGHT));
						int col = getCol(p.getOne().xLoc);
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc + HEIGHT;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						if(bp.equals(p.getOne())) {
							continue;
						}
						if((row!=9) && !(col == 8 && row == 7) && !(col==9 && row == 7)) {
							aList.add(bp);
						}else {
							reactionDisappear = bp;
							playDisappearSound();
						}
						activateAnimation = true;
					}
				}

			}else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("swordVElse")) {
				System.out.println("in swordVElse");
				number = game.findTokenToAnimate(run.currentPlayer, p);
				if(number == -10) {
					playDisappearSound();
					reactionPair = p;
					SWEDisappear = true;
				}else if(number == -11) {
					reactionDisappear = p.getTwo();
					playDisappearSound();
					reactionPair = p;
					SWEDisappear = true;
				}else if(number == -12) {
					playDisappearSound();
					reactionPair = p;
					SWEDisappear = true;
				}else if(number == -13) {
					playDisappearSound();
					game.verticalReaction(run.currentPlayer, p);
				}else if(number == -14) {
					playDisappearSound();
					game.verticalReaction(run.currentPlayer, p);
				}else if(number == -15) {

				}
			}
			else {
				game.verticalReaction(run.currentPlayer, p);
			}
		}
		if(p.getDir().equals("hori")) {
			animationDir = game.getDirectionOfAnimation(run.currentPlayer, p);
			if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("right")) {
				howManyToAnimate = game.horizontalReactionAnimation(run.currentPlayer, p);
				if(howManyToAnimate == 0) {
					System.out.println(p.getOne().toString());
					System.out.println(p.getTwo().toString());
				}else if(howManyToAnimate == -1) {
				}else if(howManyToAnimate == -2) {
					game.horizontalReaction(run.currentPlayer, p);

				}else {
					for(int i = 0; i <= howManyToAnimate; i ++) {
						int row = getRow(p.getOne().yLoc);
						int col = getCol(p.getOne().xLoc + (i*WIDTH));
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						bp.destX = bp.xLoc + WIDTH;
						if(bp.equals(p.getOne())) {
							continue;
						}
						if((col!=9) && !(col == 7 && row == 8) && !(col==7 && row == 9)) {
							aList.add(bp);
						}else {
							reactionDisappear = bp;
							playDisappearSound();
						}
						activateAnimation = true;
					}
				}
			}
			else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("left")) {
				howManyToAnimate = game.horizontalReactionAnimation(run.currentPlayer, p);
				if(howManyToAnimate == 0) {
				}else if(howManyToAnimate == -1) {
					//needs to disappear
				}else if(howManyToAnimate == -2) {
					game.horizontalReaction(run.currentPlayer, p);

				}else {
					for(int i = howManyToAnimate; i >= 0; i --) {
						////////////
						int row = getRow(p.getTwo().yLoc);
						int col = getCol(p.getTwo().xLoc - (i*WIDTH));
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						bp.destX = bp.xLoc - WIDTH;
						if(bp.equals(p.getTwo())) {
							continue;
						}
						System.out.println("============================<----------------------------------------->=========================");

						if((col!=0) && !(col == 2 && row == 0) && !(col==2 && row == 1)) {
							aList.add(bp);
						}else {
							reactionDisappear = bp;
							playDisappearSound();
						}
						/*if(col != 0) {
							aList.add(bp);
						}else {
							reactionDisappear = bp;
							playDisappearSound();
						}*/
						activateAnimation = true;
					}
				}

			}else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("swordVElse")) {
				System.out.println("in swordVElse");
				horiNumber = game.findTokenToAnimateHori(run.currentPlayer, p);
				if(horiNumber == -20) {
					playDisappearSound();
					reactionPair = p;
					SWEDisappear = true;
				}else if(horiNumber == -21) {
					reactionDisappear = p.getTwo();
					playDisappearSound();
					reactionPair = p;
					SWEDisappear = true;
				}else if(horiNumber == -22) {
					playDisappearSound();
					reactionPair = p;
					SWEDisappear = true;
				}else if(horiNumber == -23) {
					playDisappearSound();
					game.horizontalReaction(run.currentPlayer, p);
				}else if(horiNumber == -24) {
					playDisappearSound();
					game.horizontalReaction(run.currentPlayer, p);
				}
			else if(horiNumber == -25) {
				}
			}
			else {
				game.horizontalReaction(run.currentPlayer, p);
			}
		}


	}


	public void reactionHope(Graphics2D g, List<BoardPiece> toAnimate) {
		if(animationDir.equals("up")) {
			if(toAnimate.isEmpty()) {
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.verticalReaction(run.currentPlayer, reactionPiece);
				chosenToken = null;
				activateAnimation = false;
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
			for(BoardPiece bp : toAnimate) {
				if(bp == null) {
					continue;
				}
				g.setColor(Color.DARK_GRAY);
				g.fillRect(bp.moveX, bp.moveY, WIDTH, WIDTH);
				g.setColor(Color.YELLOW);
				g.fillOval(bp.moveX, bp.moveY, WIDTH, HEIGHT);
				g.setColor(Color.red);
				g.setStroke(new BasicStroke(6));
				drawToken(g, bp, bp.moveX, bp.moveY);
				g.setStroke(new BasicStroke(0));
				if (bp.moveY > bp.destY) {
					bp.moveY -= 2;
				}
				else {
					reactionMoveAnimation = false;
					reactions = false;
					skip = false;
					game.verticalReaction(run.currentPlayer, reactionPiece);
					chosenToken = null;
					activateAnimation = false;
				}
			}
			if(activateAnimation == false) {
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}

			}
			if(skip == false) {
				everyBpToAnimate.clear();
			}
		}
		else if(animationDir.equals("down")) {
			if(toAnimate.isEmpty()) {
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.verticalReaction(run.currentPlayer, reactionPiece);
				chosenToken = null;
				activateAnimation = false;
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
			///
			for(BoardPiece bp : toAnimate) {
				if(bp == null) {
					continue;
				}
				g.setColor(Color.DARK_GRAY);
				g.fillRect(bp.moveX, bp.moveY, WIDTH, WIDTH);
				g.setColor(Color.YELLOW);
				g.fillOval(bp.moveX, bp.moveY, WIDTH, HEIGHT);
				g.setColor(Color.red);
				g.setStroke(new BasicStroke(6));
				drawToken(g, bp, bp.moveX, bp.moveY);
				g.setStroke(new BasicStroke(0));
				if (bp.moveY < bp.destY) {
					bp.moveY += 2;
				}
				else {
					reactionMoveAnimation = false;
					reactions = false;
					skip = false;
					game.verticalReaction(run.currentPlayer, reactionPiece);
					chosenToken = null;
					activateAnimation = false;
				}
			}
			///

			if(activateAnimation == false) {
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}

			}
			if(skip == false) {
				everyBpToAnimate.clear();
			}
		}
		else if(animationDir.equals("right")) {
			if(toAnimate.isEmpty()) {
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.horizontalReaction(run.currentPlayer, reactionPiece);
				chosenToken = null;
				activateAnimation = false;
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
			for(BoardPiece bp : toAnimate) {
				if(bp == null) {
					continue;
				}
				g.setColor(Color.DARK_GRAY);
				g.fillRect(bp.moveX, bp.moveY, WIDTH, WIDTH);
				g.setColor(Color.YELLOW);
				g.fillOval(bp.moveX, bp.moveY, WIDTH, HEIGHT);
				g.setColor(Color.red);
				g.setStroke(new BasicStroke(6));
				drawToken(g, bp, bp.moveX, bp.moveY);
				g.setStroke(new BasicStroke(0));
				if (bp.moveX < bp.destX) {
					bp.moveX += 2;
				}
				else {
					reactionMoveAnimation = false;
					reactions = false;
					skip = false;
					game.horizontalReaction(run.currentPlayer, reactionPiece);
					chosenToken = null;
					activateAnimation = false;
				}
			}
			///

			if(activateAnimation == false) {
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}

			}
			if(skip == false) {
				everyBpToAnimate.clear();
			}
		}

		else if(animationDir.equals("left")) {
			if(toAnimate.isEmpty()) {
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.horizontalReaction(run.currentPlayer, reactionPiece);
				chosenToken = null;
				activateAnimation = false;
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
			///
			for(BoardPiece bp : toAnimate) {
				if(bp == null) {
					continue;
				}
				g.setColor(Color.DARK_GRAY);
				g.fillRect(bp.moveX, bp.moveY, WIDTH, WIDTH);
				g.setColor(Color.YELLOW);
				g.fillOval(bp.moveX, bp.moveY, WIDTH, HEIGHT);
				g.setColor(Color.red);
				g.setStroke(new BasicStroke(6));
				drawToken(g, bp, bp.moveX, bp.moveY);
				g.setStroke(new BasicStroke(0));
				if (bp.moveX > bp.destX) {
					bp.moveX -= 2;
				}
				else {
					reactionMoveAnimation = false;
					reactions = false;
					skip = false;
					game.horizontalReaction(run.currentPlayer, reactionPiece);
					chosenToken = null;
					activateAnimation = false;
				}
			}
			///

			if(activateAnimation == false) {
				aList.clear();
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}

			}
			if(skip == false) {
				everyBpToAnimate.clear();
			}
		}

	}


	public void attemptRotation() {
		if(chosenToken != null) {
			Rectangle boundingBox = new Rectangle(moveX+ WIDTH / 4, moveY + HEIGHT / 4,WIDTH / 2, HEIGHT / 2);
			if(boundingBox.contains(mouseX, mouseY)) {
				rotationAnimation = true;
				hugeToken = chosenToken;
				mouseX = 0;
				mouseY = 0;
			}

		}
	}

	// http://zetcode.com/gfx/java2d/transparency/
	public void attemptClickMove() {
		if (chosenToken != null) {
			if(run.currentPlayer.getMovesSoFar().contains(chosenToken.getName())){
				return;
			}
			mouseClicks = 0;
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

	public void playDisappearSound() {
		File sound = new File("editedFalling.wav");
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			clip.start();
		} catch (Exception e) {}
	}

	public void highlightSelectedToken(Graphics2D g) {
		if (chosenToken != null) {
			g.setColor(Color.BLUE.darker());
			g.setColor(new Color(0,0,255,80));
			g.setStroke(new BasicStroke(6));
			g.fillRect(chosenX, chosenY, WIDTH, HEIGHT);
			Rectangle boundingBox = new Rectangle(WIDTH / 4, HEIGHT / 4, WIDTH / 2, HEIGHT / 2);
			g.setColor(Color.PINK);
			g.drawRect(moveX+ WIDTH / 4, moveY + HEIGHT / 4,WIDTH / 2, HEIGHT / 2);
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
				if (board[row][col] instanceof BoardPiece && board[row][col] != null && game.getBoard().getUndoStack().size() != 1) {
					if ((mouseX >= col * WIDTH) && (mouseX <= col * WIDTH + WIDTH) && (mouseY >= row * HEIGHT)
							&& (mouseY <= row * HEIGHT + WIDTH)) {
						System.out.println("Found Tokens");
						BoardPiece temp = (BoardPiece) board[row][col];
						if(chosenToken!=null && temp.getName().equals(chosenToken.getName())) {
							mouseClicks++;
						}else {
							mouseClicks = 0;
						}
						chosenToken = (BoardPiece) board[row][col];
						chosenX = moveX = col * WIDTH;
						chosenY = moveY = row * HEIGHT;
						System.out.println(chosenToken.toString());
						if(run.currentPlayer.getEveryMovement().contains(chosenToken) || run.currentPlayer.getMovesSoFar().contains(chosenToken.getName())) {
							chosenToken = null;
							continue;
						}
						if (run.currentPlayer.getName().equals("yellow") && chosenToken.getCol().equals("yellow")) {
							mouseClicks++;
							return;
						}
						if (run.currentPlayer.getName().equals("green") && chosenToken.getCol().equals("green")) {
							mouseClicks++;
							return;
						}
						chosenToken = null;
						continue;
					} else {
						System.out.println("in setting null");
						//chosenToken = null;
					}
				}
			}
		}
	}

	public void checkForWinner() {
		if(board[1][1] == null) {
			//green wins
			run.playerKilled(game.getYellow());
		}
		if(board[8][8] == null) {
			//yellow wins
			run.playerKilled(game.getGreen());
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D _g = (Graphics2D) g;
		drawBoard(_g);
		displayInfo(_g);

		if(game.getBoard().getUndoStack().size() == 1) {
			chosenToken = null;
		}
		if(reactions) {
			drawReactions(_g);
		}

		if(SWEDisappear) {
			reactionDisappearSVE(_g);
		}

		if(pairDisappear) {

		}

		if(reactionDisappear!=null) {
			reactionDisappear(_g);
		}
		else if(activateAnimation) {
			reactionHope(_g, aList);
		}

		if(reactionMoveAnimation) {
			reactionHope(_g, reactionMoves);
		}
		if(disappearAnimation) {
			applyDisappearAnimation(_g);
		}
		else if (moveAnimation) {
			applyMoveAnimation(_g);
		}
		else if(rotationAnimation) {
			applyRotationAnimation(_g);
		}
		else {
			if(!reactions) {
				drawBoard(_g);
			}
			//drawBoard(_g);
			highlightSelectedToken(_g);
			displayInfo(_g);
		}
	}

	public void reactionDisappear(Graphics2D g){
		if(reactionDisappear!=null) {
			if((getCol(reactionDisappear.xLoc) + getRow(reactionDisappear.yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionDisappear.xLoc, reactionDisappear.yLoc, WIDTH, HEIGHT);
			//g.fillRect(disappearCol*WIDTH, disapppearRow * HEIGHT, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;

				//return;
			}
		}
	}

	public void reactionDisappearSVE(Graphics2D g){
		if(number == -10) { // both die
			if((getCol(reactionPair.getOne().xLoc) + getRow(reactionPair.getOne().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getOne().xLoc, reactionPair.getOne().yLoc, WIDTH, HEIGHT);

			if((getCol(reactionPair.getTwo().xLoc) + getRow(reactionPair.getTwo().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getTwo().xLoc, reactionPair.getTwo().yLoc, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.verticalReaction(run.currentPlayer, reactionPair);
				chosenToken = null;
				activateAnimation = false;
				SWEDisappear = false;
				number = -1000;
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
		}
		else if(number == -11) {
			if((getCol(reactionPair.getTwo().xLoc) + getRow(reactionPair.getTwo().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getTwo().xLoc, reactionPair.getTwo().yLoc, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.verticalReaction(run.currentPlayer, reactionPair);
				chosenToken = null;
				activateAnimation = false;
				SWEDisappear = false;
				number = -1000;
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
		}else if (number == -12) {
			if((getCol(reactionPair.getOne().xLoc) + getRow(reactionPair.getOne().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getOne().xLoc, reactionPair.getOne().yLoc, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.verticalReaction(run.currentPlayer, reactionPair);
				chosenToken = null;
				activateAnimation = false;
				SWEDisappear = false;
				number = -1000;
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
		}else if (horiNumber == -20) {
			System.out.println("in -20");
			if((getCol(reactionPair.getOne().xLoc) + getRow(reactionPair.getOne().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getOne().xLoc, reactionPair.getOne().yLoc, WIDTH, HEIGHT);

			if((getCol(reactionPair.getTwo().xLoc) + getRow(reactionPair.getTwo().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getTwo().xLoc, reactionPair.getTwo().yLoc, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.horizontalReaction(run.currentPlayer, reactionPair);
				chosenToken = null;
				activateAnimation = false;
				SWEDisappear = false;
				horiNumber = -1000;
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
		}else if (horiNumber == -21) {
			if((getCol(reactionPair.getTwo().xLoc) + getRow(reactionPair.getTwo().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getTwo().xLoc, reactionPair.getTwo().yLoc, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.horizontalReaction(run.currentPlayer, reactionPair);
				chosenToken = null;
				activateAnimation = false;
				SWEDisappear = false;
				horiNumber = -1000;
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
		}else if (horiNumber == -22) {
			if((getCol(reactionPair.getOne().xLoc) + getRow(reactionPair.getOne().yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionPair.getOne().xLoc, reactionPair.getOne().yLoc, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;
				reactionMoveAnimation = false;
				reactions = false;
				skip = false;
				game.horizontalReaction(run.currentPlayer, reactionPair);
				chosenToken = null;
				activateAnimation = false;
				SWEDisappear = false;
				horiNumber = -1000;
				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}
			}
		}

	}

	public void reactionDisappearPair(Graphics2D g){
		if(reactionDisappear!=null) {
			if((getCol(reactionDisappear.xLoc) + getRow(reactionDisappear.yLoc)) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(reactionDisappear.xLoc, reactionDisappear.yLoc, WIDTH, HEIGHT);
			//g.fillRect(disappearCol*WIDTH, disapppearRow * HEIGHT, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				reactionDisappear = null;
				//return;
			}
		}
	}








	public void drawReactions(Graphics2D g) {
		reactionOptions.clear();
		List<Pair> reactions = game.getBoard().getReactions();
		if(!reactions.isEmpty()) {
			run.pass.setEnabled(false);
		}
		for(Pair p : reactions) {
			if(p.getOne() instanceof BoardPiece && p.getTwo() instanceof BoardPiece) { // Check for boardpiece reactions
				if(p.getDir().equals("vert")) {
					g.setColor(new Color(108,50,180,250));
					Rectangle rect = new Rectangle(p.getOne().xLoc + WIDTH/6, p.getOne().yLoc + HEIGHT - HEIGHT/6, WIDTH - WIDTH /6* 2, (HEIGHT/6)*2);
					Reaction reaction = new Reaction(p.getOne().xLoc + WIDTH/6, p.getOne().yLoc + HEIGHT - HEIGHT/6, WIDTH - WIDTH /6* 2, (HEIGHT/6)*2, p.getOne(), p.getTwo(), p.getDir(), rect, null);
					if(!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
				if(p.getDir().equals("hori")) {
					g.setColor(new Color(108,50,180,250));
					Rectangle rect = new Rectangle(p.getOne().xLoc + WIDTH - WIDTH/6, p.getOne().yLoc + HEIGHT/6, (WIDTH/6)*2, HEIGHT- HEIGHT/6 * 2);
					Reaction reaction = new Reaction(p.getOne().xLoc + WIDTH - WIDTH/6, p.getOne().yLoc + HEIGHT/6, (WIDTH/6)*2, HEIGHT- HEIGHT/6 * 2, p.getOne(), p.getTwo(), p.getDir(), rect, null);
					if(!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
			}else if(p.getOne() instanceof BoardPiece && p.getPlayer()!=null && p.getPlayer().getName().equals("yellow")) { //reaction with player
				if(p.getDir().equals("vert")) {
					g.setColor(new Color(108,50,180,250));
					Rectangle rect = new Rectangle(p.getOne().xLoc + WIDTH/6, p.getOne().yLoc + HEIGHT - HEIGHT/6, WIDTH - WIDTH /6* 2, (HEIGHT/6)*2);
					Reaction reaction = new Reaction(p.getOne().xLoc + WIDTH/6, p.getOne().yLoc + HEIGHT - HEIGHT/6, WIDTH - WIDTH /6* 2, (HEIGHT/6)*2, p.getOne(), null, p.getDir(), rect, p.getPlayer());
					if(!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
				if(p.getDir().equals("hori")) {
					g.setColor(new Color(108,50,180,250));
					Rectangle rect = new Rectangle(p.getOne().xLoc + WIDTH - WIDTH/6, p.getOne().yLoc + HEIGHT/6, (WIDTH/6)*2, HEIGHT- HEIGHT/6 * 2);
					Reaction reaction = new Reaction(p.getOne().xLoc + WIDTH - WIDTH/6, p.getOne().yLoc + HEIGHT/6, (WIDTH/6)*2, HEIGHT- HEIGHT/6 * 2, p.getOne(), null, p.getDir(), rect, p.getPlayer());
					if(!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
			}else if(p.getOne() instanceof BoardPiece && p.getPlayer()!=null && p.getPlayer().getName().equals("green")) {
				if(p.getDir().equals("vert")) {
					g.setColor(new Color(108,50,180,250));
					Rectangle rect = new Rectangle(p.getOne().xLoc + WIDTH/6, p.getOne().yLoc - HEIGHT/6, (WIDTH/6)*4, (HEIGHT/6)*2);
					Reaction reaction = new Reaction(p.getOne().xLoc + WIDTH/6, p.getOne().yLoc - HEIGHT/6, (WIDTH/6)*4, (HEIGHT/6)*2, p.getOne(), null, p.getDir(), rect, p.getPlayer());
					if(!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
				if(p.getDir().equals("hori")) {
					g.setColor(new Color(108,50,180,250));
					Rectangle rect = new Rectangle(p.getOne().xLoc - WIDTH/6, p.getOne().yLoc + HEIGHT/6, (WIDTH/6)*2, (HEIGHT/6)*4);
					Reaction reaction = new Reaction(p.getOne().xLoc - WIDTH/6, p.getOne().yLoc + HEIGHT/6, (WIDTH/6)*2, (HEIGHT/6)*4, p.getOne(), null, p.getDir(), rect, p.getPlayer());
					if(!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);

				}
			}
		}
	}

	public void applyRotationAnimation(Graphics2D g) {
		g.setColor(new Color(175,179,177,150));
		g.fillRect(0, 0, (WIDTH * 10), (HEIGHT * 10));
		drawHugeToken(hugeToken, g);
	}

	public void drawHugeToken(BoardPiece bp, Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(WIDTH * 2, HEIGHT * 2, WIDTH*6, HEIGHT*6);
		if(run.currentPlayer.getName().equals("yellow")) {
			g.setColor(Color.yellow);
		}
		else {
			g.setColor(Color.green);
		}
		g.fillOval(WIDTH * 2, HEIGHT * 2, WIDTH*6, HEIGHT*6);
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(10));
		drawHugeTokenParts(g, hugeToken, WIDTH * 2, HEIGHT * 2);
		if(mouseX > WIDTH * 2 && mouseX < WIDTH * 2 + WIDTH*6 && mouseY > HEIGHT * 2 && mouseY < HEIGHT * 2 + HEIGHT*6) {
			switchRotationImages();
			rotationCount++;
			if(rotationCount > 3) {
				rotationCount = 0;
			}
		}else if(!(mouseX > WIDTH * 2 && mouseX < WIDTH * 2 + WIDTH*6 && mouseY > HEIGHT * 2 && mouseY < HEIGHT * 2 + HEIGHT*6) && mouseX > 0 && mouseY > 0) {
			game.rotateToken(run.currentPlayer, "rotate " + hugeToken.getName() + " " + 0);
			if(game.getBoard().checkForReaction()) {
				run.pass.setEnabled(false);
				run.setBoardReactionsTrue();
			}else {
				run.setBoardReactionsFalse();
				run.pass.setEnabled(true);
			}
			rotationCount = 0;
			rotationAnimation = false;
			chosenToken = null;
		}

	}

	public void switchRotationImages(){
		game.rotator(hugeToken);
		mouseX = -500;
		mouseY = -500;
	}

	public int getRow(int value) {
		return value/WIDTH;
	}

	public int getCol(int value) {
		return value/HEIGHT;
	}

	public void applyDisappearAnimation(Graphics2D g) {
		if(moveDir.equals("up")) {
			if(disappearSkip == false) {
				if(piecesToAnimate==-1) {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY);
					singleMove = true;
				}
				else {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY - (piecesToAnimate*HEIGHT));
				}

				disappearPiece = (BoardPiece) board[disapppearRow][disappearCol];
				disappearSkip = true;
				//https://www.youtube.com/watch?v=QVrxiJyLTqU
				playDisappearSound();
				applyHoudiniEffect(g, disappearPiece);
			}else {
				if(disappearPiece!=null) {
					applyHoudiniEffect(g, disappearPiece);
				}
			}
		}else if(moveDir.equals("down")) {
			if(disappearSkip == false) {
				if(piecesToAnimate==-1) {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY);
					singleMove = true;

				}
				else {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY + (piecesToAnimate*HEIGHT));
				}
				disappearPiece = (BoardPiece) board[disapppearRow][disappearCol];
				disappearSkip = true;
				playDisappearSound();
				applyHoudiniEffect(g, disappearPiece);
			}else {
				if(disappearPiece!=null) {
					applyHoudiniEffect(g, disappearPiece);
				}
			}
		}else if(moveDir.equals("right")) {
			if(disappearSkip == false) {
				if(piecesToAnimate==-1) {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY);
					singleMove = true;

				}
				else {
					disappearCol = getCol(chosenX+ (piecesToAnimate*WIDTH));
					disapppearRow = getRow(chosenY );
				}
				disappearPiece = (BoardPiece) board[disapppearRow][disappearCol];
				disappearSkip = true;
				playDisappearSound();
				applyHoudiniEffect(g, disappearPiece);
			}else {
				if(disappearPiece!=null) {
					applyHoudiniEffect(g, disappearPiece);
				}
			}
		}else if(moveDir.equals("left")) {
			if(disappearSkip == false) {
				if(piecesToAnimate==-1) {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY);
					singleMove = true;
				}
				else {
					disappearCol = getCol(chosenX - (piecesToAnimate*WIDTH));
					disapppearRow = getRow(chosenY );
				}
				disappearPiece = (BoardPiece) board[disapppearRow][disappearCol];
				disappearSkip = true;
				playDisappearSound();
				applyHoudiniEffect(g, disappearPiece);
			}else {
				if(disappearPiece!=null) {
					applyHoudiniEffect(g, disappearPiece);
				}
			}
		}
	}



	public void applyHoudiniEffect(Graphics2D g, BoardPiece toAnimate){
		if(toAnimate!=null) {
			if((disappearCol + disapppearRow) % 2 != 1) {
				g.setColor(new Color(255,255,255,alpha)); // white
			}
			else {
				g.setColor(new Color(0,0,0,alpha));
			}
			g.fillRect(disappearCol*WIDTH, disapppearRow * HEIGHT, WIDTH, HEIGHT);
			if(alpha < 250) {
				alpha +=5;
			}else {
				alpha = 0;
				disappearAnimation = false;
				disappearSkip = false;
				BoardPiece toMove;
				if(singleMove) {
					toMove = toAnimate;
				}else {
					toMove = chosenToken;
				}
				singleMove = false;
				if(moveDir.equals("up")) {
					game.moveToken(run.currentPlayer, "move " + toMove.getName() + " up");
				}else if(moveDir.equals("down")) {
					game.moveToken(run.currentPlayer, "move " + toMove.getName() + " down");
				}
				else if(moveDir.equals("right")) {
					game.moveToken(run.currentPlayer, "move " + toMove.getName() + " right");
				}
				else if(moveDir.equals("left")) {
					game.moveToken(run.currentPlayer, "move " + toMove.getName() + " left");
				}

				if(game.getBoard().checkForReaction()) {
					run.pass.setEnabled(false);
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
					run.pass.setEnabled(true);
				}

				/*if(game.getBoard().checkForReaction()) {
					run.setBoardReactionsTrue();
				}else {
					run.setBoardReactionsFalse();
				}*/

			}
		}
	}

	public void applyMoveAnimation(Graphics2D g) {
		/*
		 * Moving up works by counting number of tiles above it that need to be pushed aswell
		 * From create a list containing all the those board pieces by doing some maths and finding the the rows and columns
		 * Update those tokens x and y, and their destanations (ie how much they need to be moved)
		 * Keep animating untill theyve arrived to their destination
		 * Once they have arrived set of boolean to turn of move animation, and actually move the piece to create the illusion
		 */
		if (moveDir.equals("up")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				piecesToAnimate = run.currentPlayer.upCounter(chosenToken, game.getBoard());
				if(piecesToAnimate == -1) {
					disappearAnimation = true;
					moveAnimation = false;
					skip = false;
					chosenToken = null;
					return;
				}
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destY = chosenY - HEIGHT;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY - (i*HEIGHT));
					int col = getCol(chosenX);
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
					bp.needToAnimate = true;
					bp.moveX = chosenX;
					bp.moveY = chosenY - (i * HEIGHT);
					bp.destY = chosenY -((i+1) * HEIGHT);
					everyBpToAnimate.add(bp);

					if(((row-1) == 1 && col == 0 )|| ((row-1)==1 && col ==1)) {
						everyBpToAnimate.remove(bp);
						disappearPiece = bp;
						disappearAnimation = true;
						return;
					}
				}
				if(piecesToAnimate == 0) {
					int row = getRow(chosenY);
					int col = getCol(chosenX);
					if(((row-1) == 1 && col == 0 )|| ((row-1)==1 && col ==1)) {
						disappearPiece = chosenToken;
						disappearAnimation = true;
						return;
					}
				}
				skip = true;
				BoardPiece temp;
				if(piecesToAnimate > 0) {
					temp = everyBpToAnimate.get(1);
					if(temp.destY < 0) {
						disappearPiece = temp;
						everyBpToAnimate.remove(temp);
						disappearAnimation = true;
						return;
					}
				}
			}else {
				animateUp(g, everyBpToAnimate);
			}
		}else if(moveDir.equals("down")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				piecesToAnimate = run.currentPlayer.downCounter(chosenToken, game.getBoard());
				if(piecesToAnimate == -1) {
					disappearAnimation = true;
					moveAnimation = false;
					skip = false;
					chosenToken = null;
					return;
				}
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destY = chosenY + HEIGHT;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY + (i*HEIGHT));
					int col = getCol(chosenX);
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
					bp.moveX = chosenX;
					bp.moveY = chosenY + (i * HEIGHT);
					bp.destY = chosenY +((i+1) * HEIGHT);
					everyBpToAnimate.add(bp);
					if(((row+1) == 8 && col == 8 )|| ((row+1)==8 && col ==9)) {
						everyBpToAnimate.remove(bp);
						disappearPiece = bp;
						disappearAnimation = true;
						return;
					}
				}
				if(piecesToAnimate == 0) {
					int row = getRow(chosenY);
					int col = getCol(chosenX);
					if(((row+1) == 8 && col == 8 )|| ((row+1)==8 && col ==9)) {
						disappearPiece = chosenToken;
						disappearAnimation = true;
						moveAnimation = false;
						return;
					}
				}
				skip = true;
				BoardPiece temp;
				if(piecesToAnimate > 0) {
					temp = everyBpToAnimate.get(1);
					if(temp.destY > 540) {
						disappearPiece = temp;
						everyBpToAnimate.remove(temp);
						disappearAnimation = true;
						return;
					}
				}
			}else {
				animateDown(g, everyBpToAnimate);
			}
		}else if(moveDir.equals("right")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				piecesToAnimate = run.currentPlayer.rightCounter(chosenToken, game.getBoard());
				if(piecesToAnimate == -1) {
					disappearAnimation = true;
					moveAnimation = false;
					skip = false;
					chosenToken = null;
					return;
				}
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destX = chosenX + WIDTH;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY);
					int col = getCol(chosenX + (i*WIDTH));
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);

					bp.moveX = chosenX + (i*WIDTH);
					bp.moveY = chosenY;
					bp.destX = chosenX +((i+1) * WIDTH);
					everyBpToAnimate.add(bp);
					if((row == 8 && (col + 1) == 8 )|| (row == 9 && (col + 1) == 8)) {
						everyBpToAnimate.remove(bp);
						disappearPiece = bp;
						disappearAnimation = true;
						return;
					}
				}
				if(piecesToAnimate == 0) {
					int row = getRow(chosenY);
					int col = getCol(chosenX);
					if((row == 8 && (col + 1) == 8 )|| (row == 9 && (col + 1) == 8)) {
						disappearPiece = chosenToken;
						disappearAnimation = true;
						moveAnimation = false;
						return;
					}
				}
				skip = true;
				BoardPiece temp;
				if(piecesToAnimate > 0) {
					temp = everyBpToAnimate.get(1);
					if(temp.destX > 540) {
						disappearPiece = temp;
						everyBpToAnimate.remove(temp);
						disappearAnimation = true;
						return;
					}
				}
			}else {
				animateRight(g, everyBpToAnimate);
			}
		}
		else if(moveDir.equals("left")) {
			if(skip == false) {
				everyBpToAnimate.clear();
				piecesToAnimate = run.currentPlayer.leftCounter(chosenToken, game.getBoard());
				if(piecesToAnimate == -1) {
					disappearAnimation = true;
					moveAnimation = false;
					skip = false;
					chosenToken = null;
					return;
				}
				chosenToken.moveX = moveX;
				chosenToken.moveY = moveY;
				chosenToken.destX = chosenX - WIDTH;
				everyBpToAnimate.add(chosenToken);
				for(int i = piecesToAnimate; i > 0; i--) {
					int row = getRow(chosenY);
					int col = getCol(chosenX - (i*WIDTH));
					BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
					bp.moveX = chosenX - (i*WIDTH);
					bp.moveY = chosenY;
					bp.destX = chosenX - ((i+1) * WIDTH);
					everyBpToAnimate.add(bp);
					if((row == 0 && (col - 1) == 1 )|| (row == 1 && (col - 1) == 1)) {
						everyBpToAnimate.remove(bp);
						disappearPiece = bp;
						disappearAnimation = true;
						return;
					}
				}
				if(piecesToAnimate == 0) {
					int row = getRow(chosenY);
					int col = getCol(chosenX);
					if((row == 0 && (col - 1) == 1 )|| (row == 1 && (col - 1) == 1)) {
						disappearPiece = chosenToken;
						disappearAnimation = true;
						moveAnimation = false;
						return;
					}
				}
				skip = true;
				BoardPiece temp;
				if(piecesToAnimate > 0) {
					temp = everyBpToAnimate.get(1);
					if(temp.destX < 0) {
						disappearPiece = temp;
						everyBpToAnimate.remove(temp);
						disappearAnimation = true;
						return;
					}
				}
			}else {
				animateLeft(g, everyBpToAnimate);
			}
		}

	}

	public void animateLeft(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}
			if(bp!=null) {
			}

			g.setColor(Color.DARK_GRAY);
			g.fillRect(bp.moveX, moveY, WIDTH, WIDTH);
			if(bp.getCol().equals("yellow")) {
				g.setColor(Color.YELLOW);
			}
			else {
				g.setColor(Color.GREEN);
			}
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
					if(game.getBoard().checkForReaction()) {
						run.pass.setEnabled(false);
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
						run.pass.setEnabled(true);
					}
					/*if(game.getBoard().checkForReaction()) {
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
					}*/
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			everyBpToAnimate.clear();
		}
	}

	public void animateRight(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}

			bp.needToAnimate = true;
			g.setColor(Color.DARK_GRAY);
			g.fillRect(bp.moveX, moveY, WIDTH, WIDTH);
			if(bp.getCol().equals("yellow")) {
				g.setColor(Color.YELLOW);
			}
			else {
				g.setColor(Color.GREEN);
			}
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
					if(game.getBoard().checkForReaction()) {
						run.pass.setEnabled(false);
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
						run.pass.setEnabled(true);
					}
					/*if(game.getBoard().checkForReaction()) {
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
					}*/
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			everyBpToAnimate.clear();
		}
	}

	public void animateDown(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}

			g.setColor(Color.DARK_GRAY);
			g.fillRect(moveX, bp.moveY, WIDTH, WIDTH);
			if(bp.getCol().equals("yellow")) {
				g.setColor(Color.YELLOW);
			}
			else {
				g.setColor(Color.GREEN);
			}
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
					if(game.getBoard().checkForReaction()) {
						run.pass.setEnabled(false);
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
						run.pass.setEnabled(true);
					}
					/*if(game.getBoard().checkForReaction()) {
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
					}*/
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			everyBpToAnimate.clear();
		}
	}

	public void animateUp(Graphics2D g, List<BoardPiece> toAnimate) {
		for(BoardPiece bp : toAnimate) {
			if(bp == null) {
				continue;
			}

			g.setColor(Color.DARK_GRAY);
			g.fillRect(moveX, bp.moveY, WIDTH, WIDTH);
			if(bp.getCol().equals("yellow")) {
				g.setColor(Color.YELLOW);
			}
			else {
				g.setColor(Color.GREEN);
			}
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
					if(game.getBoard().checkForReaction()) {
						run.pass.setEnabled(false);
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
						run.pass.setEnabled(true);
					}
					/*if(game.getBoard().checkForReaction()) {
						run.setBoardReactionsTrue();
					}else {
						run.setBoardReactionsFalse();
					}*/
				}
				chosenToken = null;
			}
		}
		if(skip == false) {
			for(BoardPiece bp : toAnimate) {
				bp.needToAnimate = false;
			}
			everyBpToAnimate.clear();
		}
	}

	public Color getColor(int row, int col) {
		if ((row + col) % 2 != 1) {
			return new Color(255,255,255);
		} else {
			return new Color(0,0,0);
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
		WIDTH = Math.min(getWidth(), getHeight())/10 - Math.min(getWidth(), getHeight())/60;
		HEIGHT = Math.min(getWidth(), getHeight())/10 - Math.min(getWidth(), getHeight())/60;
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board.length; col++) {
				if (board[row][col] instanceof InvalidSquare) {
					g.setColor(Color.GRAY);
					g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
				} else if (row == 2 && col == 2) {
					if (board[row][col] instanceof BoardPiece) {
						if((((BoardPiece)board[row][col]).equals(chosenToken)&&moveAnimation)) {
							g.setColor(new Color(144, 238, 144));
							g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
							continue;
						}
						g.setColor(new Color(95,170,163));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
						g.setColor(Color.GREEN);
						g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(6));
						BoardPiece temp = (BoardPiece) board[row][col];
						temp.xLoc = col*WIDTH;
						temp.yLoc = row*HEIGHT;
						drawToken(g, (BoardPiece) board[row][col], col * WIDTH, row * HEIGHT);
						g.setStroke(new BasicStroke(0));
					} else {
						g.setColor(new Color(144, 238, 144));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					}
				} else if (row == 7 && col == 7) {
					if (board[row][col] instanceof BoardPiece) {
						if((((BoardPiece)board[row][col]).equals(chosenToken)&&moveAnimation)) {
							g.setColor(new Color(255, 250, 205));
							g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
							continue;
						}

						g.setColor(new Color(160,149,130));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
						g.setColor(Color.YELLOW);
						g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(6));
						BoardPiece temp = (BoardPiece) board[row][col];
						temp.xLoc = col*WIDTH;
						temp.yLoc = row*HEIGHT;
						drawToken(g, (BoardPiece) board[row][col], col * WIDTH, row * HEIGHT);

						g.setStroke(new BasicStroke(0));
					} else {
						g.setColor(new Color(255, 250, 205));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					}
				} else if (board[row][col] instanceof BoardPiece) {

					g.setColor(new Color(160,149,130));
					g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
					BoardPiece temp = (BoardPiece) board[row][col];
					 if(temp.equals(chosenToken)&&moveAnimation && !disappearAnimation) {
						g.setColor(getColor(row,col));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						continue;
					}
					temp.xLoc = col*WIDTH;
					temp.yLoc = row*HEIGHT;
					if ((row + col) % 2 != 1) {
						temp.color = Color.BLACK;
					}else {
						temp.color = Color.WHITE;
					}

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
					g.setColor(Color.PINK);
					g.setFont(new Font("Serif", Font.BOLD, 12));
					if (game.getYellow().getMovesSoFar().contains(temp.getName())) {
						g.drawString("M", col * WIDTH, row * HEIGHT + 10);
					}
					else if (game.getYellow().getEveryMovement().contains(temp)) {
						g.drawString("R", col * WIDTH, row * HEIGHT + 10);
					}
					if (game.getGreen().getMovesSoFar().contains(temp.getName())) {
						g.drawString("M", col * WIDTH, row * HEIGHT + 10);
					}
					else if (game.getGreen().getEveryMovement().contains(temp)) {
						g.drawString("R", col * WIDTH, row * HEIGHT + 10);
					}

				}
				else if(board[row][col] == null && row == 8 && col == 8) {
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(6));
					g.drawLine(col * WIDTH + STROKE, row * HEIGHT + STROKE, (col+1) * WIDTH - STROKE, (row+1) * HEIGHT - STROKE);
					g.drawLine(col * WIDTH - STROKE, (row+1) * HEIGHT + STROKE, (col+1) * WIDTH - STROKE, (row) * HEIGHT + STROKE);
					g.setStroke(new BasicStroke(0));
				}
				else if(board[row][col] == null && row == 1 && col == 1) {
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(6));
					g.drawLine(col * WIDTH + STROKE, row * HEIGHT + STROKE, (col+1) * WIDTH - STROKE, (row+1) * HEIGHT - STROKE);
					g.drawLine(col * WIDTH - STROKE, (row+1) * HEIGHT + STROKE, (col+1) * WIDTH - STROKE, (row) * HEIGHT + STROKE);
					g.setStroke(new BasicStroke(0));

				}
				else if (board[row][col] instanceof Player) {
					Player p = (Player) board[row][col];
					if (p.getName().equals("yellow")) {
						g.setColor(Color.YELLOW);
					} else {
						g.setColor(Color.GREEN);
					}
					g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
                    g.setColor(Color.black);
                    g.drawOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
                    g.setColor(Color.BLACK);
                    g.fillOval(col * WIDTH +WIDTH/3, row * HEIGHT + HEIGHT/3, WIDTH /10, HEIGHT/10);
                    g.fillOval(col*WIDTH-WIDTH/3 + WIDTH-5, row*HEIGHT+HEIGHT/3, WIDTH/10, WIDTH/10);
                    g.drawArc(col*WIDTH+WIDTH/3, row*HEIGHT+HEIGHT/3, WIDTH/3, HEIGHT/3, 180, 180);
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

	private void drawHugeTokenParts(Graphics2D g, BoardPiece piece, int x, int y) {

		//g.fillOval(WIDTH * 2, HEIGHT * 2, WIDTH*6, HEIGHT*6);


		if (piece.getNorth() == 1) {
			g.drawLine(x + WIDTH*6 / 2, y + STROKE, x + WIDTH*6 / 2, y + HEIGHT*6 / 2);
		} else if (piece.getNorth() == 2) {
			g.drawLine(x + STROKE, y + STROKE, x + WIDTH*6 - STROKE, y + STROKE);
		}

		if (piece.getEast() == 1) {
			g.drawLine(x + WIDTH*6 / 2 + STROKE, y + HEIGHT*6 / 2, x + WIDTH*6 - STROKE, y + HEIGHT*6 / 2);
		} else if (piece.getEast() == 2) {
			g.drawLine(x + WIDTH*6 - STROKE, y + STROKE, x + WIDTH*6 - STROKE, y + HEIGHT*6 - STROKE);
		}

		if (piece.getSouth() == 1) {
			g.drawLine(x + WIDTH*6 / 2, y + HEIGHT*6 / 2, x + WIDTH*6 / 2, y + HEIGHT*6 - STROKE);
		} else if (piece.getSouth() == 2) {
			g.drawLine(x + STROKE, y + HEIGHT*6 - STROKE, x + WIDTH*6 - STROKE, y + HEIGHT*6 - STROKE);
		}

		if (piece.getWest() == 1) {
			g.drawLine(x + STROKE, y + HEIGHT*6 / 2, x + WIDTH*6 / 2 - STROKE, y + HEIGHT*6 / 2);
		} else if (piece.getWest() == 2) {
			g.drawLine(x + STROKE, y + STROKE, x + STROKE, y + HEIGHT*6 - STROKE);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 640);
	}
}
