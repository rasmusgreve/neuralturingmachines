package dk.itu.ejuuragr.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.Controller;
import dk.itu.ejuuragr.fitness.Utilities;

public class Evaluator {

	public static final int NUMBER_OF_TESTS = 100_000;
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0){
			args = getArgsFromStdIn();
		}
		
		//Setup
		String propsFilename = args.length > 0 ? args[0] : prompt("Properties filename: ");
		if(!propsFilename.endsWith(".properties"))
			propsFilename += ".properties";
		Properties props = new Properties(propsFilename);
		props.setProperty("base.dir", "./db");
		props.setProperty("controller.iterations", "1");
		Chromosome chrom = loadChromosome(args.length > 1 ? args[1] : prompt("Chromosome ID: "), props);
		
		
		//Setup activator
		ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class);
		Activator activator = activatorFactory.newActivator(chrom);
	
		//Initiate simulator and controller from properties to test their types
		Simulator simulator = (Simulator) Utilities.instantiateObject(props.getProperty("simulator.class"),new Object[]{props},null);
		Controller controller = (Controller) Utilities.instantiateObject(props.getProperty("controller.class"),new Object[]{props,simulator}, new Class<?>[]{Properties.class,Simulator.class});
	
		SummaryStatistics stats = new SummaryStatistics();
		
		for (int run = 0; run < NUMBER_OF_TESTS; run++){
			controller.getSimulator().setRandomOffset(run);
			stats.addValue(controller.evaluate(activator) / (1.0 * controller.getMaxScore()));
			if (run % (NUMBER_OF_TESTS / 100) == 0)
				System.out.println(run*1.0/NUMBER_OF_TESTS*100 + "%");
		}
		
		System.out.println("All done, " + NUMBER_OF_TESTS + " runs! Results:");
		System.out.printf("[%f - %f] mean: %f +- %f\n", stats.getMin(), stats.getMax(), stats.getMean(), stats.getStandardDeviation());
		
	}
	
	
	
	private static String prompt(String string) {
		System.out.println(string);
		try {
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Chromosome loadChromosome(String id, Properties props){
		FilePersistence db = new FilePersistence();
		db.init(props);
		return db.loadChromosome(id, new DummyConfiguration());
	}
	
	private static String[] getArgsFromStdIn() throws IOException{
		String[] result = new String[2];
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Properties filename: ");
		result[0] = br.readLine();
		System.out.println("Chromosome ID: ");
		result[1] = br.readLine();
		return result;
	}
}
