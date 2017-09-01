package Controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Model.GameFrame;
import Model.SwordAndShieldGame;
import Model.TokenPanel;

/**
 * This class 
 * @author Chin Patel
 *
 */
public class TokenController implements MouseListener{
	SwordAndShieldGame game;
	GameFrame run;
	TokenPanel tokenPanel;

	public TokenController(SwordAndShieldGame game, GameFrame run, TokenPanel tokenPanel) {
		this.game = game;
		this.run = run;
		this.tokenPanel = tokenPanel;

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		tokenPanel.setMouseX(e.getX());
		tokenPanel.setMouseY(e.getY());
		if(game.getBoard().getUndoStack().size() == 1) {
			if(tokenPanel.getClickedPiece()==null) {
				tokenPanel.clicked();
				if(tokenPanel.getClickedPiece()!=null) {
					if(!tokenPanel.getClickedPiece().getCol().equals(run.currentPlayer.getName())) {
						tokenPanel.setClickedPiece(null);
						return;
					}
					tokenPanel.getRotations();

				}
			}else {
				tokenPanel.playToken();
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {	}

}
