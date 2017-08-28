package Model;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class holds the main board, and deals with most of the drawing. This class is responsible
 * for what the user can see
 * @author Chin Patel
 */

public class Board {
	private Player green;
	private Player yellow;
	private boolean gameEnded = false;
	private Token[][] board = new Token[10][10]; // Main board that is drawn in the console and contains tokens
	private Stack<Token[][]> undoStack = new Stack<Token[][]>(); // Stack that is used for the undo command
	private List<Pair> reactions = new ArrayList<Pair>();
	private static final String SEPARATOR = "     "; // Separator between token and board, and board and token
	private static final String DOUBLE_SEPARATOR = "          "; // Width of two squares of the board
	private static final String SEPERATOR_X5 = "                         "; // Space of 5 separators
	private static final String EDGESEPARATOR = "                              "; // Distance between edge and board
	private static final String GRAVEYARD_SEPARATOR = "                         "; // Gap between the two grave yards
	private static final String TLINE = "-------------------------"; // Token board line
	private static final String BLINE = "-------------------------------------------------------------"; // Board line
	private static final String GLINE = "-------------------------------------------------"; //Grave yard line
	private static final String INVALID_SQUARE = "|:::::"; // The three out of bounds areas behind each player

	/**
	 * Initisalises the board to nulls
	 */
	public void initialise() {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board.length; c++) {
				board[r][c] = null;
			}
		}
	}

	/**
	 * Adds the two players onto the board, in their specified locations
	 * @param green --- green player which is being added
	 * @param yellow --- yellow player which is being added
	 */
	public void addPlayers(Player green, Player yellow) {
		board[1][1] = green;
		board[8][8] = yellow;
	}

	/**
	 * Adds the 6 invalid squares, 3 behind each player
	 */
	public void addInvalidSquares() {
		board[0][0] = new InvalidSquare();
		board[0][1] = new InvalidSquare();
		board[1][0] = new InvalidSquare();
		board[8][9] = new InvalidSquare();
		board[9][8] = new InvalidSquare();
		board[9][9] = new InvalidSquare();
	}

	/**
	 * Method checks for reactions between board pieces and player. This is done by
	 * finding tokens that are adjacent to each other, and if either of them has a
	 * sword pointing to its adjacent pair, the pair is put in a list of reactions,
	 * containing pairs.
	 *
	 * @return
	 */
	public boolean checkForReaction() {
		reactions.clear();
		// Checks for horizontal reactions between two board pieces (if a sword is involved)
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length - 1; c++) {
				if (board[r][c] instanceof BoardPiece && board[r][c + 1] instanceof BoardPiece) {
					BoardPiece temp1 = (BoardPiece) board[r][c];
					BoardPiece temp2 = (BoardPiece) board[r][c + 1];
					if (temp1.getEast() == 1 || temp2.getWest() == 1) {
						reactions.add(new Pair(temp1, temp2, "hori"));
					}
				}
			}
		}
		// Checks for vertical reactions between two board pieces (if a sword is involved)
		for (int r = 0; r < board.length - 1; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] instanceof BoardPiece && board[r + 1][c] instanceof BoardPiece) {
					BoardPiece temp1 = (BoardPiece) board[r][c];
					BoardPiece temp2 = (BoardPiece) board[r + 1][c];
					if (temp1.getSouth() == 1 || temp2.getNorth() == 1) {
						reactions.add(new Pair(temp1, temp2, "vert"));
					}
				}
			}
		}
		// Checks for horizontal reaction between the green player and a board piece (if a sword is involved)
		if (board[1][1] instanceof Player && board[1][2] instanceof BoardPiece) {
			BoardPiece temp1 = (BoardPiece) board[1][2];
			Player temp2 = (Player) board[1][1];
			if (temp1.getWest() == 1) {
				reactions.add(new Pair(temp1, temp2, "hori"));
			}
		}
		// Checks for a vertical reaction between a green player and a board piece (if a sword is involved)
		if (board[1][1] instanceof Player && board[2][1] instanceof BoardPiece) {
			BoardPiece temp1 = (BoardPiece) board[2][1];
			Player temp2 = (Player) board[1][1];
			if (temp1.getNorth() == 1) {
				reactions.add(new Pair(temp1, temp2, "vert"));

			}
		}
		// Checks for a vertical reaction between a yellow player and a board piece (if a sword is involved)
		if (board[8][8] instanceof Player && board[7][8] instanceof BoardPiece) {
			BoardPiece temp1 = (BoardPiece) board[7][8];
			Player temp2 = (Player) board[8][8];
			if (temp1.getSouth() == 1) {
				reactions.add(new Pair(temp1, temp2, "vert"));
			}
		}
		// Checks for a horizontal reaction between a yellow player and a board piece (if a sword is involved)
		if (board[8][8] instanceof Player && board[8][7] instanceof BoardPiece) {
			BoardPiece temp1 = (BoardPiece) board[8][7];
			Player temp2 = (Player) board[8][8];
			if (temp1.getEast() == 1) {
				reactions.add(new Pair(temp1, temp2, "hori"));
			}
		}
		return reactions.isEmpty() ? false : true;
	}

	/**
	 * Returns the column index of the specified token letter
	 * @param letter --- letter of the token we are searching for
	 * @return --- the column index
	 */
	public int getX(String letter) {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) board[r][c];
					if (temp.getName().equals(letter)) {
						return c;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the row index of the specified token letter
	 * @param letter --- letter of the token we are searching for
	 * @return --- the row index
	 */
	public int getY(String letter) {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) board[r][c];
					if (temp.getName().equals(letter)) {
						return r; // r is y
					}
				}
			}
		}
		return -1;
	}

	private void drawTopRowBoard(int r) {
		for (int c = 0; c < board[0].length; c++) {
			if (board[r][c] instanceof BoardPiece) {
				BoardPiece temp = (BoardPiece) board[r][c];
				System.out.print(getNorth(temp)); // Deal with North
			} else if ((r == 0 && c < 2)) {
				System.out.print(INVALID_SQUARE);
			} else if (r == 1 && c == 0) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 8 && c == 9)) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 9 && c > 7)) {
				System.out.print(INVALID_SQUARE);
			} else {
				System.out.print("|     ");
			}
		}
	}

	private void drawTopRowTokens(Player player, int r) {
		for (int c = 0; c < player.getTokens()[0].length; c++) {
			if (player.getTokens()[r][c] != null) {
				System.out.print(getNorth(player.getTokens()[r][c])); // Deal with North
			} else {
				System.out.print("|     ");
			}
		}
	}

	private void drawMiddleRowBoard(int r) {
		for (int i = 0; i < 10; i++) {
			if (board[r][i] instanceof Player) { // Write the players
				System.out.print(r == 1 && i == 1 ? "|green" : "|yelow");
			} else if (board[r][i] instanceof BoardPiece) { // Logic for drawing the tokens in the array
				BoardPiece temp = (BoardPiece) board[r][i];
				System.out.print(getWest(temp) + temp.getName() + getEast(temp));
			} else if (r == 2 && i == 2 && !(board[2][2] instanceof Token)) { // Draw creation box for green
				System.out.print("| [ ] ");
			} else if (r == 7 && i == 7 && !(board[7][7] instanceof Token)) { // Draw creation box for yellow
				System.out.print("| [ ] ");
			} else if ((r == 1 && i == 0)) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 0 && i < 2)) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 8 && i == 9)) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 9 && i > 7)) {
				System.out.print(INVALID_SQUARE);
			} else {
				System.out.print("|     ");
			}
		}
	}

	private void drawMiddleRowTokens(Player player, int r) {
		if (r < player.getTokens().length) {
			for (int i = 0; i < player.getTokens()[0].length; i++) {
				if (player.getTokens()[r][i] != null) { // Logic for drawing the tokens in the array
					System.out.print(getWest(player.getTokens()[r][i]) + player.getTokens()[r][i].getName()
							+ getEast(player.getTokens()[r][i])); // Deal with west, name, and east
				} else {
					System.out.print("|     ");
				}
			}
			System.out.print("|" + SEPARATOR);
		} else {
			System.out.print(EDGESEPARATOR);
		}
	}

	private void drawLastRowBoard(int r) {
		for (int i = 0; i < 10; i++) {
			if (board[r][i] instanceof BoardPiece) { // Logic for drawing the tokens in the array
				BoardPiece temp = (BoardPiece) board[r][i];
				System.out.print(getSouth(temp)); // Deal with South
			} else if ((r == 1 && i == 0)) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 0 && i < 2)) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 8 && i == 9)) {
				System.out.print(INVALID_SQUARE);
			} else if ((r == 9 && i > 7)) {
				System.out.print(INVALID_SQUARE);
			} else {
				System.out.print("|     ");
			}
		}
	}

	private void drawLastRowTokens(Player player, int r) {
		for (int i = 0; i < player.getTokens()[0].length; i++) {
			if (player.getTokens()[r][i] != null) {
				System.out.print(getSouth(player.getTokens()[r][i])); // Deal with South
			} else {
				System.out.print("|     ");
			}
		}
	}

	public void temp() {
		System.out.println("\n\n");
		System.out.println(SEPARATOR + "~~Green Tokens~~" + EDGESEPARATOR + "  ~~Game Board~~" + EDGESEPARATOR
				+ "   ~~Yellow Tokens~~");
		System.out.println(TLINE + SEPARATOR + BLINE + SEPARATOR + TLINE);
		for (int r = 0; r < board.length; r++) {
			// ~~~~~~~~~ TOP ROW ~~~~~~~~~~~~~~~
			// Top row of player tokens
			if (r < green.getTokens().length) {
				drawTopRowTokens(green, r);
				System.out.print("|" + SEPARATOR);
			} else {
				System.out.print(EDGESEPARATOR);
			}
			// Top row for board
			drawTopRowBoard(r);
			// Top row for yellow tokens
			if (r < yellow.getTokens().length) {
				System.out.print("|" + SEPARATOR);
				drawTopRowTokens(yellow, r);
			}

			System.out.println("|");
			// ~~~~~~~~~~~~ MIDDLE ROW ~~~~~~~~~~
			// Middle row for green tokens
			drawMiddleRowTokens(green, r);
			// Middle row for board
			drawMiddleRowBoard(r);
			System.out.print("|" + SEPARATOR);
			// Middle row for yellow tokens
			drawMiddleRowTokens(yellow, r);
			System.out.println("");

			// ~~~~~~~ LAST ROW ~~~~~~~~~~~~~
			// Last row for green tokens
			if (r < green.getTokens().length) {
				drawLastRowTokens(green, r);
				System.out.print("|" + SEPARATOR);
			} else {
				System.out.print(EDGESEPARATOR);
			}
			// Last row for board
			drawLastRowBoard(r);
			// Last row for yellow tokens
			if (r < yellow.getTokens().length) {
				System.out.print("|" + SEPARATOR);
				drawLastRowTokens(yellow, r);
			}
			System.out.println("|");
			// If the players tokens are drawn then the appropriate space needs to be added
			// between the edge and board
			if (r < yellow.getTokens().length) {
				System.out.print(TLINE + SEPARATOR + BLINE + SEPARATOR + TLINE);
				System.out.println();
			} else {
				System.out.print(EDGESEPARATOR + BLINE);
				System.out.println();
			}
		}
	}

	/**
	 * Method draws the board. The way it draws the board is goes through each row.
	 * For each row it draws the top row of the green players tokens, top row for
	 * the board, and then the top row for the yellow players tokens.
	 */
	public void redraw() {
		if (gameEnded == false) {
			addPlayers(green, yellow);
		}
		addInvalidSquares();
		//temp();
		// Update grave yards and draw them
		green.getDifferences().clear();
		yellow.getDifferences().clear();
		green.updateGraveyard(board);
		yellow.updateGraveyard(board);
		populateGraveyard(green, green.getDifferences());
		populateGraveyard(yellow, yellow.getDifferences());
		drawGraveYard();
	}

	/**
	 * Fill up the players grave yard
	 * @param player
	 * @param deadTokens --- list of all dead tokens
	 */
	private void populateGraveyard(Player player, List<BoardPiece> deadTokens) {
		int size = deadTokens.size();
		for (int i = size; i < 24; i++) {
			deadTokens.add(null);
		}
		int i = 0;
		for (int r = 0; r < player.getGraveYard().length; r++) {
			for (int c = 0; c < player.getGraveYard()[0].length; c++) {
				player.getGraveYard()[r][c] = deadTokens.get(i++);
			}
		}
	}

	private void drawTopRowGraveYard(Player player, int r) {
		for (int c = 0; c < player.getGraveYard()[0].length; c++) {
			if (player.getGraveYard()[r][c] != null) {
				System.out.print(getNorth(player.getGraveYard()[r][c])); // Deal with North
			} else {
				System.out.print("|     ");
			}
		}
	}

	private void drawMiddleRowGraveYard(Player player, int r) {
		for (int i = 0; i < player.getGraveYard()[0].length; i++) {
			if (player.getGraveYard()[r][i] != null) { // Logic for drawing the tokens in the array
				System.out.print(getWest(player.getGraveYard()[r][i]) + player.getGraveYard()[r][i].getName()
						+ getEast(player.getGraveYard()[r][i]));
			} else {
				System.out.print("|     ");
			}
		}
	}

	private void drawLastRowGraveYard(Player player, int r) {
		for (int i = 0; i < player.getGraveYard()[0].length; i++) {
			if (player.getGraveYard()[r][i] != null) {
				System.out.print(getSouth(player.getGraveYard()[r][i])); // Deal with South
			} else {
				System.out.print("|     ");
			}
		}
	}

	private void drawGraveYard() {
		System.out.println();
		System.out.println(DOUBLE_SEPARATOR + SEPARATOR + "~~Green GraveYard~~ " + DOUBLE_SEPARATOR + GRAVEYARD_SEPARATOR
				+ DOUBLE_SEPARATOR + DOUBLE_SEPARATOR + "~~Yellow GraveYard~~ ");
		System.out.println(GLINE + SEPERATOR_X5 + GLINE);
		for (int r = 0; r < yellow.getGraveYard().length; r++) {
			// Top row green
			drawTopRowGraveYard(green, r);
			System.out.print("|" + SEPERATOR_X5);
			// Top row yellow
			drawTopRowGraveYard(yellow, r);
			System.out.println("|");
			// Middle Row green
			drawMiddleRowGraveYard(green, r);
			System.out.print("|" + SEPERATOR_X5);
			// Middle Row yellow
			drawMiddleRowGraveYard(yellow, r);
			System.out.println("|");
			// Last row green
			drawLastRowGraveYard(green, r);
			System.out.print("|" + SEPERATOR_X5);
			// Last row yellow
			drawLastRowGraveYard(yellow, r);
			System.out.println("|");
			System.out.println(GLINE + SEPERATOR_X5 + GLINE);
		}
	}

	private String getNorth(BoardPiece b) {
		return (b.getNorth() == 0) ? "|     " : (b.getNorth() == 1) ? "|  |  " : "|  +  ";
	}
	private String getSouth(BoardPiece b) {
		return (b.getSouth() == 0) ? "|     " : (b.getSouth() == 1) ? "|  |  " : "|  +  ";
	}
	private String getEast(BoardPiece b) {
		return (b.getEast() == 0) ? "  " : (b.getEast() == 1) ? "- " : "+ ";
	}
	private String getWest(BoardPiece b) {
		return (b.getWest() == 0) ? "|  " : (b.getWest() == 1) ? "| -" : "| +";
	}

	/**
	 * Method that deep clones the board, after every successful move. The deep cloned board is put into
	 * a stack, for later use when the user wants to undo. It is cloned by going through a board, and putting new
	 * board pieces into a new 2D array.
	 */
	public void createRecord() {
		Token[][] record = new Token[10][10];
		for (int r = 0; r < record.length; r++) {
			for (int c = 0; c < record[0].length; c++) {
				if (board[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) board[r][c];
					BoardPiece newBP = new BoardPiece(temp.getName(), temp.getNorth(), temp.getEast(), temp.getSouth(),
							temp.getWest(), temp.getCol());
					record[r][c] = newBP;

				} else {
					record[r][c] = board[r][c];
				}
			}
		}
		undoStack.push(record);
	}

	/**
	 * Method goes through the 2D array popped from the stack and rewrites the original board. Used when
	 * the user undos.
	 */
	public void setBoard() {
		Token[][] original = undoStack.pop(); // Need to pop out the current identical version in the stack
		Token[][] setter = undoStack.pop();
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				board[r][c] = setter[r][c];
			}
		}
	}

	/**
	 * Kills the token by setting it to null. This is used in reactions when there is a sword v sword
	 * and or sword v nothing.
	 * @param letter --- the letter of the token to be killed
	 */
	public void killToken(String letter) {
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) board[r][c];
					if (temp.getName().equals(letter)) {
						board[r][c] = null;
						temp = null;
					}
				}
			}
		}
	}

	/**
	 * Kills the player by setting it to null. This is used in reactions when there is a sword up against a player.
	 * @param player
	 */
	public void killPlayer(String player) {
		if (player.equals("green")) {
			gameEnded = true;
			board[1][1] = null;
		} else if (player.equals("yellow")) {
			board[8][8] = null;
			gameEnded = true;
			redraw();
		}
	}


	/**
	 * Finds the token wanted to be moved, and returns that token
	 * @param player --- player who wants to move the token
	 * @param letter --- the letter of the token the player wants to move
	 * @return --- returns the boardPeice or null
	 */
	public BoardPiece findMoveToken(Player player, String letter) {
		BoardPiece returnToken = null;
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) board[r][c];
					if (temp.getCol().equals(player.getName()) && temp.getName().equals(letter)) {
						returnToken = temp;
						return returnToken;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Finds the boardPiece corresponding the letter and returns it. If it doesn't exist returns null.
	 * @param letter --- letter of the token which is being returned if not null
	 * @return
	 */
	public BoardPiece findToken(String letter) {
		BoardPiece returnToken = null;
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (board[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) board[r][c];
					if (temp.getName().equals(letter)) {
						returnToken = temp;
						return returnToken;
					}
				}
			}
		}
		return null;
	}

	public List<Pair> getReactions() {
		return reactions;
	}

	public Stack<Token[][]> getUndoStack() {
		return undoStack;
	}

	public Token[][] getBoard() {
		return board;
	}

	public void setGameEnded(boolean gameEnded) {
		this.gameEnded = gameEnded;
	}

	public void setGreen(Player green) {
		this.green = green;
	}

	public void setYellow(Player yellow) {
		this.yellow = yellow;
	}
}