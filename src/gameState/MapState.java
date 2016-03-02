 package gameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import map.Area;
import map.FloodFiller;
import player.Player;
import view.GamePanel;

@SuppressWarnings("serial")
public class MapState extends GameState {
	
	public static final int MAP_DISPLAY_SIZE = 800;
	public static final int SCROLL_SPEED = 10;
	
	protected String mapName;
	
	protected BufferedImage map;
	protected int xCoord, yCoord;	//current position of mouse ON MAP, -1 for outside of screen
	protected double imgScale;			//zoom level, 1 default, 2 zoomed in
	protected int xOffset, yOffset;	//distance from left / top border in pixel
	protected boolean scrollable;
	protected boolean mapMoveRight, mapMoveDown, mapMoveLeft, mapMoveUp;
	
	protected FloodFiller ff;
	
	protected ArrayList<Area> areas;
	protected int[][] pixelArea;
	protected int mouseArea;	//area id where the mouse is currently hovering. when outside of screen its = clickedarea
	protected int clickedArea; //selected area per click
	
	protected Font titleFont;
	public static Font textFont;
	
	protected KeyListener mapMovementKL;
	
	
	public MapState(GameStateManager gsm){
		this.gsm = gsm;
		mapName = "ornament1";
		
		clickedArea = FloodFiller.VOID_PIXEL;
		
		titleFont = new Font("Arial", Font.BOLD, 24);
		textFont = new Font("Arial", Font.PLAIN, 12);
		
		mapMovementKL = new KeyListener() {
			public void keyPressed(KeyEvent k) {
				if(scrollable) {
					switch(k.getKeyCode()) {
					case KeyEvent.VK_RIGHT:
						mapMoveRight = true;
						break;
					case KeyEvent.VK_DOWN:
						mapMoveDown = true;
						break;
					case KeyEvent.VK_LEFT:
						mapMoveLeft = true;
						break;
					case KeyEvent.VK_UP:
						mapMoveUp = true;
						break;
					case KeyEvent.VK_PERIOD:
						zoomIn();
						break;
					case KeyEvent.VK_COMMA:
						zoomOut();
						break;
					}
				}
			}
			public void keyReleased(KeyEvent k) {
				if(scrollable) {
					switch(k.getKeyCode()) {
					case KeyEvent.VK_RIGHT:
						mapMoveRight = false;
						break;
					case KeyEvent.VK_DOWN:
						mapMoveDown = false;
						break;
					case KeyEvent.VK_LEFT:
						mapMoveLeft = false;
						break;
					case KeyEvent.VK_UP:
						mapMoveUp = false;
						break;
					}
				}
			}

			public void keyTyped(KeyEvent k) {}
		};
		
		try {
			map = ImageIO.read(new File("res/maps/"+mapName+".png"));
		} catch(Exception e) { e.printStackTrace(); }
		
		imgScale = MAP_DISPLAY_SIZE / map.getWidth();
		
		
		scrollable = true;
		ff = new FloodFiller(map);
		
		if((new File("res/maps/"+mapName+".txt").exists())) {
			loadMapFromTxt();
		}
		else {
			saveMapAsTxt();
		}
		
		System.out.println("The map has " + areas.size() + " areas!");
		
		for(Area a : areas) {
			a.setMap(map);
//			if(a.getSize() < 1200) System.out.println(a.getId() + " " + a.getSize());
		}
		
		imgScale = (double)MAP_DISPLAY_SIZE/(double)map.getWidth();
	}
	
