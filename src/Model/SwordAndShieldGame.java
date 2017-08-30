package Model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class contains code for most of the logic of the game. This class has all the methods,
 * that get passed the user input from the text client, and deals with it appropriately.
 * @author Chin Patel
 *
 */

public class SwordAndShieldGame {
	private Player green;
	private Player yellow;
	private Board board;
	private List<BoardPiece> yelList = new ArrayList<>(); // List of yellows tokens
	private List<BoardPiece> greList = new ArrayList<>(); // List of greens Tokens
	private static final Set<String> movement = new HashSet<String>(Arrays.asList("up", "down", "left", "right")); // Set that contains all possible movements
	private static final Set<Integer> rotations = new HashSet<Integer>(Arrays.asList(0, 90, 180, 270)); // Set that contains all possible rotations
	private boolean firstCreation = true; // Keeps track of only being able to create one token per turn
	private boolean gameEnd = false; // Keeps track of the game state
	public boolean passed = false;

	public SwordAndShieldGame() {
		green = new Player("green");
		yellow = new Player("yellow");
		board = new Board();
		initialiseGame();
	}

	/**
	 * Initialises the game
	 * 	- Creates all the tokens for the users
	 * 	- Populates all the boards
	 *  - Creates records of the game state
	 */
	public void initialiseGame(){
		gerneratePieces(yelList);
		gerneratePieces(greList);
		for (BoardPiece bp : greList) {
			bp.setName(bp.getName().toUpperCase());
		}
		green.setListOfTokens(greList);
		yellow.setListOfTokens(yelList);
		board.initialise();
		board.createRecord();
		yellow.populateTokens(yellow, yelList);
		green.populateTokens(green, greList);
		yellow.createRecord();
		green.createRecord();
		board.setGreen(green);
		board.setYellow(yellow);
		board.addPlayers(green, yellow);
		board.addInvalidSquares();
		board.redraw();
		//For reactions
		/*BoardPiece one = yellow.find("s");
		board.getBoard()[0][3] = one;
		BoardPiece two = yellow.find("g");
		board.getBoard()[6][7] = two;
		BoardPiece three = yellow.find("c");
		board.getBoard()[7][6] = three;
		BoardPiece four = yellow.find("k");
		board.getBoard()[9][7] = four;*/

		//For pushing and disappearing
		/*BoardPiece one = yellow.find("s");
		board.getBoard()[0][9] = one;
		BoardPiece two = yellow.find("e");
		board.getBoard()[0][8] = two;
		BoardPiece three = yellow.find("c");
		board.getBoard()[0][7] = three;*/


		//For player reactions
		/*BoardPiece one = yellow.find("g");
		board.getBoard()[7][8] = one;*/

		//For off the board reactions
		/*BoardPiece one = yellow.find("g");
		board.getBoard()[7][9] = one;
		BoardPiece three = yellow.find("c");
		board.getBoard()[7][8] = three;*/

		//For move reaction
		//BoardPiece one = yellow.find("g");
		//board.getBoard()[4][4] = one;
		//BoardPiece three = yellow.find("c");
		//board.getBoard()[5][4] = three;

		//For move and disappear

		BoardPiece one = yellow.find("g");
		board.getBoard()[1][4] = one;
		BoardPiece three = yellow.find("c");
		board.getBoard()[2][4] = three;
		BoardPiece two = yellow.find("e");
		board.getBoard()[0][4] = two;


		//reaction disappear test
		/*BoardPiece one = yellow.find("s");
		board.getBoard()[0][7] = one;
		BoardPiece two = yellow.find("a");
		board.getBoard()[1][7] = two;
		BoardPiece three = yellow.find("c");
		board.getBoard()[2][7] = three;*/
	}

	/**
	 * Checks if the user is allowed to move the token or not. Method checks if the user has moved the token yet or not
	 * @param player --- player trying to move token
	 * @param letter --- letter of the token that the player is trying to move
	 * @return
	 */
	public boolean checkIfAllowedToMove(Player player, String letter) {
		if (!player.getMovesSoFar().contains(letter)) {
			player.getMovesSoFar().add(letter);
			return true;
		}
		System.out.println("You have already moved this piece this turn");
		System.out.println("You can move another existing piece or move it next turn");
		return false;
	}

