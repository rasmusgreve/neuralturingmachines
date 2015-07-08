package dk.itu.ejuuragr.fitness;

import java.util.List;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;

public class FitnessEvaluator implements BulkFitnessFunction, Configurable {
	private static final long serialVersionUID = 1L;

	ActivatorTranscriber activatorFactory;
	private Controller controller;

	private int generation;

	private boolean toOffset = true;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	final public void evaluate(List arg0) {
		if(toOffset) controller.getSimulator().setRandomOffset(generation);
		
		List<Chromosome> list = (List<Chromosome>) arg0;
		for (Chromosome chromosome : list) {
			try {
				int score = controller.evaluate(activatorFactory.newActivator(chromosome));
				chromosome.setFitnessValue(score);

			} catch (TranscriberException e) {
				e.printStackTrace();
			}

		}
		generation++;
	}

	@Override
	public int getMaxFitnessValue() {
		int result = controller.getMaxScore();
//		System.out.println("Max Score: "+result);
		return result;
	}

	@Override
	public void init(Properties properties) {
		// Load properties
		activatorFactory = (ActivatorTranscriber) properties.singletonObjectProperty(ActivatorTranscriber.class);
		toOffset = properties.getBooleanProperty("simulate.generations.identical");

		// Initialize
		generation = 0;
		Simulator simulator = (Simulator) Utilities.instantiateObject(properties.getProperty("simulator.class"),
				new Object[] { properties }, null);
		controller = (Controller) Utilities.instantiateObject(properties.getProperty("controller.class"),
				new Object[] { properties, simulator }, new Class<?>[] { Properties.class, Simulator.class });
		simulator.reset();
		simulator.restart();
		controller.reset();
	}
}
