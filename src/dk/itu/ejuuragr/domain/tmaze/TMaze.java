package dk.itu.ejuuragr.domain.tmaze;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static dk.itu.ejuuragr.domain.tmaze.TMaze.MAP_TYPE.*;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.Pair;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.BaseSimulator;
import dk.itu.ejuuragr.fitness.Utilities;

/**
 * The classical T-Maze of Machine Learning for challenging
 * agents to use memory to consistently find the high-reward
 * location during multiple tries.
 * The maze is build from specifications in a BMP image, so
 * any imaginable maze is possible.
 * 
 * @author Emil
 *
 */
public class TMaze extends BaseSimulator {
	
	public static final boolean DEBUG = false; // If true the Simulator will print the state in each step
	
	// Simulation specifics
	public final double SPEED; // How many tiles you can move in one step
	public final double SENSOR_CUTOFF; // The maximum distance of the sensors (wherefrom it will have a value of 1.0)
	public final double[] SENSOR_ANGLES = new double[]{-Math.PI / 4.0, 0, Math.PI / 4.0}; // What sensors and their angles to return
	public final double STEER_AMOUNT; // Max 45 degrees to either side
	public String START_DIRECTION; // The initial orientation of the agent (N, S, E or W)
	public final double START_DIR_OFFSET; // The actual initial orientation will be randomly offset by this number of degrees
	public final double START_POS_OFFSET; // The actual initial position will be randomly offset by this amount in X and Y direction
	public final boolean TURN_SIGNAL; // True if the agent should get an input for when it is in an intersection
	public final boolean STEER_SINGLE; // If the steering should be one (STEER_AMOUNT) or multiple with highest taking effect
	
	// Things
	private int maxSteps;
	private int maxStepsPerNewTile;

	
	// The map
	private MazeMap map;
	private int[] startPos;
	private int highReward, lowReward;
	
	private double[] initialObservation;
	
	// Distance helpers
	private List<double[]> walls; // int[x1,y1,x2,y2]
	
	// Live fields
	private double[] location;
	private double angle;
	public int[] goal;
	
	private int stepCounter;
	private HashSet<Pair<Integer, Integer>> visitedTiles;
	
	private int finished = -1;
	
	/**
	 * The required constructor to instantiate the Simulator through
	 * recursion.
	 * @param props The properties from ANJI.
	 */
	public TMaze(Properties props) {
		super(props);
		highReward = props.getIntProperty("simulator.tmaze.reward.high", 10);
		lowReward = props.getIntProperty("simulator.tmaze.reward.low", 1);
		maxSteps = props.getIntProperty("simulator.steps.max", 50);
		maxStepsPerNewTile = props.getIntProperty("simulator.steps.tile.max", 10);
		SPEED = props.getDoubleProperty("simulator.tmaze.game.speed", 0.1);
		SENSOR_CUTOFF = props.getIntProperty("simulator.tmaze.game.sensors.length", 3);
		STEER_AMOUNT = (props.getIntProperty("simulator.tmaze.game.steer.max", 45) / 180.0) * Math.PI;
		START_DIRECTION = props.getProperty("simulator.tmaze.dir.initial", null);
		START_DIR_OFFSET = (props.getIntProperty("simulator.tmaze.dir.offset", 0) / 180.0) * Math.PI;
		START_POS_OFFSET = props.getDoubleProperty("simulator.tmaze.pos.offset", 0.0);
		TURN_SIGNAL = props.getBooleanProperty("simulator.tmaze.turnsignal", false);
		STEER_SINGLE = props.getProperty("simulator.tmaze.game.steer.mode", "single").toLowerCase().equals("single");
	
		String mapFile = props.getProperty("simulator.tmaze.map", "tmaze.bmp");
		loadMap(mapFile);
		loadWalls();
		moveGoal(true);
		
		if(START_DIRECTION == null) {
			// Figure it out based on the maze
			if(map.getType(startPos[0], startPos[1] + 1) == empty) { START_DIRECTION = "N"; }
			else if(map.getType(startPos[0], startPos[1] - 1) == empty) { START_DIRECTION = "S"; }
			else if(map.getType(startPos[0] - 1, startPos[1]) == empty) { START_DIRECTION = "W"; }
			else if(map.getType(startPos[0] + 1, startPos[1]) == empty) { START_DIRECTION = "E"; }
			else { START_DIRECTION = "N"; }
		}else {
			START_DIRECTION = START_DIRECTION.substring(0, 1).toUpperCase();
		}
	}

