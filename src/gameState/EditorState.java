 package gameState;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JTextField;

import map.Area;
import map.FloodFiller;
import view.Game;

@SuppressWarnings("serial")
public class EditorState extends MapState {
	
	private JTextField nameField;
	private JButton nameButton;
	private JButton centerButton;
	private JButton addNeighbourButton;
	
	private int mouseMode;
	private static final int MOUSE_NORMAL_MODE = 0;
	private static final int MOUSE_CENTER_SELECTION_MODE = 1;
	private static final int MOUSE_ADD_NEIGHBOUR_MODE = 2;
	
	public EditorState(GameStateManager gsm){
		super(gsm);
		
		nameField = new JTextField("");
		nameField.setBounds(10,890,200,20);
		nameField.addKeyListener(mapMovementKL);
		nameButton = new JButton("Set Name");
		nameButton.setBounds(210,890,100,20);
		centerButton = new JButton("Set Center");
		centerButton.setBounds(10,910,100,20);
		addNeighbourButton = new JButton("Add Neighbour");
		addNeighbourButton.setBounds(10, 930, 100, 20);
		
		Random rnd = new Random();
		for(Area a : areas) {
			Color c = new Color(FloodFiller.randomColor());
			a.setAreaColor(c);
			a.setFrameColor(Area.getContrastColor(c));
			a.setTextColor(Area.getContrastColor(c));
			a.setArmy(rnd.nextInt(1000));
		}
	}
	
	public void draw(Graphics2D g) {
		super.draw(g);
		if(mouseArea >= 0) {
			g.drawString("Size: " + areas.get(mouseArea).getSize() + "        Border Size: " + areas.get(mouseArea).getBorderSize() + "        Frame Size: " + areas.get(mouseArea).getFrameSize(), 10, 880);
		}
		paintComponents(g);
	}
	
	public void mouseMoved(MouseEvent m) {
		if((m.getX() > MAP_DISPLAY_SIZE || m.getY() > MAP_DISPLAY_SIZE) && clickedArea >= 0) mouseArea = clickedArea;
	}
	
