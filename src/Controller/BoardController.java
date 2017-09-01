package Controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Model.BoardPanel;
import Model.GameFrame;
import Model.SwordAndShieldGame;

/**
 * This class represents the controller for the board panel. It sets all the values in the boardPanel
 * class depending on what the user does with their mouse and keys.
 * @author Chin Patel
 *
 */
public class BoardController implements KeyListener, MouseListener{
	private SwordAndShieldGame game;
	private GameFrame run;
	private BoardPanel boardPanel;

	/**
	 * Constructor takes in the game, gameFrame, and boardPanel to access allow it set variables in any class
	 * @param game
	 * @param run
	 * @param boardPanel
	 */
	public BoardController(SwordAndShieldGame game, GameFrame run, BoardPanel boardPanel) {
		this.game = game;
		this.run = run;
		this.boardPanel = boardPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//If there are any animations happening don't let the player click until the animation is done
		if(boardPanel.isMoveAnimation() || boardPanel.isSWEDisappear() || boardPanel.isDisappearAnimation()) {
			return;
		}
		boardPanel.mouseX = e.getX();
		boardPanel.mouseY = e.getY();
		if(!boardPanel.reactions) {
			boardPanel.findClickedToken();
		}
		System.out.println(boardPanel.mouseClicks);
		if (boardPanel.chosenToken != null && boardPanel.mouseClicks >=2 && !boardPanel.isRotationAnimation() && boardPanel.reactions == false) {
			boardPanel.attemptRotation();
			boardPanel.attemptClickMove();
		}else if(boardPanel.reactions){
			boardPanel.findChosenReaction();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(boardPanel.chosenToken == null) {
			return;
		}
		if(run.getCurrentPlayer().getMovesSoFar().contains(boardPanel.chosenToken.getName())){
			return;
		}
		if (key == KeyEvent.VK_UP) {
			if (boardPanel.chosenToken != null) {
				boardPanel.setMoveAnimation(true);
				boardPanel.moveDir = "up";
			}
		} else if (key == KeyEvent.VK_RIGHT) {
			if (boardPanel.chosenToken != null) {
				boardPanel.setMoveAnimation(true);
				boardPanel.moveDir = "right";
			}
		} else if (key == KeyEvent.VK_LEFT) {
			if (boardPanel.chosenToken != null) {
				boardPanel.setMoveAnimation(true);
				boardPanel.moveDir = "left";

			}
		} else if (key == KeyEvent.VK_DOWN) {
			if (boardPanel.chosenToken != null) {
				boardPanel.setMoveAnimation(true);
				boardPanel.moveDir = "down";
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {}

}
