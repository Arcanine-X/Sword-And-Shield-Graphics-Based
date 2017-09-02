package View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

import Controller.BoardController;
import Model.BoardPiece;
import Model.InvalidSquare;
import Model.Pair;
import Model.Player;
import Model.Reaction;
import Model.SwordAndShieldGame;
import Model.Token;

public class BoardPanel extends JPanel {
	private static final Color TOKEN_SQUARE = new Color(179, 218, 255);
	private static final Color YELLOW_CREATION = new Color(255, 250, 205);
	private static final Color GREEN_CREATION = new Color(204, 255, 204);
	private static final int STROKE = 3;
	public int WIDTH = 60;
	public int HEIGHT = 60;
	public int mouseX, mouseY;
	public int mouseClicks;
	private int chosenX, chosenY;
	private int moveX, moveY;
	private int disappearCol, disapppearRow;
	private int vertnumber, horiNumber;
	private int alpha = 0;
	private int piecesToAnimate;
	private int rotationCount = 0;
	public String moveDir = "";
	private String letter = "";
	private String animationDir = "";
	private boolean moveAnimation = false, rotationAnimation = false;
	private boolean skip = false;
	private boolean disappearAnimation = false;
	private boolean disappearSkip = false;
	private boolean singleMove = false;
	private boolean SWEDisappear;
	private boolean activateAnimation = false;
	public boolean reactions = false;
	private List<BoardPiece> everyBpToAnimate = new ArrayList<BoardPiece>();
	private List<Reaction> reactionOptions = new ArrayList<Reaction>();
	private List<BoardPiece> aList = new ArrayList<BoardPiece>();
	public BoardPiece reactionDisappear;
	private BoardPiece disappearPiece;
	public BoardPiece chosenToken;
	private BoardPiece hugeToken;
	private SwordAndShieldGame game;
	private Pair reactionPair;
	private GameFrame run;
	private Token[][] board;
	private BoardController boardController;

	public BoardPanel(SwordAndShieldGame game, GameFrame run) {
		this.game = game;
		this.run = run;
		boardController = new BoardController(game, run, this);
		this.addMouseListener(boardController);
		this.addKeyListener(boardController);
		board = game.getBoard().getBoard();
		this.setMinimumSize(new Dimension(300, 300));
	}

