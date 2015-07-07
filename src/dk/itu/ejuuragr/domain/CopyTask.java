package dk.itu.ejuuragr.domain;

import java.util.Arrays;
import java.util.Random;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;

/**
 * This is the Copy Task as described by Graves et.al.
 * in "Neural Turing Machine".
 * It tests the controller in first receiving a random
 * sequence of vectors of a random length and then
 * sees if it can then output it correctly.
 * 
 * @author Emil
 *
 */
public class CopyTask implements Simulator {
	
	private int m;
	private Random rand;
	private int maxLength;
	private double[] zeroVector;
	
	private double[][] sequence;
	private int step;
	private double score;

	public CopyTask(Properties props) {
		this.m = props.getIntProperty("tm.m");
		this.rand = new Random(props.getIntProperty("random.seed"));
		this.maxLength = props.getIntProperty("simulator.copytask.length.max");
		
		this.zeroVector = new double[this.m];
		reset();
	}

	@Override
	public int getInputCount() {
		return this.m; // The read output from the controller
	}

	@Override
	public int getOutputCount() {
		return this.m; // The input we give the controller in each iteration
		// Will be empty when we expect the controller to read back
		// the sequence.
	}

	@Override
	public void reset() {
		// Create the random sequence
		int length = rand.nextInt(this.maxLength);
		
		this.sequence = new double[length][];
		for(int i = 0; i < length; i++) {
			sequence[i] = new double[this.m];
			for(int j = 0; j < m; j++) {
				sequence[i][j] = rand.nextInt(2);
			}
			//System.out.println(Arrays.toString(sequence[i]));
		}
		
		
		// reset variables
		this.step = 1;
		this.score = 0.0;
	}

	@Override
	public double[] getInitialObservation() {
		return getObservation(0);
	}

	@Override
	public double[] performAction(double[] action) {
		double[] result;
		if(step < sequence.length) { // First we don't care what it reads
			result = getObservation(step);
		} else { // The controllers "action" is the reading after |seq| steps
			this.score += calcSimilarity(sequence[step - sequence.length],action);
			// Just return the 0-vector (so we don't aid it in reading)
			result = zeroVector;
		}
		
		step++; // Increment step
		return result;
	}

	@Override
	public int getCurrentScore() {
		return (int)(score * 10.0 * (maxLength / (1.0 * sequence.length)));
	}

	@Override
	public int getMaxScore() {
		return (int) (sequence.length * 10.0 * (maxLength / (1.0 * sequence.length)));
	}

	@Override
	public boolean isTerminated() {
		return this.step >= 2*sequence.length;
	}
	
	// PRIVATE HELPER METHODS
	
	/**
	 * Gets the observations at the given step.
	 * @param step The point in the sequence to give
	 * observations from. Must be below 2*m and will
	 * be the 0-vector after m steps.
	 * @return The content of the sequence at that
	 * step.
	 */
	private double[] getObservation(int step) {
		if(step < this.sequence.length)
			return sequence[step];
		return zeroVector;
	}
	
	/**
	 * Calculates how similar the two vectors are as
	 * a value between 0.0 and 1.0;
	 * @param first The first vector to compare.
	 * @param second The vector to compare it to.
	 * @return 0.0 if the vectors are totally different,
	 * 1.0 if they are identical, or somewhere in between.
	 */
	private double calcSimilarity(double[] first, double[] second) {
		return Utilities.emilarity(first, second);
	}

}
