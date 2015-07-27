package dk.itu.ejuuragr.domain;

import java.util.Arrays;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;

/**
 * This is the Copy Task as described by Graves et.al. in
 * "Neural Turing Machine". It tests the controller in first receiving a random
 * sequence of vectors of a random length and then sees if it can then output it
 * correctly.
 * 
 * @author Emil
 *
 */
public class CopyTask extends BaseSimulator {
	
	private static final boolean DEBUG = false; // True if it should print all input and output

	private int m;
	private int maxLength;

	private double[][] sequence;
	private int step;
	private double score;



	public CopyTask(Properties props) {
		super(props);
		this.m = props.getIntProperty("tm.m");
		this.maxLength = props.getIntProperty("simulator.copytask.length.max");
	}
	
	@Override
	public void restart() {
		if(DEBUG) System.out.print("Restart: [");
		// Create the random sequence
		int length = getRandom().nextInt(this.maxLength) + 1;

		this.sequence = new double[length][];
		for (int i = 0; i < length; i++) {
			sequence[i] = new double[this.m];
			for (int j = 0; j < m; j++) {
				sequence[i][j] = getRandom().nextInt(2);
			}
			if(DEBUG) System.out.print(Arrays.toString(sequence[i])+",");
		}
		if(DEBUG) System.out.println("]");

		// reset variables
		this.step = 1;
		this.score = 0.0;
	}

	@Override
	public int getInputCount() {
		return this.m; // The read output from the controller
	}

	@Override
	public int getOutputCount() {
		return this.m + 2; // The input we give the controller in each iteration
		// Will be empty when we expect the controller to read back
		// the sequence.
		// The two extra ones are START and DELIMITER bits.
	}

	@Override
	public void reset() {
		if(DEBUG) System.out.println("---------- RESET ----------");
		super.reset();
	}

	@Override
	public double[] getInitialObservation() {
		return getObservation(0);
	}

	@Override
	public double[] performAction(double[] action) {
		double[] result = getObservation(step);

		// Compare and score (if reading)
		if (step >= sequence.length + 2) {
			// The controllers "action" is the reading after 2 + |seq| steps
			
			int index = step - sequence.length - 2;
			double thisScore = calcSimilarity(sequence[index], action);
			this.score += thisScore;
			
			if(DEBUG) System.out.println("\tReading: "+Utilities.toString(action)+" compared to "+Utilities.toString(sequence[index])+" = "+thisScore);
		}

		step++; // Increment step
		return result;
	}

	@Override
	public int getCurrentScore() {
		return (int) (score * 10.0 * (maxLength / (1.0 * sequence.length)));
	}

	@Override
	public int getMaxScore() {
		return (int) (sequence.length * 10.0 * (maxLength / (1.0 * sequence.length)));
	}

	@Override
	public boolean isTerminated() {
		return this.step >= 2 * sequence.length + 2;
	}

	// PRIVATE HELPER METHODS

	/**
	 * Gets the observations at the given step.
	 * 
	 * @param step
	 *            The point in the sequence to give observations from. Must be
	 *            below 2*|seq|+2.
	 * @return The content of the sequence at that step.
	 */
	private double[] getObservation(int step) {
		double[] result = new double[this.m + 2];
		
		if (step == 0) { // Send start vector
			result[this.m] = 1; // START bit

		} else if (step <= this.sequence.length) { // sending the sequence
			Utilities.copy(sequence[step - 1],result,0);

		} else if (step == this.sequence.length + 1) { // DELIMITER bit
			result[result.length - 1] = 1;

		} else { // When we are reading we just send zeros

		}
		
		if(DEBUG) System.out.println(step+": "+Utilities.toString(result,"%.0f"));
		
		return result;
	}

	/**
	 * Calculates how similar the two vectors are as a value between 0.0 and
	 * 1.0;
	 * 
	 * @param first
	 *            The first vector to compare.
	 * @param second
	 *            The vector to compare it to.
	 * @return 0.0 if the vectors are totally different, 1.0 if they are
	 *         identical, or somewhere in between.
	 */
	private double calcSimilarity(double[] first, double[] second) {
//		return Utilities.emilarity(first, second);
		return strictCloseToTarget(first, second);
	}
	
	private double strictCloseToTarget(double[] target, double[] actual) {
		final double threshold = 0.25;
		double result = 0.0;
		
		for(int i = 0; i < target.length; i++) {
			result += 1.0 - Math.min(threshold, Math.abs(target[i] - actual[i])) / threshold;
		}
		
		return result / target.length;
	}
}
