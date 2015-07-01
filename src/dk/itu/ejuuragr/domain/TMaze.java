package dk.itu.ejuuragr.domain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static dk.itu.ejuuragr.domain.TMaze.MAP_TYPE.*;

import javax.imageio.ImageIO;

import com.anji.util.Properties;

public class TMaze implements Simulator {
	
	public static final double SPEED = 0.1; // How many tiles you can move in one step
	public static final int SENSOR_CUTOFF = 3; // The maximum distance of the sensors (wherefrom it will have a value of 1.0)
	public static final double[] SENSOR_ANGLES = new double[]{-Math.PI / 4.0, 0, Math.PI / 4.0};
	public static final double STEER_AMOUNT = Math.PI / 8; // Max 45 degrees to either side
 
	// Random things
	private Random rand;
	
	// The map
	private MazeMap map;
	private int[] startPos;
	private int highReward, lowReward;
	
	// Distance helpers
	private List<double[]> walls; // int[x1,y1,x2,y2]
	
	// Live fields
	private double[] location;
	private double angle;
	private int[] goal;
	
	private int stepCounter;
	private int finished = -1;
	
	public TMaze(Properties props) {
		rand = new Random(props.getIntProperty("random.seed"));
		highReward = props.getIntProperty("simulator.tmaze.reward.high");
		lowReward = props.getIntProperty("simulator.tmaze.reward.low");
		
		String mapFile = props.getProperty("simulator.tmaze.map");
		loadMap(mapFile);
		loadWalls();
	}

	@Override
	public int getInputCount() {
		return 1; // The angle of stearing. 0.0 = Right, 1.0 Left
	}

	@Override
	public int getOutputCount() {
		return 3; // Distance sensor at 45, 90 and 135 degrees
	}

	@Override
	public void reset() {
		location = new double[]{startPos[0] + 0.5, startPos[1] + 0.5};
		angle = Math.PI / 2.0;
		this.goal = null;
		stepCounter = 0;
		finished = -1;
		moveGoal();
	}

	@Override
	public double[] getInitialObservation() {
		return getObservation();
	}

	@Override
	public double[] performAction(double[] action) {
		steer(action[0]);
		moveAgent();
		
		if(isWithinGoal())
			finished = stepCounter;
		stepCounter++; // next round
		
		return getObservation();
	}

	@Override
	public int getCurrentScore() {
		return getReward();
	}

	@Override
	public int getMaxScore() {
		return highReward;
	}

	@Override
	public boolean isTerminated() {
		return isInWall() || finishedLastStep();
	}

	// SPECIFIC PUBLIC METHODS
	
	public MazeMap getMap() {
		return map;
	}
	
	public double[] getPosition() {
		return new double[]{location[0],location[1]};
	}
	
	public double getAngle() {
		return angle;
	}
	
	public int[] getHighRewardGoal() {
		return new int[]{goal[0],goal[1]};
	}
	
	// HELPER METHODS
	
	private void loadWalls() {
		walls = new ArrayList<double[]>();
		
		for(int x = -1; x < map.getWidth(); x++) {
			for(int y = -1; y < map.getHeight(); y++) {
				// Check upper and right side
				MAP_TYPE cur = map.getType(x, y);
				if(cur == wall) { // Only Check walls
					MAP_TYPE upper = map.getType(x,y+1);
					MAP_TYPE right = map.getType(x+1,y);
					if(upper != wall) walls.add(new double[]{x,y+1,x+1,y+1});
					if(right != wall) walls.add(new double[]{x+1,y,x+1,y+1});
				}
			}
		}
	}
	
	private void steer(double dir) {
		dir = dir * 2 - 1;
		angle += dir * STEER_AMOUNT;
	}
	
	private void moveAgent() {
		double dx = Math.cos(angle) * SPEED;
		double dy = Math.sin(angle) * SPEED;
		location[0] += dx;
		location[1] += dy;
	}
	
	private double[] getObservation() {
		double[] result = new double[SENSOR_ANGLES.length];
		
		for(int i = 0; i < result.length; i++) {
			double sensorAngle = angle + SENSOR_ANGLES[i];
			
			// calculate intersection to each 
			List<double[]> wallIntersects = intersections(new double[]{location[0] ,location[1]
							,location[0] + Math.cos(sensorAngle) ,location[1] + Math.sin(sensorAngle)}
					,walls);
			double closest = lowestDistance(location,wallIntersects);
			
			result[i] = Math.min(SENSOR_CUTOFF, closest) / SENSOR_CUTOFF;
		}
		
		return result;
	}
	