	@Override
	public void restart() {
		init();
	}

	@Override
	public int getInputCount() {
		return (STEER_SINGLE ? 1 : 3); // The angle of stearing. 0.0 = Right, 1.0 Left, or 3 separate inputs
	}

	@Override
	public int getOutputCount() {
		return 3 + 1 + (TURN_SIGNAL ? 1 : 0); // Distance sensor at 45, 90 and 135 degrees + 1 * reward + possible turn signal
	}

	@Override
	public double[] getInitialObservation() {
		if(initialObservation == null)
			initialObservation = getObservation();
		return initialObservation;
	}

	@Override
	public double[] performAction(double[] action) {
		steer(action);
		moveAgent();
		
		if(isWithinGoal()) {
			finished = stepCounter;
		}
		
		
		this.visitedTiles.add(new Pair<Integer,Integer>((int)location[0],(int)location[1]));
		stepCounter++; // next round
		
		double[] obs = getObservation();
		printState(action, obs);
		//System.out.println("is within goal: " + isWithinGoal() + " obs: " + obs[3]);
		return obs;
	}

	@Override
	public double getCurrentScore() {
		return getReward();
	}

	@Override
	public int getMaxScore() {
		return highReward;
	}

	@Override
	public boolean isTerminated() {
	return isInWall() || finishedLastStep() || stepCounter >= maxSteps-1 || (stepCounter >= this.maxStepsPerNewTile * this.visitedTiles.size());
	}

	// SPECIFIC PUBLIC METHODS
	
	/**
	 * Returns the static map of the maze
	 * @return The MazeMap including the different
	 * types of wall/empty/goal at each position
	 */
	public MazeMap getMap() {
		return map;
	}
	
	/**
	 * The current position of the agent in the maze
	 * @return An array with two elements corresponding
	 * to the x and y coordinates in the maze.
	 */
	public double[] getPosition() {
		return new double[]{location[0],location[1]};
	}
	
	/**
	 * The tile index that the agent is currently within.
	 * @return An array with two elements corresponding
	 * to the x and y coordinates in the map.
	 */
	public int[] getPositionTile() {
		return new int[]{(int) location[0],(int) location[1]};
	}
	
	/**
	 * Gets the angle that the agent is currently facing
	 * in the maze. 
	 * @return The angle in radians where 0 is to the right
	 * (positive x), � * pi is up (positive y) etc.
	 */
	public double getAngle() {
		return angle;
	}
	
	/**
	 * Gets the coordinates of the high reward tile in the map.
	 * @return An array with two elements corresponding to
	 * the x and y coordinates in the map.
	 */
	public int[] getHighRewardGoal() {
		return new int[]{goal[0],goal[1]};
	}
	
	/**
	 * Gets the current observations from the agent in the maze.
	 * @return An array with a value for each defined sensor 
	 * angle. The value is between 0 and 1 normalized from how
	 * far there is to the nearest wall in that direction
	 * (cut off that the SENSOR_CUTOFF distance).
	 */
	public double[] getCurrentObservation() {
		return getObservation();
	}
	
	/**
	 * Moves the goal from its current location to another
	 * possible spot.
	 * @param canBeSame True if the goal can be the same as
	 * what it is currently.
	 */
	public void swapGoal(boolean canBeSame) {
		moveGoal(canBeSame);
	}
	
	/**
	 * Moves the goal from its current location to the
	 * requested goal id, can also be the same as current.
	 * @param goal_id The id of the new placement of the
	 * goal.
	 */
	public void swapGoal(int goal_id) {
		List<int[]> goals = map.getOfType(MAP_TYPE.goal);
		goal = goals.get(goal_id);
	}
	
