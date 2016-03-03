package player;

import gameState.BattleState;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import map.Area;

public class Player {

	protected String name;
	protected boolean human;
	protected Color color;
	protected Color frameColor;
	protected ArrayList<Area> areas;				//the areas that belong to this player
	protected boolean active;						//true when its this players turn
	protected boolean dead;
	protected int armiesToDistribute;
	
	//borderAttacker.get(i) ONLY belongs to borderDefender.get(i)!!!!dont use different index for the 2 lists.
	protected ArrayList<Area> borderAttacker;		//Used for possible attacks. Only own areas with an army > 1.
	protected ArrayList<Area> borderDefender;		//Used for possible attacks. Stores enemy areas, the defending.
	
	protected BattleState game;
	protected Random rnd;
	
	public Player(String name, Color color, BattleState game) {
		this.name = name;
		this.color = color;
		frameColor = Area.getContrastColor(color);
		this.game = game;
		areas = new ArrayList<Area>();
		borderAttacker = new ArrayList<Area>();
		borderDefender = new ArrayList<Area>();
		rnd = new Random();
	}
	
	public void update() {
		if(!dead) {
			findPossibleAttacks();
			dead = areas.isEmpty();
		}
	}
	
	protected void findPossibleAttacks() {
		borderAttacker.clear();
		borderDefender.clear();
		for(Area a : areas) {
			if(a.getArmy() > 1) {
				for(Area n : a.getNeighbours()) {
					if(!areas.contains(n)) {
						borderAttacker.add(a);
						borderDefender.add(n);
					}
					
				}
			}
		}
		if(borderAttacker.size() != borderDefender.size()) System.err.println("CRITICAL ERROR IN FINDING POSSIBLE ATTACKS!!!");
	}
	
	/**
	 * Starts the turn of this player, giving him new armies.
	 */
	public void startTurn() {
		armiesToDistribute = areas.size();
	}
	
	/**
	 * AI Players do their turn. 
	 * > They move armies from inland to borderland. if > 1.
	 * > They attack .
	 * > If they can't attack (only have areas with 1 army) they skip the turn.
	 */
	public void doTurn() {}
	
	/**
	 * Adds area a to the players area pool.
	 */
	public void addArea(Area a) {
		areas.add(a);
	}
	
	/**
	 * Removes area a from the players area pool.
	 */
	public void removeArea(Area a) {
		areas.remove(a);
	}
	
	public void setName(String name) { this.name = name; }
	public void setColor(Color color) { this.color = color; }
	public void setActive(boolean active) { this.active = active; }
	public void setDead(boolean dead) { this.dead = dead; }
	public void setArmiesToDistribute(int armiesToDistribute) { this.armiesToDistribute = armiesToDistribute; }
	
	public String getName() { return name; }
	public Color getColor() { return color; }
	public boolean getActive() { return active; }
	public boolean getDead() { return dead; }
	public boolean isHuman() { return human; }
	public int getArmiesToDistribute() { return armiesToDistribute; }
	public Color getFrameColor() { return frameColor; }
	public ArrayList<Area> getAreas() { return areas; }
	
}
