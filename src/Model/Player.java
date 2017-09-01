package Model;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class represents the player, and is a type of a token. The player class
 * holds it own collection of tokens, and their own grave yard.
 *
 * @author Chin Patel
 *
 */

public class Player implements Token {
	private String name = "";
	private int originalCount = 0;
	private int setterCount = 0;
	private BoardPiece[][] tokens = new BoardPiece[6][4]; // players set of 24 board pieces
	private BoardPiece[][] graveYard = new BoardPiece[3][8]; // players grave yard
	private Stack<BoardPiece[][]> undoStack = new Stack<BoardPiece[][]>(); // stack to undo
	private List<String> movesSoFar = new ArrayList<String>(); // contains names of the moved pieces
	private List<BoardPiece> everyMovement = new ArrayList<BoardPiece>(); // contains each moved piece and rotated pieces
	private List<BoardPiece> listOfTokens = new ArrayList<BoardPiece>(); // list of the players 24 tokens
	private List<BoardPiece> differences = new ArrayList<BoardPiece>(); // holds the grave yard board pieces
	public int numMoveAnimations = 0;

	/**
	 * Constructor which takes the player name, (yellow/green).
	 * @param name
	 */
	public Player(String name) {
		this.name = name;
	}


	/**
	 * This method adds the token to the board. It checks if the creation spot is
	 * not already taken. If it is it will return false, otherwise try to find the
	 * token in the players set of token, if it exists it will add the token to the
	 * board, and return true. Otherwise it will return false.
	 *
	 * @param letter
	 *            --- letter of the token which is intended to be added
	 * @param player
	 *            --- the player in whose token we are adding
	 * @param board
	 *            --- the current board
	 * @return
	 */
	public boolean addToken(String letter, Player player, Board board) {
		if (checkValidCreationSpot(board, player.name) == false) { // Check if the creation spot is already taken or not
			System.out.println("Invalid Move\nCreation Spot is already taken");
			return false;
		}
		BoardPiece tokenToAdd = null;
		tokenToAdd = find(letter);
		if (tokenToAdd != null) {
			if (player.name.equals("green")) { // Add it to the correct creation spot
				board.getBoard()[2][2] = tokenToAdd;
			} else {
				board.getBoard()[7][7] = tokenToAdd;
			}
			return true;
		}
		return false;
	}



	/**
	 * This method goes through the players set of tokens and tries to find the
	 * letter, which corresponds to the token which it returns.
	 *
	 * @param player
	 * @param letter
	 * @return
	 */
	public BoardPiece find(String letter) {
		BoardPiece returnToken = null;
		for (int r = 0; r < tokens.length; r++) {
			for (int c = 0; c < tokens[0].length; c++) {
				if (tokens[r][c] == null) {
					continue;
				}
				if (tokens[r][c].getName().equals(letter)) {
					returnToken = tokens[r][c];
					tokens[r][c] = null;
					return returnToken;
				}
			}
		}
		return null;
	}