	/**
	 * Finds the id of the goal at the given tile.
	 * @param tile The coordinates in the maze of the goal.
	 * @return The id between 0 and the number of goals (exclusive),
	 * or -1 of there is no goal at that tile.
	 */
	public int getGoalId(int[] tile) {
		List<int[]> goals = this.getMap().getOfType(MAP_TYPE.goal);
		for(int i = 0; i < goals.size(); i++)
			if(goals.get(i)[0] == tile[0] && goals.get(i)[1] == tile[1])
				return i;
		return -1;
	}
	
	/**
	 * Gets the current step in the simulation.
	 * @return A number between 0 and maxSteps.
	 */
	public int getStep() {
		return this.stepCounter;
	}
	
	public boolean isInHighGoal() {
		return this.getReward() == this.highReward;
	}
	
	public int getHighReward() {
		return this.highReward;
	}
	
	public int getLowReward() {
		return this.lowReward;
	}
	
	// HELPER METHODS
	
	private void init() {
		double x = startPos[0] + 0.5;
		double y = startPos[1] + 0.5;
		if (START_POS_OFFSET > 0.0){
			x += 2 * getRandom().nextDouble() * START_POS_OFFSET - START_POS_OFFSET;
			y += 2 * getRandom().nextDouble() * START_POS_OFFSET - START_POS_OFFSET;
		}
		location = new double[]{x, y};
		angle = getInitialAngle();
		stepCounter = 0;
		this.visitedTiles = new HashSet<>();
		finished = -1;
	}
	
	private double getInitialAngle() {
		double result;

		switch(START_DIRECTION) {
		case("E"): result = 0; break;
		case("S"): result = Math.PI * 1.5; break;
		case("W"): result = Math.PI; break;
		default: result = Math.PI * 0.5; // (N)orth
		}
		
		// random offset
		if(START_DIR_OFFSET > 0.0)
			result += getRandom().nextDouble() * 2 * START_DIR_OFFSET - START_DIR_OFFSET;
		
		return result;
	}

	private void printState(double[] steer, double[] sensors) {
		if(DEBUG){
			System.out.println("----------------------");
			printMap();
			System.out.printf("\n Pos: %s, Angle: %.2f, Steer: %s, sensors: %s\n",Arrays.toString(getPosition()),(angle / (2*Math.PI))*360,Utilities.toString(steer,"%.2f"),Arrays.toString(sensors));
		}
	}
	
	private void printMap() {
		int[] pos = getPositionTile();
		for(int y = map.getHeight()-1; y >= 0; y--) {
			for(int x = 0; x < map.getWidth(); x++) {
				if(x == pos[0] && y == pos[1]) {
					System.out.print("o");
				} else {
					switch(map.getType(x, y)) {
					case wall:
						System.out.print("x");
						break;
					case empty:
						System.out.print(" ");
						break;
					case goal:
						if(x == goal[0] && y == goal[1]) {
							System.out.print("G");
						} else {
							System.out.print("g");
						}
						break;
					case start:
						System.out.print("s");
					}
				}
				
			}
			System.out.println();
		}
	}

	private void loadWalls() {
		walls = new ArrayList<double[]>();
		
		for(int x = -1; x < map.getWidth(); x++) {
			for(int y = -1; y < map.getHeight(); y++) {
				// Check upper and right side
				MAP_TYPE cur = map.getType(x, y);
				MAP_TYPE upper = map.getType(x,y+1);
				MAP_TYPE right = map.getType(x+1,y);
				if(cur != upper && (cur == wall || upper == wall)) walls.add(new double[]{x,y+1,x+1,y+1});
				if(cur != right && (cur == wall || right == wall)) walls.add(new double[]{x+1,y,x+1,y+1});
			}
		}
	}
	
