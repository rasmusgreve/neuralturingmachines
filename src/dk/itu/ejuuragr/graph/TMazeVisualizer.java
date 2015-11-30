package dk.itu.ejuuragr.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import dk.itu.ejuuragr.domain.tmaze.TMaze;
import dk.itu.ejuuragr.domain.tmaze.TMaze.MAP_TYPE;
import dk.itu.ejuuragr.domain.tmaze.TMaze.MazeMap;

/**
 * Visualizes the replay of a game of
 * T-Maze with the settings (map etc) from
 * the used properties file.
 * 
 * @author Rasmus
 *
 */
public class TMazeVisualizer {

	final TMaze maze;
	final MazeMap map;
	final TMazeVisualizerComponent component;
	
	private final int blockSize = 150;
	private final double rewardSize = 0.5;
	private final double agentSize = 0.05;
	private final Color highRewardColor = new Color(220,0,0);
	private final Color lowRewardColor = new Color(210,72,88);
	private volatile boolean doProgress = false;
	
	public TMazeVisualizer(final TMaze maze, final boolean userControllable){
		this.maze = maze;
		this.map = maze.getMap();
		
		JFrame frame = new JFrame();
		frame.setSize(blockSize*(map.getWidth()+1), blockSize*(map.getHeight()+1));
		
		BorderLayout layout = new BorderLayout();
		frame.setLayout(layout);
		
		component = new TMazeVisualizerComponent();
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (userControllable){
					if (e.getKeyCode() == KeyEvent.VK_UP)
						maze.performAction(new double[]{.5});
					if (e.getKeyCode() == KeyEvent.VK_LEFT)
						maze.performAction(new double[]{1-Math.random() * 0.1});
					if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						maze.performAction(new double[]{0+Math.random() * 0.1});
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					doProgress = true;
				}
				component.repaint();
			}
		});
		frame.add(component, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public boolean getDoProgressAndReset(){
		if (doProgress){
			doProgress = false;
			return true;
		}
		return false;
	}
	
	public void update(){
		component.repaint();
	}
	
	private class TMazeVisualizerComponent extends JComponent{
		
		private static final long serialVersionUID = 1L;
		final Font rewardFont = new Font("Verdana", Font.PLAIN, 20);

		@Override
		public void paint(Graphics arg0) {
			Graphics2D g = (Graphics2D)arg0;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
			transform.translate(0, -map.getHeight()*blockSize);
			g.setTransform(transform);
			
			//Clear map
			g.setColor(new Color(MAP_TYPE.empty.value));
			g.fillRect(0, 0, 5000, 5000);
		
			drawMap(g);
			drawAgent(g);
			
			g.setColor(Color.green);
			g.setTransform(new AffineTransform());
			g.setFont(rewardFont);
			
		}
	}
	
	private void drawAgent(Graphics2D g){

		double[] position = maze.getPosition();
		
		//Direction
		double[] endPoint = new double[]{position[0] + Math.cos(maze.getAngle()) * 0.2, position[1] + Math.sin(maze.getAngle()) * 0.2};
		
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(2));
		//g.draw(new Line2D.Double(position[0] * blockSize, position[1]*blockSize, endPoint[0]*blockSize, endPoint[1]*blockSize));
		
		//Sensors

		g.setColor(Color.gray);
		g.setStroke(new BasicStroke(2f));
		
		double[] distances = maze.getCurrentObservation();
		
		for (int i = 0; i < maze.SENSOR_ANGLES.length; i++){
			double sensorAngle = maze.SENSOR_ANGLES[i] + maze.getAngle();
			double sensorLength = distances[i] * maze.SENSOR_CUTOFF;
			double[] sensorEnd = new double[]{position[0] + Math.cos(sensorAngle) * sensorLength, position[1] + Math.sin(sensorAngle) * sensorLength};
			
			g.setColor(Color.gray);
			g.draw(new Line2D.Double(position[0] * blockSize, position[1]*blockSize, sensorEnd[0]*blockSize, sensorEnd[1]*blockSize));
			
			g.setColor(Color.yellow);
			drawCircleCenteredAt(g, sensorEnd[0], sensorEnd[1], 0.02);
		}

		//Agent
		g.setColor(new Color(120,120,255));
		drawCircle(g, position[0] * blockSize - agentSize * blockSize, position[1] * blockSize - agentSize * blockSize, agentSize);
				
		
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
						drawGoal(g, x, y, rewardSize, (x == goal[0] && y == goal[1]));
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
	
	private void drawBlock(Graphics2D g, Color color, int x, int y){
		g.setColor(color);
		g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
	}
	
	private void drawCircleCenteredAt(Graphics2D g, double x, double y, double size)
	{
		x = x * blockSize - size * blockSize;
		y = y * blockSize - size * blockSize;
		g.fillOval((int)x, (int)y, (int)(size*blockSize*2), (int)(size*blockSize*2));
	}
	
	private void drawCircle(Graphics2D g, double x, double y, double size){
		g.fillOval((int)x, (int)y, (int)(size*blockSize*2), (int)(size*blockSize*2));
	}
	
	private void drawCircle(Graphics2D g, int x, int y, double size){
		double nx = (x + (0.5 - size)) * blockSize;
		double ny = (y + (0.5 - size)) * blockSize;
		drawCircle(g, nx, ny, size);
	}
	
	private void drawGoal(Graphics2D g, int x, int y, double size, boolean high){
		if (high){
			g.setColor(highRewardColor);
			drawCircle(g, x, y, size-0.05);
			g.setColor(Color.white);
			Font f = new Font("Verdana", Font.PLAIN, 102);
			Font f2 = f.deriveFont(AffineTransform.getScaleInstance(g.getTransform().getScaleX(), g.getTransform().getScaleY()));
			g.setFont(f2);
			g.drawString("R",x * blockSize + (int)(size*blockSize / 2),y * blockSize + (int)(size * blockSize/2));
		}
		else
		{
			g.setColor(lowRewardColor);
			drawCircle(g, x, y, size-0.25);
			g.setColor(Color.white);
			Font f = new Font("Verdana", Font.PLAIN, 50);
			Font f2 = f.deriveFont(AffineTransform.getScaleInstance(g.getTransform().getScaleX(), g.getTransform().getScaleY()));
			g.setFont(f2);
			g.drawString("R",x * blockSize + (int)(size*blockSize / 1.30),y * blockSize + (int)(size * blockSize/1.35));
		}
	}
}