	/**
	 * Method moves the token either up, down, left or right. The method takes the player, and the user input. It parses the input
	 * and checks if its a valid input, and if it is will move the token accordingly else return a invalid input.
	 * @param player --- player that is trying to move the token
	 * @param options --- a string input containing information of the what token, and what direction
	 */
	public void moveToken(Player player, String options) {
		if (player == null || options == null) { // Should never happen
			throw new NullPointerException();
		}
		String[] tokens = options.split(" ");
		//if (tokens.length != 3) { // The input should only be 3 strings as its "move letter direction"
		//	System.out.println("Input error");
		//}
		String letter = tokens[1];
		String direction = tokens[2];
		if(player.getName().equals("green")){ // Just to make it more user friendly, the green player doesn't have to keep writing in caps
			letter = letter.toUpperCase();
		}
		BoardPiece tokenToMove = board.findMoveToken(player, letter); // Find the token on the board that the user is trying to move
		if (tokenToMove == null) {
			System.out.println("Your token to move doesn't exist");
			return;
		}
		if (!movement.contains(direction) || letter.length() != 1) { // Check if input is correct
			System.out.println("Incorrect input");
			return;
		}
		if (checkIfAllowedToMove(player, letter) == false) { // Check if the user hasn't already the same token yet
			return;
		}
		if (player.getEveryMovement().contains(tokenToMove)) { 	// Check if the user hasn't already rotated the token their trying to move
			System.out.println("You have already rotated or moved this piece this turn.\nSo you cannot rotate");
			return;
		}
		// Find the piece to move
		player.tryMoveToken(player, tokenToMove, direction, board); // move the token
		player.getEveryMovement().add(tokenToMove); // add it to the list of movements to ensure the user cannot move the same token again
		success(); // create a record of this game state for undo
	}

	/**
	 * Method rotates the token either 0, 90, 180 or 270 degrees. The method takes the player, and the user input. It parses the input
	 * and checks if its a valid input, and if it is will rotate the token accordingly else return a invalid input.
	 * @param player --- player trying to move token
	 * @param options - a string input containing information of the what token, and what rotation
	 */
	public void rotateToken(Player player, String options) {
		if (player == null || options == null) { // Should never happen
			throw new NullPointerException();
		}
		String[] tokens = options.split(" ");
		if (tokens.length != 3) {  // The input should only be 3 strings as its "rotate letter rotation"
			System.out.println("Incorrect input");
		}
		String letter = tokens[1];
		if(player.getName().equals("green")){// Just to make it more user friendly, the green player doesn't have to keep writing in caps
			letter = letter.toUpperCase();
		}
		int rotation = Integer.parseInt(tokens[2]);
		if (!rotations.contains(rotation) || letter.length() != 1) {  // Check if input is correct
			System.out.println("Incorrect Input");
			return;
		}
		BoardPiece itemToRotate = board.findMoveToken(player, letter); // find the item on the board
		if (itemToRotate == null) {
			System.out.println("Your rotation piece doesn't exist");
			return;
		}
		int num = (rotation == 0) ? 0 : (rotation == 90) ? 1 : (rotation == 180) ? 2 : 3;
		while (num > 0) { // Rotator only rotates 90 degrees at a time, so keep looping until its been rotated enough
			rotator(itemToRotate);
			num--;
		}
		if (player.getEveryMovement().contains(itemToRotate)) { // Check that the token hasn;t already been moved or rotated already this turn
			System.out.println("You have already moved or rotated this token this turn");
			System.out.println("You cannot rotate it again");
			return;
		}
		player.getEveryMovement().add(itemToRotate); // Add it to the list of movements so it isn't rotate again.
		player.getMovesSoFar().add("" + rotation);	// Have to add have this to for to ensure that you cannot move after undoing a rotation.
		success(); // Create a record of the game state for undo
	}


