package map;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import player.Player;

public class Area {
	
	private int id;
	protected BufferedImage map;
	protected String name;
	protected Player player;
	protected Color areaColor;
	protected Color frameColor;
	protected Color textColor;
	private ArrayList<Area> neighbours;
	private HashSet<Point> areaPoints;
	private HashSet<Point> borderPoints;
	private HashSet<Point> framePoints;
	private Point center;
	
	private int army;

	public Area(int id) {
		this.id = id;	
		neighbours = new ArrayList<Area>();
		areaPoints = new HashSet<Point>();
		borderPoints = new HashSet<Point>();
		framePoints = new HashSet<Point>();
	}
	
	/**
	 * Adds a Point / Pixel to this area.
	 */
	public void addAreaPoint(Point p) {
		areaPoints.add(p);
	}
	
	/**
	 * Adds a Point / Pixel to the border of this area. Used for highlighting.
	 */
	public void addBorderPoint(Point p) {
		borderPoints.add(p);
	}
	
	public void addFramePoint(Point p) {
		framePoints.add(p);
	}
	
	public void addNeighbour(Area a) {
		neighbours.add(a);
//		System.out.println(id + ": Neighbour " + a.getId() + " added!");
	}
	public void fill(Color color) {
		for(Point p : areaPoints) {
			map.setRGB(p.x, p.y, color.getRGB());
		}
	}
	
	public void fillFrame(Color color) {
		for(Point p : framePoints) {
			map.setRGB(p.x, p.y, color.getRGB());
		}
	}
	
	/**
	 * Returns if this area has borders belonging to someone othe than the owner.
	 */
	public boolean hasEnemyBorders() {
		for(Area a : neighbours) {
			if(a.getPlayer() != player) return true;
		}
		return false;
	}
	
	public void setActive(boolean active) { 
		if(active) fillFrame(frameColor);
		else fillFrame(areaColor);
	}
	
	public void setPlayer(Player player) { 
		this.player = player; 
		setAreaColor(player.getColor());
		setFrameColor(player.getFrameColor());
		setTextColor(player.getFrameColor());
	}
	public void setAreaColor(Color areaColor) { 
		this.areaColor = areaColor; 
		fill(areaColor);
	}
	
	/**
	 * If set to true, the area gets highlighted by swapping area and frame color.
	 */
	public void highlight(boolean highlight) {
		if(highlight) {
			Color tmp = areaColor;
			areaColor = frameColor;
			frameColor = tmp;
			textColor = Color.WHITE;
			fill(Color.BLACK);
			fillFrame(frameColor);
		}
		else {
			Color tmp = areaColor;
			areaColor = frameColor;
			frameColor = tmp;
			textColor = frameColor;
			fill(areaColor);
			fillFrame(areaColor);
		}
	}
	
	/**
	 * Returns black or white depending on bigger contrast.
	 */
	public static Color getContrastColor(Color color) {
		  double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
		  return y >= 70 ? Color.black : Color.white;
		}
	
	public void setArmy(int army) { this.army = army; }
	public void setFrameColor(Color frameColor) { this.frameColor = frameColor; }
	public void setTextColor(Color textColor) { this.textColor = textColor; }
	public void setName(String name) { this.name = name; }
	public void setMap(BufferedImage map) { this.map = map; }
	public void setCenter(Point center) { this.center = center; }
	

	public String getName() { return name; }
	public Player getPlayer() { return player; }
	public int getArmy() { return army; }
	public Color getFrameColor() { return frameColor; }
	public Color getTextColor() { return textColor; }
	public ArrayList<Area> getNeighbours() { return neighbours; }
	public HashSet<Point> getAreaPoints() { return areaPoints; }
	public HashSet<Point> getBorderPoints() { return borderPoints; }
	public HashSet<Point> getFramePoints() { return framePoints; }
	public int getSize() { return areaPoints.size(); }
	public Point getCenter() { return center; }
	public int getBorderSize() { return borderPoints.size(); }
	public int getFrameSize () { return framePoints.size(); }
	public int getId() { return id; }
	
}
