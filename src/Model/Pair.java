package Model;
/**
 * This class is used to contain pair of reactions. It takes two board pieces
 * and or a board piece and a player along with the direction of the reaction,
 * to later create a list of all the reactions for the user.
 *
 * @author Chin Patel
 *
 */
public class Pair {
	private BoardPiece one;
	private BoardPiece two;
	private Player player;
	private String dir = "";

	/**
	 * Constructor that takes two board pieces involved in the reaction and the
	 * direction in which they are reacting (horizontal / vertical).
	 *
	 * @param one
	 *            -- piece one that is in a reaction
	 * @param two
	 *            -- piece two that is in a reaction
	 * @param dir
	 *            -- direction of the reaction
	 */
	public Pair(BoardPiece one, BoardPiece two, String dir) {
		this.one = one;
		this.two = two;
		this.dir = dir;
	}

	/**
	 * Constructor that takes a board piece and a player that are involved in the
	 * reaction, and the direction in which they are reacting (horizontal /
	 * vertical).
	 *
	 * @param one
	 *            -- piece that is reacting with the player
	 * @param two
	 *            -- player that is in the reaction
	 * @param dir
	 *            -- direction in which the piece and player are reacting
	 *            (horizontal / vertical).
	 */
	public Pair(BoardPiece one, Player two, String dir) {
		this.one = one;
		this.player = two;
		this.dir = dir;
	}

	@Override
	public String toString() {
		String returnString = "";
		if (dir.equals("hori")) {
			if (one.getEast() == 1 && two.getWest() == 0) { // sword v nothing
				returnString = two.getName() + " died, due to " + one.getName() + "'s Sword, vs Nothing.";
			} else if (one.getEast() == 1 && two.getWest() == 1) { // sword v sword
				returnString = one.getName() + " and " + two.getName() + " died, due to Sword vs Sword.";
			} else if (one.getEast() == 1 && two.getWest() == 2) { // sword v shield
				returnString = one.getName() + " got pushed back from " + two.getName() + "'s shield";
			} else if (one.getEast() == 0 && two.getWest() == 1) { // nothing v sword
				returnString = one.getName() + " died, due to " + two.getName() + "'s Sword, vs Nothing.";
			} else if (one.getEast() == 2 && two.getWest() == 1) { // shield v sword
				returnString = two.getName() + " got pushed back from " + one.getName() + "'s shield";
			} else { // shouldn't ever happen
				System.out.println("Invalid Reaction");
			}
		} else if (dir.equals("vert")) {
			if (one.getSouth() == 1 && two.getNorth() == 0) { // sword v nothing
				returnString = two.getName() + " died, due to " + one.getName() + "'s Sword, vs Nothing.";
			} else if (one.getSouth() == 1 && two.getNorth() == 1) { // sword v sword
				returnString = one.getName() + " and " + two.getName() + " died, due to Sword vs Sword.";
			} else if (one.getSouth() == 1 && two.getNorth() == 2) { // sword v shield
				returnString = one.getName() + " got pushed back from " + two.getName() + "'s shield";
			} else if (one.getSouth() == 0 && two.getNorth() == 1) { // nothing v sword
				returnString = one.getName() + " died, due to " + two.getName() + "'s Sword, vs Nothing.";
			} else if (one.getSouth() == 2 && two.getNorth() == 1) { // shield v sword
				returnString = two.getName() + " got pushed back from " + one.getName() + "'s shield";
			} else { // shouldn't ever happen
				System.out.println("Invalid Reaction");
			}
		} else { // shouldn't ever happen
			System.out.println("Invalid Reaction");
		}
		return returnString;
	}

	public BoardPiece getOne() {
		return one;
	}

	public BoardPiece getTwo() {
		return two;
	}

	public String getDir() {
		return dir;
	}

	public Player getPlayer() {
		return player;
	}
}
