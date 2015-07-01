package dk.itu.ejuuragr.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.TMaze;
import dk.itu.ejuuragr.domain.TMaze.MAP_TYPE;
import dk.itu.ejuuragr.domain.TMaze.MazeMap;

public class TMazeVisualizer {

	final TMaze maze;
	final MazeMap map;
	final TMazeVisualizerComponent component;
	
	
	private final int blockSize = 150;
	private final double rewardSize = 0.5;
	private final double agentSize = 0.05;
	private final Color highRewardColor = Color.red;
	private final Color lowRewardColor = Color.gray;
	
	public TMazeVisualizer(TMaze maze){
		this.maze = maze;
		this.map = maze.getMap();
		
		
		JFrame frame = new JFrame();
		frame.setSize(blockSize*(map.getWidth()+1), blockSize*(map.getHeight()+1));
		
		BorderLayout layout = new BorderLayout();
		frame.setLayout(layout);
		
		component = new TMazeVisualizerComponent();
		component.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				System.out.println(e.getX() + " , " + e.getY());
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		frame.add(component, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void update(){
		component.repaint();
	}
	
	private class TMazeVisualizerComponent extends JComponent{
		
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics arg0) {
			Graphics2D g = (Graphics2D)arg0;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			//Clear map
			g.setColor(new Color(MAP_TYPE.empty.value));
			g.fillRect(0, 0, 5000, 5000);
		
			drawMap(g);
			drawAgent(g);
			
		}
	}
	
	private void drawAgent(Graphics2D g){
		
		//Agent
		double[] position = maze.getPosition();
		g.setColor(new Color(120,120,255));
		drawCircle(g, position[0] * blockSize - agentSize * blockSize, position[1] * blockSize + agentSize * blockSize, agentSize);
		
		//Direction
		double angle = maze.getAngle();
		
		g.setColor(Color.black);
		g.drawLine((int)(position[0] * blockSize), ((map.getHeight())*blockSize) - (int)(position[1] * blockSize), 0, 0);
	}
	
	private void drawMap(Graphics2D g){
		int[] goal = maze.getHighRewardGoal();
		
		for (int x = 0; x < map.getWidth(); x++){
			for (int y = 0; y < map.getHeight(); y++){
				MAP_TYPE type = map.getType(x, y);
				
				switch (type) {
					case start:
						break;
					case goal:
						if (x == goal[0] && y == goal[1])	g.setColor(highRewardColor);
						else								g.setColor(lowRewardColor);
						drawCircle(g, x, y, rewardSize);
						break;
					case empty:
					case wall:
					default:
						drawBlock(g, new Color(type.value), x ,y);
					break;
				}
			}
		}
	}
	
	private int fixY(int y){
		return (map.getHeight()-1) * blockSize - y;
	}
	
	private void drawBlock(Graphics2D g, Color color, int x, int y){
		g.setColor(color);
		g.fillRect(x * blockSize, fixY(y * blockSize), blockSize, blockSize);
	}
	
	private void drawCircle(Graphics2D g, double x, double y, double size){
		//double ny = ((map.getHeight())*blockSize) - y;
		int ny = fixY((int)y);
		g.fillOval((int)x, (int)ny, (int)(size*blockSize)*2, (int)(size*blockSize)*2);
	}
	
	private void drawCircle(Graphics2D g, int x, int y, double size){
		double nx = (x + (0.5 - size)) * blockSize;
		double ny = (y + (0.5 - size)) * blockSize;
		drawCircle(g, nx, ny, size);
	}
	
	//Quick and dirty test main
	public static void main(String[] args) throws Exception {
		Properties props = new Properties("turingmachine.properties");
		TMaze maze = new TMaze(props);
		maze.reset();
		TMazeVisualizer viz = new TMazeVisualizer(maze);		
	}
	
}
