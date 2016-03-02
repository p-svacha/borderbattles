package gameState;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import view.Game;
import view.GamePanel;

public class GameStateManager {

	private GameState currentState;
	
	public static int mouseX;
	public static int mouseY;
	
	public static BufferedImage background;
	
	

	
	public GameStateManager()
	{
		loadPictures();
		BattleState bs = new BattleState(this);
		currentState = bs;
		Game.mainPanel.add(bs);
		
//		EditorState es = new EditorState(this);
//		currentState = es;
//		Game.mainPanel.add(es);
	}
	
	private void loadPictures() {
		
	}
	
	
	public void setState(GameState state)
	{
		currentState = state;
	}
	
	public void update()
	{
		currentState.update();
	}
	public void draw(Graphics2D g)
	{
		currentState.draw(g);
	}
	
	
	public void keyPressed(KeyEvent k)
	{
		currentState.keyPressed(k);
	}
	public void keyReleased(KeyEvent k)
	{
		currentState.keyReleased(k);
	}
	
	
	public void mouseDragged(MouseEvent m) {
		mouseX = m.getX();
		mouseY = m.getY();
		currentState.mouseDragged(m);
	}
	public void mouseMoved(MouseEvent m) {
		mouseX = m.getX();
		mouseY = m.getY();
		currentState.mouseMoved(m);
	}
	
	public void mousePressed(MouseEvent m) {
		currentState.mousePressed(m);
	}
	
	public void mouseReleased(MouseEvent m) {
		currentState.mouseReleased(m);
	}
	
	public void mouseClicked(MouseEvent m) {
		currentState.mouseClicked(m);
	}
	
	
	
	public static boolean mouseOnScreen() {
		return (mouseX > 0 && mouseX <= GamePanel.WIDTH && mouseY > 0 && mouseY <= GamePanel.HEIGHT);
	}
	
	
}