	/**
	 * Method creates the token with either a 0, 90, 180 or 270 degrees rotation. The method takes the player, and the user input. It parses the input
	 * and checks if its a valid input, and if it is will create the token and rotate the token accordingly else return a invalid input.
	 * @param player  --- player trying to move token
	 * @param options a string input containing information of the what token to add, and what rotation
	 */
	public void createToken(Player player, String options) {
		if (player == null || options == null) { // Should never happen
			throw new NullPointerException();
		}
		if (!firstCreation) { // Sanity check to ensure that the user isn't able to create twice per turn
			System.out.println("You have already created a token this turn. You cannot create another one");
			return;
		}
		String[] tokens = options.split(" ");
		if (tokens.length != 3) {  // The input should only be 3 strings as its "create letter rotation"
			System.out.println("The format is incorrect. It should be create <letter> <0/90/180/270>");
		}
		String letter = tokens[1];
		int rotation = Integer.parseInt(tokens[2]);
		if (!rotations.contains(rotation) || letter.length() != 1) { //Check that the input is a valid length, and valid rotation
			System.out.println("Incorrect Inout");
			return;
		}
		if(player.getName().equals("green")){ // Just to make it more user friendly, the green player doesn't have to keep writing in caps
			letter = letter.toUpperCase();
		}
		if (player.addToken(letter, player, board) == false) { //Add the token
			System.out.println("Invalid input");
			return;
		}
		BoardPiece item = board.findMoveToken(player, letter);
		int num = (rotation == 0) ? 0 : (rotation == 90) ? 1 : (rotation == 180) ? 2 : 3;
		while (num > 0) {  // Rotator only rotates 90 degrees at a time, so keep looping until its been rotated enough
			rotator(item);
			num--;
		}
		firstCreation = false;
		success(); // Create a record of the game state for undo
	}

	/**
	 * Undo method, reverts to the previous game state that was saved. This method removes the last added things from
	 * all the lists, and creates new records of the game states.
	 * @param player
	 */
	public void undo(Player player) {
		board.setBoard(); // undo board
		yellow.setBoard();
		green.setBoard();
		green.createRecord();
		yellow.createRecord();
		board.createRecord(); // create new record
		green.clearGraveYards();
		yellow.clearGraveYards();
		if (!player.getMovesSoFar().isEmpty()) {
			player.getMovesSoFar().remove(player.getMovesSoFar().size() - 1);
		}
		if (player.getSetterCount() > player.getOriginalCount()) {
			firstCreation = true;
		}
		if (!player.getEveryMovement().isEmpty()) {
			player.getEveryMovement().remove(player.getEveryMovement().size() - 1);
		}
		if (board.checkForReaction()) {
			board.redraw();
			return;
		}
		board.redraw();
	}

	/**
	 * This method is a method that is called after each successful reaction to save the game state after a successful
	 * execution. It also removes that pair from the list of reactions.
	 * @param player --- player that executed the reaction
	 * @param p --- pair of reaction that was successful
	 */
	public void reactionCompleted(Player player, Pair p) {
		if(gameEnd == true) {
			return;
		}
		board.redraw();
		System.out.println(p.toString());
		board.getReactions().remove(p);
		board.createRecord();
		yellow.createRecord();
		green.createRecord();
		if (board.checkForReaction()) {
			return;
		}
		//############### Come up with a better fix ##################
		// ######## may not even work with multiple reactions
		//player.getEveryMovement().add(null);
		//player.getMovesSoFar().add("reaction");
	}

	/**
	 * Method that is called after every successful execution where the game state needs to be saved.
	 */
	public void success() {
		board.createRecord();
		green.createRecord();
		yellow.createRecord();
		board.redraw();
	}

	/**
	 * Method that is called after the user passes. This will reset everything, including all the stacks.
	 * This ensures that the player can only undo back to the start of their turn.
	 * @param player
	 * @param board
	 */
	public void reset(Player player, Board board) {
		yellow.getEveryMovement().clear();
		green.getEveryMovement().clear();
		yellow.getMovesSoFar().clear();
		green.getMovesSoFar().clear();
		firstCreation = true;
		yellow.getUndoStack().clear();
		green.getUndoStack().clear();
		board.getUndoStack().clear();
		green.createRecord();
		yellow.createRecord();
		board.createRecord();
	    passed = true;
	}

