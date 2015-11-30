package dk.itu.ejuuragr.fitness;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;

/**
 * A simple controller which just activates the simulator
 * and nothing else. The numbers in the properties file
 * obviously still have to reflect this (same number of
 * stimulus and responses as the simulator requires)
 * 
 * @author Emil
 *
 */
public class SimpleController extends BaseController {

	public SimpleController(Properties props, Simulator sim) {
		super(props, sim);
	}

	@Override
	public void reset() {
		/* Nothing to do */
	}

	@Override
	public double[] processOutputs(double[] fromNN) {
		return getInitialInput();
	}

	@Override
	public double[] getInitialInput() {
		return new double[]{};
	}

}