	/**
	 * Goes through the list of reactions and finds the pair specific pair of
	 * reaction
	 *
	 * @param one
	 *            --- board piece one involved in a reaction
	 * @param two
	 *            --- board piece two involved in a reaction
	 * @param player
	 *            --- player involved in reaction
	 * @return --- a pair of reaction that conntains the parameters
	 */
	public Pair findPair(BoardPiece one, BoardPiece two, Player player) {
		for (Pair p : game.getBoard().getReactions()) {
			if (player != null) { // Player v Sword reactions
				if (p.getOne().equals(one) && p.getPlayer().getName().equals(player.getName())) {
					return p;
				}
			} else if (two != null) { // Non player reactions
				if (p.getOne().equals(one) && p.getTwo().equals(two)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * Finds the reaction that the user has clicked on
	 */
	public void findChosenReaction() {
		for (Reaction r : reactionOptions) {
			Pair p;
			if (r.player != null) {
				p = findPair(r.one, null, r.player);
			} else {
				p = findPair(r.one, r.two, null);
			}
			if (r.rect.contains(mouseX, mouseY)) {
				doReaction(p);
				break;
			}
		}
	}

	/**
	 * Checks if there are any more reactions. If there are than it will disable the
	 * pass button to force the player to complete all the reactions.
	 */
	public void checkForMoreReactions() {
		if (game.getBoard().checkForReaction()) {
			run.setBoardReactionsTrue(); // reactions so disable button
			run.getButtonPanel().getPass().setEnabled(false);
		} else {
			run.setBoardReactionsFalse(); // no reactions to enable buttons
			run.getButtonPanel().getPass().setEnabled(true);
		}
	}

	public void doReaction(Pair p) {
		if (p.getDir().equals("hori") || p.getDir().equals("vert")) { //sanity check
			reactionPair = p;
			tryReactionAnimation(p);
		}
		checkForWinner();
		checkForMoreReactions();
		p = null;
		chosenToken = null;
	}

	/**
	 * Sets all all the destinations of the board pieces that need to be animated
	 * upwards
	 *
	 * @param p
	 *            --- the board pieces reacting together
	 * @param howManyToAnimate
	 *            --- the amount of board pieces needing to be animated
	 */
	public void upReactionAnimation(Pair p, int howManyToAnimate) {
		if (howManyToAnimate == 0 || howManyToAnimate == -1 || howManyToAnimate == -2) {
			game.verticalReaction(run.getCurrentPlayer(), p);
		} else {
			//Set all destination values to move to
			for (int i = howManyToAnimate; i >= 0; i--) {
				int row = getRow(p.getTwo().getyLoc() - (i * HEIGHT));
				int col = getCol(p.getTwo().getxLoc());
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setDestY(bp.getyLoc() - HEIGHT);
				bp.setMoveY(bp.getyLoc());
				bp.setMoveX(bp.getxLoc());
				if (bp.equals(p.getTwo())) {
					continue;
				}
				//Check all the out of bounds areas - easiest to hard code it in
				if ((row != 0) && !(col == 0 && row == 2) && !(col == 1 && row == 2)) {
					bp.setNeedToAnimate(true);
					aList.add(bp);
				} else {
					//If its going out of bounds make it disappear
					reactionDisappear = bp;
					playDisappearSound();
				}
				activateAnimation = true;
			}
		}
	}

	/**
	 * Method does the appropriate reaction in the case that it is a sword vs
	 * anything thats not a shield, in the vertical direction
	 *
	 * @param p
	 *            --- the two board pieces that are reacting together
	 */
	public void vertReactionSVE(Pair p) {
		vertnumber = game.findTokenToAnimateVert(run.getCurrentPlayer(), p);
		//Sword v nothing / nothing v Sword / Sword v Sword
		if (vertnumber == -10 || vertnumber == -11 || vertnumber == -12) {
			playSwordFightSound();
			reactionPair = p;
			SWEDisappear = true;
		} else if (vertnumber == -13) { //Sword v Player
			playVictorySound();
			game.verticalReaction(run.getCurrentPlayer(), p);
		} else if (vertnumber == -14) { // Sword v Player
			playVictorySound();
			game.verticalReaction(run.getCurrentPlayer(), p);
		} else if (vertnumber == -15) { // Shouldn't ever get in here
			game.verticalReaction(run.getCurrentPlayer(), p);
		}
	}

	/**
	 * Sets all all the destinations of the board pieces that need to be animated
	 * downwards
	 *
	 * @param p
	 *            --- the board pieces reacting together
	 * @param howManyToAnimate
	 *            --- the amount of board pieces needing to be animated
	 */
	public void downReactionAnimation(Pair p, int howManyToAnimate) {
		//Set all destination values to move to
		if (howManyToAnimate == 0 || howManyToAnimate == -1 || howManyToAnimate == -2) {
			game.verticalReaction(run.getCurrentPlayer(), p);
		} else {
			for (int i = 0; i <= howManyToAnimate; i++) {
				int row = getRow(p.getOne().getyLoc() + (i * HEIGHT));
				int col = getCol(p.getOne().getxLoc());
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setDestY(bp.getyLoc() + HEIGHT);
				bp.setMoveY(bp.getyLoc());
				bp.setMoveX(bp.getxLoc());
				if (bp.equals(p.getOne())) {
					continue;
				}
				//Check all the out of bounds areas - easiest to hard code it in
				if ((row != 9) && !(col == 8 && row == 7) && !(col == 9 && row == 7)) {
					bp.setNeedToAnimate(true);
					aList.add(bp);
				} else {
					//If its going out of bounds make it disappear
					reactionDisappear = bp;
					playDisappearSound();
				}
				activateAnimation = true;
			}
		}
	}

	/**
	 * Sets all all the destinations of the board pieces that need to be animated to
	 * the right
	 *
	 * @param p
	 *            --- the board pieces reacting together
	 * @param howManyToAnimate
	 *            --- the amount of board pieces needing to be animated
	 */
	public void rightReactionAnimation(Pair p, int howManyToAnimate) {
		if (howManyToAnimate == 0 || howManyToAnimate == -1 || howManyToAnimate == -2) {
			game.horizontalReaction(run.getCurrentPlayer(), p);
		} else {
			//Set all destination values to move to
			for (int i = 0; i <= howManyToAnimate; i++) {
				int row = getRow(p.getOne().getyLoc());
				int col = getCol(p.getOne().getxLoc() + (i * WIDTH));
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setDestY(bp.getyLoc());
				bp.setMoveY(bp.getyLoc());
				bp.setMoveX(bp.getxLoc());
				bp.setDestX(bp.getxLoc() + WIDTH);
				if (bp.equals(p.getOne())) {
					continue;
				}
				//Check all the out of bounds areas - easiest to hard code it in
				if ((col != 9) && !(col == 7 && row == 8) && !(col == 7 && row == 9)) {
					bp.setNeedToAnimate(true);
					aList.add(bp);
				} else {
					//If its going out of bounds make it disappear
					reactionDisappear = bp;
					playDisappearSound();
				}
				activateAnimation = true;
			}
		}
	}

	/**
	 * Sets all all the destinations of the board pieces that need to be animated to
	 * the left
	 *
	 * @param p
	 *            --- the board pieces reacting together
	 * @param howManyToAnimate
	 *            --- the amount of board pieces needing to be animated
	 */
	public void leftReactionAnimation(Pair p, int howManyToAnimate) {
		if (howManyToAnimate == 0 || howManyToAnimate == -1 || howManyToAnimate == -2) {
			game.horizontalReaction(run.getCurrentPlayer(), p);
		} else {
			//Set all destination values to move to
			for (int i = howManyToAnimate; i >= 0; i--) {
				int row = getRow(p.getTwo().getyLoc());
				int col = getCol(p.getTwo().getxLoc() - (i * WIDTH));
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setDestY(bp.getyLoc());
				bp.setMoveY(bp.getyLoc());
				bp.setMoveX(bp.getxLoc());
				bp.setDestX(bp.getxLoc() - WIDTH);
				if (bp.equals(p.getTwo())) {
					continue;
				}
				//Check all the out of bounds areas - easiest to hard code it in
				if ((col != 0) && !(col == 2 && row == 0) && !(col == 2 && row == 1)) {
					bp.setNeedToAnimate(true);
					aList.add(bp);
				} else {
					//If its going out of bounds make it disappear
					reactionDisappear = bp;
					playDisappearSound();
				}
				activateAnimation = true;
			}
		}
	}

	/**
	 * Method does the appropriate reaction in the case that it is a sword vs
	 * anything thats not a shield, in the horizontal direction
	 *
	 * @param p
	 *            --- the two board pieces that are reacting together
	 */
	public void horiReactionSVE(Pair p) {
		horiNumber = game.findTokenToAnimateHori(run.getCurrentPlayer(), p);
		//Sword v nothing / nothing v Sword / Sword v Sword
		if (horiNumber == -20 || horiNumber == -21 || horiNumber == -22) {
			playSwordFightSound();
			reactionPair = p;
			SWEDisappear = true;
		} else if (horiNumber == -23) { // Sword v player
			playVictorySound();
			game.horizontalReaction(run.getCurrentPlayer(), p);
		} else if (horiNumber == -24) { // Sword v player
			playVictorySound();
			game.horizontalReaction(run.getCurrentPlayer(), p);
		} else if (horiNumber == -25) { // Shouldn't ever get in here
			game.horizontalReaction(run.getCurrentPlayer(), p);
		}
	}

	/**
	 * Method calls the appropriate animation to occur based on the reaction.
	 *
	 * @param p
	 *            --- the pair of the board pieces reacting
	 */
	public void tryReactionAnimation(Pair p) {
		int howManyToAnimate;
		if (p.getDir().equals("vert")) {
			howManyToAnimate = game.verticalReactionAnimation(run.getCurrentPlayer(), p);
			animationDir = game.getDirectionOfAnimation(run.getCurrentPlayer(), p);
			if (game.getDirectionOfAnimation(run.getCurrentPlayer(), p).equals("up")) {
				upReactionAnimation(p, howManyToAnimate);
			} else if (game.getDirectionOfAnimation(run.getCurrentPlayer(), p).equals("down")) {
				downReactionAnimation(p, howManyToAnimate);
			} else if (game.getDirectionOfAnimation(run.getCurrentPlayer(), p).equals("swordVElse")) {
				vertReactionSVE(p);
			} else {
				game.verticalReaction(run.getCurrentPlayer(), p);
			}
		}
		if (p.getDir().equals("hori")) {
			animationDir = game.getDirectionOfAnimation(run.getCurrentPlayer(), p);
			howManyToAnimate = game.horizontalReactionAnimation(run.getCurrentPlayer(), p);
			if (game.getDirectionOfAnimation(run.getCurrentPlayer(), p).equals("right")) {
				rightReactionAnimation(p, howManyToAnimate);
			} else if (game.getDirectionOfAnimation(run.getCurrentPlayer(), p).equals("left")) {
				leftReactionAnimation(p, howManyToAnimate);
			} else if (game.getDirectionOfAnimation(run.getCurrentPlayer(), p).equals("swordVElse")) {
				horiReactionSVE(p);
			} else {
				game.horizontalReaction(run.getCurrentPlayer(), p);
			}
		}
	}

	/**
	 * Method draws the animating token depending on the tokens moveX and moveY
	 * value.
	 *
	 * @param g
	 * @param bp
	 *            --- token being animated
	 */
	public void drawAnimatingToken(Graphics2D g, BoardPiece bp) {
		g.setColor(TOKEN_SQUARE);
		g.fillRect(bp.getMoveX(), bp.getMoveY(), WIDTH, WIDTH);
		if (bp.getCol().equals("yellow")) {
			g.setColor(Color.YELLOW);
		} else {
			g.setColor(Color.GREEN);
		}
		g.fillOval(bp.getMoveX(), bp.getMoveY(), WIDTH, HEIGHT);
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(6));
		drawToken(g, bp, bp.getMoveX(), bp.getMoveY());
		g.setStroke(new BasicStroke(0));
	}

	/**
	 * Displays the moving animation in reactions. Depending on the direction of the
	 * reaction, either moveY or moveX is changed until the board piece reaches is
	 * destination value.
	 *
	 * @param g
	 * @param toAnimate
	 *            --- List of board pieces being animated
	 */
	public void displayReactionAnimation(Graphics2D g, List<BoardPiece> toAnimate) {
		if (toAnimate.isEmpty()) {
			activateAnimation = false;
		}
		for (BoardPiece bp : toAnimate) {
			if (bp == null) {
				// Shouldn't ever be null
				continue;
			}
			// draws the token at the drawX and drawY locations
			drawAnimatingToken(g, bp);
			if (animationDir.equals("up")) {
				if (bp.getMoveY() > bp.getDestY()) {
					bp.setMoveY(bp.getMoveY() - 2);
				} else {
					activateAnimation = false;
				}
			} else if (animationDir.equals("down")) {
				if (bp.getMoveY() < bp.getDestY()) {
					bp.setMoveY(bp.getMoveY() + 2);
				} else {
					activateAnimation = false;
				}

			} else if (animationDir.equals("left")) {
				if (bp.getMoveX() > bp.getDestX()) {
					bp.setMoveX(bp.getMoveX() - 2);
				} else {
					activateAnimation = false;
				}
			} else if (animationDir.equals("right")) {
				if (bp.getMoveX() < bp.getDestX()) {
					bp.setMoveX(bp.getMoveX() + 2);
				} else {
					activateAnimation = false;
				}
			}

		}
		//After animations are done set everything to false/reset it
		if (activateAnimation == false) {
			for (BoardPiece bp : toAnimate) {
				bp.setNeedToAnimate(false);
			}
			reactions = false;
			skip = false;
			//Once animation is done actually to the reaction
			if (animationDir.equals("up") || animationDir.equals("down")) {
				game.verticalReaction(run.getCurrentPlayer(), reactionPair);

			} else if (animationDir.equals("left") || animationDir.equals("right")) {
				game.horizontalReaction(run.getCurrentPlayer(), reactionPair);

			}
			chosenToken = null;
			activateAnimation = false;
			aList.clear();
			checkForMoreReactions();
		}
		if (skip == false) {
			everyBpToAnimate.clear();
		}
	}

	/**
	 * This method creates the rotation bounding box. It sents the mouseX and mouseY
	 * to zero to ensure that it doesn't rotate as soon as you click on the rotation
	 * phase.
	 */
	public void attemptRotation() {
		if (chosenToken != null) {
			Rectangle boundingBox = new Rectangle(moveX + WIDTH / 4, moveY + HEIGHT / 4, WIDTH / 2, HEIGHT / 2);
			if (boundingBox.contains(mouseX, mouseY)) {
				rotationAnimation = true;
				hugeToken = chosenToken;
				mouseX = 0;
				mouseY = 0;
			}

		}
	}

	/**
	 * Method deals with the click to move a token. It finds what side is clicked
	 * and and moves triggers the movement animation.
	 */
	public void attemptClickMove() {
		if (chosenToken != null) {
			if (run.getCurrentPlayer().getMovesSoFar().contains(chosenToken.getName())) {
				return;
			}
			mouseClicks = 0;
			Rectangle moveUp = new Rectangle(chosenX, chosenY, WIDTH, HEIGHT / 4);
			Rectangle moveLeft = new Rectangle(chosenX, chosenY, WIDTH / 4, HEIGHT);
			Rectangle moveRight = new Rectangle(chosenX + (WIDTH / 4) * 3, chosenY, WIDTH / 4, HEIGHT);
			Rectangle moveDown = new Rectangle(chosenX, chosenY + (HEIGHT / 4) * 3, WIDTH, HEIGHT / 4);
			if (moveUp.contains(mouseX, mouseY)) {
				moveAnimation = true;
				moveDir = "up";
			} else if (moveRight.contains(mouseX, mouseY)) {
				moveAnimation = true;
				moveDir = "right";
			} else if (moveDown.contains(mouseX, mouseY)) {
				moveAnimation = true;
				moveDir = "down";
			} else if (moveLeft.contains(mouseX, mouseY)) {
				moveAnimation = true;
				moveDir = "left";
			} else {
			}
		}
	}

	/**
	 * This method plays the falling of the board sound
	 */
	public void playDisappearSound() {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem
					.getAudioInputStream(getClass().getResource("/Resources/falling.wav"));
			clip = AudioSystem.getClip();
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {
		}
	}

	/**
	 * Plays this sound when theres a sword involved in a reaction excluding sword v shields
	 */
	public void playSwordFightSound() {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem
					.getAudioInputStream(getClass().getResource("/Resources/swordsClashing.wav"));
			clip = AudioSystem.getClip();
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {
		}
	}

	/**
	 * Plays this sound when a player kills the opponent
	 */
	public void playVictorySound() {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem
					.getAudioInputStream(getClass().getResource("/Resources/victory.wav"));
			clip = AudioSystem.getClip();
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {
		}
	}

	/**
	 * Highlights the chosen token.
	 *
	 * @param g
	 */
	public void highlightSelectedToken(Graphics2D g) {
		if (chosenToken != null) {
			g.setColor(Color.BLUE.darker());
			g.setColor(new Color(0, 0, 255, 80));
			g.setStroke(new BasicStroke(6));
			g.fillRect(chosenX, chosenY, WIDTH, HEIGHT);
			Rectangle boundingBox = new Rectangle(WIDTH / 4, HEIGHT / 4, WIDTH / 2, HEIGHT / 2);
			g.setColor(Color.PINK);
			g.drawRect(moveX + WIDTH / 4, moveY + HEIGHT / 4, WIDTH / 2, HEIGHT / 2);
		}
	}

	/**
	 * Finds the clicked token and sets that token to the chosen token. This method
	 * ensures you have to click on a token twice to move it, and that you can only
	 * click on a token that hasn't been moved, rotated and is yours.
	 */
	public void findClickedToken() {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if (board[row][col] instanceof BoardPiece && board[row][col] != null
						&& game.getBoard().getUndoStack().size() != 1) {
					if ((mouseX >= col * WIDTH) && (mouseX <= col * WIDTH + WIDTH) && (mouseY >= row * HEIGHT)
							&& (mouseY <= row * HEIGHT + WIDTH)) {
						BoardPiece temp = (BoardPiece) board[row][col];
						//Increase mouse clicks to make player click on token twice to move it
						if (chosenToken != null && temp.getName().equals(chosenToken.getName())) {
							mouseClicks++;
						} else {
							mouseClicks = 0;
						}
						chosenToken = (BoardPiece) board[row][col];
						chosenX = moveX = col * WIDTH;
						chosenY = moveY = row * HEIGHT;
						//If the tokens been moved dont let the player select it
						if (run.getCurrentPlayer().getEveryMovement().contains(chosenToken)
								|| run.getCurrentPlayer().getMovesSoFar().contains(chosenToken.getName())) {
							chosenToken = null;
							continue;
						}
						// Don't let yellow player touch greens tokens
						if (run.getCurrentPlayer().getName().equals("yellow")
								&& chosenToken.getCol().equals("yellow")) {
							mouseClicks++;
							return;
						}
						// Don't let green player touch yellows tokens
						if (run.getCurrentPlayer().getName().equals("green") && chosenToken.getCol().equals("green")) {
							mouseClicks++;
							return;
						}
						chosenToken = null;
						continue;
					} else {
					}
				}
			}
		}
	}

	/**
	 * Method checks for a winner. If one of the player locations is null, it must
	 * be dead and there the game stops - checked after each reaction.
	 */
	public void checkForWinner() {
		if (board[1][1] == null) {
			// green wins
			run.playerKilled(game.getYellow());
		}
		if (board[8][8] == null) {
			// yellow wins
			run.playerKilled(game.getGreen());
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D _g = (Graphics2D) g;
		drawBoard(_g);
		if (game.getBoard().getUndoStack().size() == 1) {
			chosenToken = null;
		}
		if (reactions) { // displays reactions
			drawReactions(_g);
		}
		if (SWEDisappear) { // displays sword v sword/nothing disappear animation
			reactionDisappearSVE(_g);
		}
		if (reactionDisappear != null) { // displays push of the board reaction disappear animation
			reactionDisappear(_g);
		} else if (activateAnimation) { // displays moving reaction animation
			displayReactionAnimation(_g, aList);
		}
		if (disappearAnimation) { // displays moving token off the board disappear animation
			applyDisappearAnimation(_g);
		} else if (moveAnimation) { // displays moving animation
			applyMoveAnimation(_g);
		} else if (rotationAnimation) { // displays rotation animation (big token)
			applyRotationAnimation(_g);
		} else {
			if (!reactions) {
				drawBoard(_g);
			}
			highlightSelectedToken(_g); // shows selected token
		}
	}

	/**
	 * Does the disappear animation on the reactionDisappear board piece
	 *
	 * @param g
	 */
	public void reactionDisappear(Graphics2D g) {
		if (reactionDisappear != null) {
			//Set color to the color of the board
			if ((getCol(reactionDisappear.getxLoc()) + getRow(reactionDisappear.getyLoc())) % 2 != 1) {
				g.setColor(new Color(255, 255, 255, alpha)); // white
			} else {
				g.setColor(new Color(0, 0, 0, alpha)); // black
			}
			g.fillRect(reactionDisappear.getxLoc(), reactionDisappear.getyLoc(), WIDTH, HEIGHT);
			//Keep increasing alpha value to create 'disappearing' effect
			if (alpha < 250) {
				alpha += 5;
			} else {
				alpha = 0;
				reactionDisappear = null;
			}
		}
	}
	/**
	 * Returns the color of board under the board piece one of a reaction
	 * @param p --- pair of reactions
	 * @return
	 */
	public Color getCorrectColorPairOne(Pair p) {
		if ((getCol(reactionPair.getOne().getxLoc()) + getRow(reactionPair.getOne().getyLoc())) % 2 != 1) {
			return (new Color(255, 255, 255, alpha)); // white
		} else {
			return (new Color(0, 0, 0, alpha)); // black
		}
	}
	/**
	 * Returns the color of board under the board piece two of a reaction
	 * @param p --- pair of reactions
	 * @return
	 */
	public Color getCorrectColorPairTwo(Pair p) {
		if ((getCol(reactionPair.getTwo().getxLoc()) + getRow(reactionPair.getTwo().getyLoc())) % 2 != 1) {
			return (new Color(255, 255, 255, alpha)); // white
		} else {
			return (new Color(0, 0, 0, alpha));
		}
	}

	/**
	 * Helper method to set everything back to false
	 */
	public void doneDisappearAnimation() {
		alpha = 0;
		reactionDisappear = null;
		reactions = false;
		skip = false;
		chosenToken = null;
		activateAnimation = false;
		SWEDisappear = false;
		vertnumber = -1000;
		horiNumber = -1000;
		checkForMoreReactions();
	}

	/**
	 * Does the appropriate reaction depending on reaction they pick. This method
	 * deals with anything where a sword isn't up against a shield.
	 *
	 * @param g
	 */
	public void reactionDisappearSVE(Graphics2D g) {
		if (vertnumber == -10) { // Sword v Sword reaction
			g.setColor(getCorrectColorPairOne(reactionPair));
			g.fillRect(reactionPair.getOne().getxLoc(), reactionPair.getOne().getyLoc(), WIDTH, HEIGHT);
			g.setColor(getCorrectColorPairTwo(reactionPair));
			g.fillRect(reactionPair.getTwo().getxLoc(), reactionPair.getTwo().getyLoc(), WIDTH, HEIGHT);
			if (alpha < 250) {
				alpha += 5;
			} else {
				game.verticalReaction(run.getCurrentPlayer(), reactionPair);
				doneDisappearAnimation();
			}
		} else if (vertnumber == -11) { // Sword v Nothing reaction
			g.setColor(getCorrectColorPairTwo(reactionPair));
			g.fillRect(reactionPair.getTwo().getxLoc(), reactionPair.getTwo().getyLoc(), WIDTH, HEIGHT);
			if (alpha < 250) {
				alpha += 5;
			} else {
				game.verticalReaction(run.getCurrentPlayer(), reactionPair);
				doneDisappearAnimation();
			}
		} else if (vertnumber == -12) { // Nothing v Sword reaction
			g.setColor(getCorrectColorPairOne(reactionPair));
			g.fillRect(reactionPair.getOne().getxLoc(), reactionPair.getOne().getyLoc(), WIDTH, HEIGHT);
			if (alpha < 250) {
				alpha += 5;
			} else {
				game.verticalReaction(run.getCurrentPlayer(), reactionPair);
				doneDisappearAnimation();
			}
		} else if (horiNumber == -20) { // Sword v Sword reaction
			g.setColor(getCorrectColorPairOne(reactionPair));
			g.fillRect(reactionPair.getOne().getxLoc(), reactionPair.getOne().getyLoc(), WIDTH, HEIGHT);
			g.setColor(getCorrectColorPairTwo(reactionPair));
			g.fillRect(reactionPair.getTwo().getxLoc(), reactionPair.getTwo().getyLoc(), WIDTH, HEIGHT);
			if (alpha < 250) {
				alpha += 5;
			} else {
				game.horizontalReaction(run.getCurrentPlayer(), reactionPair);
				doneDisappearAnimation();
			}
		} else if (horiNumber == -21) { // Sword v Nothing Reaction
			g.setColor(getCorrectColorPairTwo(reactionPair));
			g.fillRect(reactionPair.getTwo().getxLoc(), reactionPair.getTwo().getyLoc(), WIDTH, HEIGHT);
			if (alpha < 250) {
				alpha += 5;
			} else {
				game.horizontalReaction(run.getCurrentPlayer(), reactionPair);
				doneDisappearAnimation();
			}
		} else if (horiNumber == -22) { // Nothing v Sword Reaction
			g.setColor(getCorrectColorPairOne(reactionPair));
			g.fillRect(reactionPair.getOne().getxLoc(), reactionPair.getOne().getyLoc(), WIDTH, HEIGHT);
			if (alpha < 250) {
				alpha += 5;
			} else {
				game.horizontalReaction(run.getCurrentPlayer(), reactionPair);
				doneDisappearAnimation();
			}
		}
	}

	/**
	 * Draws the purple boxes to indicate reactions, and adds it to a list of
	 * reaction options.
	 *
	 * @param g
	 */
	public void drawReactions(Graphics2D g) {
		reactionOptions.clear();
		List<Pair> reactions = game.getBoard().getReactions();
		if (!reactions.isEmpty()) {
			run.getButtonPanel().getPass().setEnabled(false);
		}
		for (Pair p : reactions) {
			if (p.getOne() instanceof BoardPiece && p.getTwo() instanceof BoardPiece) {	//BoardPiece v BoardPiece Reaction
				if (p.getDir().equals("vert")) {
					g.setColor(new Color(108, 50, 180, 250));
					Rectangle rect = new Rectangle(p.getOne().getxLoc() + WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT - HEIGHT / 6, WIDTH - WIDTH / 6 * 2, (HEIGHT / 6) * 2);
					Reaction reaction = new Reaction(p.getOne().getxLoc() + WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT - HEIGHT / 6, WIDTH - WIDTH / 6 * 2, (HEIGHT / 6) * 2,
							p.getOne(), p.getTwo(), p.getDir(), rect, null);
					if (!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
				if (p.getDir().equals("hori")) {
					g.setColor(new Color(108, 50, 180, 250));
					Rectangle rect = new Rectangle(p.getOne().getxLoc() + WIDTH - WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT / 6, (WIDTH / 6) * 2, HEIGHT - HEIGHT / 6 * 2);
					Reaction reaction = new Reaction(p.getOne().getxLoc() + WIDTH - WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT / 6, (WIDTH / 6) * 2, HEIGHT - HEIGHT / 6 * 2, p.getOne(),
							p.getTwo(), p.getDir(), rect, null);
					if (!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
			} else if (p.getOne() instanceof BoardPiece && p.getPlayer() != null
					&& p.getPlayer().getName().equals("yellow")) { // Sword v Player Reaction --- yellow player
				if (p.getDir().equals("vert")) {
					g.setColor(new Color(108, 50, 180, 250));
					Rectangle rect = new Rectangle(p.getOne().getxLoc() + WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT - HEIGHT / 6, WIDTH - WIDTH / 6 * 2, (HEIGHT / 6) * 2);
					Reaction reaction = new Reaction(p.getOne().getxLoc() + WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT - HEIGHT / 6, WIDTH - WIDTH / 6 * 2, (HEIGHT / 6) * 2,
							p.getOne(), null, p.getDir(), rect, p.getPlayer());
					if (!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
				if (p.getDir().equals("hori")) {
					g.setColor(new Color(108, 50, 180, 250));
					Rectangle rect = new Rectangle(p.getOne().getxLoc() + WIDTH - WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT / 6, (WIDTH / 6) * 2, HEIGHT - HEIGHT / 6 * 2);
					Reaction reaction = new Reaction(p.getOne().getxLoc() + WIDTH - WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT / 6, (WIDTH / 6) * 2, HEIGHT - HEIGHT / 6 * 2, p.getOne(),
							null, p.getDir(), rect, p.getPlayer());
					if (!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
			} else if (p.getOne() instanceof BoardPiece && p.getPlayer() != null
					&& p.getPlayer().getName().equals("green")) { // Sword v Player reaction --- green player
				if (p.getDir().equals("vert")) {
					g.setColor(new Color(108, 50, 180, 250));
					Rectangle rect = new Rectangle(p.getOne().getxLoc() + WIDTH / 6, p.getOne().getyLoc() - HEIGHT / 6,
							(WIDTH / 6) * 4, (HEIGHT / 6) * 2);
					Reaction reaction = new Reaction(p.getOne().getxLoc() + WIDTH / 6,
							p.getOne().getyLoc() - HEIGHT / 6, (WIDTH / 6) * 4, (HEIGHT / 6) * 2, p.getOne(), null,
							p.getDir(), rect, p.getPlayer());
					if (!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);
				}
				if (p.getDir().equals("hori")) {
					g.setColor(new Color(108, 50, 180, 250));
					Rectangle rect = new Rectangle(p.getOne().getxLoc() - WIDTH / 6, p.getOne().getyLoc() + HEIGHT / 6,
							(WIDTH / 6) * 2, (HEIGHT / 6) * 4);
					Reaction reaction = new Reaction(p.getOne().getxLoc() - WIDTH / 6,
							p.getOne().getyLoc() + HEIGHT / 6, (WIDTH / 6) * 2, (HEIGHT / 6) * 4, p.getOne(), null,
							p.getDir(), rect, p.getPlayer());
					if (!reactionOptions.contains(reaction)) {
						reactionOptions.add(reaction);
					}
					g.fill(rect);

				}
			}
		}
	}

	/**
	 * Method applies the rotation animation, by graying out the board and drawing
	 * the huge token.
	 *
	 * @param g
	 */
	public void applyRotationAnimation(Graphics2D g) {
		g.setColor(new Color(175, 179, 177, 150));
		g.fillRect(0, 0, (WIDTH * 10), (HEIGHT * 10));
		drawHugeToken(hugeToken, g);
	}

	/**
	 * Draws a large token and deals checks if the mouse clicks are within this
	 * token, or outside of it. If the mouse is within this token, it will rotate
	 * it, otherwise exit out of the rotation mode
	 *
	 * @param bp
	 *            --- token being rotated
	 * @param g
	 */
	public void drawHugeToken(BoardPiece bp, Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(WIDTH * 2, HEIGHT * 2, WIDTH * 6, HEIGHT * 6);
		if (run.getCurrentPlayer().getName().equals("yellow")) {
			g.setColor(Color.yellow);
		} else {
			g.setColor(Color.green);
		}
		g.fillOval(WIDTH * 2, HEIGHT * 2, WIDTH * 6, HEIGHT * 6);
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(10));
		drawHugeTokenParts(g, hugeToken, WIDTH * 2, HEIGHT * 2);
		if (mouseX > WIDTH * 2 && mouseX < WIDTH * 2 + WIDTH * 6 && mouseY > HEIGHT * 2
				&& mouseY < HEIGHT * 2 + HEIGHT * 6) {
			switchRotationImages();
			rotationCount++;
			if (rotationCount > 3) {
				rotationCount = 0;
			}
		} else if (!(mouseX > WIDTH * 2 && mouseX < WIDTH * 2 + WIDTH * 6 && mouseY > HEIGHT * 2
				&& mouseY < HEIGHT * 2 + HEIGHT * 6) && mouseX > 0 && mouseY > 0) {
			game.rotateToken(run.getCurrentPlayer(), "rotate " + hugeToken.getName() + " " + 0);
			checkForMoreReactions();
			rotationCount = 0;
			rotationAnimation = false;
			chosenToken = null;
		}

	}

	/**
	 * Rotates the large rotation token. Sets the mouseX and mouseY to unrealistic
	 * values to stop it from continuously rotating.
	 */
	public void switchRotationImages() {
		game.rotator(hugeToken);
		mouseX = -500;
		mouseY = -500;
	}

	/**
	 * Gets the row from dividing the value --- value should correspond to the y
	 * value of a token
	 *
	 * @param value
	 *            --- y value of the token
	 * @return
	 */
	public int getRow(int value) {
		return value / WIDTH;
	}

	/**
	 * Gets the col from dividing the value --- value should correspond to the x
	 * value of a token
	 *
	 * @param value
	 *            --- x value of the token
	 * @return
	 */
	public int getCol(int value) {
		return value / HEIGHT;
	}

	/**
	 * Finds what piece has died and applies the disappearing effect on it.
	 *
	 * @param g
	 */
	public void applyDisappearAnimation(Graphics2D g) {
		if (moveDir.equals("up") || moveDir.equals("down")) {
			if (disappearSkip == false) {
				if (piecesToAnimate == -1) {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY);
					singleMove = true;
				} else {
					disappearCol = getCol(chosenX);
					if (moveDir.equals("up")) { //if direction is up or down, set the row accordingly
						disapppearRow = getRow(chosenY - (piecesToAnimate * HEIGHT));
					} else {
						disapppearRow = getRow(chosenY + (piecesToAnimate * HEIGHT));
					}
				}
				disappearPiece = (BoardPiece) board[disapppearRow][disappearCol];
				disappearSkip = true;
				playDisappearSound();
				applyDisappearEffect(g, disappearPiece);
			} else {
				if (disappearPiece != null) {
					applyDisappearEffect(g, disappearPiece);
				}
			}
		} else if (moveDir.equals("right") || moveDir.equals("left")) {
			if (disappearSkip == false) {
				if (piecesToAnimate == -1) {
					disappearCol = getCol(chosenX);
					disapppearRow = getRow(chosenY);
					singleMove = true;
				} else {
					if (moveDir.equals("right")) { //if direction is right or left, set the col accordingly
						disappearCol = getCol(chosenX + (piecesToAnimate * WIDTH));
					} else {
						disappearCol = getCol(chosenX - (piecesToAnimate * WIDTH));
					}
					disapppearRow = getRow(chosenY);
				}
				disappearPiece = (BoardPiece) board[disapppearRow][disappearCol];
				disappearSkip = true;
				playDisappearSound();
				applyDisappearEffect(g, disappearPiece);
			} else {
				if (disappearPiece != null) {
					applyDisappearEffect(g, disappearPiece);
				}
			}
		}
	}

	/**
	 * Applies the disappear effect to the given boardPiece. A increasing opacity
	 * rectangle is drawn over it, until a certain threshold.
	 *
	 * @param g
	 * @param toAnimate
	 *            --- board piece to disappear / has died
	 */
	public void applyDisappearEffect(Graphics2D g, BoardPiece toAnimate) {
		if (toAnimate != null) {
			if ((disappearCol + disapppearRow) % 2 != 1) {
				g.setColor(new Color(255, 255, 255, alpha)); // white
			} else {
				g.setColor(new Color(0, 0, 0, alpha));
			}
			g.fillRect(disappearCol * WIDTH, disapppearRow * HEIGHT, WIDTH, HEIGHT);
			if (alpha < 250) { // keep increasing alpha until it looks like that piece has disappeared
				alpha += 5;
			} else { // reset all booleans and actually move the token
				alpha = 0;
				disappearAnimation = false;
				disappearSkip = false;
				BoardPiece toMove;
				if (singleMove) {
					toMove = toAnimate;
				} else {
					toMove = chosenToken;
				}
				singleMove = false;
				if (moveDir.equals("up")) {
					game.moveToken(run.getCurrentPlayer(), "move " + toMove.getName() + " up");
				} else if (moveDir.equals("down")) {
					game.moveToken(run.getCurrentPlayer(), "move " + toMove.getName() + " down");
				} else if (moveDir.equals("right")) {
					game.moveToken(run.getCurrentPlayer(), "move " + toMove.getName() + " right");
				} else if (moveDir.equals("left")) {
					game.moveToken(run.getCurrentPlayer(), "move " + toMove.getName() + " left");
				}
				checkForMoreReactions();
			}
		}
	}

	/**
	 * Helper method to turn on and off animations
	 */
	public void negativeOne() {
		disappearAnimation = true;
		moveAnimation = false;
		skip = false;
		chosenToken = null;
	}

	/**
	 * Counts the tiles above the one moving. If there are lots of adjacent tiles it
	 * will put them in a list, and update their destination values depending on the
	 * row the piece is in. It will then keep animating until it has reached its
	 * destination. If the piece is being pushed into a out of bounds area it will
	 * apply the disappear animation on that token first.
	 *
	 * @param g
	 */
	public void applyMoveAnimationUp(Graphics2D g) {
		if (skip == false) {
			everyBpToAnimate.clear();
			piecesToAnimate = run.getCurrentPlayer().upCounter(chosenToken, game.getBoard());
			if (piecesToAnimate == -1) { // piece is going out of bounds so make it disappear
				negativeOne();
				return;
			}
			chosenToken.setMoveX(moveX);
			chosenToken.setMoveY(moveY);
			chosenToken.setDestY(chosenY - HEIGHT);
			everyBpToAnimate.add(chosenToken);
			for (int i = piecesToAnimate; i > 0; i--) { // work from bottom up decreasing their destination values
				int row = getRow(chosenY - (i * HEIGHT));
				int col = getCol(chosenX);
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setMoveX(chosenX);
				bp.setMoveY(chosenY - (i * HEIGHT));
				bp.setDestY(chosenY - ((i + 1) * HEIGHT));
				everyBpToAnimate.add(bp);
				//check boundaries (around the players)
				if (((row - 1) == 1 && col == 0) || ((row - 1) == 1 && col == 1)) {
					everyBpToAnimate.remove(bp);
					disappearPiece = bp;
					disappearAnimation = true;
					return;
				}
			}
			if (piecesToAnimate == 0) { //moving single token
				int row = getRow(chosenY);
				int col = getCol(chosenX);
				if (((row - 1) == 1 && col == 0) || ((row - 1) == 1 && col == 1)) { // if its going out of bounds make it disappear
					disappearPiece = chosenToken;
					disappearAnimation = true;
					return;
				}
			}
			skip = true;
			BoardPiece temp;
			if (piecesToAnimate > 0) {
				temp = everyBpToAnimate.get(1);
				if (temp.getDestY() < 0) {
					disappearPiece = temp;
					everyBpToAnimate.remove(temp);
					disappearAnimation = true;
					return;
				}
			}
		} else { // keep animating until its done
			animateUp(g, everyBpToAnimate);
		}
	}

	/**
	 * Counts the tiles below the one moving. If there are lots of adjacent tiles it
	 * will put them in a list, and update their destination values depending on the
	 * row the piece is in. It will then keep animating until it has reached its
	 * destination. If the piece is being pushed into a out of bounds area it will
	 * apply the disappear animation on that token first.
	 *
	 * @param g
	 */
	public void applyMoveAnimationDown(Graphics2D g) {
		if (skip == false) {
			everyBpToAnimate.clear();
			piecesToAnimate = run.getCurrentPlayer().downCounter(chosenToken, game.getBoard());
			if (piecesToAnimate == -1) { // piece is moving of the board so make it disappear
				negativeOne();
				return;
			}
			chosenToken.setMoveX(moveX);
			chosenToken.setMoveY(moveY);
			chosenToken.setDestY(chosenY + HEIGHT);
			everyBpToAnimate.add(chosenToken);
			for (int i = piecesToAnimate; i > 0; i--) { // set destination values for all the pieces moving
				int row = getRow(chosenY + (i * HEIGHT));
				int col = getCol(chosenX);
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setMoveX(chosenX);
				bp.setMoveY(chosenY + (i * HEIGHT));
				bp.setDestY(chosenY + ((i + 1) * HEIGHT));
				everyBpToAnimate.add(bp);
				//check boundaries
				if (((row + 1) == 8 && col == 8) || ((row + 1) == 8 && col == 9)) {
					everyBpToAnimate.remove(bp);
					disappearPiece = bp;
					disappearAnimation = true;
					return;
				}
			}
			if (piecesToAnimate == 0) { //moving single token
				int row = getRow(chosenY);
				int col = getCol(chosenX);
				if (((row + 1) == 8 && col == 8) || ((row + 1) == 8 && col == 9)) { // if its moving out of bounds make it disappear
					disappearPiece = chosenToken;
					disappearAnimation = true;
					moveAnimation = false;
					return;
				}
			}
			skip = true;
			BoardPiece temp;
			if (piecesToAnimate > 0) {
				temp = everyBpToAnimate.get(1);
				if (temp.getDestY() > (9 * HEIGHT)) {
					disappearPiece = temp;
					everyBpToAnimate.remove(temp);
					disappearAnimation = true;
					return;
				}
			}
		} else { // keep animating until its done
			animateDown(g, everyBpToAnimate);
		}
	}

	/**
	 * Counts the tiles to the left of the one moving. If there are lots of adjacent
	 * tiles it will put them in a list, and update their destination values
	 * depending on the row the piece is in. It will then keep animating until it
	 * has reached its destination. If the piece is being pushed into a out of
	 * bounds area it will apply the disappear animation on that token first.
	 *
	 * @param g
	 */
	public void applyMoveAnimationLeft(Graphics2D g) {
		if (skip == false) {
			everyBpToAnimate.clear();
			piecesToAnimate = run.getCurrentPlayer().leftCounter(chosenToken, game.getBoard());
			if (piecesToAnimate == -1) { // piece is moving of the board so make it disappear
				negativeOne();
				return;
			}
			chosenToken.setMoveX(moveX);
			chosenToken.setMoveY(moveY);
			chosenToken.setDestX(chosenX - WIDTH);
			everyBpToAnimate.add(chosenToken);
			for (int i = piecesToAnimate; i > 0; i--) { // set destination values for all the moving pieces
				int row = getRow(chosenY);
				int col = getCol(chosenX - (i * WIDTH));
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setMoveX(chosenX - (i * WIDTH));
				bp.setMoveY(chosenY);
				bp.setDestX(chosenX - ((i + 1) * WIDTH));
				everyBpToAnimate.add(bp);
				//check boundaries
				if ((row == 0 && (col - 1) == 1) || (row == 1 && (col - 1) == 1)) {
					everyBpToAnimate.remove(bp);
					disappearPiece = bp;
					disappearAnimation = true;
					return;
				}
			}
			if (piecesToAnimate == 0) { // only one piece is moving
				int row = getRow(chosenY);
				int col = getCol(chosenX);
				//check if its moving out of bounds if yes then make it disappear
				if ((row == 0 && (col - 1) == 1) || (row == 1 && (col - 1) == 1)) {
					disappearPiece = chosenToken;
					disappearAnimation = true;
					moveAnimation = false;
					return;
				}
			}
			skip = true;
			BoardPiece temp;
			if (piecesToAnimate > 0) {  //reset everything
				temp = everyBpToAnimate.get(1);
				if (temp.getDestX() < 0) {
					disappearPiece = temp;
					everyBpToAnimate.remove(temp);
					disappearAnimation = true;
					return;
				}
			}
		} else { // keep animating until its done
			animateLeft(g, everyBpToAnimate);
		}
	}

	/**
	 * Counts the tiles to the right of the one moving. If there are lots of
	 * adjacent tiles it will put them in a list, and update their destination
	 * values depending on the row the piece is in. It will then keep animating
	 * until it has reached its destination. If the piece is being pushed into a out
	 * of bounds area it will apply the disappear animation on that token first.
	 *
	 * @param g
	 */
	public void applyMoveAnimationRight(Graphics2D g) {
		if (skip == false) {
			everyBpToAnimate.clear();
			piecesToAnimate = run.getCurrentPlayer().rightCounter(chosenToken, game.getBoard());
			if (piecesToAnimate == -1) { // piece is moving out of bounds so make it disappear
				negativeOne();
				return;
			}
			chosenToken.setMoveX(moveX);
			chosenToken.setMoveY(moveY);
			chosenToken.setDestX(chosenX + WIDTH);
			everyBpToAnimate.add(chosenToken);
			for (int i = piecesToAnimate; i > 0; i--) { // set destination values for all the moving pieces
				int row = getRow(chosenY);
				int col = getCol(chosenX + (i * WIDTH));
				BoardPiece bp = ((BoardPiece) game.getBoard().getBoard()[row][col]);
				bp.setMoveX(chosenX + (i * WIDTH));
				bp.setMoveY(chosenY);
				bp.setDestX(chosenX + ((i + 1) * WIDTH));
				everyBpToAnimate.add(bp);
				//check out of bound areas
				if ((row == 8 && (col + 1) == 8) || (row == 9 && (col + 1) == 8)) {
					everyBpToAnimate.remove(bp);
					disappearPiece = bp;
					disappearAnimation = true;
					return;
				}
			}
			if (piecesToAnimate == 0) { // only one piece is moving
				int row = getRow(chosenY);
				int col = getCol(chosenX);
				if ((row == 8 && (col + 1) == 8) || (row == 9 && (col + 1) == 8)) { // check if its moving out of bounds, if yes make it disappear
					disappearPiece = chosenToken;
					disappearAnimation = true;
					moveAnimation = false;
					return;
				}
			}
			skip = true;
			BoardPiece temp;
			if (piecesToAnimate > 0) { // reset everything
				temp = everyBpToAnimate.get(1);
				if (temp.getDestX() > (9 * WIDTH)) {
					disappearPiece = temp;
					everyBpToAnimate.remove(temp);
					disappearAnimation = true;
					return;
				}
			}
		} else { // keep animating until its done
			animateRight(g, everyBpToAnimate);
		}

	}

	/**
	 * Calls the appropriate method - to animate in the according direction.
	 *
	 * @param g
	 */
	public void applyMoveAnimation(Graphics2D g) {
		if (moveDir.equals("up")) {
			applyMoveAnimationUp(g);
		} else if (moveDir.equals("down")) {
			applyMoveAnimationDown(g);
		} else if (moveDir.equals("right")) {
			applyMoveAnimationRight(g);
		} else if (moveDir.equals("left")) {
			applyMoveAnimationLeft(g);
		}
	}

	/**
	 * Draws the token in location of moveX and moveY which are constantly changing.
	 *
	 * @param g
	 * @param bp
	 *            --- board piece being animated
	 */
	public void animateMove(Graphics2D g, BoardPiece bp) {
		g.setColor(TOKEN_SQUARE);
		g.fillRect(bp.getMoveX(), bp.getMoveY(), WIDTH, WIDTH);
		if (bp.getCol().equals("yellow")) {
			g.setColor(Color.YELLOW);
		} else {
			g.setColor(Color.GREEN);
		}
		g.fillOval(bp.getMoveX(), bp.getMoveY(), WIDTH, HEIGHT);
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(6));
		drawToken(g, bp, bp.getMoveX(), bp.getMoveY());
		g.setStroke(new BasicStroke(0));
	}

	/**
	 * Animates the tokens in the given list right. The x value of the tokens keep
	 * decreasing until a certain value which is their destination.
	 *
	 * @param g
	 * @param toAnimate
	 *            --- list of tokens to be animated
	 */
	public void animateLeft(Graphics2D g, List<BoardPiece> toAnimate) {
		for (BoardPiece bp : toAnimate) {
			if (bp == null) {
				continue;
			}
			animateMove(g, bp);
			if (bp.getMoveX() > bp.getDestX()) { //decrease moveX to give the effect of animating it left
				bp.setMoveX(bp.getMoveX() - 2);
			} else {
				moveAnimation = false;
				skip = false;
				if (chosenToken != null) {
					letter = chosenToken.getName();
					game.moveToken(run.getCurrentPlayer(), "move " + letter + " left"); // actually move the token
					checkForMoreReactions();
				}
				chosenToken = null;
			}
		}
		if (skip == false) {
			everyBpToAnimate.clear();
		}
	}

	/**
	 * Animates the tokens in the given list right. The x value of the tokens keep
	 * increasing until a certain value which is their destination.
	 *
	 * @param g
	 * @param toAnimate
	 *            --- list of tokens to be animated
	 */
	public void animateRight(Graphics2D g, List<BoardPiece> toAnimate) {
		for (BoardPiece bp : toAnimate) {
			if (bp == null) {
				continue;
			}
			animateMove(g, bp);
			if (bp.getMoveX() < bp.getDestX()) { // increase moveX to give the effect of animating right
				bp.setMoveX(bp.getMoveX() + 2);
			} else {
				moveAnimation = false;
				skip = false;
				if (chosenToken != null) {
					letter = chosenToken.getName();
					game.moveToken(run.getCurrentPlayer(), "move " + letter + " right"); // actually move the token
					checkForMoreReactions();
				}
				chosenToken = null;
			}
		}
		if (skip == false) {
			everyBpToAnimate.clear();
		}
	}

	/**
	 * Animates the tokens in the given list down. The y value of the tokens keep
	 * increasing until a certain value which is their destination.
	 *
	 * @param g
	 * @param toAnimate
	 *            --- list of tokens to be animated
	 */
	public void animateDown(Graphics2D g, List<BoardPiece> toAnimate) {
		for (BoardPiece bp : toAnimate) {
			if (bp == null) {
				continue;
			}
			animateMove(g, bp);
			if (bp.getMoveY() < bp.getDestY()) {
				bp.setMoveY(bp.getMoveY() + 2); // increase moveY to give the effect of animating down
			} else {
				moveAnimation = false;
				skip = false;
				if (chosenToken != null) {
					letter = chosenToken.getName();
					game.moveToken(run.getCurrentPlayer(), "move " + letter + " down"); // actually move the token
					checkForMoreReactions();
				}
				chosenToken = null;
			}
		}
		if (skip == false) {
			everyBpToAnimate.clear();
		}
	}

	/**
	 * Animates the tokens in the given list up. The y value of the tokens keep
	 * decreasing until a certain value which is their destination.
	 *
	 * @param g
	 * @param toAnimate
	 *            --- list of tokens to be animated
	 */
	public void animateUp(Graphics2D g, List<BoardPiece> toAnimate) {
		for (BoardPiece bp : toAnimate) {
			if (bp == null) {
				continue;
			}
			animateMove(g, bp);
			if (bp.getMoveY() > bp.getDestY()) { // decrease moveY to give the effect of animating up
				bp.setMoveY(bp.getMoveY() - 2);
			} else {
				moveAnimation = false;
				skip = false;
				if (chosenToken != null) {
					letter = chosenToken.getName();
					game.moveToken(run.getCurrentPlayer(), "move " + letter + " up"); // actually move the token
					checkForMoreReactions();
				}
				chosenToken = null;
			}
		}
		if (skip == false) {
			for (BoardPiece bp : toAnimate) {
				bp.setNeedToAnimate(false);
			}
			everyBpToAnimate.clear();
		}
	}

	public Color getColor(int row, int col) {
		if ((row + col) % 2 != 1) {
			return new Color(255, 255, 255);
		} else {
			return new Color(0, 0, 0);
		}
	}

	public void drawBoard(Graphics2D g) {
		WIDTH = Math.min(getWidth(), getHeight()) / 10 - Math.min(getWidth(), getHeight()) / 60;
		HEIGHT = Math.min(getWidth(), getHeight()) / 10 - Math.min(getWidth(), getHeight()) / 60;
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board.length; col++) {
				if (board[row][col] instanceof InvalidSquare) {
					g.setColor(Color.GRAY);
					g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
				} else if (row == 2 && col == 2) { // draw green creation grid
					if (board[row][col] instanceof BoardPiece) { // check if theres a token there
						if ((((BoardPiece) board[row][col]).equals(chosenToken) && moveAnimation)) {
							g.setColor(GREEN_CREATION);
							g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
							continue;
						}
						BoardPiece temp = (BoardPiece) board[row][col];
						if (temp.isNeedToAnimate()) {
							g.setColor(new Color(144, 238, 144));
							g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
							continue;
						}
						g.setColor(TOKEN_SQUARE);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
						if (temp.getCol().equals("yellow")) {
							g.setColor(Color.YELLOW);
						} else {
							g.setColor(Color.GREEN);
						}
						g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(6));

						temp.setxLoc(col * WIDTH);
						temp.setyLoc(row * HEIGHT);
						drawToken(g, (BoardPiece) board[row][col], col * WIDTH, row * HEIGHT);
						g.setStroke(new BasicStroke(0));
						drawLetter(g, temp, row, col);
					} else {
						g.setColor(GREEN_CREATION);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					}
				} else if (row == 7 && col == 7) { // draw yellow creation grid
					if (board[row][col] instanceof BoardPiece) { // check if theres a token there
						if ((((BoardPiece) board[row][col]).equals(chosenToken) && moveAnimation)) {
							g.setColor(new Color(255, 250, 205));
							g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
							continue;
						}
						BoardPiece temp = (BoardPiece) board[row][col];
						if (temp.isNeedToAnimate()) {
							g.setColor(new Color(255, 250, 205));
							g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
							continue;
						}
						g.setColor(TOKEN_SQUARE);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
						if (temp.getCol().equals("yellow")) {
							g.setColor(Color.YELLOW);
						} else {
							g.setColor(Color.GREEN);

						}
						g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						g.setColor(Color.red);
						g.setStroke(new BasicStroke(6));

						temp.setxLoc(col * WIDTH);
						temp.setyLoc(row * HEIGHT);
						drawToken(g, (BoardPiece) board[row][col], col * WIDTH, row * HEIGHT);
						g.setStroke(new BasicStroke(0));
						drawLetter(g, temp, row, col);
					} else {
						g.setColor(YELLOW_CREATION);
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
					}
				} else if (board[row][col] instanceof BoardPiece) { // draw board piece
					g.setColor(TOKEN_SQUARE);
					g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, WIDTH);
					BoardPiece temp = (BoardPiece) board[row][col];
					if (temp.isNeedToAnimate()) {
						g.setColor(getColor(row, col));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						continue;
					}
					if (temp.equals(chosenToken) && moveAnimation && !disappearAnimation) {
						g.setColor(getColor(row, col));
						g.fillRect(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT);
						continue;
					}
					temp.setxLoc(col * WIDTH);
					temp.setyLoc(row * HEIGHT);
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
					drawLetter(g, temp, row, col);
				} else if (board[row][col] == null && row == 8 && col == 8) { // draw cross when yellow dies
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(6));
					g.drawLine(col * WIDTH + STROKE, row * HEIGHT + STROKE, (col + 1) * WIDTH - STROKE,
							(row + 1) * HEIGHT - STROKE);
					g.drawLine(col * WIDTH - STROKE, (row + 1) * HEIGHT + STROKE, (col + 1) * WIDTH - STROKE,
							(row) * HEIGHT + STROKE);
					g.setStroke(new BasicStroke(0));
				} else if (board[row][col] == null && row == 1 && col == 1) { // draw cross when green dies
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(6));
					g.drawLine(col * WIDTH + STROKE, row * HEIGHT + STROKE, (col + 1) * WIDTH - STROKE,
							(row + 1) * HEIGHT - STROKE);
					g.drawLine(col * WIDTH - STROKE, (row + 1) * HEIGHT + STROKE, (col + 1) * WIDTH - STROKE,
							(row) * HEIGHT + STROKE);
					g.setStroke(new BasicStroke(0));

				} else if (board[row][col] instanceof Player) { // draw player
					Player p = (Player) board[row][col];
					if (p.getName().equals("yellow")) {
						g.setColor(Color.YELLOW);
					} else {
						g.setColor(Color.GREEN);
					}
					g.fillOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT); // the face
					g.setColor(Color.black);
					g.drawOval(col * WIDTH, row * HEIGHT, WIDTH, HEIGHT); // face outline
					g.setColor(Color.BLACK);
					g.fillOval(col * WIDTH + WIDTH / 3, row * HEIGHT + HEIGHT / 3, WIDTH / 10, HEIGHT / 10); // eyes
					g.fillOval(col * WIDTH - WIDTH / 3 + WIDTH - 5, row * HEIGHT + HEIGHT / 3, WIDTH / 10, WIDTH / 10); // eyes
					g.drawArc(col * WIDTH + WIDTH / 3, row * HEIGHT + HEIGHT / 3, WIDTH / 3, HEIGHT / 3, 180, 180); // mouth
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

	/**
	 * Method draws either a 'M' or an 'R' depending on if its been rotated or
	 * moved. It draws in a white color 1 pixel to the left, right, up and down from
	 * the destination x and y, and then drawing the letter in the middle in black.
	 * This gives it the effect of the letter having a white outline.
	 *
	 * @param g
	 * @param temp
	 *            --- the board piece to draw on
	 * @param row
	 *            --- the row of the board piece
	 * @param col
	 *            --- the column of the board pirce
	 */
	public void drawLetter(Graphics2D g, BoardPiece temp, int row, int col) {
		WIDTH = Math.min(getWidth(), getHeight()) / 10 - Math.min(getWidth(), getHeight()) / 60;
		HEIGHT = Math.min(getWidth(), getHeight()) / 10 - Math.min(getWidth(), getHeight()) / 60;
		int size = (16 * (WIDTH + 40)) / 100;
		g.setFont(new Font("Serif", Font.BOLD, size));
		if (game.getYellow().getMovesSoFar().contains(temp.getName())) {
			g.setColor(Color.WHITE);
			g.drawString("M", col * WIDTH - 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("M", col * WIDTH - 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.drawString("M", col * WIDTH + 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("M", col * WIDTH + 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.setColor(Color.BLACK);
			g.drawString("M", col * WIDTH, row * HEIGHT + HEIGHT / 4);
		} else if (game.getYellow().getEveryMovement().contains(temp)) {
			g.setColor(Color.WHITE);
			g.drawString("R", col * WIDTH - 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("R", col * WIDTH - 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.drawString("R", col * WIDTH + 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("R", col * WIDTH + 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.setColor(Color.BLACK);
			g.drawString("R", col * WIDTH, row * HEIGHT + HEIGHT / 4);
		}
		if (game.getGreen().getMovesSoFar().contains(temp.getName())) {
			g.setColor(Color.WHITE);
			g.drawString("M", col * WIDTH - 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("M", col * WIDTH - 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.drawString("M", col * WIDTH + 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("M", col * WIDTH + 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.setColor(Color.BLACK);
			g.drawString("M", col * WIDTH, row * HEIGHT + HEIGHT / 4);
		} else if (game.getGreen().getEveryMovement().contains(temp)) {
			g.setColor(Color.WHITE);
			g.drawString("R", col * WIDTH - 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("R", col * WIDTH - 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.drawString("R", col * WIDTH + 1, row * HEIGHT + 1 + HEIGHT / 4);
			g.drawString("R", col * WIDTH + 1, row * HEIGHT - 1 + HEIGHT / 4);
			g.setColor(Color.BLACK);
			g.drawString("R", col * WIDTH, row * HEIGHT + HEIGHT / 4);
		}
	}

	/**
	 * Draws the the board pieces swords and shields appropriately in the given x
	 * and y coordinates. The x and y coordinates refer to point(0,0) of the board
	 * piece token square.
	 *
	 * @param g
	 * @param piece
	 *            --- the piece whose swords and shields are being drawn
	 * @param x
	 *            --- x coordinate of the board piece square
	 * @param y
	 *            --- y coordinate of the board piece square
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

	/**
	 * Draws the the board pieces swords and shields appropriately in the given x
	 * and y coordinates. The x and y coordinates refer to point(0,0) of the board
	 * piece token square. The difference with this method is that the width and
	 * height are larger.
	 *
	 * @param g
	 * @param piece
	 *            --- the piece whose swords and shields are being drawn
	 * @param x
	 *            --- x coordinate of the board piece square
	 * @param y
	 *            --- y coordinate of the board piece square
	 */
	private void drawHugeTokenParts(Graphics2D g, BoardPiece piece, int x, int y) {
		if (piece.getNorth() == 1) {
			g.drawLine(x + WIDTH * 6 / 2, y + STROKE, x + WIDTH * 6 / 2, y + HEIGHT * 6 / 2);
		} else if (piece.getNorth() == 2) {
			g.drawLine(x + STROKE, y + STROKE, x + WIDTH * 6 - STROKE, y + STROKE);
		}
		if (piece.getEast() == 1) {
			g.drawLine(x + WIDTH * 6 / 2 + STROKE, y + HEIGHT * 6 / 2, x + WIDTH * 6 - STROKE, y + HEIGHT * 6 / 2);
		} else if (piece.getEast() == 2) {
			g.drawLine(x + WIDTH * 6 - STROKE, y + STROKE, x + WIDTH * 6 - STROKE, y + HEIGHT * 6 - STROKE);
		}
		if (piece.getSouth() == 1) {
			g.drawLine(x + WIDTH * 6 / 2, y + HEIGHT * 6 / 2, x + WIDTH * 6 / 2, y + HEIGHT * 6 - STROKE);
		} else if (piece.getSouth() == 2) {
			g.drawLine(x + STROKE, y + HEIGHT * 6 - STROKE, x + WIDTH * 6 - STROKE, y + HEIGHT * 6 - STROKE);
		}
		if (piece.getWest() == 1) {
			g.drawLine(x + STROKE, y + HEIGHT * 6 / 2, x + WIDTH * 6 / 2 - STROKE, y + HEIGHT * 6 / 2);
		} else if (piece.getWest() == 2) {
			g.drawLine(x + STROKE, y + STROKE, x + STROKE, y + HEIGHT * 6 - STROKE);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 640);
	}

	public boolean isDisappearAnimation() {
		return disappearAnimation;
	}

	public boolean isSWEDisappear() {
		return SWEDisappear;
	}

	public boolean isMoveAnimation() {
		return moveAnimation;
	}

	public boolean isRotationAnimation() {
		return rotationAnimation;
	}

	public void setMoveAnimation(boolean moveAnimation) {
		this.moveAnimation = moveAnimation;
	}

}
