package player;

import gameState.BattleState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

public class PlayerController {
	
	private int mode;
	
	private static final int DISTRIBUTE_ARMY = 0;	//need to chose ally area to give new army
	private static final int FREE_MODE = 1; //need to chose ally area or end turn
	private static final int AREA_CHOSEN = 2; //need to chose ally area to move or enemy area to attack
	
	private BattleState game;
	private Player player;
	
	private int distributionAmount = 1;
	
	private Font font;
	private String action;
	private String action2;
	private String action3;
	
	private int selectedArea;
	
	public PlayerController(BattleState game) {
		this.game = game;
		font = new Font("Arial", Font.PLAIN, 16);
		action = ""; action2 = "";
	}
	
	public void update() {
		switch(mode) {
		case DISTRIBUTE_ARMY:
			action = "Chose one of your areas to put your new armies!";
			action2 = "Scroll to change the amount (currently " + distributionAmount + ")!";
			action3 = "You can distribute " + player.getArmiesToDistribute() + " more armies!";
			break;
			
		case FREE_MODE:
			action = "Chose one of your areas with more than 1 army to perform an action!";
			action2 = "Press Enter to end your turn!";
			action3 = "";
			break;
			
		case AREA_CHOSEN:
			action = "Chose an adjacent allyto move your armies there (1 will stay)!";
			action2 = "Chose an adjacent enemy area to attack it!";
			action3 = "Right click to abort!";
			break;
		}
	}
	
	public void mouseClicked(MouseEvent m) {
		switch(mode) {
		case DISTRIBUTE_ARMY:
			if(game.mouseOnArea() && game.mouseOnAreaOf(player)) game.addNewArmy(game.getMouseArea(), distributionAmount);
			if(player.getArmiesToDistribute()-distributionAmount == 0) mode = FREE_MODE;
			break;
			
		case FREE_MODE:
			if(game.mouseOnArea() && game.mouseOnAreaOf(player)  && game.getAreas().get(game.getMouseArea()).getArmy() > 1) {
				selectedArea = game.getMouseArea();
				game.getAreas().get(selectedArea).highlight(true);
				mode = AREA_CHOSEN;
			}
			break;
			
		case AREA_CHOSEN:
			//move
			if(SwingUtilities.isLeftMouseButton(m) && game.mouseOnAreaOf(player) && game.mouseOnAreaAdjacentTo(selectedArea)) {
				game.getAreas().get(selectedArea).highlight(false);
				game.moveArmy(selectedArea, game.getMouseArea());
				mode = FREE_MODE;
			}
			//attack
			if(SwingUtilities.isLeftMouseButton(m) && game.mouseOnAreaNotOf(player) && game.mouseOnAreaAdjacentTo(selectedArea)) {
				game.attackArea(selectedArea, game.getMouseArea());
				game.getAreas().get(selectedArea).highlight(false);
				mode = FREE_MODE;
			}
			//abort
			if(SwingUtilities.isRightMouseButton(m)) {
				game.getAreas().get(selectedArea).highlight(false);
				mode = FREE_MODE;
			}
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent m) {
		if(mode == DISTRIBUTE_ARMY) {
			int noches = -1*m.getWheelRotation();
			if(distributionAmount + noches > player.getArmiesToDistribute()) distributionAmount = player.getArmiesToDistribute();
			else if(distributionAmount + noches <= 0) distributionAmount = 1;
			else distributionAmount += noches;
		}
	}
	
	public void KeyPressed(KeyEvent k) {
		if(k.getKeyCode() == KeyEvent.VK_ENTER && mode == FREE_MODE) {
			player = null;
			game.endTurn();
		}
	}
	
	public void draw(Graphics2D g) {
		g.setFont(font);
		g.setColor(new Color(255, 255, 255));
		g.drawString(action, 800, 890);
		g.drawString(action2, 800, 920);
		g.drawString(action3, 800, 950);
	}
	
	public void start(Player p) { 
		distributionAmount = 1;
		mode = DISTRIBUTE_ARMY;
		player = p; 
	}

}