	/**
	 * Helper method that checks if the creation spot is available to create more
	 * tokens or not for the given player. Returns true/false accordingly.
	 *
	 * @param board
	 * @param color
	 *            --- color representing the player (i.e yellow/green)
	 * @return
	 */
	public boolean checkValidCreationSpot(Board board, String color) {
		if (color.equals("green")) { // greens creation spot
			if (board.getBoard()[2][2] instanceof BoardPiece) {
				return false;
			}
		}
		if (color.equals("yellow")) { // yellows creation spot
			if (board.getBoard()[7][7] instanceof BoardPiece) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method which fills up each players tokens from the hard coded list of tokens.
	 *
	 * @param player
	 * @param toks
	 *            --- hard coded tokens
	 */
	public void populateTokens(Player player, List<BoardPiece> toks) {
		int i = 0; // index for the tokens list
		for (int r = 0; r < tokens.length; r++) {
			for (int c = 0; c < tokens[0].length; c++) {
				tokens[r][c] = toks.get(i++);
				tokens[r][c].setCol(name);
			}
		}
	}

	/**
	 * Method tries moving tokens in the specified direction.
	 * @param player --- player trying to move token
	 * @param token --- token moving
	 * @param dir --- direction token is moving
	 * @param board -- current board
	 */
	public void tryMoveToken(Player player, BoardPiece token, String dir, Board board) {
		if (dir.equals("up")) {
			moveUp(player, token, board);
		} else if (dir.equals("down")) {
			moveDown(player, token, board);
		} else if (dir.equals("left")) {
			moveLeft(player, token, board);
		} else if (dir.equals("right")) {
			moveRight(player, token, board);
		} else {
			System.out.println("Cant move token");
		}
	}

	/**
	 * Calculates and returns the number of adjacent tiles to the right of the board piece given.
	 * @param token --- The board piece being counted across from
	 * @param board --- the current game state
	 * @return --- the number of adjacent tiles
	 */
	public int rightCounter(BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (c + 1 > 9) {
			return -1;
		} else if (!(board.getBoard()[r][c + 1] instanceof BoardPiece) && !(c + 1 > 9)) {
			return 0;
		} else {
			for (int i = c + 1, j = 0; i < board.getBoard().length; i++, j++) {
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) {// calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Calculates and returns the number of adjacent tiles to the left of the board piece given.
	 * @param token --- The board piece being counted across from
	 * @param board --- the current game state
	 * @return --- the number of adjacent tiles
	 */
	public int leftCounter(BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (c - 1 < 0) {
			return -1;
		} else if (!(board.getBoard()[r][c - 1] instanceof BoardPiece) && !(c - 1 < 0)) {
			return 0;
		} else {
			for (int i = c - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) {// calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Calculates and returns the number of adjacent tiles above the board piece given.
	 * @param token --- The board piece being counted up from
	 * @param board --- the current game state
	 * @return --- the number of adjacent tiles
	 */
	public int upCounter(BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (r - 1 < 0) {
			return -1;
		} else if (!(board.getBoard()[r - 1][c] instanceof BoardPiece) && !(r - 1 < 0)) {
			return 0;
		} else { // requires shifting
			for (int i = r - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) { //calculate number of adjacent tiles going from the the tile being pushed
					count++;
				}
			}

		}
		return count;
	}

	/**
	 * Calculates and returns the number of adjacent tiles below the board piece given.
	 * @param token --- The board piece being counted down from
	 * @param board --- the current game state
	 * @return --- the number of adjacent tiles
	 */
	public int downCounter(BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (r + 1 > 9) {
			return -1;
		} else if (!(board.getBoard()[r + 1][c] instanceof BoardPiece) && !(r + 1 > 9)) {
			return 0;
		} else {
			for (int i = r + 1, j = 0; i < board.getBoard().length; i++, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) {// calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * This method moves the token up. If there is a token where it is trying to move to,
	 * it will push it up. It finds the number of adjacent tiles of the tile that is being pushed,
	 * by the moving tile. With this number it can loop through backwards, so from the
	 * row - number of adjacent tiles, off setting each tile by - 1 row. If it tries moving out of
	 * bounds the token will die.
	 * @param player --- player trying to move token
	 * @param token --- token being moved
	 * @param board --- current board
	 */
	private void moveUp(Player player, BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (r - 1 < 0) { // of the board
			System.out.println("in if statement");
			board.getBoard()[r][c] = null;
		} else if (!(board.getBoard()[r - 1][c] instanceof BoardPiece) && !(r - 1 < 0)) { // movin by its self
			System.out.println("in else if statement");
			board.getBoard()[r][c] = null;
			r--;
			board.getBoard()[r][c] = token;
		} else { // requires shifting
			System.out.println("in else statement");
			for (int i = r - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) { //calculate number of adjacent tiles going from the the tile being pushed
					count++;
				}
			}
			if (count != 0) {
				numMoveAnimations = count;
				for (int i = r - count; i <= r; i++) {// go back the number the of adjacent of tiles, and shift everything up 1 working backwards
					if (i - 1 < 0) {
						board.getBoard()[i][c] = null;
					} else {
						board.getBoard()[i - 1][c] = board.getBoard()[i][c];
					}
				}
				board.getBoard()[r][c] = null; // set the tile originally moving to null
			}
		}
	}
	/**
	 * This method moves the token down. If there is a token where it is trying to move to,
	 * it will push it down. It finds the number of adjacent tiles of the tile that is being pushed,
	 * by the moving tile. With this number it can loop through backwards, so from the
	 * row + number of adjacent tiles, off setting each tile by + 1 row. If it tries moving out of
	 * bounds the token will die.
	 * @param player --- player trying to move token
	 * @param token --- token being moved
	 * @param board --- current board
	 */
	private void moveDown(Player player, BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (r + 1 > 9) {
			board.getBoard()[r][c] = null;
		} else if (!(board.getBoard()[r + 1][c] instanceof BoardPiece) && !(r + 1 > 9)) {
			board.getBoard()[r][c] = null;
			r++;
			board.getBoard()[r][c] = token;
		} else {
			for (int i = r + 1, j = 0; i < board.getBoard().length; i++, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) {// calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
			if (count != 0) {
				for (int i = r + count; i >= r; i--) {// go down the number of adjacent tiles and shift everything down by 1 working backwards
					if (i + 1 > 9) {
						board.getBoard()[i][c] = null;
					} else {
						board.getBoard()[i + 1][c] = board.getBoard()[i][c];
					}
				}
				board.getBoard()[r][c] = null;// set the tile originally moving to null
			}
		}
	}

	/**
	 * This method moves the token right. If there is a token where it is trying to move to,
	 * it will push it right. It finds the number of adjacent tiles of the tile that is being pushed,
	 * by the moving tile. With this number it can loop through backwards, so from the
	 * col + number of adjacent tiles, off setting each tile by + 1 col. If it tries moving out of
	 * bounds the token will die.
	 * @param player --- player trying to move token
	 * @param token --- token being moved
	 * @param board --- current board
	 */
	private void moveRight(Player player, BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (c + 1 > 9) {
			board.getBoard()[r][c] = null;
		} else if (!(board.getBoard()[r][c + 1] instanceof BoardPiece) && !(c + 1 > 9)) {
			board.getBoard()[r][c] = null;
			c++;
			board.getBoard()[r][c] = token;
		} else {
			for (int i = c + 1, j = 0; i < board.getBoard().length; i++, j++) {
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) {// calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
			if (count != 0) {
				for (int i = c + count; i >= c; i--) {// go right the number of adjacent tiles and shift everything down by 1 working backwards
					if (i + 1 > 9) {
						board.getBoard()[r][i] = null;
					} else {
						board.getBoard()[r][i + 1] = board.getBoard()[r][i];
					}
				}
				board.getBoard()[r][c] = null;// set the tile originally moving to null
			}
		}
	}

	/**
	 * This method moves the token left. If there is a token where it is trying to move to,
	 * it will push it left. It finds the number of adjacent tiles of the tile that is being pushed,
	 * by the moving tile. With this number it can loop through backwards, so from the
	 * col - number of adjacent tiles, off setting each tile by - 1 col. If it tries moving out of
	 * bounds the token will die.
	 * @param player --- player trying to move token
	 * @param token --- token being moved
	 * @param board --- current board
	 */
	private void moveLeft(Player player, BoardPiece token, Board board) {
		int c = board.getX(token.getName());
		int r = board.getY(token.getName());
		int count = 0;
		if (c - 1 < 0) {
			board.getBoard()[r][c] = null;
		} else if (!(board.getBoard()[r][c - 1] instanceof BoardPiece) && !(c - 1 < 0)) {
			board.getBoard()[r][c] = null;
			c--;
			board.getBoard()[r][c] = token;
		} else {
			for (int i = c - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) {// calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
			if (count != 0) {
				for (int i = c - count; i <= c; i++) {// go left the number of adjacent tiles and shift everything down by 1 working backwards
					if (i - 1 < 0) {
						board.getBoard()[r][i] = null;
					} else {
						board.getBoard()[r][i - 1] = board.getBoard()[r][i];
					}
				}
				board.getBoard()[r][c] = null;// set the tile originally moving to null
			}
		}
	}


	/**
	 * Creates a copy of the players tokens, and pushes it in the stack. This stack
	 * is popped when the user wants to undo.
	 */
	public void createRecord() {
		BoardPiece[][] record = new BoardPiece[6][4];
		for (int r = 0; r < record.length; r++) {
			for (int c = 0; c < record[0].length; c++) {
				record[r][c] = tokens[r][c];
			}
		}
		undoStack.push(record);
	}

	/**
	 * Sets the board to the previous version of the players tokens. This method is
	 * called every time after the user wants to undo, to copy over the old contents
	 * to the latest player tokens.
	 */
	public void setBoard() {
		BoardPiece[][] original = undoStack.pop(); // get rid of original
		BoardPiece[][] setter = undoStack.pop(); // this is the old copy we want to use
		counter(original, setter);
		for (int r = 0; r < setter.length; r++) {
			for (int c = 0; c < setter[0].length; c++) {
				tokens[r][c] = setter[r][c];
			}
		}
	}

	/**
	 * Helper method for undo --- Counts the instances of a token on the board, and
	 * the number instances on the setter. If there are less instances on the setter
	 * that means undo took away a creation, and the player should be allowed to
	 * create another token
	 */
	public void counter(Token[][] original, Token[][] setter) {
		originalCount = 0;
		setterCount = 0;
		for (int r = 0; r < setter.length; r++) {
			for (int c = 0; c < setter[0].length; c++) {
				if (setter[r][c] instanceof BoardPiece) {
					setterCount++;
				}
				if (original[r][c] instanceof BoardPiece) {
					originalCount++;
				}
			}
		}
	}

	/**
	 * This method figures out what should be in the grave yard. This is done by
	 * going through the players tokens, and through the boards to figure out which
	 * tokens are missing from the original list of 24 tokens. These missing tokens
	 * are then added to the differences list.
	 *
	 * @param board
	 */
	public void updateGraveyard(Token[][] board) {
		// check differences between board and players tokens and add them to grave yard
		List<String> boardTokens = new ArrayList<String>();
		List<String> playerTokens = new ArrayList<String>();
		// get all the tokens that are on the board
		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				if (board[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) board[r][c];
					boardTokens.add(temp.getName());
				}
			}
		}
		// get all the tokens that are in the players 2D array of tokens
		for (int r = 0; r < tokens.length; r++) {
			for (int c = 0; c < tokens[0].length; c++) {
				if (tokens[r][c] instanceof BoardPiece) {
					BoardPiece temp = (BoardPiece) tokens[r][c];
					playerTokens.add(temp.getName());
				}
			}
		}
		// find what's missing from the board and players tokens
		for (BoardPiece bp : listOfTokens) {
			if (!boardTokens.contains(bp.getName()) && !playerTokens.contains(bp.getName())) {
				differences.add(bp);
			}
		}
	}



	/**
	 * Clears the grave yards by setting everything in it to null.
	 */
	public void clearGraveYards() {
		for (int r = 0; r < graveYard.length; r++) {
			for (int c = 0; c < graveYard[0].length; c++) {
				graveYard[r][c] = null;
			}
		}
	}

	public int getOriginalCount() {
		return originalCount;
	}

	public int getSetterCount() {
		return setterCount;
	}

	public String getName() {
		return name;
	}

	public List<String> getMovesSoFar() {
		return movesSoFar;
	}

	public List<BoardPiece> getEveryMovement() {
		return everyMovement;
	}

	public BoardPiece[][] getTokens() {
		return tokens;
	}

	public Stack<BoardPiece[][]> getUndoStack() {
		return undoStack;
	}

	public BoardPiece[][] getGraveYard() {
		return graveYard;
	}

	public void setListOfTokens(List<BoardPiece> listOfTokens) {
		this.listOfTokens = listOfTokens;
	}

	public List<BoardPiece> getDifferences() {
		return differences;
	}
}