	/**
	 * Rotator method which rotates the give item by 90 degrees.
	 * @param item --- token that needs to be rotated.
	 */
	public void rotator(BoardPiece item) {
		int tn = item.getNorth(), te = item.getEast(), ts = item.getSouth(), tw = item.getWest();
		item.setNorth(tw);
		item.setEast(tn);
		item.setSouth(te);
		item.setWest(ts);
	}


	/**
	 * Method that generates all the pieces. It takes a list to add all the board pieces too.
	 * @param list --- list in which the items are added to
	 */
	public void gerneratePieces(List<BoardPiece> list) {
		list.add(new BoardPiece("a", 1, 2, 1, 1, ""));
		list.add(new BoardPiece("b", 1, 0, 1, 1, ""));
		list.add(new BoardPiece("c", 2, 2, 2, 2, ""));
		list.add(new BoardPiece("d", 1, 0, 2, 0, ""));
		list.add(new BoardPiece("e", 0, 0, 0, 0, ""));
		list.add(new BoardPiece("f", 1, 0, 0, 1, ""));
		list.add(new BoardPiece("g", 1, 1, 1, 1, ""));
		list.add(new BoardPiece("h", 1, 0, 2, 2, ""));
		list.add(new BoardPiece("i", 0, 2, 0, 0, ""));
		list.add(new BoardPiece("j", 1, 2, 1, 2, ""));
		list.add(new BoardPiece("k", 1, 2, 0, 1, ""));
		list.add(new BoardPiece("l", 1, 0, 0, 0, ""));
		list.add(new BoardPiece("m", 1, 2, 2, 0, ""));
		list.add(new BoardPiece("n", 0, 2, 2, 0, ""));
		list.add(new BoardPiece("o", 1, 0, 1, 2, ""));
		list.add(new BoardPiece("p", 1, 0, 2, 1, ""));
		list.add(new BoardPiece("q", 1, 0, 0, 2, ""));
		list.add(new BoardPiece("r", 1, 2, 0, 2, ""));
		list.add(new BoardPiece("s", 0, 2, 0, 2, ""));
		list.add(new BoardPiece("t", 1, 0, 1, 0, ""));
		list.add(new BoardPiece("u", 1, 0, 0, 1, ""));
		list.add(new BoardPiece("v", 1, 2, 0, 0, ""));
		list.add(new BoardPiece("w", 1, 2, 2, 2, ""));
		list.add(new BoardPiece("x", 0, 2, 2, 2, ""));
	}

