package command;

import gameState.BattleState;

import java.util.Random;

public class AttackCommand extends GameCommand{
	
	private int attackerArea;
	private int defenderArea;
	
	private int attackerDeaths;
	private int defenderDeaths;
	
	private Random rnd;
	
	public AttackCommand(BattleState game, int attackerArea, int defenderArea) {
		super(game);
		this.attackerArea = attackerArea;
		this.defenderArea = defenderArea;
		rnd = new Random();
		commandDelay = 1;
		init();
	}
	
	public void init() {
		game.getAreas().get(attackerArea).highlight(true);
		game.getAreas().get(defenderArea).highlight(true);
	}
	
	public void update() {
		if(counter%commandDelay == 0) {
			if(game.getAreas().get(attackerArea).getArmy() == 1) {
				finish();
				game.addLog(game.getAreas().get(attackerArea).getPlayer().getName() + "'s " + game.getAreas().get(attackerArea).getName() 
						+ " (" + attackerDeaths + " Deaths) " + " was unsuccesful in conquering " 
						+ game.getAreas().get(defenderArea).getPlayer().getName() + "'s " + game.getAreas().get(defenderArea).getName() 
						+ " (" + defenderDeaths + " Deaths)!");
			}
			else if(game.getAreas().get(defenderArea).getArmy() == 0) {
				finish();
				game.addLog(game.getAreas().get(attackerArea).getPlayer().getName() + "'s " + game.getAreas().get(attackerArea).getName() 
						+ " (" + attackerDeaths + " Deaths) " + " took over " 
						+ game.getAreas().get(defenderArea).getPlayer().getName() + "'s " + game.getAreas().get(defenderArea).getName()
						+ " (" + defenderDeaths + " Deaths)!");
				game.takeArea(attackerArea, defenderArea);
			}
			else {
				int rng = rnd.nextInt(100);
				if(rng < game.ATTACK_CHANCE) {
					game.getAreas().get(defenderArea).setArmy(game.getAreas().get(defenderArea).getArmy()-1);
					defenderDeaths++;
				}
				else {
					game.getAreas().get(attackerArea).setArmy(game.getAreas().get(attackerArea).getArmy()-1);
					attackerDeaths++;
				}
			}
		}
		counter++;
	}
	
	public void finish() {
		game.getAreas().get(attackerArea).highlight(false);
		game.getAreas().get(defenderArea).highlight(false);
		super.finish();
	}
	
}
