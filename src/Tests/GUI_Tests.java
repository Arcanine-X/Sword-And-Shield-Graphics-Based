package Tests;


import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.junit.Test;

import Model.BoardPiece;
import Model.SwordAndShieldGame;
import View.BoardPanel;
import View.GameFrame;

public class GUI_Tests {

	GameFrame gameFrame;

	void renew() throws InterruptedException{
		SwingUtilities.invokeLater(()->{
			new Timer(5000,e->{
				if(gameFrame!=null){
					gameFrame.dispose();
					gameFrame = null;
				}}).start();
		});
		Thread.sleep(5000);
	}


	@Test
	public void validYellowCreation() throws InterruptedException {

		/*SwordAndShieldGame game = new SwordAndShieldGame();
		gameFrame = new GameFrame();
		new Timer(50, (e) -> {
			gameFrame.repaint();
		}).start();
		Thread.sleep(2000);
		BoardPiece one = game.getYellow().find("c");
		game.getBoard().getBoard()[7][7] = one;
		gameFrame.boardPanel.validate();
		gameFrame.boardPanel.repaint();
		gameFrame.tokenPanelY.clickedPiece = one;
		System.out.println("da fuck");
		if(game.getBoard().getBoard()[7][7] == null) {
			System.out.println("wot");
		}else{System.out.println("good");}
		Thread.sleep(2000);*/


	}

	@Test
	public void test() throws InterruptedException {

	}

}