	/**
	 * This method is used in reactions. It takes name of the pusher, which is the token
	 * which has the shield. It finds the number of adjacent tiles
	 * of the tile that is being pushed. With this number it can loop through backwards, so from the
	 * row - number of adjacent tiles, off setting each tile by - 1 row.
	 * @param pusher --- name of the token with the shield pushing the sword token
	 */
	public void tryPushUp(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;
		if (r - 1 < 0) {
			board.getBoard()[r][c] = null;
		} else { // requires shifting
			for (int i = r - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) { //calculate number of adjacent tiles going from the the tile being push
					count++;
				}
			}
			if (count != 0) {
				for (int i = r - count; i <= r; i++) { // go back the number the of adjacent of tiles, and shift everything up 1 working backwords
					if (i - 1 < 0) {
						board.getBoard()[i][c] = null;
					} else {
						board.getBoard()[i - 1][c] = board.getBoard()[i][c];
					}
				}
				board.getBoard()[r - 1][c] = null; // set the tile originally being pushed to null
			}
		}
	}
	/**
	 * This method is used in reactions. It takes name of the pusher, which is the token
	 * which has the shield. It finds the number of adjacent tiles
	 * of the tile that is being pushed. With this number it can loop through backwards, so from the
	 * row + number of adjacent tiles, off setting each tile by + 1 row.
	 * @param pusher --- name of the token with the shield pushing the sword token
	 */
	public void tryPushDown(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;

		if (r + 1 > 9) {
			board.getBoard()[r][c] = null;
		} else {
			for (int i = r + 1, j = 0; i < board.getBoard().length; i++, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) { // calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
			if (count != 0) {
				for (int i = r + count; i >= r; i--) { // go down the number of adjacent tiles and shift everything down by 1 working backwards
					if (i + 1 > 9) {
						board.getBoard()[i][c] = null;
					} else {
						board.getBoard()[i + 1][c] = board.getBoard()[i][c];
					}
				}
				board.getBoard()[r + 1][c] = null; // set the tile originally being pushed to null
			}
		}
	}
	/**
	 * This method is used in reactions. It takes name of the pusher, which is the token
	 * which has the shield. It finds the number of adjacent tiles
	 * of the tile that is being pushed. With this number it can loop through backwards, so from the
	 * col + number of adjacent tiles, off setting each tile by + 1 column.
	 * @param pusher --- name of the token with the shield pushing the sword token
	 */
	public void tryPushRight(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;
		if (c + 1 > 9) {
			board.getBoard()[r][c] = null;
		} else {
			for (int i = c + 1, j = 0; i < board.getBoard().length; i++, j++) { // calculate the number of adjacent tiles to the tile being pushed
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) {
					count++;
				}
			}
			if (count != 0) {
				for (int i = c + count; i >= c; i--) { // go right the number of adjacent tiles and shift everything right by 1 working backwards
					if (i + 1 > 9) {
						board.getBoard()[r][i] = null;
					} else {
						board.getBoard()[r][i + 1] = board.getBoard()[r][i];
					}
				}
				board.getBoard()[r][c + 1] = null; // set the tile originally being pushed to null
			}
		}
	}
	/**
	 * This method is used in reactions. It takes name of the pusher, which is the token
	 * which has the shield. It finds the number of adjacent tiles
	 * of the tile that is being pushed. With this number it can loop through backwards, so from the
	 * col - number of adjacent tiles, off setting each tile by - 1 column.
	 * @param pusher --- name of the token with the shield pushing the sword token
	 */
	public void tryPushLeft(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;
		// if its less then 0 than set it to null - going of the board
		if (c - 1 < 0) {
			board.getBoard()[r][c] = null;
		} else {
			//calculate number of adjacent tiles
			for (int i = c - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) { // calculate the number of adjacent tiles to the tile thats being pushed
					count++;
				}
			}
			if (count != 0) {
				//shift everything down by 1
				for (int i = c - count; i <= c; i++) { // go left the number number of adjacent tiles and shift everything left by 1
					if (i - 1 < 0) { //in this case set it to null as we are going off the board
						board.getBoard()[r][i] = null;
					} else {
						board.getBoard()[r][i - 1] = board.getBoard()[r][i];
					}
				}
				//set the position of the original tile being pushed to null as its been pushed by now
				board.getBoard()[r][c - 1] = null;
			}
		}
	}


	/**
	 * This method deals with all the horizontal reactions. There are five situations in which a reaction can occur between two board pieces
	 * Sword v Sword, Sword v Nothing, Nothing v Sword,  Sword v Shield, Shield v Sword. This method checks each case and does the appropriate
	 * action of either killing the tokens, or pushing them correctly.
	 * @param player --- player executing the reaction
	 * @param p --- the pair of which the reaction is occurring between
	 */
	public void horizontalReaction(Player player, Pair p) {
		BoardPiece one = p.getOne();
		BoardPiece two = p.getTwo();
		Player play = p.getPlayer();
		if(two!=null) { // if two doesn't equal null, then the player ins't involved
			if (one.getEast() == 1 && two.getWest() == 1) { // sword - sword
				board.killToken(one.getName());
				board.killToken(two.getName());
				System.out.println(one.getName() + " and " + two.getName() + " died, due to Sword vs Sword. ");
				reactionCompleted(player,p);
			} else if (one.getEast() == 1 && two.getWest() == 0) { // sword - nothing
				board.killToken(two.getName());
				System.out.println(two.getName() + " died, due to " + one.getName() + "'s Sword, vs Nothing. ");
				reactionCompleted(player,p);
			} else if (one.getEast() == 1 && two.getWest() == 2) { // sword - shield
				tryPushLeft(two.getName());
				System.out.println(one.getName() + " got pushed back from " + two.getName() + "'s shield");
				reactionCompleted(player,p);
			} else if (one.getEast() == 0 && two.getWest() == 1) { // nothing - sword
				board.killToken(one.getName());
				System.out.println(one.getName() + " died from " + two.getWest() + "'s sword");
				reactionCompleted(player,p);
			} else if (one.getEast() == 2 && two.getWest() == 1) { // shield - sword
				tryPushRight(one.getName());
				System.out.println(two.getName() + " got pushed back from " + one.getName() + "'s shield");
				reactionCompleted(player,p);
			} else {
				System.out.println("Invalid Pair");
			}
		}else { //do the player vs board piece reactions
			if(one.getEast() == 1 && play!=null && play.getName().equals("yellow")) { // sword - yellow player
				gameEnd = true;
				board.killPlayer("yellow");
				reactionCompleted(player, p);
			}else if(one.getWest() == 1 && play!=null && play.getName().equals("green")){ // sword - green player
				gameEnd = true;
				board.killPlayer("green");
				reactionCompleted(player, p);
			}else {
				System.out.println("Invalid Pair");
			}
		}
	}

	/**
	 * This method deals with all the vertical reactions. There are five situations in which a reaction can occur between two board pieces
	 * Sword v Sword, Sword v Nothing, Nothing v Sword,  Sword v Shield, Shield v Sword. This method checks each case and does the appropriate
	 * action of either killing the tokens, or pushing them correctly.
	 * @param player --- player executing the reaction
	 * @param p --- the pair of which the reaction is occurring between
	 */
	public void verticalReaction(Player player, Pair p) {
		// Five possible reactions, sword - sword, sword - nothing, nothing - sword, shield - sword, sword - shield
		BoardPiece one = p.getOne();
		BoardPiece two = p.getTwo();
		Player play = p.getPlayer();
		if(two!=null) { // if two doesn't equal null, then the player ins't involved
			if (one.getSouth() == 1 && two.getNorth() == 1) { // sword - sword
				board.killToken(one.getName());
				board.killToken(two.getName());
				System.out.println(one.getName() + " and " + two.getName() + " died, due to Sword vs Sword. ");
				reactionCompleted(player, p);
			} else if (one.getSouth() == 1 && two.getNorth() == 2) { // sword - shield
				tryPushUp(two.getName());
				System.out.println(one.getName() + " got pushed back from " + two.getName() + "'s shield");
				reactionCompleted(player, p);
			} else if (one.getSouth() == 1 && two.getNorth() == 0) { // sword - nothing
				board.killToken(two.getName());
				System.out.println(two.getName() + " died, due to " + one.getName() + "'s Sword, vs Nothing. ");
				reactionCompleted(player, p);
			} else if (one.getSouth() == 0 && two.getNorth() == 1) { // nothing - sword
				board.killToken(one.getName());
				System.out.println(one.getName() + " died, due to Nothing vs Sword. ");
				reactionCompleted(player, p);
			} else if (one.getSouth() == 2 && two.getNorth() == 1) { // shield - sword
				tryPushDown(one.getName());
				System.out.println(two.getName() + " got pushed back from " + one.getName() + "'s shield");
				reactionCompleted(player, p);
			} else {
				System.out.println("Invalid Pair");
			}
		}else { //do the player vs board piece reactions
			if(one.getSouth()==1 && play!=null && play.getName().equals("yellow")) { // sword - yellow player
				board.killPlayer("yellow");
				gameEnd = true;
				reactionCompleted(player, p);
			}else if(one.getNorth() == 1 && play!=null && play.getName().equals("green")){ // sword - green player
				board.killPlayer("green");
				gameEnd = true;
				reactionCompleted(player, p);
			}else {
				System.out.println("Invalid Pair");
			}
		}
	}

	public String getDirectionOfAnimation(Player player, Pair p) {
		BoardPiece one = p.getOne();
		BoardPiece two = p.getTwo();
		Player play = p.getPlayer();
		if(two!=null) { // if two doesn't equal null, then the player ins't involved
			if (one.getSouth() == 1 && two.getNorth() == 2 && p.getDir().equals("vert")) { // sword - shield
				return "up";
			} else if (one.getSouth() == 2 && two.getNorth() == 1 && p.getDir().equals("vert")) { // shield - sword
				return "down";
			} else if (one.getEast() == 1 && two.getWest() == 2 && p.getDir().equals("hori")) { // sword - shield
				return "left";
			}else if (one.getEast() == 2 && two.getWest() == 1 && p.getDir().equals("hori")) { // shield - sword
				return "right";
			}else {
				System.out.println("Invalid Pair");
			}
		}
		return "error";
	}

	public int horizontalReactionAnimation(Player player, Pair p) {
		BoardPiece one = p.getOne();
		BoardPiece two = p.getTwo();
		if (one.getEast() == 1 && two.getWest() == 2) { // sword - shield
			return tryPushLeftAnimation(two.getName());
		}else if (one.getEast() == 2 && two.getWest() == 1) {// shield - sword
			return tryPushRightAnimation(one.getName());
		}
		return -2;
	}

	public int tryPushRightAnimation(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;
		if (c + 1 > 9) {
			return -1;
		} else {
			for (int i = c + 1, j = 0; i < board.getBoard().length; i++, j++) { // calculate the number of adjacent tiles to the tile being pushed
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) {
					count++;
				}
			}
			return count;
		}
	}



	public int verticalReactionAnimation(Player player, Pair p) {
		// Five possible reactions, sword - sword, sword - nothing, nothing - sword, shield - sword, sword - shield
		BoardPiece one = p.getOne();
		BoardPiece two = p.getTwo();
		Player play = p.getPlayer();
		if(two!=null) { // if two doesn't equal null, then the player ins't involved
			if (one.getSouth() == 1 && two.getNorth() == 2) { // sword - shield
				return tryPushUpAnimation(two.getName());
			} else if (one.getSouth() == 2 && two.getNorth() == 1) { // shield - sword
				return tryPushDownAnimation(one.getName());
				//tryPushDown(one.getName());
			} else {
				System.out.println("Invalid Pair");
			}
		}
		return -2;
	}

	public int tryPushLeftAnimation(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;
		// if its less then 0 than set it to null - going of the board
		if (c - 1 < 0) {
			return -1;
		} else {
			//calculate number of adjacent tiles
			for (int i = c - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[r][i] instanceof BoardPiece && count == j) { // calculate the number of adjacent tiles to the tile thats being pushed
					count++;
				}
			}
			return count;
		}
	}

	public int tryPushDownAnimation(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;

		if (r + 1 > 9) {
			return -1;
		} else {
			for (int i = r + 1, j = 0; i < board.getBoard().length; i++, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) { // calculate number of adjacent tiles to the tile being pushed
					count++;
				}
			}
			return count;
		}
	}



	public int tryPushUpAnimation(String pusher) {
		int c = board.getX(pusher);
		int r = board.getY(pusher);
		int count = 0;
		if (r - 1 < 0) {
			return -1;
		} else { // requires shifting
			for (int i = r - 1, j = 0; i >= 0; i--, j++) {
				if (board.getBoard()[i][c] instanceof BoardPiece && count == j) { //calculate number of adjacent tiles going from the the tile being push
					count++;
				}
			}
			return count;
		}
	}

	public Player getGreen() {
		return green;
	}
	public Player getYellow() {
		return yellow;
	}

	public Board getBoard() {
		return board;
	}

	public boolean getFirstCreation() {
		return firstCreation;
	}

	public void setFirstCreation(boolean firstCreation) {
		this.firstCreation = firstCreation;
	}

	public boolean isGameEnd() {
		return gameEnd;
	}
}

