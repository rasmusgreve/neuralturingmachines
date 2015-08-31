package dk.itu.ejuuragr.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.anji.util.Properties;

/**
 * A simple domain where you move around (like in the T-Maze)
 * and need to eat the other species in order to survive.
 * Different species have different sensable features so the
 * agent should be able to "learn" the traits of the various
 * species and go for the ones that are easiest to catch and
 * have the maximum nutritional value, and especially avoid
 * those who are poisonous (negative nutritional value).
 * 
 * @author Emil Juul Jacobsen
 *
 */
public class HuntingSimulator extends BaseSimulator {
	
	private final int numberOfSpecies;
	private final int startHealth;
	
	private int health;
	private double direction;
	
	private Set<Animal> animals;
	private List<Species> species;
	private double speciesSum;
	private double[] initialObservation;

	public HuntingSimulator(Properties props) {
		super(props);
		this.numberOfSpecies = props.getIntProperty("simulator.hunting.species", 5);
		this.startHealth = props.getIntProperty("simulator.hunting.health", 100);
	}

	@Override
	public int getInputCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOutputCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void restart() {
		this.direction = 0.0;
		this.health = startHealth;
		
		// Create species;
		animals = new TreeSet<Animal>();
		species = new ArrayList<Species>();
		
		this.speciesSum = 0.0;
		for(int i = 0; i < numberOfSpecies; i++) {
			Species newSpecies = new Species(
					getRandom(0.0, 1.0),
					getRandom(-10, 25),
					getRandom(0.0, 1.0));
			species.add(newSpecies);
			speciesSum += newSpecies.getPopulation();
		}
		
		// Create animals for the whole map (-5 to 5)
		for(int i = -5; i < 5; i++) {
			for(int j = -5; j < 5; j++) {
				addRandomAnimal(i,j);
			}
		}
		
		// Get initial observation
		initialObservation = getObservation();
	}

	@Override
	public double[] getInitialObservation() {
		return initialObservation;
	}

	@Override
	public double[] performAction(double[] action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}
	
	// PRIVATE HELPER METHODS
	
	private double[] getObservation() {
		double[] result = new double[3];
		// angle to closest
		// Current nutrition
		// closest animal's features
		
		//TODO: Incomplete
		
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
		
		private double x,y;
		private double direction;
		private Species species;
		
		public Animal(double x, double y, Species origin) {
			this.species = origin;
			this.x = x;
			this.y = y;
		}
		
		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getDirection() {
			return direction;
		}

		public Species getSpecies() {
			return species;
		}

		public void act() {
			// TODO: No movement in the beginning
		}
	}

	private class Species {
		
		private double population;
		private int value;
		private double speed;

		public Species(double population, int value, double speed) {
			this.population = population;
			this.value = value;
			this.speed = speed;
		}

		public double getPopulation() {
			return population;
		}

		public int getValue() {
			return value;
		}

		public double getSpeed() {
			return speed;
		}
	}
}
