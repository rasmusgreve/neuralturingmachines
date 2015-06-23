package fitness;

import java.util.List;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import turing.TuringController;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import domain.Simulator;
import domain.tmaze.ForwardSimulator;

public class FitnessEvaluator implements BulkFitnessFunction, Configurable {
	private static final long serialVersionUID = 1L;
	
	ActivatorTranscriber activatorFactory;
	private TuringController controller;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	final public void evaluate(List arg0) {
		List<Chromosome> list = (List<Chromosome>)arg0;
		for (Chromosome chromosome : list){
			controller.reset();
			try {
				int score = controller.evaluate(activatorFactory.newActivator(chromosome));
				chromosome.setFitnessValue(score);
				
			} catch (TranscriberException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public int getMaxFitnessValue() {
		return controller.getSimulator().getMaxScore();
	}

	@Override
	public void init(Properties properties){
		//Load properties
		activatorFactory = (ActivatorTranscriber)properties.singletonObjectProperty(ActivatorTranscriber.class);
		
		//Initialize
		Simulator simulator = new ForwardSimulator(); //; //TODO: Initialize using reflection wooo
		controller = new TuringController(properties, simulator);
	}
}
