package map;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class FloodFiller {
	
	private static final int FRAME_WIDTH = 3;
	
	public static final int BLACK = -16777216;
	public static final int WHITE = -1;
	public static final int VOID_PIXEL = -1;
	public static final int BORDER_PIXEL = -2;
	
	private BufferedImage img;
	
	boolean[][] visited;
	int[][] pixelAreaId;
	Point[][] points;
	
	private Queue<Point> currentAreaQueue;
	private ArrayList<Area> areas;
	private int areaId;
	
	private long loadStart, loadElapsed;
	
	public FloodFiller(BufferedImage img) {
		this.img = img;
		currentAreaQueue = new LinkedList<Point>();
		areas = new ArrayList<Area>();
		visited = new boolean[img.getWidth()][img.getHeight()];
		pixelAreaId = new int[img.getWidth()][img.getHeight()];
		points = new Point[img.getWidth()][img.getHeight()];
	}
	
	public void analyse(boolean full) {
		
		loadStart = System.currentTimeMillis();
		
		for(int y = 0; y < img.getHeight(); y++) {
			for(int x = 0; x < img.getWidth(); x++) {
				points[x][y] = new Point(x,y);
			}
		}
		
		for(int y = 0; y < img.getHeight(); y++) {
			for(int x = 0; x < img.getWidth(); x++) {
				if(visited[x][y]) {}
				else if(img.getRGB(x, y) == BLACK) {
					visited[x][y] = true;
					pixelAreaId[x][y] = BORDER_PIXEL;
//					img.setRGB(x, y, 0x444444);
				}
				else if(img.getRGB(x, y) == WHITE) {
					currentAreaQueue.add(points[x][y]);
					flood();
				}
				else {
					visited[x][y] = true;
					pixelAreaId[x][y] = VOID_PIXEL;
				}
			}
		}
		
		if(full) findNeighbours();

		loadElapsed = System.currentTimeMillis() - loadStart;
		System.out.println(loadElapsed);
	}

	/**
	 * This methonds takes all areas and compares their Borderpoints. 
	 * If multiple areas have the same Borderpoint, the will be added
	 * as neighbours.
	 */
	private void findNeighbours() {
		for(Area a1 : areas) {
			for(Point bp : a1.getBorderPoints()) {
				for(Area a2 : areas) {
					if(!a1.equals(a2)) {
						for(Point bp2 : a2.getBorderPoints()) {
							if(bp.x == bp2.x && bp.y == bp2.y && !a1.getNeighbours().contains(a2)) a1.addNeighbour(a2);
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method takes one point (the first in the currentAreaQueue)
	 * and floods a white area from there. It adds a new area and finds
	 * its areaPoints, borderPoints and framePoints.
	 */
	private void flood() {
		
		areas.add(new Area(areaId));
		Point currentPoint;
		
		while(!currentAreaQueue.isEmpty()) {
			
			currentPoint = currentAreaQueue.poll();
			
			if(!visited[currentPoint.x][currentPoint.y]) {
			
				visited[currentPoint.x][currentPoint.y] = true;
				
				pixelAreaId[currentPoint.x][currentPoint.y] = areaId;
				areas.get(areaId).addAreaPoint(currentPoint);
				
				if(currentPoint.x < img.getWidth()-1) {
					if(img.getRGB(currentPoint.x+1, currentPoint.y) == BLACK) {
						pixelAreaId[currentPoint.x+1][currentPoint.y] = BORDER_PIXEL;
						areas.get(areaId).addFramePoint(currentPoint);
						areas.get(areaId).addBorderPoint(points[currentPoint.x+1][currentPoint.y]);
					}
					else if(img.getRGB(currentPoint.x+1, currentPoint.y) == WHITE) {
						currentAreaQueue.add(points[currentPoint.x+1][currentPoint.y]);
					}
					else {
						areas.get(areaId).addFramePoint(currentPoint);
						pixelAreaId[currentPoint.x+1][currentPoint.y] = VOID_PIXEL;
					}
				}
				
				if(currentPoint.y < img.getHeight()-1) {
					if(img.getRGB(currentPoint.x, currentPoint.y+1) == BLACK) {
						pixelAreaId[currentPoint.x][currentPoint.y+1] = BORDER_PIXEL;
						areas.get(areaId).addFramePoint(currentPoint);
						areas.get(areaId).addBorderPoint(points[currentPoint.x][currentPoint.y+1]);
					}
					else if(img.getRGB(currentPoint.x, currentPoint.y+1) == WHITE) {
						currentAreaQueue.add(points[currentPoint.x][currentPoint.y+1]);
					}
					else {
						areas.get(areaId).addFramePoint(currentPoint);
						pixelAreaId[currentPoint.x][currentPoint.y+1] = VOID_PIXEL;
					}
				}
				
				if(currentPoint.x > 0) {
					if(img.getRGB(currentPoint.x-1, currentPoint.y) == BLACK) {
						pixelAreaId[currentPoint.x-1][currentPoint.y] = BORDER_PIXEL;
						areas.get(areaId).addFramePoint(currentPoint);
						areas.get(areaId).addBorderPoint(points[currentPoint.x-1][currentPoint.y]);
					}
					else if(img.getRGB(currentPoint.x-1, currentPoint.y) == WHITE) {
						currentAreaQueue.add(points[currentPoint.x-1][currentPoint.y]);
					}
					else {
						areas.get(areaId).addFramePoint(currentPoint);
						pixelAreaId[currentPoint.x-1][currentPoint.y] = VOID_PIXEL;
					}
				}
				
				if(currentPoint.y > 0) {
					if(img.getRGB(currentPoint.x, currentPoint.y-1) == BLACK) {
						pixelAreaId[currentPoint.x][currentPoint.y-1] = BORDER_PIXEL;
						areas.get(areaId).addFramePoint(currentPoint);
						areas.get(areaId).addBorderPoint(points[currentPoint.x][currentPoint.y-1]);
					}
					else if(img.getRGB(currentPoint.x, currentPoint.y-1) == WHITE) {
						currentAreaQueue.add(points[currentPoint.x][currentPoint.y-1]);
					}
					else {
						areas.get(areaId).addFramePoint(currentPoint);
						pixelAreaId[currentPoint.x][currentPoint.y-1] = VOID_PIXEL;
					}
				}
			}
		}
		
		//widthen frame
		for(int i = 1; i < FRAME_WIDTH; i++) {
			ArrayList<Point> toAddToFrame = new ArrayList<Point>();
			for(Point p : areas.get(areaId).getFramePoints()) {
				if(p.x < img.getWidth()-1 && areas.get(areaId).getAreaPoints().contains(points[p.x+1][p.y])) toAddToFrame.add(points[p.x+1][p.y]);
				if(p.y < img.getWidth()-1 && areas.get(areaId).getAreaPoints().contains(points[p.x][p.y+1])) toAddToFrame.add(points[p.x][p.y+1]);
				if(p.x > 0 && areas.get(areaId).getAreaPoints().contains(points[p.x-1][p.y])) toAddToFrame.add(points[p.x-1][p.y]);
				if(p.y > 0 && areas.get(areaId).getAreaPoints().contains(points[p.x][p.y-1])) toAddToFrame.add(points[p.x][p.y-1]);
			}
			areas.get(areaId).getFramePoints().addAll(toAddToFrame);
		}
		areaId++;
	}
	
	
	/**
	 * Returns a random color as int.
	 * Use 'new Color(FloodFiller.randomColor())' to make a new Color.
	 */
	public static int randomColor() {
		Random rng = new Random();
		int r,g,b;
		r = rng.nextInt(255);  g = rng.nextInt(255); b = rng.nextInt(255);
		r = (r << 16) & 0xFF0000;
	    g = (g << 8) & 0x00FF00;
	    b = b & 0x0000FF;

	    return 0x000000 | r | g | b;
	}
	
	public Point getPointAt(int x, int y) { return points[x][y]; }
	public ArrayList<Area> getAreas() { return areas; }
	public int[][] getPixelAreas() { return pixelAreaId; }
            

}
