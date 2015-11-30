package dk.itu.ejuuragr.domain;

import java.util.Random;

import dk.itu.ejuuragr.fitness.Controller;

/**
 * A Simulator should have a constructor taking a single Properties object.
 * This way it can be used by simply referencing it in the properties file.
 * 
 * @author Emil
 *
 */
public interface Simulator {
	
	/**
	 * @param controller The Controller that wants to register itself.
	 */
	void setController(Controller controller);
	
	/**
	 * @return Returns the Controller supervising this Simulator if
	 * it has registered itself.
	 */
	Controller getController();
	
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
	double getCurrentScore();
	
	/**
	 * @return The highest possibly obtainable score
	 */
	int getMaxScore();
	
	/**
	 * @return True in case the simulation must stop now (e.g. you won/lost the entire thing)
	 */
	boolean isTerminated();
	
	/**
	 * Gives the live Random object.
	 * @return The Random object for generating pseudo-random numbers.
	 */
	Random getRandom();
	
	/**
	 * Sets an offset which will be used in all future calls to
	 * reset().
	 * @param offset A number which will be added to the seed for the
	 * random number generator.
	 */
	void setRandomOffset(int offset);
}
