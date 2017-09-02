package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JTextArea;


/**
 * This class represents the information frame. 
 * @author patelchin1
 *
 */
public class InformationFrame extends JFrame{
	private StringBuilder sb;
	public InformationFrame() {
		this.setTitle("Information");
		this.setLayout(new BorderLayout());
		InformationButtonsPanel buttons = new InformationButtonsPanel(this);
		this.add(buttons, BorderLayout.NORTH);
		buttons.setBackground(Color.WHITE);
		displayInfo();
		JTextArea information = new JTextArea();
		information.setText(sb.toString());
		information.setWrapStyleWord(true);
		information.setLineWrap(true);
		information.setEditable(false);
		Font font = new Font("Verdana", Font.BOLD, 12);
		information.setFont(font);
		this.add(information, BorderLayout.CENTER);
		information.setLocation(500, 700);
		this.pack();
		this.setVisible(true);
	}
	
	
	private void displayInfo() {
		sb = new StringBuilder();
		sb.append("Sword and shield is a two player game of pure skill, with no element of luck.");
		sb.append("Shields are vertical lines around the outside of the piece, and swords are lines that go through the\n" +
				"piece.");
		sb.append("Independently of their colour, whenever two pieces meet on the board, if they point a sword to one\n" +
				"another, a reaction happens. It is not relevant if the pieces are of the same colour or of different\n" +
				"colours.");
		sb.append("• Sword against nothing: the piece with nothing is eliminated.\n" +
				"• Sword against sword: both pieces are eliminated.\n" +
				"\n" +
				"• Sword against shield: the piece with sword is pushed back one cell. If a piece go out of the\n" +
				"board is eliminated. In this game, the corner positions 1*1,1*2,2*1,2*2, 9*10, 9*10, 10*10 are\n" +
				"considered out of the board.\n" +
				"• Sword against Face: Victory condition; the Face attacked by the sword loses the game.");
		sb.append("Eliminated pieces are removed from the board, can not be created again and are placed in\n" +
				"the cemetery.");
		sb.append(""
				+ "• At the start of your turn you may create a piece if your creation grid is empty. Your creation\n" +
				"grid is at position (3,3) for yellow and (8,8) for green. Select a piece that is not on the board or\n" +
				"in the cemetery, and place it onto the creation square in the orientation you prefer.\n" +
				"• Next, you may choose to move or rotate any number of your pieces, choosing the order. More\n" +
				"in details:\n" +
				"(a) If you want, you can pass the turn\n" +
				"(b) Chose a piece that you have not already moved or rotated,\n" +
				"(c) Rotate it (to any new orientation) or move it (a single cell up, down, left, or right).\n" +
				"You can freely move your pieces in one of the adjacent positions: when a piece moves (or\n" +
				"is pushed by another piece) all the pieces in its way are pushed away n the same direction.\n" +
				"No two pieces may be in the same square. Remember that pieces pushed off the board are\n" +
				"sent to the cemetery.\n" +
				"(d) Apply all the reactions. There could be a very long chain of reactions. Moves that\n" +
				"requires to perform and infinite amount of reactions are invalid, just choose another move.\n" +
				"(e) go back to (a), remember you will need to chose a different piece every time.");
	}


	public Dimension getPreferredSize() {return new Dimension(800, 800);}


}