	public void mouseClicked(MouseEvent m) {
		if(mouseMode == MOUSE_NORMAL_MODE) {
			//ON AN AREA
			if(m.getX() < MAP_DISPLAY_SIZE && m.getY() < MAP_DISPLAY_SIZE && mouseArea >= 0) {
				if(clickedArea >= 0) areas.get(clickedArea).setActive(false);
				clickedArea = mouseArea;
				showAreaOptions(true);
				areas.get(clickedArea).setActive(true);
				nameField.setText(areas.get(mouseArea).getName());
				nameField.requestFocus();
			}
			//ON SET NAME BUTTON
			else if(clickedArea >= 0 && new Rectangle(nameButton.getX(), nameButton.getY(), nameButton.getWidth(), nameButton.getHeight()).contains(m.getX(), m.getY())) {
				areas.get(clickedArea).setName(nameField.getText());
				replaceAreaNameInTxt(clickedArea, nameField.getText());
				requestFocus();
			}
			//ON SET CENTER BUTTON
			else if(clickedArea >= 0 && new Rectangle(centerButton.getX(), centerButton.getY(), centerButton.getWidth(), centerButton.getHeight()).contains(m.getX(), m.getY())) {
				mouseMode = MOUSE_CENTER_SELECTION_MODE;
				requestFocus();
				Game.mainPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			//ON ADD NEIGHBOUR BUTTON
			else if(clickedArea >= 0 && new Rectangle(addNeighbourButton.getX(), addNeighbourButton.getY(), addNeighbourButton.getWidth(), addNeighbourButton.getHeight()).contains(m.getX(), m.getY())) {
				mouseMode = MOUSE_ADD_NEIGHBOUR_MODE;
				requestFocus();
				Game.mainPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			//NOT ON SCREEN OR ON VOID PIXEL
			else {
				if(clickedArea >= 0) areas.get(clickedArea).setActive(false);
				clickedArea = FloodFiller.VOID_PIXEL;
				nameField.setText("");
				showAreaOptions(false);
				Game.mainPanel.requestFocus();
			}
		}
		else if(mouseMode == MOUSE_CENTER_SELECTION_MODE) {
			Point p = ff.getPointAt(xCoord, yCoord);
			if(areas.get(clickedArea).getAreaPoints().contains(p)) {
				replaceAreaCenterInTxt(clickedArea, p);
				areas.get(clickedArea).setCenter(p);
			}
			mouseMode = MOUSE_NORMAL_MODE;
			Game.mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		else if(mouseMode == MOUSE_ADD_NEIGHBOUR_MODE) {
			if(mouseArea >= 0 && mouseArea != clickedArea && !areas.get(clickedArea).getNeighbours().contains(areas.get(clickedArea))) {
				addNeighbourInTxt(clickedArea, mouseArea);
				areas.get(clickedArea).addNeighbour(areas.get(mouseArea));
				areas.get(mouseArea).addNeighbour(areas.get(clickedArea));
			}
			mouseMode = MOUSE_NORMAL_MODE;
			Game.mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	/**
	 * Define if the editing options of an area should be visible. 
	 * Stuff like changing name, center, neigbours, etc..
	 */
	private void showAreaOptions(boolean show) {
		if(show) {
			add(nameField);
			add(nameButton);
			add(centerButton);
			add(addNeighbourButton);
		}
		else {
			remove(nameField);
			remove(nameButton);
			remove(centerButton);
			remove(addNeighbourButton);
		}
	}
	
	/**
	 * Changes name from the area with id areaId to newName in mapName.txt
	 */
	private void replaceAreaNameInTxt(int areaId, String newName) {
		
		String[] lines = new String[areas.size()];
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			reader = new BufferedReader(new FileReader(new File("res/maps/"+mapName+".txt")));
			String line;
			int i = 0;
			while((line = reader.readLine()) != null) {
				lines[i] = line;
				i++;
			}
			
			String[] splittedLine = lines[areaId].split(" ");
			splittedLine[1] = newName;
			StringBuilder sb = new StringBuilder();
			for(String s : splittedLine) sb.append(s + " ");
			lines[areaId] = sb.toString();
			
			writer = new BufferedWriter(new FileWriter(new File("res/maps/"+mapName+".txt")));
			for(String s : lines) {
				writer.write(s);
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
				writer.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * Changes the center point from the area with id areaId to newName in mapName.txt
	 */
	private void replaceAreaCenterInTxt(int areaId, Point newCenter) {
		
		String[] lines = new String[areas.size()];
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			reader = new BufferedReader(new FileReader(new File("res/maps/"+mapName+".txt")));
			String line;
			int i = 0;
			while((line = reader.readLine()) != null) {
				lines[i] = line;
				i++;
			}
			
			String[] splittedLine = lines[areaId].split(" ");
			splittedLine[2] = Integer.toString(newCenter.x);
			splittedLine[3] = Integer.toString(newCenter.y);
			StringBuilder sb = new StringBuilder();
			for(String s : splittedLine) sb.append(s + " ");
			lines[areaId] = sb.toString();
			
			writer = new BufferedWriter(new FileWriter(new File("res/maps/"+mapName+".txt")));
			for(String s : lines) {
				writer.write(s);
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
				writer.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * Adds area with id neighbourId as a new neighbour to the area with id areaId and vice versa in mapName.txt
	 */
	private void addNeighbourInTxt(int areaId, int neighbourId) {
		
		String[] lines = new String[areas.size()];
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			reader = new BufferedReader(new FileReader(new File("res/maps/"+mapName+".txt")));
			String line;
			int i = 0;
			while((line = reader.readLine()) != null) {
				lines[i] = line;
				i++;
			}
			
			lines[areaId] = lines[areaId] + neighbourId + " ";
			lines[neighbourId] = lines[neighbourId] + areaId + " ";
			
			writer = new BufferedWriter(new FileWriter(new File("res/maps/"+mapName+".txt")));
			for(String s : lines) {
				writer.write(s);
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
				writer.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
	}


}
