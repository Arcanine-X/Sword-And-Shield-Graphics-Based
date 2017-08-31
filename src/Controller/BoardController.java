package Controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Model.BoardPanel;
import Model.GameFrame;
import Model.SwordAndShieldGame;

public class BoardController implements KeyListener, MouseListener{
	private SwordAndShieldGame game;
	private GameFrame run;
	private BoardPanel boardPanel;

	public BoardController(SwordAndShieldGame game, GameFrame run, BoardPanel boardPanel) {
		this.game = game;
		this.run = run;
		this.boardPanel = boardPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//if(boardPanel.moveAnimation || boardPanel.rotationAnimation || boardPanel.SWEDisappear || boardPanel.disappearAnimation) {
		//	return;
		//}
		
		boardPanel.mouseX = e.getX();
		boardPanel.mouseY = e.getY();
		if(!boardPanel.reactions) {
			boardPanel.findClickedToken();
		}
		System.out.println(boardPanel.mouseClicks);
		if (boardPanel.chosenToken != null && boardPanel.mouseClicks >=2 && !boardPanel.rotationAnimation && boardPanel.reactions == false) {
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
		if(run.currentPlayer.getMovesSoFar().contains(boardPanel.chosenToken.getName())){
			return;
		}
		if (key == KeyEvent.VK_UP) {
			if (boardPanel.chosenToken != null) {
				String letter = boardPanel.chosenToken.getName();
				System.out.println("move " + letter + " up");
				boardPanel.moveAnimation = true;
				boardPanel.moveDir = "up";
			}
		} else if (key == KeyEvent.VK_RIGHT) {
			if (boardPanel.chosenToken != null) {
				String letter = boardPanel.chosenToken.getName();
				System.out.println("move " + letter + " right");
				boardPanel.moveAnimation = true;
				boardPanel.moveDir = "right";
			}
		} else if (key == KeyEvent.VK_LEFT) {
			if (boardPanel.chosenToken != null) {
				String letter = boardPanel.chosenToken.getName();
				System.out.println("move " + letter + " left");
				boardPanel.moveAnimation = true;
				boardPanel.moveDir = "left";

			}
		} else if (key == KeyEvent.VK_DOWN) {
			if (boardPanel.chosenToken != null) {
				String letter = boardPanel.chosenToken.getName();
				System.out.println("move " + letter + " down");
				boardPanel.moveAnimation = true;
				boardPanel.moveDir = "down";
			}
		} else {
			System.out.println("invalid key");
		}


	}
	@Override
	public void keyReleased(KeyEvent e) {}

}
