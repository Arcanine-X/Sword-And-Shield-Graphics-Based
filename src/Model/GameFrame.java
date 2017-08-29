package Model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

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
	public boolean disableBoard = false;
	public BoardPiece creationPiece = null;
	//public boolean reactions = false;

	Player currentPlayer;
	int turn = 1;

	public GameFrame() {
		game = new SwordAndShieldGame();
		this.setTitle("~~Sword And Shiled Game~~");
		/*panelContainer = new JPanel();
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
		graveyardY = new GraveyardPanel(game, game.getYellow());
		graveyardG = new GraveyardPanel(game, game.getGreen());
		JPanel graveHolder = new JPanel();
		graveHolder.add(graveyardG);
		graveHolder.add(graveyardY);*/
		panelContainer = new JPanel();

		boardPanel = new BoardPanel(game, this);
		boardPanel.setFocusable(true);
		boardPanel.requestFocusInWindow();
		tokenPanelY = new TokenPanel(game, game.getYellow(), this);
		tokenPanelG = new TokenPanel(game, game.getGreen(), this);
		buttonPanel = new JPanel();
		graveyardY = new GraveyardPanel(game, game.getYellow());
		graveyardG = new GraveyardPanel(game, game.getGreen());
		buttonPanel = new ButtonPanel(game);
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
		JSplitPane firstSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tokenPanelG,boardPanel); // green tokens - board
		firstSplit.setResizeWeight(1);
		JSplitPane secondSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, firstSplit, tokenPanelY); // first split and yellow otkens
		secondSplit.setResizeWeight(0.1);
		JSplitPane thirdSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonPanel, secondSplit);
		thirdSplit.setResizeWeight(0);
		JSplitPane fourthSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graveyardG, graveyardY);
		fourthSplit.setResizeWeight(0.5);
		JSplitPane fifthSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, thirdSplit, fourthSplit);
		fifthSplit.setResizeWeight(0.75);
		this.add(fifthSplit);
		new Timer(50,
                (e)->{
                    repaint();
                }
            ).start();

		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(game.getBoard().getUndoStack().size() != 1) {
					GameFrame.this.game.undo(currentPlayer);
				}
			}
		});
		surrender.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameFrame.this.surrender();
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
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainGame();
	}

	public void surrender() {
		String surrenderMsg = "";
		if(currentPlayer.getName().equals("yellow")) {
			surrenderMsg = "Green Wins";
		}
		else {
			surrenderMsg = "Yellow Wins";
		}
		JOptionPane.showMessageDialog(this, surrenderMsg);
		MenuFrame menu = new MenuFrame();
		this.dispose();
	}

	public void setBoardReactionsTrue() {
		boardPanel.reactions = true;
	}

	public void setBoardReactionsFalse() {
		boardPanel.reactions = false;
	}

	public void mainGame() {
		if (turn % 2 == 0) {
			System.out.println("It is greens turn!");
			currentPlayer = game.getGreen();
		}
		else {
			System.out.println("It is yellows turn!");
			currentPlayer = game.getYellow();
		}

	}


	public void fight(Player player) {
		while(!game.getBoard().getReactions().isEmpty()) {
			if(game.isGameEnd()) { // check if game has finished
				return;
			}
			//// <----- display animations
			//might do this in one actually
			for(Pair p : game.getBoard().getReactions()) {
				if(p.getPlayer()!=null) {
					// display player and token reaction
				}else {
					// display two board piece reactions
				}
			}
			if(game.getBoard().getReactions().size() > 1) {
				//user now has to pick the reactions
				//#### let them undo

			}
		}
	}

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
		return new Dimension((int) dimension.getWidth(), 1000);
		//return new Dimension(1500,700);
	}

	public void addClearPanel() {
		ClearPanel glass = new ClearPanel(game, this);
		glass.setBackground(new Color(0,200,0,0));
		glass.setLocation(0, 0);
		System.out.println("added glass panel");
		this.add(glass);

	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

}
