package log;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class EventLog {
	
	private final int LOG_SIZE = 30;
	private ArrayList<String> logs;

	public EventLog() {
		logs = new ArrayList<String>();
	}
	
	public void addLog(String s) {
		logs.add(0, s);
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		int size = LOG_SIZE;
		if(logs.size() < size) size = logs.size();
		for(int i = 0; i < size; i++) {
			int fontSize = 15 - i;
			if(fontSize < 12) fontSize = 12;
			g.setFont(new Font("Arial", Font.PLAIN, fontSize));
			g.drawString(logs.get(i), 810, 800-20*i);
		}
	}
}
