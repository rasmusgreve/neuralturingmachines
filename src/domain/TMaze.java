package domain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.Pair;

import com.anji.util.Properties;

public class TMaze implements Simulator {
	
	// Random things
	private Random rand;
	
	// The map
	private MazeMap map;
	private int[] startPos;
	private int highReward, lowReward;
	
	// Live fields
	private double[] location;
	private double angle;
	private int[] goal;
	
	public TMaze(Properties props) {
		rand = new Random(props.getIntProperty("random.seed"));
		highReward = props.getIntProperty("simulator.tmaze.reward.high");
		lowReward = props.getIntProperty("simulator.tmaze.reward.low");
		
		String mapFile = props.getProperty("simulator.tmaze.map");
		loadMap(mapFile);
	}

	private void loadMap(String mapFile) {
		try {
			BufferedImage b = ImageIO.read(new File(mapFile));
			map = new MazeMap(b.getWidth(),b.getHeight());
			
			// load map
			for(int x = 0; x < b.getWidth(); x++) {
				for(int y = 0; y < b.getHeight(); y++) {
					MAP_TYPE type = MAP_TYPE.valueOf(b.getRGB(x, y));
					map.setType(x, y, type);
					
					if(type == MAP_TYPE.start)
						this.startPos = new int[]{x,y};
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e); // There is no recovery from this
		}
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
		moveGoal();
	}

	@Override
	public double[] getInitialObservation() {
		return getObservation();
	}

	@Override
	public double[] performAction(double[] action) {
		moveAgent();
		
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

	// HELPER METHODS
	
	private void moveAgent() {
		// TODO Auto-generated method stub
	}
	
	private boolean finishedLastStep() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isInWall() {
		return map.getType(location[0], location[1]) == MAP_TYPE.wall;
	}
	
	private int getReward() {
		// TODO AT CURRENT LOCATION
		return 0;
	}
	
	private double[] getObservation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void moveGoal() {
		List<int[]> goals = map.getOfType(MAP_TYPE.goal);

		int roll = rand.nextInt(goals.size()-1);
		goal = Arrays.equals(goals.get(roll), goal) ? goals.get(goals.size()-1) : goals.get(roll);
	}
	
	// HELPER CLASSES & ENUMS
	
	private enum MAP_TYPE { 
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
	
	private class MazeMap {
		
		private MAP_TYPE[][] map;

		public MazeMap(int width, int height) {
			this.map = new MAP_TYPE[width][];
			for(int i = 0; i < width; i++) {
				map[i] = new MAP_TYPE[height];
			}
		}
		
		public void setType(int x, int y, MAP_TYPE type) {
			map[x][y] = type;
		}
		
		public MAP_TYPE getType(int x, int y) {
			return map[x][y];
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
