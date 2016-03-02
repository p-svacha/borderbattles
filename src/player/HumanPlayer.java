package player;

import gameState.BattleState;

import java.awt.Color;

public class HumanPlayer extends Player{

	
	public HumanPlayer(String name, Color color, BattleState game) {
		super(name, color, game);
		human = true;
	}

	
	public void startTurn() {
		super.startTurn();
	}
	
	public void doTurn() {}
}
