package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import Model.SwordAndShieldGame;

/**
 * This class represents the button panel, which is instantiated when the
 * gameFrame is created. It holds the four buttons, and displays information to
 * guide the player in their turn.
 *
 * @author Chin Patel
 *
 */
public class ButtonPanel extends JPanel {
	private SwordAndShieldGame game;
	private GameFrame run;
	private JButton undo = new JButton("Undo");
	private JButton pass = new JButton("Pass");
	private JButton surrender = new JButton("Surrender");
	private JButton quit = new JButton("Quit");

	public ButtonPanel(SwordAndShieldGame game, GameFrame run) {
		this.game = game;
		this.run = run;
		this.setMinimumSize(new Dimension((int) run.getPreferredSize().getWidth(), 60));
		// Add the buttons
		this.add(undo);
		this.add(surrender);
		this.add(pass);
		this.add(quit);
		// Set the focusable to false to ensure the boardPanel can detect keys
		undo.setFocusable(false);
		surrender.setFocusable(false);
		pass.setFocusable(false);
		quit.setFocusable(false);
		// Action listeners for buttons
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (game.getBoard().getUndoStack().size() == 1) {
					game.setFirstCreation(true);
				}
				// Only let them undo if the stack size is >1
				if (game.getBoard().getUndoStack().size() > 1) {
					run.getGame().undo(run.getCurrentPlayer());
					// If they undo check for reactions again
					if (game.getBoard().checkForReaction()) {
						pass.setEnabled(false);
						run.setBoardReactionsTrue();
					} else {
						run.setBoardReactionsFalse();
						pass.setEnabled(true);
					}
				}
			}
		});
		surrender.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				run.surrender();
			}
		});
		pass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (game.getBoard().getUndoStack().size() == 1) {
					game.setFirstCreation(true);
					game.success();
				} else {
					run.getGame().reset(run.getCurrentPlayer(), game.getBoard());
					run.setTurn(run.getTurn()+1);
				}
				run.mainGame();
			}
		});
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
	}

	/**
	 * Displays the information in the button panel --- the information displayed is dependent
	 * on the players turn and if there are any reactions in play.
	 * @param g
	 */
	public void displayInfo(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Serif", Font.BOLD, 16));
		g.drawString("It is " + run.getCurrentPlayer().getName() + "'s turn :", (run.returnWidth() / 2) - 180, 50);
		if (run.getBoardPanel().reactions) {
			g.setColor(Color.red);
			g.drawString("Reactions! You cannot continue untill you complete all the reactions",
					(run.returnWidth() / 2), 50);
		} else if (game.getBoard().getUndoStack().size() > 1) {
			g.drawString("Rotate, Move, Undo or Pass ", (run.returnWidth() / 2), 50);
		} else {
			g.drawString("Create a token or Pass", (run.returnWidth() / 2), 50);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D _g = (Graphics2D) g;
		GradientPaint blackToGray = new GradientPaint(0, 30, new Color(255, 255, 204),
				(float) run.getPreferredSize().getWidth(), 30, new Color(204, 255, 204));
		_g.setPaint(blackToGray);
		_g.fillRect(0, 0, (int) run.getPreferredSize().getWidth(), (int) run.getPreferredSize().getHeight());
		displayInfo((Graphics2D) g);
	}

	public JButton getPass() {
		return pass;
	}

}
