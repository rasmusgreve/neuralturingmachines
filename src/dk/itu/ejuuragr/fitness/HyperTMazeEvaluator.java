package dk.itu.ejuuragr.fitness;

import java.util.Iterator;
import java.util.Map.Entry;

import org.jgapcustomised.Chromosome;

import com.anji_ahni.integration.Activator;
import com.ojcoleman.ahni.evaluation.BulkFitnessFunctionMT;
import com.ojcoleman.ahni.hyperneat.Properties;
import com.ojcoleman.ahni.util.Point;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.domain.tmaze.RoundsTMaze;
import dk.itu.ejuuragr.fitness.Utilities.ActivatorProxy;
import dk.itu.ejuuragr.turing.TuringController;

public class HyperTMazeEvaluator extends BulkFitnessFunctionMT {

	private static final long serialVersionUID = 1L;
//	private RoundsTMaze tmaze;
//	private TuringController controller;
	private Properties properties;
	private com.anji.util.Properties anjiProps;
	
	
	private com.anji.util.Properties convertProps(Properties props){
		com.anji.util.Properties anjiProps = new com.anji.util.Properties();
		Iterator<Entry<Object, Object>> ite = props.entrySet().iterator();
		while(ite.hasNext()){
			Entry<Object, Object> item = ite.next();
			anjiProps.put(item.getKey(), item.getValue());
		}
		return anjiProps;
	}
	
	
	@Override
	public void init(Properties props) {
		super.init(props);
		this.properties = props;
		this.anjiProps = convertProps(props);
	}

	@Override
	protected double evaluate(Chromosome genotype, Activator substrate, int evalThreadIndex) {
		return _evaluate(genotype, substrate, null, false, false);
	}

	@Override
	public void evaluate(Chromosome genotype, Activator substrate, String baseFileName, boolean logText, boolean logImage) {
		_evaluate(genotype, substrate, baseFileName, logText, logImage);
	}	
		
	
	
	public double _evaluate(Chromosome genotype, Activator substrate, String baseFileName, boolean logText, boolean logImage) {
		Simulator tmaze = (Simulator) Utilities.instantiateObject(
				props.getProperty("simulator.class"),
				new Object[] { anjiProps }, new Class[]{com.anji.util.Properties.class});
		
		Controller controller = (Controller) Utilities.instantiateObject(props.getProperty("controller.class"),new Object[]{anjiProps,tmaze}, new Class<?>[]{com.anji.util.Properties.class,Simulator.class});
		
		ActivatorProxy proxy = new ActivatorProxy(substrate);
		//controller.reset();
		double fitness = controller.evaluate(proxy) / controller.getMaxScore();
		
		genotype.setFitnessValue(fitness);
		genotype.setPerformanceValue(fitness);
		
		return fitness;
	}
	
	@Override
	public int[] getLayerDimensions(int layer, int totalLayerCount) {
		//TODO: HARDCODED M=2
		if (layer == 0){ // Input layer.
			// 3 range sensors plus reward plus TM 1+2.
			return new int[] { 3+1+2, 1 };
		} else if (layer == totalLayerCount - 1) { // Output layer.
			return new int[] { 1+2+5, 1 }; // 1 Domain (S), 2 TM data, 5 TM control (W,J,L,S,R)
		}
		return null;
	}

	@Override
	public Point[] getNeuronPositions(int layer, int totalLayerCount) {
		// Coordinates are given in unit ranges and translated to whatever range is specified by the
		// experiment properties.
		Point[] positions = null;
		//TODO: HARDCODED M=2
		if (layer == 0) { // Input layer.
			positions = new Point[6];
			positions[0] = new Point(0,   0, 0);
			positions[1] = new Point(0.5, 0, 0);
			positions[2] = new Point(1,   0, 0);
			positions[3] = new Point(0.25, 0.25, 0);
			positions[4] = new Point(0.75, 0.25, 0);
			positions[5] = new Point(0.5, 0.4, 0);
//			positions[6] = new Point(0.5, 0.6, 0);
		} else if (layer == totalLayerCount - 1) { // Output layer.
			positions = new Point[8];
			positions[0] = new Point(0.5,  0.6, 0);
			positions[1] = new Point(0.25, 0.75, 0);
			positions[2] = new Point(0.75, 0.75, 0);
			positions[3] = new Point(0,    1, 0);
			positions[4] = new Point(1,    1, 0);
			positions[5] = new Point(0.4,  1, 0);
			positions[6] = new Point(0.5,  1, 0);
			positions[7] = new Point(0.6,  1, 0);

		}
		return positions;
	}

}