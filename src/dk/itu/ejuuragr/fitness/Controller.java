package dk.itu.ejuuragr.fitness;

import com.anji.integration.Activator;

import dk.itu.ejuuragr.domain.Simulator;

/**
 * Is responsible for progressing the neural network
 * through a simulator.
 */
public interface Controller {

	/**
	 * From a neural network and a Simulator it should
	 * perform some simulation and return the final
	 * fitness score of the test.
	 * @param An activator for the neural network to evaluate
	 */
	double evaluate(Activator nn);
	
	/**
	 * Reset the controller to some initial state
	 */
	void reset();

	/**
	 * @return A reference to the internally used simulator
	 */
	Simulator getSimulator();

	/**
	 * Should calculate the maximum possible score based
	 * on what the Simulator says and number of iterations.
	 * @return
	 */
	int getMaxScore();
}
