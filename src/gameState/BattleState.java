package gameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import log.EventLog;
import map.Area;
import player.AI1Player;
import player.HumanPlayer;
import player.NeutralPlayer;
import player.Player;
import player.PlayerController;
import view.GamePanel;

import command.AttackCommand;
import command.GameCommand;
import command.MoveArmyCommand;
import command.NewArmyCommand;

@SuppressWarnings("serial")
public class BattleState extends MapState{
	
	
	//OPTIONS
	private boolean FULL_SIMULATION = true;
	private final int STARTING_ARMY = 5;
	public final int ATTACK_CHANCE = 50; //Attack chance in % that the attacker wins a fight
	public final int HUMAN_PLAYERS = 1;
	private final int AI_PLAYER_AMOUNT = 9;
	private final int STARTAREA_AMOUNT = 3;
	
	private ArrayList<Area> neutralAreas;
	private Random rnd;

	NeutralPlayer neutralPlayer;
	ArrayList<Player> players;
	ArrayList<Player> playersByArea;
	private int activePlayer;
	private PlayerController playerController;	//for human turns
	
	private String title;
	private EventLog log;
	
	private Queue<GameCommand> commands;
	private GameCommand activeCommand;
	
	//handling inputs
	private boolean animationRunning;
	private boolean humanTurn;
	
	@SuppressWarnings("unchecked")
	public BattleState(GameStateManager gsm) {
		super(gsm);
		players = new ArrayList<Player>();
		playersByArea = new ArrayList<Player>();
		playerController = new PlayerController(this);
		neutralAreas = (ArrayList<Area>) areas.clone();
		rnd = new Random();
		log = new EventLog();
		commands = new LinkedList<GameCommand>();
		
		initGame();
		
	}
	
