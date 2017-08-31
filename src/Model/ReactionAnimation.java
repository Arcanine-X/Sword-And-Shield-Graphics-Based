package Model;

public class ReactionAnimation {

	SwordAndShieldGame game;
	GameFrame run;
	BoardPanel boardPanel;
	String animationDir = "";
	int number;
	int horiNumber;

	public ReactionAnimation(SwordAndShieldGame game, GameFrame run, BoardPanel boardPanel) {
		this.game = game;
		this.run = run;
		this.boardPanel = boardPanel;
	}

	public int getRow(int value) {
		return value/boardPanel.WIDTH;
	}

	public int getCol(int value) {
		return value/boardPanel.HEIGHT;
	}

	public void tryReactionAnimation(Pair p) {
		System.out.println(p.getDir());
		int howManyToAnimate;
		if(p.getDir().equals("vert")) {
			animationDir = game.getDirectionOfAnimation(run.currentPlayer, p);
			if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("up")) {
				howManyToAnimate = game.verticalReactionAnimation(run.currentPlayer, p);
				System.out.println("bloop " + howManyToAnimate);
				if(howManyToAnimate == 0) {
					System.out.println(p.getOne().toString());
					System.out.println(p.getTwo().toString());
				}
				else if(howManyToAnimate == -1) {
					//needs to disappear
				}else if(howManyToAnimate == -2) {
					game.verticalReaction(run.currentPlayer, p);
				}else {
					for(int i = howManyToAnimate; i >= 0; i --) {
						int row = getRow(p.getTwo().yLoc - (i*boardPanel.HEIGHT));
						int col = getCol(p.getTwo().xLoc);
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc - boardPanel.HEIGHT;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						if(bp.equals(p.getTwo())) {
							continue;
						}
						if((row!=0) && !(col == 0 && row == 2) && !(col==1 && row == 2)) {
							bp.needToAnimate = true;
							boardPanel.aList.add(bp);
						}else {
							boardPanel.reactionDisappear = bp;
							boardPanel.playDisappearSound();
						}
						boardPanel.activateAnimation = true;
					}
				}
			}
			else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("down")) {
				System.out.println("in down animation for reactions");
				howManyToAnimate = game.verticalReactionAnimation(run.currentPlayer, p);
				System.out.println("bloop " + howManyToAnimate);
				if(howManyToAnimate == 0) {
					System.out.println(p.getOne().toString());
					System.out.println(p.getTwo().toString());
				}else if(howManyToAnimate == -1) {
					//needs to disappear
				}else if(howManyToAnimate == -2) {
					game.verticalReaction(run.currentPlayer, p);
				}
				else {
					System.out.println("in else?");
					for(int i = 0; i <= howManyToAnimate; i ++) {
						System.out.println("in for loooooooooooooooooop???");
						int row = getRow(p.getOne().yLoc + (i*boardPanel.HEIGHT));
						int col = getCol(p.getOne().xLoc);
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc + boardPanel.HEIGHT;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						if(bp.equals(p.getOne())) {
							continue;
						}
						if((row!=9) && !(col == 8 && row == 7) && !(col==9 && row == 7)) {
							bp.needToAnimate = true;
							boardPanel.aList.add(bp);
						}else {
							boardPanel.reactionDisappear = bp;
							boardPanel.playDisappearSound();
						}
						boardPanel.activateAnimation = true;
					}
				}

			}else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("swordVElse")) {
				number = game.findTokenToAnimate(run.currentPlayer, p);
				if(number == -10) {
					boardPanel.playDisappearSound();
					boardPanel.reactionPair = p;
					boardPanel.SWEDisappear = true;
				}else if(number == -11) {
					boardPanel.reactionDisappear = p.getTwo();
					boardPanel.playDisappearSound();
					boardPanel.reactionPair = p;
					boardPanel.SWEDisappear = true;
				}else if(number == -12) {
					boardPanel.playDisappearSound();
					boardPanel.reactionPair = p;
					boardPanel.SWEDisappear = true;
				}else if(number == -13) {
					boardPanel.playDisappearSound();
					game.verticalReaction(run.currentPlayer, p);
				}else if(number == -14) {
					boardPanel.playDisappearSound();
					game.verticalReaction(run.currentPlayer, p);
				}else if(number == -15) {

				}
			}
			else {
				game.verticalReaction(run.currentPlayer, p);
			}
		}
		if(p.getDir().equals("hori")) {
			animationDir = game.getDirectionOfAnimation(run.currentPlayer, p);
			if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("right")) {
				howManyToAnimate = game.horizontalReactionAnimation(run.currentPlayer, p);
				if(howManyToAnimate == 0) {
					System.out.println(p.getOne().toString());
					System.out.println(p.getTwo().toString());
				}else if(howManyToAnimate == -1) {
				}else if(howManyToAnimate == -2) {
					game.horizontalReaction(run.currentPlayer, p);

				}else {
					for(int i = 0; i <= howManyToAnimate; i ++) {
						int row = getRow(p.getOne().yLoc);
						int col = getCol(p.getOne().xLoc + (i*boardPanel.WIDTH));
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						bp.destX = bp.xLoc + boardPanel.WIDTH;
						if(bp.equals(p.getOne())) {
							continue;
						}
						if((col!=9) && !(col == 7 && row == 8) && !(col==7 && row == 9)) {
							bp.needToAnimate = true;
							boardPanel.aList.add(bp);
						}else {
							boardPanel.reactionDisappear = bp;
							boardPanel.playDisappearSound();
						}
						boardPanel.activateAnimation = true;
					}
				}
			}
			else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("left")) {
				howManyToAnimate = game.horizontalReactionAnimation(run.currentPlayer, p);
				if(howManyToAnimate == 0) {
				}else if(howManyToAnimate == -1) {
					//needs to disappear
				}else if(howManyToAnimate == -2) {
					game.horizontalReaction(run.currentPlayer, p);

				}else {
					for(int i = howManyToAnimate; i >= 0; i --) {
						int row = getRow(p.getTwo().yLoc);
						int col = getCol(p.getTwo().xLoc - (i*boardPanel.WIDTH));
						BoardPiece bp = ((BoardPiece)game.getBoard().getBoard()[row][col]);
						bp.destY = bp.yLoc;
						bp.moveY = bp.yLoc;
						bp.moveX = bp.xLoc;
						bp.destX = bp.xLoc - boardPanel.WIDTH;
						if(bp.equals(p.getTwo())) {
							continue;
						}
						if((col!=0) && !(col == 2 && row == 0) && !(col==2 && row == 1)) {
							bp.needToAnimate = true;
							boardPanel.aList.add(bp);
						}else {
							boardPanel.reactionDisappear = bp;
							boardPanel.playDisappearSound();
						}
						boardPanel.activateAnimation = true;
					}
				}

			}else if(game.getDirectionOfAnimation(run.currentPlayer, p).equals("swordVElse")) {
				System.out.println("in swordVElse");
				boardPanel.horiNumber = game.findTokenToAnimateHori(run.currentPlayer, p);
				if(boardPanel.horiNumber == -20) {
					boardPanel.playDisappearSound();
					boardPanel.reactionPair = p;
					boardPanel.SWEDisappear = true;
				}else if(horiNumber == -21) {
					boardPanel.reactionDisappear = p.getTwo();
					boardPanel.playDisappearSound();
					boardPanel.reactionPair = p;
					boardPanel.SWEDisappear = true;
				}else if(horiNumber == -22) {
					boardPanel.playDisappearSound();
					boardPanel.reactionPair = p;
					boardPanel.SWEDisappear = true;
				}else if(horiNumber == -23) {
					boardPanel.playDisappearSound();
					game.horizontalReaction(run.currentPlayer, p);
				}else if(horiNumber == -24) {
					boardPanel.playDisappearSound();
					game.horizontalReaction(run.currentPlayer, p);
				}
			else if(horiNumber == -25) {
				}
			}
			else {
				game.horizontalReaction(run.currentPlayer, p);
			}
		}


	}

}
