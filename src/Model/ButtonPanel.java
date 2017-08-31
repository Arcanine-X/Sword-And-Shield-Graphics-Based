package Model;

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
		this.setMinimumSize(new Dimension((int)run.getPreferredSize().getWidth(), 60));
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

	public void displayInfo(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Serif", Font.BOLD, 16));
		g.drawString("It is " + run.currentPlayer.getName() + "'s turn :", (run.returnWidth()/2) - 180, 50);
		if(run.boardPanel.reactions) {
			g.setColor(Color.red);
			g.drawString("Reactions! You cannot continue untill you complete all the reactions", (run.returnWidth()/2), 50);
		}
		else if (game.getBoard().getUndoStack().size() > 1) {
			g.drawString("Rotate, Move, Undo or Pass ", (run.returnWidth()/2), 50);
		}
		else {
			g.drawString("Create a token or Pass", (run.returnWidth()/2), 50);
		}
	}

	 @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D _g = (Graphics2D)g;
		GradientPaint blackToGray = new GradientPaint(0, 30, new Color(255, 255, 204),
                (float) run.getPreferredSize().getWidth(), 30, new Color(204, 255, 204));
       _g.setPaint(blackToGray);
        _g.fillRect(0, 0, (int) run.getPreferredSize().getWidth(), (int) run.getPreferredSize().getHeight());

		displayInfo((Graphics2D)g);
	}
}
