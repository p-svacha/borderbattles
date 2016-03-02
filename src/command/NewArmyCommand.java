package command;

import gameState.BattleState;

public class NewArmyCommand extends GameCommand{

	private int areaId;
	private int amount;
	
	public NewArmyCommand(BattleState game, int areaId, int amount) {
		super(game);
		this.areaId = areaId;
		this.amount = amount;
	}
	
	public void update() {
		if(amount == 0) finish();
		else game.getAreas().get(areaId).setArmy(game.getAreas().get(areaId).getArmy()+1);
		amount--;
	}
	
	public void finish() {
		super.finish();
	}

}
