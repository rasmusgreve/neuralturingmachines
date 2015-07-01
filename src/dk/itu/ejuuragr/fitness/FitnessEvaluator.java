package dk.itu.ejuuragr.fitness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.RPSSimulator;
import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.turing.TuringController;

public class FitnessEvaluator implements BulkFitnessFunction, Configurable {
	private static final long serialVersionUID = 1L;
	
	ActivatorTranscriber activatorFactory;
	private Controller controller;
	
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
		Simulator simulator = (Simulator) Utilities.instantiateObject(properties.getProperty("simulator.class"),new Object[]{properties},null);
		controller = (Controller) Utilities.instantiateObject(properties.getProperty("controller.class"),new Object[]{properties,simulator}, new Class<?>[]{Properties.class,Simulator.class});
	}
}