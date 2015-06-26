package run;

import org.jgap.Chromosome;

import turing.TuringController;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import domain.RPSSimulator;
import domain.Simulator;
import fitness.Controller;

public class Replay {

	public static void main(String[] args) throws Exception {
		String chromosomeId = "11253";
		
		//Setup
		Properties props = new Properties("turingmachine.properties");
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
		Controller con = new TuringControllerProxy(props, sim);

		int fitness = con.evaluate(activator);
		System.out.println("Fitness: " + fitness);

	}
	
	private static class TuringControllerProxy extends TuringController{

		public TuringControllerProxy(Properties props, Simulator sim) {
			super(props, sim);
			iterations = 1;
		}
		
		@Override
		public double[] processOutputs(double[] fromNN) {
			double[] result = super.processOutputs(fromNN);
			
			displayTMActivation(fromNN, result);
			
			return result;
		}
		
		
		private void displayTMActivation(double[] fromNN, double[] result){

			double[][] readWeightings = tm.getReadWeightings();
			double[][] writeWeightings = tm.getWriteWeightings();

			System.out.println("-------- Activation ----------");
			
			for (int i = 0; i < writeWeightings.length; i++){
				System.out.printf("Write head #%d focus: ", i);
				for (int j = 0; j < readWeightings[i].length;j++){
					System.out.printf("%.2f ", readWeightings[i][j]);
				}
			}
			
			for (int i = 0; i < readWeightings.length; i++){
				System.out.printf("Read  head #%d focus: ", i);
				for (int j = 0; j < readWeightings[i].length;j++){
					System.out.printf("%.2f ", readWeightings[i][j]);
				}
				System.out.println();
			}
			
			System.out.println("Result:");
			for (int i = 0; i < result.length; i++){
				System.out.print(result[i] + " ");
			}
			System.out.println();
			
		}
	}
	
}
