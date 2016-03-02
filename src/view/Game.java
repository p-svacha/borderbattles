package view;

import javax.swing.JFrame;

public class Game {
	
	public static GamePanel mainPanel;
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame("Border Battles");
		mainPanel = new GamePanel();
		
		window.setContentPane(mainPanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);	
		window.pack();
		window.setVisible(true);
		

	}

}
