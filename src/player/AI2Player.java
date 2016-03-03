package player;

import gameState.BattleState;

import java.awt.Color;

import map.Area;

public class AI2Player extends Player{

	
	public AI2Player(String name, Color color, BattleState game) {
		super(name, color, game);
		human = false;
	}

	
	/**
	 * Distributes random armies to border areas.
	 */
	public void startTurn() {
		super.startTurn();
		int rngArea, rngAmount;
		while(armiesToDistribute > 0) {
			do {
				rngArea = rnd.nextInt(areas.size());
			} while(!areas.get(rngArea).hasEnemyBorders());
			rngAmount = rnd.nextInt(armiesToDistribute)+1;
			game.addNewArmy(areas.get(rngArea).getId(), rngAmount);
			armiesToDistribute -= rngAmount;
		}
	}
	
	public void doTurn() {
		for(Area a : areas) {
			if(!a.hasEnemyBorders() && a.getArmy() > 5) {
				for(Area n : a.getNeighbours()) {
					if(n.hasEnemyBorders()) {
						game.moveArmy(a.getId(), n.getId());
						break;
					}
				}
			}
		}
		
		findPossibleAttacks();
		
		if(borderAttacker.isEmpty()) {
			game.addLog(name + " ended his turn due to no attack possibilites!");
			game.endTurn();
		}
		else {
			int rng = rnd.nextInt(borderAttacker.size());
			game.attackArea(borderAttacker.get(rng).getId(), borderDefender.get(rng).getId());
		}
	}
	
	@Override
	protected void findPossibleAttacks() {
		borderAttacker.clear();
		borderDefender.clear();
		for(Area a : areas) {
			if(a.getArmy() > 1) {
				for(Area n : a.getNeighbours()) {
					if(!areas.contains(n) && a.getArmy() > n.getArmy()) {
						borderAttacker.add(a);
						borderDefender.add(n);
					}
					
				}
			}
		}
		if(borderAttacker.size() != borderDefender.size()) System.err.println("CRITICAL ERROR IN FINDING POSSIBLE ATTACKS!!!");
	}
	
}
