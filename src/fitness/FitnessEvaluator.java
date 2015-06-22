package fitness;

import java.util.List;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import turing.TuringController;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import domain.Simulator;
import domain.tmaze.ForwardSimulator;

public class FitnessEvaluator implements BulkFitnessFunction, Configurable {
	private static final long serialVersionUID = 1L;
	
	ActivatorTranscriber activatorFactory;
	Simulator simulator;
	private TuringController controller;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	final public void evaluate(List arg0) {
		List<Chromosome> list = (List<Chromosome>)arg0;
		for (Chromosome chromosome : list){
			int i = 0;
			simulator.reset();
			double[] obs = simulator.initialObservation();
			Activator activator;
			try {
				activator = activatorFactory.newActivator(chromosome);
			} catch (TranscriberException e) {
				throw new RuntimeException(e);
			}
			while (!simulator.isTerminated() && i < 10){
				obs = simulator.performAction(activator.next(obs));
				i++;
			}
			chromosome.setFitnessValue(simulator.getCurrentScore());
		}
	}

	@Override
	public int getMaxFitnessValue() {
		return simulator.getMaxScore();
	}

	public void init(Properties properties){
		//Load properties
		activatorFactory = (ActivatorTranscriber)properties.singletonObjectProperty(ActivatorTranscriber.class);
		
		//Initialize
		simulator = new ForwardSimulator(); //; //TODO: Initialize using reflection wooo
		controller = new TuringController(properties, simulator);
	}
}
