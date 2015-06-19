package turing;

import com.anji.integration.Activator;

import domain.Simulator;
import fitness.Controller;

public class TuringController implements Controller {

	public TuringController() {
		// Initialize everything
	}

	@Override
	public int evaluate(Activator nn, Simulator sim) {
		// For each iteration
		// 1: Take input from Turing and Sim
		// 2: Activate
		// 3: Take output of nn to Turing and Sim respectively
		return -1;
	}

}
