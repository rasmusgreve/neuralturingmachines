package dk.itu.ejuuragr.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;

/**
 * A simple domain where you move around (like in the T-Maze)
 * and need to eat the other species in order to survive.
 * Different species have different sensable features so the
 * agent should be able to "learn" the traits of the various
 * species and go for the ones that are easiest to catch and
 * have the maximum nutritional value, and especially avoid
 * those who are poisonous (negative nutritional value).
 * 
 * @author Emil
 *
 */
public class HuntingSimulator extends BaseSimulator {
	
	public static final boolean DEBUG = false; // True if it should print out changes as text

	private static final double EDIBLE_DISTANCE = 0.2; // How close you need to be to eat an animal
	
	private final int mapSize = 5;
	private final int numberOfFeatures;
	private final double featureBlur;
	private final int numberOfSpecies;
	private final double startHealth;
	private final double steerAmount;
	private final double SPEED;
	private final double decayRate;
	private final boolean samePopulation;
	
	private boolean lastEdible;
	private double health;
	private double angle;
	private double nutritionSum;
	
	private List<Animal> animals;
	private List<Species> species;
	private double speciesSum;
	
	private double[] initialObservation;
	private double maxScore;

	public HuntingSimulator(Properties props) {
		super(props);
		this.numberOfSpecies = props.getIntProperty("simulator.hunting.species", 5);
		this.startHealth = props.getDoubleProperty("simulator.hunting.health", 10);
		this.numberOfFeatures = props.getIntProperty("simulator.hunting.features", 1);
		this.featureBlur = props.getDoubleProperty("simulator.hunting.features.blur", 0.0);
		this.steerAmount = (props.getIntProperty("simulator.hunting.steer.max", 45) / 180.0) * Math.PI;
		this.SPEED = props.getDoubleProperty("simulator.hunting.speed", 0.1);
		this.decayRate = props.getDoubleProperty("simulator.hunting.decay", 0.05);
		
		this.samePopulation = props.getBooleanProperty("simulator.hunting.populations.equal", true);
	}

	@Override
	public int getInputCount() {
		return 1;
	}

	@Override
	public int getOutputCount() {
		return 2 + numberOfFeatures;
	}

	@Override
	public void restart() {
		this.angle = 0.0;
		this.health = startHealth;
		this.nutritionSum = 0.0;
		
		// Create species;
		animals = new LinkedList<Animal>();
		species = new ArrayList<Species>();
		boolean hasPositive = false;
		boolean hasNegative = false;
		
		this.speciesSum = 0.0;
		for(int i = 0; i < numberOfSpecies; i++) {
			double[] features = new double[numberOfFeatures];
			for(int f = 0; f < features.length; f++) {
				features[f] = getRandom(0.0, 1.0);
			}
			
			// Making sure we have at least one positive and then
			// at least one negative, all others are random.
			double nutrition;
			if(!hasPositive) {
				nutrition = getRandom(0.34, 1.0);
				hasPositive = true;
			}else if(!hasNegative) {
				nutrition = getRandom(0.0, 0.33);
				hasNegative = true;
			}else {
				nutrition = getRandom(0.0, 1.0);
			}
			
			double population = samePopulation ? 1.0 : getRandom(0.0, 1.0);
			
			Species newSpecies = new Species(
					population,
					nutrition,
					getRandom(0.0, 1.0),
					features);
			
			species.add(newSpecies);
			speciesSum += newSpecies.getPopulation();
		}
		
		// Create animals for the whole map (-5 to 5)
		for(int i = -mapSize; i < mapSize; i++) {
			for(int j = -mapSize; j < mapSize; j++) {
				addRandomAnimal(i,j);
			}
		}
		
		// Get initial observation & max score
		initialObservation = getObservation();
		
		maxScore = startHealth;
		for(Animal a : animals) {
			double curNut = a.getSpecies().getNutrition();
			if(curNut > 0.0)
				maxScore += curNut;
		}
		// Maximum score is if you had eaten all animals
		// without spending any time at all
		
		if(DEBUG){
			System.out.println(">>> Restart");
			System.out.println(Arrays.toString(species.toArray()));
			System.out.println(Arrays.toString(animals.toArray()));
		}
	}