	private void loadMapFromTxt() {
		ff.analyse(false);
		areas = ff.getAreas();
		pixelArea = ff.getPixelAreas();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(new File("res/maps/"+mapName+".txt")));
			String line;
			int areaId = 0;
			while((line = reader.readLine()) != null) {
				String[] attributes = line.split(",");
				areas.get(areaId).setName(attributes[1]);
				areas.get(areaId).setCenter(new Point(Integer.parseInt(attributes[2]), Integer.parseInt(attributes[3])));
				for(int i = 4; i < attributes.length; i++) {
					areas.get(areaId).addNeighbour(areas.get(Integer.parseInt(attributes[i])));
				}
				areaId++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	private void saveMapAsTxt() {
		
		ff.analyse(true);
		areas = ff.getAreas();
		pixelArea = ff.getPixelAreas();
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File("res/maps/"+mapName+".txt")));
			StringBuilder sb;
			for(Area a : areas) {
				sb = new StringBuilder();
				sb.append(a.getId() + ",NameNotYetSet,");
				sb.append(a.getAreaPoints().iterator().next().x + "," + a.getAreaPoints().iterator().next().y + ",");
				a.setCenter(new Point(a.getAreaPoints().iterator().next().x, a.getAreaPoints().iterator().next().y));
				a.setName("NameNotYetSet");
				for(Area n : a.getNeighbours()) {
					sb.append(n.getId() + ",");
				}
				writer.write(sb.toString());
				writer.newLine();
			}
			
		} 
		catch(Exception e) { 
			e.printStackTrace(); 
		}
		finally {
			try {
				writer.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
	}

	public void update() {
		for(int i = 0; i < SCROLL_SPEED; i++) {
			if(mapMoveRight && xOffset < map.getWidth()*imgScale-MAP_DISPLAY_SIZE) xOffset++;
			if(mapMoveLeft && xOffset > 0) xOffset--;
			if(mapMoveDown && yOffset < map.getHeight()*imgScale-MAP_DISPLAY_SIZE) yOffset++;
			if(mapMoveUp && yOffset > 0) yOffset--;
		}
		xCoord = (int) (GameStateManager.mouseX/imgScale+xOffset);
		yCoord = (int) (GameStateManager.mouseY/imgScale+yOffset);
		if(GameStateManager.mouseX < MAP_DISPLAY_SIZE && GameStateManager.mouseY < MAP_DISPLAY_SIZE) {
			mouseArea = pixelArea[xCoord][yCoord];
		}
		else {
			mouseArea = clickedArea;
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(textFont);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		if(imgScale >= 1) g.drawImage(map.getSubimage(xOffset, yOffset, MAP_DISPLAY_SIZE, MAP_DISPLAY_SIZE), 0, 0, MAP_DISPLAY_SIZE, MAP_DISPLAY_SIZE, null);
		else g.drawImage(map, 0, 0, MAP_DISPLAY_SIZE, MAP_DISPLAY_SIZE, null);
		for(Area a : areas) {
			if(new Rectangle(xOffset, yOffset, (int) (MAP_DISPLAY_SIZE/imgScale), (int) (MAP_DISPLAY_SIZE/imgScale)).contains(a.getCenter())) {
				g.setColor(a.getTextColor());
				int strLen = g.getFontMetrics(g.getFont()).stringWidth(""+a.getArmy());
				g.drawString("" + a.getArmy(),(int) ((a.getCenter().x-xOffset)*imgScale-strLen/2), (int) ((a.getCenter().y-yOffset)*imgScale+g.getFont().getSize()/2));
			}
		}
		g.setColor(Color.WHITE);
		if(GameStateManager.mouseX <= MAP_DISPLAY_SIZE && GameStateManager.mouseY <= MAP_DISPLAY_SIZE) {
			g.drawString("Map Coordinates: " + (xCoord) + "|" + (yCoord), 10, 820);
		}
		if(mouseArea >= 0) {
			g.drawString("Current Area: " + areas.get(mouseArea).getName() + " (" + mouseArea + ")", 10, 840);
			StringBuilder sb2 = new StringBuilder("Neighbours: ");
			for(int i = 0; i < areas.get(mouseArea).getNeighbours().size(); i++) {
				sb2.append(areas.get(mouseArea).getNeighbours().get(i).getName());
				if(i < areas.get(mouseArea).getNeighbours().size()-1) sb2.append(", ");
			}
			g.drawString(sb2.toString(), 10, 860);
		}
		
	}
	
	private void zoomOut() {
		if(map.getWidth()*imgScale > MAP_DISPLAY_SIZE) {
			imgScale /= 2;
			xOffset = 0;
			yOffset = 0;
		}
	}
	
	private void zoomIn() {
		if(imgScale < 1) {
			imgScale *= 2;
			System.out.println("zoomin");
		}
	}
	
	/**
	 * Returns if the mouse is currently hovering over any area.
	 */
	public boolean mouseOnArea() {
		return (GameStateManager.mouseX < MAP_DISPLAY_SIZE && GameStateManager.mouseY < MAP_DISPLAY_SIZE && mouseArea >= 0);
	}
	
	/**
	 * Returns if the area the mouse is currently hovering over belong to Player p
	 */
	public boolean mouseOnAreaOf(Player p) {
		return mouseOnArea() && areas.get(mouseArea).getPlayer() == p;
	}
	
	/**
	 * Returns true, if the area does NOT belong to Player p.
	 */
	public boolean mouseOnAreaNotOf(Player p) {
		return mouseOnArea() && areas.get(mouseArea).getPlayer() != p;
	}
	
	/**
	 * Returns if the area where the mouse is currently over is adjacent to the area with areaId.
	 */
	public boolean mouseOnAreaAdjacentTo(int areaId) {
		return mouseOnArea() && areas.get(mouseArea).getNeighbours().contains(areas.get(areaId));
	}
	
	public void keyPressed(KeyEvent k) {
		mapMovementKL.keyPressed(k);
	}
	public void keyReleased(KeyEvent k) {
		mapMovementKL.keyReleased(k);
	}
	
	public void mouseDragged(MouseEvent m) {}
	public void mouseMoved(MouseEvent m) {}
	
	public void mousePressed(MouseEvent m) {}
	public void mouseReleased(MouseEvent m) {}
	public void mouseClicked(MouseEvent m) {}
	
	public BufferedImage getMap() { return map; }
	public int getMouseArea() { return mouseArea; }

	
}
