package command;

import gameState.BattleState;
import player.Player;

public class NewArmyCommand extends GameCommand{

	private int areaId;
	private Player player;
	private int originalAmount;
	private int amount;
	
	public NewArmyCommand(BattleState game, int areaId, int amount) {
		super(game);
		this.areaId = areaId;
		this.amount = amount;
		this.originalAmount = amount;
		player = game.getAreas().get(areaId).getPlayer();
	}
	
	public void update() {
		if(amount == 0) finish();
		else game.getAreas().get(areaId).setArmy(game.getAreas().get(areaId).getArmy()+1);
		amount--;
	}
	
	public void finish() {
		super.finish();
		player.setArmiesToDistribute(player.getArmiesToDistribute()-originalAmount);
		game.addLog(player.getName() + " distributed " + originalAmount + " armies to " + game.getAreas().get(areaId).getName() + "!");
	}

}
