package Model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel{
	private SwordAndShieldGame game;
	private GameFrame run;
	private JButton undo = new JButton("Undo");
	JButton pass = new JButton("Pass");
	private JButton surrender = new JButton("Surrender");
	private JButton quit = new JButton("Quit");
	public ButtonPanel(SwordAndShieldGame game, GameFrame run) {
		this.game = game;
		this.run = run;
		this.add(undo);
		this.add(surrender);
		this.add(pass);
		this.add(quit);
		undo.setFocusable(false);
		surrender.setFocusable(false);
		pass.setFocusable(false);
		quit.setFocusable(false);

		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (game.getBoard().getUndoStack().size() == 1) {
					game.setFirstCreation(true);
				}
				if (game.getBoard().getUndoStack().size() > 1) {
					run.game.undo(run.currentPlayer);
					run.boardPanel.repaint();
					if(game.getBoard().checkForReaction()) {
						pass.setEnabled(false);
						run.setBoardReactionsTrue();
					}else {
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
					run.game.reset(run.currentPlayer, game.getBoard());
					run.turn++;
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
}
