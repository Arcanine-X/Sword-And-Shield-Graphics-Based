package Controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Model.SwordAndShieldGame;
import View.GameFrame;
import View.TokenPanel;

/**
 * This class is the controller for the token panels. It sets the values in token panel according to what is the controller is used to click on.
 * @author Chin Patel
 *
 */
public class TokenController implements MouseListener{
	private SwordAndShieldGame game;
	private GameFrame run;
	private TokenPanel tokenPanel;

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
				//If clicked piece is null then let the player select a token
				tokenPanel.clicked();
				if(tokenPanel.getClickedPiece()!=null) {
					//Ensure that they dont click on the other players tokens --- if so return
					if(!tokenPanel.getClickedPiece().getCol().equals(run.getCurrentPlayer().getName())) {
						tokenPanel.setClickedPiece(null);
						return;
					}
					//Display the rotations of the clicked token
					tokenPanel.getRotations();
				}
			}else {
				//Play that token
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
