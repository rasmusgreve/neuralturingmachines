package dk.itu.ejuuragr.run;

import java.util.Random;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.FitnessEvaluator;
import dk.itu.ejuuragr.turing.TuringController;
import dk.itu.ejuuragr.turing.TuringMachine;

public class Evolver {

	public static void main(String[] args) throws Throwable {
		Properties props = new Properties(args[0]);
		com.anji.neat.Evolver evolver = new com.anji.neat.Evolver();
		
		TuringController controller = FitnessEvaluator.loadController(props);
		Simulator sim = controller.getSimulator();
		TuringMachine tm = controller.getTuringMachine();
		
		props.setProperty("stimulus.size", String.valueOf(tm.getOutputCount() + sim.getOutputCount() + 1)); //Plus a bias input that is always 1
		props.setProperty("response.size", String.valueOf(tm.getInputCount() + sim.getInputCount()));
		
		if(props.getIntProperty("rand.seed",-1) == -1) {
			props.setProperty("random.seed",String.valueOf(new Random().nextInt()));
		}
		
		evolver.init(props);
		evolver.run();
		System.exit(0);
	}

}