	@Override
	public double[] getInitialObservation() {
		return initialObservation;
	}

	@Override
	public double[] performAction(double[] action) {
		if(DEBUG)
			System.out.println("-------------------------------");
		// perform action
		steer(action[0]);
		moveAgent();
		
		// manipulate world
		for(Animal a : animals) {
			if(a.getX() < -mapSize) {
				a.setX(2*mapSize + a.getX());
			}else if(a.getX() >= mapSize) {
				a.setX(-2*mapSize + a.getX());
			}
			if(a.getY() < -mapSize) {
				a.setY(2*mapSize + a.getY());
			}else if(a.getY() >= mapSize) {
				a.setY(-2*mapSize + a.getY());
			}
		}
		
		// always lose health
		health -= decayRate;
		
		if(DEBUG) {
			System.out.printf("Status: health=%.2f angle=%.2f eaten=%.2f\n", health, angle, nutritionSum);
		}
		
		return getObservation();
	}

	@Override
	public double getCurrentScore() {
		return nutritionSum > 0.0 ? nutritionSum * 100.0 : 0.0;
	}

	@Override
	public int getMaxScore() {
		return (int)(maxScore * 100);
	}

	@SuppressWarnings("unused")
	@Override
	public boolean isTerminated() {
		boolean result = health <= 0.0 || animals.size() == 0;
		
		if(result && DEBUG) {
			if(health <= 0.0) {
				System.out.printf("DEATH! - Eaten=%.2f\n", nutritionSum);
			}else {
				System.out.printf("WIN! - Health left=%.2f Eaten=%.2f\n", health, nutritionSum);
			}
		}
		return result;
	}
	
	// PUBLIC GETTERS
	
	public List<Animal> getAnimals() {
		return Collections.unmodifiableList(animals);
	}
	
	public double getAngle() {
		return this.angle;
	}
	
	public double getHealth() {
		return this.health;
	}
 	
	// PRIVATE HELPER METHODS
	
	private void moveAgent() {
		// Moving everything relative to the agent
		double dx = Math.cos(angle) * -SPEED;
		double dy = Math.sin(angle) * -SPEED;
		
		for(Animal a : animals) {
			a.move(dx, dy);
		}
	}
	
	private void steer(double dir) {
		dir = dir * 2 - 1;
		this.angle += dir * this.steerAmount;
	}
	
	private double[] getObservation() {
		double[] result = new double[getOutputCount()];
		
		// angle to closest (as a value between 0 and 1,
		// where 0 is to the right (or more) and 1 is to
		// the left or more
		if(animals.size() > 0) {
			Animal closest = findClosestAnimal();
			if(DEBUG) System.out.printf("Closest: %s\n",closest);
			double angle = Math.atan2(closest.getY(), closest.getX());
			if(DEBUG) System.out.printf("Angle to closest: %.2f\n", angle);
			angle -= this.angle; // relative to agent
			if(DEBUG) System.out.printf("Adjusted angle: %.2f\n", angle);
			result[0] = ( Utilities.clamp(angle, -Math.PI / 2, Math.PI / 2) + (Math.PI / 2) ) / Math.PI;
			if(DEBUG) System.out.printf("Normalized angle: %.2f\n", result[0]);
			
			// Current nutrition
			if(lastEdible) {
				double effect = closest.getSpecies().getNutrition() - (1.0 / 3.0);
				health += effect;
				nutritionSum += effect;
				result[1] = closest.getSpecies().getNutrition();
				
				lastEdible = false;
				animals.remove(closest);
			}
			
			// closest animal's features
			Utilities.copy(closest.getFeatures(), result, 2);
			
			// Are we within range to eat?
			double distance = Utilities.euclideanDistance(new double[2], closest.getPosition());
			if(distance < EDIBLE_DISTANCE) {
				lastEdible = true;
			}
		}
		
		if(DEBUG) {
			System.out.println("Final observation: "+Utilities.toString(result));
		}
		
		return result;
	}
	
