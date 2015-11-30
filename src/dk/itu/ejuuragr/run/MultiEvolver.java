package dk.itu.ejuuragr.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.FitnessEvaluator;
import dk.itu.ejuuragr.turing.TuringController;
import dk.itu.ejuuragr.turing.TuringMachine;

/**
 * Our version of the ANJI evolver which is capable
 * of querying for missing properties so it is easy
 * to de deploy the evolutions on multiple systems
 * with different properties.
 * 
 * @author Emil
 *
 */
public class MultiEvolver {

	public static void main(String[] args) throws Throwable {
		final java.util.Properties props = new java.util.Properties();
		File file = new File(args[0]);
		if (!file.exists())
			props.load(ClassLoader.getSystemResourceAsStream(args[0]));
		else
			props.load(new FileReader(file));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		if (args.length > 1){
			props.setProperty("random.seed", args[1]);
			System.out.println("Setting seed from stdin: " + args[1]);
			
		}
		
		ask(props,br,"tm.m");
		ask(props,br,"popul.size");
		ask(props,br,"controller.iterations");
		ask(props,br,"weight.mutation.std.dev");
		ask(props,br,"random.seed");
		
		runEvolution(new Properties(props));
	}
	
	public static void ask(java.util.Properties props, BufferedReader br, String key) throws IOException{
		if (!props.containsKey(key)){
			System.out.println("Enter " + key);
			props.setProperty(key, br.readLine());
		}
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
		System.out.println("The errors below are not a problem :)");
		evolver.init(props);
		evolver.run();
		System.exit(0);
	}

}
