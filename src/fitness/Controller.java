package fitness;

import com.anji.integration.Activator;

import domain.Simulator;

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
	int evaluate(Activator nn);
	
	/**
	 * Reset the controller to some initial state
	 */
	void reset();

	/**
	 * @return A reference to the internally used simulator
	 */
	Simulator getSimulator();
}
