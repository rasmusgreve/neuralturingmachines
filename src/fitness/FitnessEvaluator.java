package fitness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import turing.TuringController;

import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import domain.RPSSimulator;
import domain.Simulator;

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
		Simulator simulator = (Simulator) instantiateObject(properties.getProperty("simulator.class"),null,null);
		controller = (Controller) instantiateObject(properties.getProperty("controller.class"),new Object[]{properties,simulator}, new Class<?>[]{Properties.class,Simulator.class});
	}
	
	private static Object instantiateObject(String className, Object[] params, Class<?>[] constructor) {
		Object result = null;
		try {
			if(params == null || params.length == 0) {
				result = Class.forName(className).newInstance();
			} else {
				Constructor<?> con = Class.forName(className).getDeclaredConstructor(constructor == null ? getClasses(params) : constructor);
				result = con.newInstance(params);
			}
		} catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException | InstantiationException 
				| IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static Class<?>[] getClasses(Object[] objects) {
		Class<?>[] result = new Class[objects.length];
		for(int i = 0; i < objects.length; i++)
			result[i] = objects[i].getClass();
		return result;
	}
}
