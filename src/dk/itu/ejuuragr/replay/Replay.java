package dk.itu.ejuuragr.replay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.RPSSimulator;
import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.Controller;
import dk.itu.ejuuragr.graph.ReplayVisualizer;
import dk.itu.ejuuragr.turing.TuringController;
import dk.itu.ejuuragr.turing.TuringMachine.TuringTimeStep;

public class Replay {

	public static void main(String[] args) throws Exception {
		String chromosomeId, propertiesFile;
		if (args.length == 0){
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Properties filename: ");
			propertiesFile = br.readLine();
			System.out.println("Chromosome ID: ");
			chromosomeId = br.readLine();
		}
		else
		{
			propertiesFile = args[0];
			chromosomeId = args[1];
		}
		
		//Setup
		Properties props = new Properties(propertiesFile); // "turingmachine.properties"
		props.setProperty("base.dir", "./db");
		
		//Loading chromosome
		FilePersistence db = new FilePersistence();
		db.init(props);
		Chromosome chrom = db.loadChromosome(chromosomeId, new DummyConfiguration());
		
		//Setup activator
		ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class);
		Activator activator = activatorFactory.newActivator(chrom);
	
		
		//Simulator and controller
		Simulator sim = new RPSSimulator(props);
		TuringControllerMemoryVizProxy con = new TuringControllerMemoryVizProxy(props, sim);

		int fitness = con.evaluate(activator);
		new ReplayVisualizer().show(con.getSteps());
	}
	
	private static void printActivation(Activator activator, double[] activation){
		System.out.println("Activating with " + arrayString(activation));
		System.out.println("Result: " + arrayString(activator.next(activation)));
	}
	
	private static String arrayString(double[] arr){
		StringBuilder sb = new StringBuilder("[");
		for (double d : arr)
			sb.append(d).append(" ,");
		return sb.substring(0, sb.length()-2) + "]";
	}
	
}