	private void initGame() {
		neutralPlayer = new NeutralPlayer(this);
		
		Player[] possiblePlayers = {
//			new AI1Player("Red", new Color(255, 0, 0), this),
			new AI1Player("Lime", new Color(0, 255, 0), this),
			new AI1Player("Blue", new Color(0, 0, 255), this),
			new AI1Player("Yellow", new Color(255, 255, 0), this),
			new AI1Player("Magenta", new Color(255, 0, 255), this), //5
			new AI1Player("Cyan", new Color(0, 255, 255), this),
			new AI1Player("Green", new Color(0, 128, 0), this),
			new AI1Player("Maroon", new Color(128, 0, 0), this),
			new AI1Player("Navy", new Color(0, 0, 128), this),
			new AI1Player("Orange", new Color(255, 128, 0), this), //10
			new AI1Player("Indigo", new Color(75, 0, 130), this),
			new AI1Player("Teal", new Color(0, 128, 128), this),
			new AI1Player("Salmon", new Color(250, 128, 114), this),
			new AI1Player("Olive", new Color(128, 128, 0), this),
			new AI1Player("Silver", new Color(192, 192, 192), this), //15
			new AI1Player("Gray", new Color(128, 128, 128), this),
			new AI1Player("Purple", new Color(128, 0, 128), this),
			new AI1Player("Rosy", new Color(188, 143, 143), this),
			new AI1Player("Khaki", new Color(189, 183, 107), this),
			new AI1Player("Brown", new Color(139, 69, 19), this),
		};
		
		for(int i = 0; i < HUMAN_PLAYERS; i++) {
			players.add(new HumanPlayer("YOU", Color.RED, this));
		}
		for(int i = 0; i < AI_PLAYER_AMOUNT; i++) {
			int rng = rnd.nextInt(possiblePlayers.length);
			while(players.contains(possiblePlayers[rng])) {
				rng = rnd.nextInt(possiblePlayers.length);
			}
			players.add(possiblePlayers[rng]);
		}
		
		for(Area a : areas) {
			a.setPlayer(neutralPlayer);
			neutralPlayer.addArea(a);
		}
		
		for(int i = 0; i < STARTAREA_AMOUNT; i++) {
			for(int j = 0; j < AI_PLAYER_AMOUNT+HUMAN_PLAYERS; j++) {
				int rng = rnd.nextInt(neutralAreas.size());
				players.get(j).addArea(neutralAreas.get(rng));
				neutralAreas.get(rng).setArmy(STARTING_ARMY);
				neutralAreas.get(rng).setPlayer(players.get(j));
				neutralAreas.remove(rng);
			}
		}
		
		players.get(activePlayer).startTurn();
		if(humanTurn = players.get(activePlayer).isHuman()) playerController.start(players.get(activePlayer));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		//Human
		if(humanTurn) playerController.update();
		
		//AI
		super.update();
		neutralPlayer.update();
		ArrayList<Player> toRemove = new ArrayList<Player>();
		for(int i = 0; i < players.size(); i++) {
			players.get(i).update();
			players.get(i).setActive(activePlayer == i);
			if(players.get(i).getDead()) {
				toRemove.add(players.get(i));
				log.addLog(players.get(i).getName() + " DIED!");
			}
		}
		Player currentPlayer = players.get(activePlayer);
		players.removeAll(toRemove);
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i) == currentPlayer) activePlayer = i;
		}
		title = players.get(activePlayer).getName().toUpperCase() + "'S TURN";
		
		//Sort players for statistics
		playersByArea = (ArrayList<Player>) players.clone();
		Collections.sort(playersByArea, new Comparator<Player>() {
			public int compare(Player p2, Player p1) {
				return p1.getAreas().size() - p2.getAreas().size();
			}
		});
		
		//Update Commands
		if(activeCommand == null) {
			if(!commands.isEmpty()) {
				activeCommand = commands.poll();
				animationRunning = true;
			}
		}
		if(activeCommand != null) {
			activeCommand.update();
			if(activeCommand.done()) {
				activeCommand = null;
				if(commands.isEmpty()) animationRunning = false;
			}
		}
		
		//Check win
		if(players.size() == 1) {
			if(!animationRunning) log.addLog(players.get(0).getName() + " wins!");
			animationRunning = true;
		}
		
		//Simulation
		if(FULL_SIMULATION && !animationRunning && !humanTurn) players.get(activePlayer).doTurn();
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		if(humanTurn) playerController.draw(g);
		g.setColor(Color.WHITE);
		g.setFont(textFont);
		if(mouseArea >= 0) {
			g.drawString("Player: " + areas.get(mouseArea).getPlayer().getName(), 10, 880);
			g.drawString("Size: " + areas.get(mouseArea).getSize(), 10, 900);
			g.drawString("Army: " + areas.get(mouseArea).getArmy(), 10, 920);
		}
		for(int i = 0; i < players.size(); i++) {
			g.drawString(playersByArea.get(i).getName() + ": " + playersByArea.get(i).getAreas().size(), 820 + 80*(i%5), 60 + 20 * (i/5));
		}
		g.setFont(titleFont);
		int strLen = g.getFontMetrics(g.getFont()).stringWidth(title);
		g.drawString(title, MAP_DISPLAY_SIZE+(GamePanel.WIDTH-MAP_DISPLAY_SIZE)/2-strLen/2, 20);
		log.draw(g);
	}
	

	/**
	 * Follow upon a successful attack. The attacker moves half of his remaining army onto the new area.
	 * The area with id areaId gets taken by the area with id attackerArea.
	 */
	public void takeArea(int attackerArea, int defenderArea) {
		neutralAreas.remove(areas.get(defenderArea));
		areas.get(defenderArea).getPlayer().removeArea(areas.get(defenderArea));
		areas.get(defenderArea).setPlayer(players.get(activePlayer));
		players.get(activePlayer).addArea(areas.get(defenderArea));
		int movingArmy = areas.get(attackerArea).getArmy()-1;
		areas.get(attackerArea).setArmy(areas.get(attackerArea).getArmy()-movingArmy);
		areas.get(defenderArea).setArmy(movingArmy);
	}
	
	/**
	 * Area with id attackerArea attacks the area with id defenderArea.
	 * Every army fights 1v1 with 50% chance to win until one the attacker 1 army left
	 * or the defender has 0 armies left.
	 * Should only be called by players.
	 */
	public void attackArea(int attackerArea, int defenderArea) {
		commands.add(new AttackCommand(this, attackerArea, defenderArea));
	}
	
	/**
	 * Adds a MoveArmyCommand to the CommandQueue, which moves amount armies from area with id areaFrom to area with id areaTo.
	 * Should only be called by Players.
	 * Does check preconditions, check your console!
	 */
	public void moveArmy(int areaFrom, int areaTo) {
		if(areas.get(areaFrom).getPlayer() != players.get(activePlayer)) System.err.println("CRITICAL ERROR IN ARMY MOVEMENT: AREA_FROM DOESNT BELONG TO THE ACTIVE PLAYER");
		if(areas.get(areaTo).getPlayer() != players.get(activePlayer)) System.err.println("CRITICAL ERROR IN ARMY MOVEMENT: AREA_TO DOESNT BELONG TO THE ACTIVE PLAYER");
		if(areas.get(areaFrom).getArmy() <= 1) System.err.println("CRITICAL ERROR IN ARMY MOVEMENT: NOT ENOUGH ARMIES IN AREA_FROM TO MOVE");
		
		commands.add(new MoveArmyCommand(this, areaFrom, areaTo));
	}
	
	/**
	 * Adds a NewArmyCommand to the CommandQueue to add amount new armies to the area with id areaid.
	 * Should only be called by players.
	 * Does check preconditions, check your console!
	 */
	public void addNewArmy(int areaId, int amount) {
		if(areas.get(areaId).getPlayer() != players.get(activePlayer)) System.err.println("CRITICAL ERROR IN ADDING NEW ARMY: AREA_ID DOESNT BELONG TO THE ACTIVE PLAYER");
		if(amount <= 0) System.err.println("CRITICAL ERROR IN ADDING NEW ARMY: AMOUNT IS NOT > 0");
		
		commands.add(new NewArmyCommand(this, areaId, amount));
	}
	
	/**
	 * Should be called by the active player to end his turn.
	 */
	public void endTurn() {
		addLog(players.get(activePlayer).getName() + " ended his turn!");
		if(activePlayer == players.size() -1) activePlayer = 0;
		else activePlayer++;
		players.get(activePlayer).startTurn();
		if(humanTurn = players.get(activePlayer).isHuman()) playerController.start(players.get(activePlayer));
	}
	
	/**
	 * Adds the String s to the log. Useful if a player wants to write to the log.
	 */
	public void addLog(String s) {
		log.addLog(s);
	}

	public ArrayList<Area> getNeutralAreas() { return neutralAreas; }

	@Override
	public void keyPressed(KeyEvent k) {
		super.keyPressed(k);
		if(k.getKeyCode() == KeyEvent.VK_SPACE && !FULL_SIMULATION && !animationRunning) players.get(activePlayer).doTurn();
		if(humanTurn) playerController.KeyPressed(k);
	}
	
	@Override
	public void mouseClicked(MouseEvent m) {
		if(humanTurn) playerController.mouseClicked(m);
	}
	
	public ArrayList<Area> getAreas() { return areas; }
	
}
