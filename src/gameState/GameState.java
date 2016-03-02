package gameState;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class GameState extends JPanel {
	
	protected GameStateManager gsm;
	

	
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void keyPressed(KeyEvent k);
	public abstract void keyReleased(KeyEvent k);
	public abstract void mouseDragged(MouseEvent m);
	public abstract void mouseMoved(MouseEvent m);
	public abstract void mousePressed(MouseEvent m);
	public abstract void mouseReleased(MouseEvent m);
	public abstract void mouseClicked(MouseEvent m);

}