	private Animal findClosestAnimal() {
		double[] origo = new double[2];
		Animal result = null;
		double dist = Double.MAX_VALUE;
		
		for(Animal cur : animals) {
			double curDist = Utilities.euclideanDistance(origo, cur.getPosition());
			if(curDist < dist) {
				result = cur;
				dist = curDist;
			}
		}
		return result;
	}

	private void addRandomAnimal(int x, int y) {
		Species s = getRandomSpecies();
		animals.add(new Animal(getRandom(x, x + 1.0), getRandom(y, y + 1.0), s));
	}
	
	private Species getRandomSpecies() {
		double target = getRandom(0.0,speciesSum);
		double value = 0.0;
		for(Species s : species) {
			value += s.population;
			if(value >= target)
				return s;
		}
		return null;
	}

	private double getRandom(double min, double max) {
		return getRandom().nextDouble() * (max - min) + min;
	}
	
	private int getRandom(int min, int max) {
		return getRandom().nextInt(max - min + 1) + min;
	}
	
	// PRIVATE CLASSES
	
	private class Animal {
		
		private double[] pos;
		private double direction;
		private Species mySpecies;
		private double[] features;
		
		public Animal(double x, double y, Species origin) {
			this.mySpecies = origin;
			this.pos = new double[]{x,y};
			this.features = new double[origin.getFeatures().length];
			for(int i = 0; i < features.length; i++) {
				features[i] = origin.getFeatures()[i] + getRandom().nextGaussian() * featureBlur;
			}
		}
		
		public void move(double dx, double dy) {
			pos[0] += dx;
			pos[1] += dy;
		}

		public double[] getPosition() {
			return pos;
		}

		public double getX() {
			return pos[0];
		}
		
		public void setX(double x) {
			pos[0] = x;
		}

		public double getY() {
			return pos[1];
		}
		
		public void setY(double y) {
			pos[1] = y;
		}
		
		public double[] getFeatures() {
			return features;
		}

		public double getDirection() {
			return direction;
		}

		public Species getSpecies() {
			return mySpecies;
		}

		public void act() {
			// TODO: No movement in the beginning
		}

		@Override
		public String toString() {
			return "Animal [pos=" + Arrays.toString(pos) + ", direction="
					+ direction + ", species_idx=" + species.indexOf(mySpecies) + ", features="
					+ Arrays.toString(features) + "]";
		}

	}

	private class Species {
		
		private double population;
		private double nutrition;
		private double speed;
		private double[] features;

		public Species(double population, double nutrition, double speed, double[] features) {
			this.population = population;
			this.nutrition = nutrition;
			this.speed = speed;
			this.features = features;
		}

		public double getPopulation() {
			return population;
		}

		public double getNutrition() {
			return nutrition;
		}

		public double getSpeed() {
			return speed;
		}

		public double[] getFeatures() {
			return features;
		}

		@Override
		public String toString() {
			return "Species [population=" + population + ", nutrition="
					+ nutrition + ", speed=" + speed + ", features="
					+ Arrays.toString(features) + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(features);
			long temp;
			temp = Double.doubleToLongBits(nutrition);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(population);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(speed);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Species other = (Species) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (!Arrays.equals(features, other.features))
				return false;
			if (Double.doubleToLongBits(nutrition) != Double
					.doubleToLongBits(other.nutrition))
				return false;
			if (Double.doubleToLongBits(population) != Double
					.doubleToLongBits(other.population))
				return false;
			if (Double.doubleToLongBits(speed) != Double
					.doubleToLongBits(other.speed))
				return false;
			return true;
		}

		private HuntingSimulator getOuterType() {
			return HuntingSimulator.this;
		}
	}
}
