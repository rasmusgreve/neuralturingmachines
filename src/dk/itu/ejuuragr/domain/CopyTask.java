package dk.itu.ejuuragr.domain;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.turing.MinimalTuringMachine;
import dk.itu.ejuuragr.turing.TuringController;
import dk.itu.ejuuragr.turing.TuringMachine;

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

	private int elementSize; // The length of an element in the sequence (usually M - 1)
	private int preparedSize;
	private int maxSeqLength; // The maximum length that the sequence can be (if random), else the actual sequence length.
	private String lengthRule; // If the sequence length should be "fixed" or "random"ly determined.

	protected double[][] sequence;
	private int step;
	private double score;

	private String fitnessFunction;

	private MinimalTuringMachine mtm = null;

	public CopyTask(Properties props) {
		super(props);
		this.elementSize = props.getIntProperty("simulator.copytask.element.size", 1); // Less than m to allow for the network to store extra
		this.preparedSize = props.getIntProperty("simulator.copytask.element.prepared", elementSize); // To prepare for extra elements in the future		
		this.maxSeqLength = props.getIntProperty("simulator.copytask.length.max", 10);
		this.lengthRule = props.getProperty("simulator.copytask.length.rule", "fixed");
		this.fitnessFunction = props.getProperty("simulator.fitness.function", "strict-close");
	}
	
	@Override
	public void restart() {
		
		// Create the random sequence
		int length = 1;
		switch(lengthRule) {
			case "random":
				length = this.getRandom().nextInt(this.maxSeqLength) + 1;
				break;
			default: // fixed
				length = maxSeqLength;
				break;
		}

		// CREATE SEQUENCE
		this.sequence = new double[length][];
		for (int i = 0; i < length; i++) {
			sequence[i] = new double[preparedSize];
			for (int j = 0; j < elementSize; j++) {
				sequence[i][j] = getRandom().nextInt(2);
			}
		}
		
		if(DEBUG) System.out.print("CT: Restart: "+Utilities.toString(sequence, "%1.0f"));

		// reset variables
		this.step = 1;
		this.score = 0.0;
	}

	@Override
	public int getInputCount() {
		return preparedSize; // The read output from the controller
	}

	@Override
	public int getOutputCount() {
		return preparedSize + 2; // The input we give the controller in each iteration
		// Will be empty when we expect the controller to read back
		// the sequence.
		// The two extra ones are START and DELIMITER bits.
	}

	@Override
	public void reset() {
		super.reset();
		
		if(getController() instanceof TuringController && fitnessFunction.equals("partial-score")) {
			TuringMachine tm = ((TuringController)getController()).getTuringMachine();
			if(tm instanceof MinimalTuringMachine) {
				((MinimalTuringMachine)tm).setRecordTimeSteps(true);
			}
		}
		
		if(DEBUG) System.out.println("---------- RESET ----------");
	}

	@Override
	public double[] getInitialObservation() {
		return getObservation(0);
	}

	@Override
	public double[] performAction(double[] action) {
		if(DEBUG) {
			System.out.println("-------------------------- COPYTASK --------------------------");
		}
		
		double[] result = getObservation(step);
		
		// Compare and score (if reading)
		if (step >= sequence.length + 2 + 1) {
			// The controllers "action" is the reading after 2 + |seq| steps
			
			int index = step - sequence.length - 2 - 1;
			double[] correct = elementSize < preparedSize ? Utilities.copy(sequence[index], 0, elementSize) : sequence[index];
			double[] received = elementSize < preparedSize ? Utilities.copy(action, 0, elementSize) : action;
			double thisScore = evaluate(correct, received);
			this.score += thisScore;
			
			if(DEBUG) System.out.println("\tReading: "+Utilities.toString(received)+" compared to "+Utilities.toString(correct)+" = "+thisScore);
		}
		
		if(DEBUG) System.out.println("--------------------------------------------------------------");

		step++; // Increment step
		return result;
	}

	protected double evaluate(double[] correct, double[] received){
		return calcSimilarity(correct, received);
	}
	
	
	@Override
	public double getCurrentScore() {
		return score * 10.0 * (maxSeqLength / (1.0 * sequence.length));
	}

	@Override
	public int getMaxScore() {
		return (int) (sequence.length * 10.0 * (maxSeqLength / (1.0 * sequence.length)));
	}

	@Override
	public boolean isTerminated() {
		return this.step >= 2 * sequence.length + 2 + 1;
	}
	
	@Override
	public String toString() {
		return Utilities.toString(sequence, "%1.0f");
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
		double[] result = new double[this.preparedSize + 2];
		
		if (step == 0) { // Send start vector
			result[0] = 1; // START bit

		} else if (step <= this.sequence.length) { // sending the sequence
			Utilities.copy(sequence[step - 1],result,2);

		} else if (step == this.sequence.length + 1) { // DELIMITER bit
			result[1] = 1;

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
		switch(fitnessFunction){
		case "partial-score": return partialScore(first, second);
		case "emilarity": return Utilities.emilarity(first, second);
		case "closest-binary": return closestBinary(first, second);
		case "complete-binary": return completeMatchClosestBinary(first, second);
		default: return strictCloseToTarget(first, second); // "strict-close"
		}
	}
	
	// FITNESS FUNCTIONS
	
	private double partialScore(double[] target, double[] actual) {
		// prep
		if(mtm != null || (getController() instanceof TuringController && ((TuringController)getController()).getTuringMachine() instanceof MinimalTuringMachine)) {
			mtm = (MinimalTuringMachine)((TuringController) getController()).getTuringMachine();
			
			// Get half the score for storing correctly
			// comparing the target with the first E elements written to memory that round
			double[] written = Utilities.copy(mtm.getLastTimeStep().key, 0, target.length);
			
			double tmResult = strictCloseToTarget(target, written);
			double baseResult = strictCloseToTarget(target, actual);
			
			if(DEBUG) System.out.printf("Target=%s Actual=%s Written=%s | TM Score=%.2f Output Score=%.2f\n",Utilities.toString(target),Utilities.toString(actual),Utilities.toString(written),tmResult,baseResult);
			
			return 0.5 * tmResult + 0.5 * baseResult;
		}else {
			throw new UnsupportedOperationException("Can not use partial-score without the Controller being of type TuringController and its TM being MinimalTuringMachine");
		}
	}
	
	private double strictCloseToTarget(double[] target, double[] actual) {
		final double threshold = 0.25;
		double result = 0.0;
		
		for(int i = 0; i < target.length; i++) {
			result += 1.0 - Math.min(threshold, Math.abs(target[i] - actual[i])) / threshold;
		}
		
		return result / target.length;
	}
	
	/**
	 * Assuming the targets are binary (e.g. either 0.0 or 1.0)
	 */
	private double closestBinary(double[] target, double[] actual) {
		double result = 0;
		for(int i = 0; i < target.length; i++) {
			if(Math.abs(target[i] - actual[i]) < 0.5) {
				result++;
			}
		}
		return result / target.length;
	}
	
	private double completeMatchClosestBinary(double[] target, double[] actual){
		int matches = 0;
		for(int i = 0; i < target.length; i++) {
			if(Math.abs(target[i] - actual[i]) < 0.5) {
				matches++;
			}
		}
		return matches == target.length ? 1 : 0;

	}
}
