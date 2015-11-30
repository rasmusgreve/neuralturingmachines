package dk.itu.ejuuragr.run;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.FitnessEvaluator;
import dk.itu.ejuuragr.turing.TuringController;
import dk.itu.ejuuragr.turing.TuringMachine;

/**
 * Our version of the ANJI evolver which will
 * calculate the number of stimulus and response
 * neurons in the ANN based on the domain and TM
 * defined in properties.
 * 
 * @author Rasmus
 *
 */
public class Evolver {

	public static void main(String[] args) throws Throwable {
		java.util.Properties p1 = new java.util.Properties();
		p1.load(ClassLoader.getSystemResourceAsStream( args[0] ));
		String seedFromStdIn = (args.length < 2) ? null : args[1];
		if (!p1.containsKey("random.seed")){
			String seed;
			if (seedFromStdIn == null){
				System.out.println("Properties contain no random seed!");
				System.out.println("Enter desired random seed:");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				seed = br.readLine();
			}
			else
			{
				System.out.println("Loaded seed from std in: " + seedFromStdIn);
				seed = seedFromStdIn.toString();
			}
			p1.setProperty("random.seed", seed);
			p1.setProperty("log4j.appender.A1.File", "./db/log" + seed + ".txt");
			p1.setProperty("persistence.base.dir", "./db"+seed);
		}
		Properties props = new Properties(p1);
		
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
