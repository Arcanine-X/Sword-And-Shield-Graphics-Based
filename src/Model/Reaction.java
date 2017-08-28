package Model;

import java.awt.Rectangle;

public class Reaction extends Rectangle{

	public int x, y, width, height;
	public BoardPiece one, two;
	public String dir = "";
	public Rectangle rect;
	public Player player;
	public Reaction(int x, int y, int width, int height, BoardPiece one, BoardPiece two, String dir, Rectangle rect, Player player) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.one = one;
		this.two = two;
		this.dir = dir;
		this.rect = rect;
		this.player = player;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + height;
		result = prime * result + ((one == null) ? 0 : one.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + ((rect == null) ? 0 : rect.hashCode());
		result = prime * result + ((two == null) ? 0 : two.hashCode());
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reaction other = (Reaction) obj;
		if (dir == null) {
			if (other.dir != null)
				return false;
		} else if (!dir.equals(other.dir))
			return false;
		if (height != other.height)
			return false;
		if (one == null) {
			if (other.one != null)
				return false;
		} else if (!one.equals(other.one))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (rect == null) {
			if (other.rect != null)
				return false;
		} else if (!rect.equals(other.rect))
			return false;
		if (two == null) {
			if (other.two != null)
				return false;
		} else if (!two.equals(other.two))
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}






}
