package player;

import gameState.BattleState;

import java.awt.Color;

public class NeutralPlayer extends Player{

	public NeutralPlayer(BattleState game) {
		super("Neutral", Color.WHITE, game);
		human = false;
	}
	
	public void update() {}
}
