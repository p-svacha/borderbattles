package player;

import gameState.BattleState;

import java.awt.Color;

import map.Area;

public class HumanPlayer extends Player{

	
	public HumanPlayer(String name, Color color, BattleState game) {
		super(name, color, game);
		human = true;

	}

	
	public void startTurn() {
		int newArmy = areas.size();
		for(int i = 0; i < newArmy; i++) {
			int rng = rnd.nextInt(areas.size());
			areas.get(rng).setArmy(areas.get(rng).getArmy()+1);
		}
	}
	
	public void doTurn() {
		for(Area a : areas) {
			if(!a.hasEnemyBorders() && a.getArmy() > 5) {
				for(Area n : a.getNeighbours()) {
					if(n.hasEnemyBorders()) {
						game.moveArmy(a.getId(), n.getId(), a.getArmy()-1);
						break;
					}
				}
			}
		}
		
		update();
		
		if(borderAttacker.isEmpty()) {
			game.addLog(name + " skipped his turn due to no attack possibilites!");
			game.endTurn();
		}
		else if(rnd.nextInt(100) < 75) {
			int rng = rnd.nextInt(borderAttacker.size());
			game.attackArea(borderAttacker.get(rng).getId(), borderDefender.get(rng).getId());
		}
		else {
			game.addLog(name + " ended his turn!");
			game.endTurn();	
		}
		
	}
}
