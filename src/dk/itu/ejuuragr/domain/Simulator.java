package dk.itu.ejuuragr.domain;

/**
 * A Simulator should have a constructor taking a single Properties object.
 * This way it can be used by simply referencing it in the properties file.
 * @author Emil
 *
 */
public interface Simulator {
	
	/**
	 * @return Number of inputs that the simulator expects
	 */
	int getInputCount();
	
	/**
	 * @return Numbe of outputs that the simulator will return
	 */
	int getOutputCount();
	
	/**
	 * Reset the simulator to some initial state (for new agents
	 * to be tested under same circumstances
	 */
	void reset();
	
	/**
	 * Move the agent back to start for a new round of evaluation
	 * (can be different from the previous state).
	 */
	void restart();
	
	/**
	 * Get values for the first input to the neural network
	 * @return
	 */
	double[] getInitialObservation();
	
	/**
	 * @param action The action to simulate (must have size {@link #getInputCount()}
	 * @return The output of the simulator that the input gave (will have size {@link #getOutputCount()}
	 */
	double[] performAction(double[] action);
	
	/**
	 * Get the current score that has been collected since the last call to {@link #reset()}
	 * @return
	 */
	int getCurrentScore();
	
	/**
	 * @return The highest possibly obtainable score
	 */
	int getMaxScore();
	
	/**
	 * @return True in case the simulation must stop now (e.g. you won/lost the entire thing)
	 */
	boolean isTerminated();
}
