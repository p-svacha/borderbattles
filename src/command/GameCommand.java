package command;

import gameState.BattleState;

public class GameCommand {
	
	protected int commandDelay;
	protected int counter;
	
	protected BattleState game;
	protected boolean done;
	
	public GameCommand(BattleState game) {
		this.game = game;
	}
	
	public void init() {}
	public void update() {}
	public void finish() {
		done = true;
	}
	
	public boolean done() { return done; }

}
