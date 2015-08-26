package dk.itu.ejuuragr.run;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.FitnessEvaluator;
import dk.itu.ejuuragr.turing.TuringController;
import dk.itu.ejuuragr.turing.TuringMachine;

public class Evolver {

	public static void main(String[] args) throws Throwable {
		Properties props = new Properties(args[0]);
		if (!props.containsKey("random.seed")){
			System.out.println("Properties contain no random seed!");
			System.out.println("Enter desired random seed:");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String seed = br.readLine();
			props.setProperty("random.seed", seed);
			props.setProperty("log4j.appender.A1.File", "./db/log" + seed + ".txt");
			props.setProperty("persistence.base.dir", "./db"+seed);
		}
		
		
		com.anji.neat.Evolver evolver = new com.anji.neat.Evolver();
		
		TuringController controller = FitnessEvaluator.loadController(props);
		Simulator sim = controller.getSimulator();
		TuringMachine tm = controller.getTuringMachine();
		
		props.setProperty("stimulus.size", String.valueOf(tm.getOutputCount() + sim.getOutputCount() + 1)); //Plus a bias input that is always 1
		props.setProperty("response.size", String.valueOf(tm.getInputCount() + sim.getInputCount()));
		
		if(props.getIntProperty("random.seed",-1) == -1) {
			props.setProperty("random.seed",String.valueOf(new Random().nextInt()));
		}
		
		evolver.init(props);
		evolver.run();
		System.exit(0);
	}

}
