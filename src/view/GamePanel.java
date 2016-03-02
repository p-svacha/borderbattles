package view;

import gameState.GameStateManager;
import gameState.MapState;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener{

	private static final long serialVersionUID = -7804795314443379967L;
	
	
	//Dimensions
	public static int WIDTH = 1400;
	public static int HEIGHT = 960;
	public static final int SCALE = 1;
	
	//CONSTANTS
	private static final int FPS = 60; //max FPS / Optimalfall
	private static final boolean CAP_FPS = true; //if false, fps is uncapped
	
	//Game Thread
	private Thread thread;
	private boolean running;
	private long targetTime = 1000 / FPS; //alle targettime milisekunden wird geupdatet
	int actualFPS = 60;
	
	//image
	private BufferedImage image;
	private Graphics2D g;
	
	//gamestate manager
	private GameStateManager gsm;
	
	//Constructor
	public GamePanel()
	{
		super();
		
		setFocusable(true);
		requestFocus();
		
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
	}

	
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			thread = new Thread(this);
			addKeyListener(this);
			addMouseMotionListener(this);
			addMouseListener(this);
			thread.start();
		}
	}
	
	public void run()
	{
		init();
		
		long start;
		long elapsed;
		long wait;
		long fpsTimerStart = System.currentTimeMillis();
		int frameCounter = 0;

	
		
		while(running)
		{
			start = System.nanoTime();
			
			update();
			draw();
			drawToScreen();
			frameCounter++;
			
			if(CAP_FPS) {
				wait = 1;
		
				while(wait > 0) {
					elapsed = System.nanoTime() - start;
					wait = targetTime - elapsed / 1000000;
				}
			}
			
			//FPS DISPLAY
			if(System.currentTimeMillis()-fpsTimerStart >= 1000) {
				//actualFPS = (int) (0.9*actualFPS + 0.1*frameCounter); //Smooth FPS
				actualFPS = frameCounter;
				frameCounter = 0;
				fpsTimerStart = System.currentTimeMillis();
			}
			
		}
		
		
	}
	
	private void init()
	{
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		running = true;
		
		gsm = new GameStateManager();
		
		
	}
	
	
	
	private void update() {
		gsm.update();
		
	}
	
	private void draw() {
		gsm.draw(g);
		g.setFont(MapState.textFont);
		g.drawString("FPS: " + Integer.toString(actualFPS), 10, 10);
	}
	
	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}
	
	
	public void keyTyped(KeyEvent e) {}
	
	public void keyPressed(KeyEvent key) {
		gsm.keyPressed(key);
	}
	
	public void keyReleased(KeyEvent key) {
		if(gsm != null) gsm.keyReleased(key);
	}

	public void mouseDragged(MouseEvent m) {
		gsm.mouseDragged(m);
	}

	public void mouseMoved(MouseEvent m) {
		if(gsm != null) gsm.mouseMoved(m);
	}


	public void mousePressed(MouseEvent m) {
		gsm.mousePressed(m);
	}
	
	public void mouseReleased(MouseEvent m) {
		gsm.mouseReleased(m);
	}


	public void mouseEntered(MouseEvent m) {}
	public void mouseExited(MouseEvent m) {}
	public void mouseClicked(MouseEvent m) {
		gsm.mouseClicked(m);
	}

	
	
}