	private void steer(double[] dir) {
		double steer = 0.0;
		if(STEER_SINGLE) {
			steer = dir[0] * 2 - 1;
		} else {
			int highest = Utilities.maxPos(dir);
			switch(highest) {
				case 0: { // Left
					steer = -1.0;
					break;
				}
				case 2: { // Right
					steer = 1.0;
					break;
				}
			}
		}
		angle += steer * STEER_AMOUNT;
	}
	
	private void moveAgent() {
		double dx = Math.cos(angle) * SPEED;
		double dy = Math.sin(angle) * SPEED;
		location[0] += dx;
		location[1] += dy;
	}
	
	private double[] getObservation() {
		double[] result = new double[SENSOR_ANGLES.length + 1 + (TURN_SIGNAL ? 1 : 0)];
		
		for(int i = 0; i < SENSOR_ANGLES.length; i++) {
			double sensorAngle = angle + SENSOR_ANGLES[i];
			
			// calculate intersection to each 
			List<double[]> wallIntersects = intersections(new double[]{location[0] ,location[1]
							,location[0] + Math.cos(sensorAngle) * SENSOR_CUTOFF ,location[1] + Math.sin(sensorAngle) * SENSOR_CUTOFF}
					,walls);
			double closest = lowestDistance(location,wallIntersects);
			
			result[i] = Math.min(SENSOR_CUTOFF, closest) / SENSOR_CUTOFF;
		}
		result[SENSOR_ANGLES.length] = getReward() / (double)highReward;
		
		if(TURN_SIGNAL) {
			if(this.getMap().isIntersection(getPositionTile())) {
				result[SENSOR_ANGLES.length + 1] = 1.0;
			}
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
			double[] intersect = lineSegIntersect(fromLine,seg);
			if(intersect != null)
				result.add(intersect);
		}
		return result;
	}

	private double[] lineSegIntersect(double[] first, double[] second) {
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


	private void loadMap(String mapFile) {
		try {
			BufferedImage b = ImageIO.read(new File(mapFile));
			map = new MazeMap(b.getWidth(),b.getHeight());
			
			// load map
			for(int x = 0; x < b.getWidth(); x++) {
				for(int y = 0; y < b.getHeight(); y++) {
					int realY = b.getHeight()-1-y;
					MAP_TYPE type = MAP_TYPE.valueOf(b.getRGB(x, y));
					map.setType(x, realY, type);
					
					if(type == MAP_TYPE.start)
						this.startPos = new int[]{x,realY};
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
		return finished != -1 && stepCounter == finished + 1;
	}

	private boolean isInWall() {
		return map.getType(location[0], location[1]) == MAP_TYPE.wall;
	}
	
	private double getReward() {
		if(map.getType(location[0], location[1]) == MAP_TYPE.goal) {
			return Arrays.equals(getTile(location), goal) ? highReward : lowReward;
		}
		return 0;
	}
	
	private void moveGoal(boolean canBeSame) {
		List<int[]> goals = map.getOfType(MAP_TYPE.goal);
		if(canBeSame) {
			goal = goals.get(getRandom().nextInt(goals.size()));
		} else {
			int roll = getRandom().nextInt(goals.size()-1);
			goal = Arrays.equals(goals.get(roll), goal) ? goals.get(goals.size()-1) : goals.get(roll);
		}
	}
	
	private int[] getTile(double[] location) {
		return new int[]{(int)location[0],(int)location[1]};
	}
	
	// HELPER CLASSES & ENUMS
	
	public enum MAP_TYPE { 
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
		
		public boolean isIntersection(int[] pos) {
			if(this.getType(pos[0], pos[1]) != MAP_TYPE.empty)
				return false;
			int counter = 0;
			int[] dirs = new int[]{-1,0, 1,0, 0,-1, 0,1};
			for(int i = 0; i < dirs.length/2; i++) {
				if(this.getType(pos[0] + dirs[i*2], pos[1] + dirs[i*2 + 1]) == MAP_TYPE.empty)
					counter++;
			}
			
			return counter > 2;
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
				for(int y = 0; y < map[0].length; y++) {
					if(map[x][y] == type)
						result.add(new int[]{x,y});
				}
			}
			return result;
		}
	}
}
