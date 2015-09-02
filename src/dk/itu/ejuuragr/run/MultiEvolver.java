package dk.itu.ejuuragr.run;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.FitnessEvaluator;
import dk.itu.ejuuragr.turing.TuringController;
import dk.itu.ejuuragr.turing.TuringMachine;

public class MultiEvolver {

	public static void main(String[] args) throws Throwable {
		final java.util.Properties props = new java.util.Properties();
		props.load(ClassLoader.getSystemResourceAsStream(args[0]));

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter elemsize:");
		int elem = Integer.parseInt(br.readLine());
		int m = elem + 2;
		props.setProperty("simulator.copytask.element.size", ""+elem);
		props.setProperty("tm.m", ""+m);
		
		
		System.out.println("Enter seed:");
		String seed = br.readLine();
		props.setProperty("random.seed", seed);
		
		System.out.println("Starting evolution of elem="+elem + ", m="+m+" seed="+seed);
		
		runEvolution(new Properties(props));
	}

	public static void runEvolution(Properties props) throws Exception {
		com.anji.neat.Evolver evolver = new com.anji.neat.Evolver();

		TuringController controller = FitnessEvaluator.loadController(props);
		Simulator sim = controller.getSimulator();
		TuringMachine tm = controller.getTuringMachine();

		props.setProperty("stimulus.size", String.valueOf(tm.getOutputCount() + sim.getOutputCount() + 1)); // Plus a bias input that is always 1
		props.setProperty("response.size", String.valueOf(tm.getInputCount() + sim.getInputCount()));

		if (props.getIntProperty("random.seed", -1) == -1) {
			props.setProperty("random.seed", String.valueOf(new Random().nextInt()));
		}

		evolver.init(props);
		evolver.run();
		System.exit(0);
	}

}
