package map;

import java.awt.Point;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class BorderPoint extends Point{
	
	private ArrayList<Integer> areas;

	public BorderPoint(int x, int y) {
		super(x,y);
		areas = new ArrayList<Integer>();
	}
	
	public void addArea(int a) {
		areas.add(a);
	}
	
}
