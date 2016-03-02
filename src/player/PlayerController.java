package player;

import gameState.BattleState;

import java.awt.event.MouseEvent;

public class PlayerController {
	
	private int mode;
	
	private static final int GIVE_ARMY = 0;	//need to chose ally area to give new army
	private static final int FREE_MODE = 1; //need to chose ally area or end turn
	private static final int CHOSE_AREA = 2; //need to chose ally area to move or enemy area to attack
	
	private BattleState game;
	private Player player;
	
	private int armyAmount;
	
	public PlayerController(BattleState game, Player player) {
		this.game = game;
		this.player = player;
	}
	
	public void mouseClicked(MouseEvent e) {
		switch(mode) {
		case GIVE_ARMY:
			if(game.mouseOnArea() && game.mouseOnAreaOf(player)) game.addNewArmy(game.getMouseArea(), armyAmount);
			if(armyAmount == 0) mode = FREE_MODE;
			break;
			
		}
	}

}
