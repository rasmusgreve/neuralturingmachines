package dk.itu.ejuuragr.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

	public static void main(String[] args) throws Exception {
		if (args.length == 0){
			args = getArgsFromStdIn();
		}
		
		//Setup
		String propsFilename = args.length > 0 ? args[0] : prompt("Properties filename: ");
		if(!propsFilename.endsWith(".properties"))
			propsFilename += ".properties";
		
		final Properties props = new Properties();
		File file = new File(propsFilename);
		if (!file.exists())
			props.load(ClassLoader.getSystemResourceAsStream(propsFilename));
		else
			props.load(new FileReader(propsFilename));
		
		
		props.setProperty("base.dir", "./db");
		props.setProperty("controller.iterations", "1");
		Chromosome chrom = loadChromosome(args.length > 1 ? args[1] : prompt("Chromosome ID: "), props);
		

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		
		ask(props,br,"simulator.copytask.element.size");
		ask(props,br,"tm.m");
		ask(props,br,"simulator.copytask.length.max");
		ask(props,br,"evaluator.num.tests");
		
		
		//Setup activator
		ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class);
		Activator activator = activatorFactory.newActivator(chrom);
	
		//Initiate simulator and controller from properties to test their types
		Simulator simulator = (Simulator) Utilities.instantiateObject(props.getProperty("simulator.class"),new Object[]{props},null);
		Controller controller = (Controller) Utilities.instantiateObject(props.getProperty("controller.class"),new Object[]{props,simulator}, new Class<?>[]{Properties.class,Simulator.class});
	
		
		
		int numberOfTests = props.getIntProperty("evaluator.num.tests", 500_000);
		
		SummaryStatistics stats = new SummaryStatistics();
		
		for (int run = 0; run < numberOfTests; run++){
			controller.getSimulator().setRandomOffset(run);
			stats.addValue(controller.evaluate(activator) / (1.0 * controller.getMaxScore()));
			if (run % (numberOfTests / 100) == 0)
				System.out.println(run*1.0/numberOfTests*100 + "%");
		}
		
		System.out.println("All done, " + numberOfTests + " runs! Results:");
		System.out.printf("[%f - %f] mean: %f +- %f\n", stats.getMin(), stats.getMax(), stats.getMean(), stats.getStandardDeviation());
		FileWriter fw = new FileWriter(new File("evaluator_results.txt"));
		fw.write("Evaluator results");
		for (Object key : props.keySet()){
			fw.write(key.toString());
			fw.write(" : ");
			fw.write(props.get(key).toString());
			fw.write("\r\n");
		}
		fw.write("--------------------------------------------------\r\n");
		fw.write(String.format("[%f - %f] mean: %f +- %f\n", stats.getMin(), stats.getMax(), stats.getMean(), stats.getStandardDeviation()));
		fw.flush();
		fw.close();
	}
	
	public static void ask(java.util.Properties props, BufferedReader br, String key) throws IOException{
		if (!props.containsKey(key)){
			System.out.println("Enter " + key);
			props.setProperty(key, br.readLine());
		}
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
