package dk.itu.ejuuragr.domain;

import java.util.Random;

import com.anji.util.Properties;

/**
 * This abstract helper level is responsible for managing the random object
 * for inheriting Simulators with it being re-seeded upon reset() and allowing
 * the FitnessEvaluator to offset the seed when starting a new generation, so
 * the simulation for each generation can be slightly different but identical
 * for each chromosome in it.
 * 
 * @author Emil
 *
 */
public abstract class BaseSimulator implements Simulator {

	private int randomSeed;
	private Random rand;
	private int offset;

	public BaseSimulator(Properties props) {
		this.randomSeed = props.getIntProperty("random.seed");
		this.offset = 0;
		resetRandom();
	}

	@Override
	public void reset() {
		resetRandom();
		this.restart();
	}

	/**
	 * Gives the live Random object.
	 * @return The Random object for generating pseudo-random numbers.
	 */
	public Random getRandom() {
		return rand;
	}

	/**
	 * Sets an offset which will be used in all future calls to
	 * reset().
	 * @param offset A number which will be added to the seed for the
	 * random number generator.
	 */
	public void setRandomOffset(int offset) {
		this.offset = offset;
	}

	// PRIVATE HELPER METHODS

	private void resetRandom() {
		this.rand = new Random(randomSeed + offset);
	}
}