	private double lowestDistance(double[] point,
			List<double[]> points) {
		double lowest = Double.MAX_VALUE;
		for(double[] otherPoint : points) {
			double dist = Math.pow(point[0] - otherPoint[0],2) + Math.pow(point[1] - otherPoint[1],2);
			if(dist < lowest)
				lowest = dist;
		}
		return Math.sqrt(lowest);
	}

	private List<double[]> intersections(double[] fromLine, List<double[]> toSegments) {
		List<double[]> result = new ArrayList<>();
		for(double[] seg : toSegments) {
			double[] intersect = lineIntersect(fromLine,seg);
			if(intersect != null && isBetween(intersect,seg))
				result.add(intersect);
		}
		return result;
	}

	private double[] lineIntersect(double[] first, double[] second) {
		double x1 = first[0];
		double y1 = first[1];
		double x2 = first[2];
		double y2 = first[3];
		double x3 = second[0];
		double y3 = second[1];
		double x4 = second[2];
		double y4 = second[3];
		double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		if (denom == 0.0) { // Lines are parallel.
			return null;
		}
		double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
		double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
		if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
			// Get the intersection point.
			return new double[]{x1 + ua * (x2 - x1), y1 + ua * (y2 - y1)};
		}

		return null;
	}
	
	private boolean isBetween(double[] point, double[] segment) {
		return point[0] >= Math.min(segment[0],segment[2])
				&& point[0] <= Math.max(segment[0], segment[2])
				&& point[1] >= Math.min(segment[1],segment[3])
				&& point[1] <= Math.max(segment[1], segment[3]);
	}

	private void loadMap(String mapFile) {
		try {
			BufferedImage b = ImageIO.read(new File(mapFile));
			map = new MazeMap(b.getWidth(),b.getHeight());
			
			// load map
			for(int x = 0; x < b.getWidth(); x++) {
				for(int y = 0; y < b.getHeight(); y++) {
					MAP_TYPE type = MAP_TYPE.valueOf(b.getRGB(x, y));
					map.setType(x, b.getHeight()-1-y, type);
					
					if(type == MAP_TYPE.start)
						this.startPos = new int[]{x,y};
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e); // There is no recovery from this
		}
	}
	
	private boolean isWithinGoal() {
		return map.getType(location[0], location[1]) == MAP_TYPE.goal; // should maybe be stricter
	}
	
	private boolean finishedLastStep() {
		return stepCounter == finished + 1;
	}

	private boolean isInWall() {
		return map.getType(location[0], location[1]) == MAP_TYPE.wall;
	}
	
	private int getReward() {
		if(map.getType(location[0], location[1]) == MAP_TYPE.goal) {
			return Arrays.equals(getTile(location), goal) ? highReward : lowReward;
		}
		return 0;
	}
	
	private void moveGoal() {
		List<int[]> goals = map.getOfType(MAP_TYPE.goal);

		int roll = rand.nextInt(goals.size()-1);
		goal = Arrays.equals(goals.get(roll), goal) ? goals.get(goals.size()-1) : goals.get(roll);
	}
	
	private int[] getTile(double[] location) {
		return new int[]{(int)location[0],(int)location[1]};
	}
	
	// HELPER CLASSES & ENUMS
	
	enum MAP_TYPE { 
		empty(new Color(255,255,255).getRGB()), 
		wall(new Color(0,0,0).getRGB()), 
		start(new Color(0,0,255).getRGB()), 
		goal(new Color(255,0,0).getRGB());
	
		public int value;
	
		private MAP_TYPE(int rgb) {
			this.value = rgb;
		}
		
		public static MAP_TYPE valueOf(int value) {
			for(MAP_TYPE mt : values()) {
				if(mt.value == value)
					return mt;
			}
			return wall;
		}
	}
	
	public class MazeMap {
		
		private MAP_TYPE[][] map;

		private MazeMap(int width, int height) {
			this.map = new MAP_TYPE[width][];
			for(int i = 0; i < width; i++) {
				map[i] = new MAP_TYPE[height];
			}
		}
		
		public int getHeight() {
			return map[0].length;
		}

		public int getWidth() {
			return map.length;
		}

		private void setType(int x, int y, MAP_TYPE type) {
			map[x][y] = type;
		}
		
		public MAP_TYPE getType(int x, int y) {
			if(x < getWidth() && y < getHeight() && x > -1 && y > -1)
				return map[x][y];
			
			return MAP_TYPE.wall; // everything outside is wall
		}
		
		public MAP_TYPE getType(double x, double y) {
			return getType((int)x,(int)y); // If they are only positive
		}
		
		public List<int[]> getOfType(MAP_TYPE type) {
			ArrayList<int[]> result = new ArrayList<>();
			for(int x = 0; x < map.length; x++) {
				for(int y = 0; y < map.length; y++) {
					if(map[x][y] == type)
						result.add(new int[]{x,y});
				}
			}
			return result;
		}
	}

}
