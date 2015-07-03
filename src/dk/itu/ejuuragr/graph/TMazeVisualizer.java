package dk.itu.ejuuragr.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.TMaze;
import dk.itu.ejuuragr.domain.TMaze.MAP_TYPE;
import dk.itu.ejuuragr.domain.TMaze.MazeMap;

public class TMazeVisualizer {

	final TMaze maze;
	final MazeMap map;
	final TMazeVisualizerComponent component;
	
	private final int blockSize = 250;
	private final double rewardSize = 0.5;
	private final double agentSize = 0.05;
	private final Color highRewardColor = Color.red;
	private final Color lowRewardColor = Color.gray;
	private boolean doProgress = false;
	
	public TMazeVisualizer(final TMaze maze){
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
				if (e.getKeyCode() == KeyEvent.VK_UP)
					maze.performAction(new double[]{.5});
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					maze.performAction(new double[]{1});
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					maze.performAction(new double[]{0});
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					doProgress = true;
				component.repaint();
				System.out.println(maze.getCurrentScore());
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
			
		}
	}
	
	private void drawAgent(Graphics2D g){
		
		//Agent
		double[] position = maze.getPosition();
		g.setColor(new Color(120,120,255));
		drawCircle(g, position[0] * blockSize - agentSize * blockSize, position[1] * blockSize - agentSize * blockSize, agentSize);
		
		//Direction
		double[] endPoint = new double[]{position[0] + Math.cos(maze.getAngle()) * 0.2, position[1] + Math.sin(maze.getAngle()) * 0.2};
		
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(2));
		//g.draw(new Line2D.Double(position[0] * blockSize, position[1]*blockSize, endPoint[0]*blockSize, endPoint[1]*blockSize));
		
		//Sensors

		g.setColor(Color.gray);
		g.setStroke(new BasicStroke(1));
		
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
	
	//Quick and dirty test main
	public static void main(String[] args) throws Exception {
		String chromosomeId, propertiesFile;
		if (args.length == 0){
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Properties filename: ");
			propertiesFile = br.readLine();
			System.out.println("Chromosome ID: ");
			chromosomeId = br.readLine();
		}
		else
		{
			propertiesFile = args[0];
			chromosomeId = args[1];
		}
		
		try{
			//Setup
			Properties props = new Properties(propertiesFile); // "turingmachine.properties"
			props.setProperty("base.dir", "./db");
			
			TMaze maze = new TMaze(props);
			maze.reset();
			TMazeVisualizer viz = new TMazeVisualizer(maze);

		}
		catch (Exception e){
			System.out.println("!!! Warning!");
			System.out.println("Chromosome load failed!");
		}
	}
	
}
