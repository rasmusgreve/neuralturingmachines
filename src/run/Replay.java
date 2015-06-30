package run;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jgap.Chromosome;

import turing.TuringController;
import turing.TuringMachine.TuringTimeStep;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import domain.RPSSimulator;
import domain.Simulator;
import fitness.Controller;
import graph.ReplayVisualizer;

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
		props.setProperty("base.dir", "./anji_2_01/db");
		
		//Loading chromosome
		FilePersistence db = new FilePersistence();
		db.init(props);
		Chromosome chrom = db.loadChromosome(chromosomeId, new DummyConfiguration());
		
		//Setup activator
		ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class);
		Activator activator = activatorFactory.newActivator(chrom);
	
		
		//Simulator and controller
		Simulator sim = new RPSSimulator(props);
		TuringControllerProxy con = new TuringControllerProxy(props, sim);

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
	
	public static class TimeStep{
		TuringTimeStep turingStep;
		double[] domainInput;
		//double[] domainOuput; //TODO: Might be interesting at some point
		public TuringTimeStep getTuringStep() {
			return turingStep;
		}
		public void setTuringStep(TuringTimeStep turingStep) {
			this.turingStep = turingStep;
		}
		public double[] getDomainInput() {
			return domainInput;
		}
		public void setDomainInput(double[] domainInput) {
			this.domainInput = domainInput;
		}
		
	}
	
	private static class TuringControllerProxy extends TuringController{

		List<TimeStep> timeSteps = new ArrayList<TimeStep>();
		
		TimeStep currentTimeStep = new TimeStep();
		
		public TuringControllerProxy(Properties props, Simulator sim) {
			super(props, sim);
			iterations = 1; //Overwrite iteration count when replaying, since we don't care about properties
			tm.setRecordTimeSteps(true);
		}
		
		public List<TimeStep> getSteps(){
			return timeSteps;
		}
		
		
		@Override
		public double[] processOutputs(double[] fromNN) {
			double[] result = super.processOutputs(fromNN);
			
			//Catch tm step
			currentTimeStep.setTuringStep(tm.getLastTimeStep());
			
			//Store and get ready for next step
			timeSteps.add(currentTimeStep);
			currentTimeStep = new TimeStep();
			
			return result;
		}
		
		@Override
		protected double[] activateNeuralNetwork(Activator nn, double[] domainInput, double[] controllerInput) {
			double[] neuralNetworkOutput = super.activateNeuralNetwork(nn, domainInput, controllerInput);
			
			currentTimeStep.setDomainInput(domainInput); //Catch domain input
			
			return neuralNetworkOutput;
		};

	}
	
}
