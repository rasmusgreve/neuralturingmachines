package fitness;

import com.anji.integration.Activator;

import domain.Simulator;

/**
 * Is responsible for progressing the neural network
 * through a simulator.
 * @author Schantz
 *
 */
public interface Controller {

	/**
	 * From a neural network and a Simulator it should
	 * perform some simulation and return the final
	 * fitness score of the test.
	 * @param nn
	 * @param sim
	 * @return
	 */
	int evaluate(Activator nn);
}
