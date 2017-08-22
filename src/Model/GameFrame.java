package Model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameFrame extends JFrame implements Observer{
	SwordAndShieldGame game;
	JPanel buttonPanel;
	BoardPanel boardPanel;
	TokenPanel tokenPanelY;
	TokenPanel tokenPanelG;
	JPanel panelContainer;
	GraveyardPanel graveyardY;
	GraveyardPanel graveyardG;
	boolean yellowTurn = true;
	boolean greenTurn = false;
	public boolean pastCreation = false;
	public boolean createAnimation = false;
	public boolean disableBoard = false;
	public BoardPiece creationPiece = null;

	Player currentPlayer;
	int turn = 1;

	public GameFrame() {
		game = new SwordAndShieldGame();
		this.setTitle("~~Sword And Shiled Game~~");
		panelContainer = new JPanel();
		panelContainer.setLayout(new BorderLayout(0, 0));

		tokenPanelG = new TokenPanel(game, game.getGreen(), this);
		tokenPanelG.setForeground(Color.green);
		tokenPanelG.setEnabled(false);
		panelContainer.add(tokenPanelG, BorderLayout.WEST);

		boardPanel = new BoardPanel(game, this);
		boardPanel.setFocusable(true);
		boardPanel.requestFocusInWindow();
		panelContainer.add(boardPanel, BorderLayout.CENTER);

		tokenPanelY = new TokenPanel(game, game.getYellow(), this);
		tokenPanelY.setForeground(Color.YELLOW);
		panelContainer.add(tokenPanelY, BorderLayout.EAST);
		buttonPanel = new JPanel();

		//graveyardY = new GraveyardPanel(game, game.getYellow());
		//panelContainer.add(graveyardY, BorderLayout.SOUTH);
		//graveyardG = new GraveyardPanel(game, game.getGreen());



		JButton undo = new JButton("Undo");
		JButton pass = new JButton("Pass");
		JButton surrender = new JButton("Surrender");
		JButton quit = new JButton("Quit");
		buttonPanel.add(undo);
		buttonPanel.add(surrender);
		buttonPanel.add(pass);
		buttonPanel.add(quit);
		undo.setFocusable(false);
		surrender.setFocusable(false);
		pass.setFocusable(false);
		quit.setFocusable(false);
		panelContainer.add(buttonPanel, BorderLayout.NORTH);
		this.add(panelContainer);

		new Timer(50,
                (e)->{
                    repaint();
                }
            ).start();

		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//GameFrame.this.game.undo(currentPlayer);
				//game.undo(game.getYellow());
				GameFrame.this.game.undo(currentPlayer);
				tokenPanelY.repaint();
				boardPanel.repaint();

				//game.undo(game.getGreen());
			}
		});
		surrender.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog();

				//MenuFrame menu = new MenuFrame();
				//GameFrame.this.dispatchEvent(new WindowEvent(GameFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		pass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					GameFrame.this.game.passed = false;
					if(game.getBoard().getUndoStack().size()==1) {
						game.setFirstCreation(false);
						game.success();
						pastCreation = true;
					}else {
						GameFrame.this.game.reset(currentPlayer, game.getBoard());
						GameFrame.this.turn++;
						pastCreation = false;
					}

					System.out.println("##########Switching players#############");
					mainGame();
			}
		});
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameFrame.this.repaint();
			}
		};
		this.pack();
		this.setVisible(true);
		mainGame();
	}

	public void mainGame() {
		if (turn % 2 == 0) {
			System.out.println("It is greens turn!");
			tokenPanelY.setEnabled(false);
			tokenPanelG.setEnabled(true);
			currentPlayer = game.getGreen();
		}
		else {
			System.out.println("It is yellows turn!");
			tokenPanelG.setEnabled(false);
			tokenPanelY.setEnabled(true);
			currentPlayer = game.getYellow();
		}

	}

	/*public void mainGame() {
		//while (!game.isGameEnd()) {// loop until game is ended
			//System.out.println("\n********************");
			//System.out.println("***** TURN " + turn + " *******");
			//System.out.println("********************\n");
			if (turn % 2 == 0 && greenTurn) {
				System.out.println("It is greens turn!");
				tokenPanelY.setEnabled(false);
				if(game.passed) {
					game.passed = false;
					turn++;
				}
			} else if(turn % 2 !=0 && yellowTurn) {
				System.out.println("It is yellows turn!");
				tokenPanelG.setEnabled(false);
				if(game.passed) {
					game.passed = false;
					turn++;
				}
			}

		//}
	}*/

	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		Graphics2D _g = (Graphics2D) g;
		boardPanel.paintComponent(_g);
		tokenPanelG.paintComponent(_g);
		tokenPanelY.paintComponent(_g);
	}

	public Dimension getPreferredSize() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension(1200, 1000);
	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

}
