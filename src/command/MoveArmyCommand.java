package command;

import gameState.BattleState;
import player.Player;

public class MoveArmyCommand extends GameCommand{

	private int areaFrom;
	private int areaTo;
	private Player player;
	private int amount;
	
	public MoveArmyCommand(BattleState game, int areaFrom, int areaTo) {
		super(game);
		this.areaFrom = areaFrom;
		this.areaTo = areaTo;
		amount = game.getAreas().get(areaFrom).getArmy()-1;
		player = game.getAreas().get(areaFrom).getPlayer();
	}
	
	public void update() {
		if(game.getAreas().get(areaFrom).getArmy() == 1) finish();
		else {
			game.getAreas().get(areaFrom).setArmy(game.getAreas().get(areaFrom).getArmy()-1);
			game.getAreas().get(areaTo).setArmy(game.getAreas().get(areaTo).getArmy()+1);
		}
	}
	
	public void finish() {
		super.finish();
		game.addLog(player.getName() + " moved " + amount + " armies from " + game.getAreas().get(areaFrom).getName() + " to " + game.getAreas().get(areaTo).getName() + "!");
	}

}
