package dk.itu.ejuuragr.fitness;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.jgapcustomised.Allele;
import org.jgapcustomised.Chromosome;

import com.anji_ahni.integration.Activator;
import com.anji_ahni.neat.NeuronAllele;
import com.ojcoleman.ahni.evaluation.BulkFitnessFunctionMT;
import com.ojcoleman.ahni.hyperneat.Properties;
import com.ojcoleman.ahni.util.Point;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.Utilities.ActivatorProxy;

public class HyperTMazeEvaluator extends BulkFitnessFunctionMT {

	private static final long serialVersionUID = 1L;
	private static final String SIMULATOR_COPYTASK = "dk.itu.ejuuragr.domain.CopyTask";
	private static final String SIMULATOR_TMAZE = "dk.itu.ejuuragr.domain.tmaze.TMaze";
	private static final String SIMULATOR_ROUNDS_TMAZE = "dk.itu.ejuuragr.domain.tmaze.RoundsTMaze";
	private static final String SIMULATOR_PERMUTATIONS_TMAZE = "dk.itu.ejuuragr.domain.tmaze.PermutationsTMaze";
	
//	private RoundsTMaze tmaze;
//	private TuringController controller;
	private Properties properties;
	private com.anji.util.Properties anjiProps;
	private int m;
	private int hidden;
	private boolean turnsignal;
	private String simulator;
	
	
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
		
		this.simulator = props.getProperty("simulator.class");
		this.m = props.getIntProperty("tm.m");
		this.hidden = props.getIntProperty("ann.topology.num.hidden.neurons", 0);
		this.turnsignal = props.getBooleanProperty("simulator.tmaze.turnsignal", false);
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
	
	private NeuronAllele findWithID(SortedSet<Allele> alleles, Long srcNeuronId) {
		for(Allele a : alleles) {
			if(a.getInnovationId() == srcNeuronId) {
				return (NeuronAllele)a;
			}
		}
		return null;
	}


	@Override
	public int[] getLayerDimensions(int layer, int totalLayerCount) {
		switch(this.simulator) {
		case SIMULATOR_TMAZE:
		case SIMULATOR_ROUNDS_TMAZE:
		case SIMULATOR_PERMUTATIONS_TMAZE:
			return getTMazeDimensions(layer, totalLayerCount);
		case SIMULATOR_COPYTASK:
			return getCopyTaskDimensions(layer, totalLayerCount);
		}
		System.out.println("No simulator match: "+this.simulator);
		return null;
	}

	private int[] getCopyTaskDimensions(int layer, int totalLayerCount) {
		if (layer == 0){ // Input layer.
			// CopyTask: start-bit, delimiter-bit, m inputs
			// TM: m (read vector)
			return new int[] { 2*this.m + 2, 1};
		} else if (this.hidden > 0 && layer == 1) {
			return new int[] { this.hidden, 1};
		} else if (layer == totalLayerCount - 1) { // Output layer.
			// CopyTask: m outputs
			// TM: m (write vector), 5 TM control (W,J,L,S,R)
			return new int[] { this.m + this.m + 5, 1};
		}
		return null;
	}


	private int[] getTMazeDimensions(int layer, int totalLayerCount) {
		if (layer == 0){ // Input layer.
			// 3 range sensors plus reward plus TM 1+2 (or whatever m is) + possible turn signal.
			return new int[] { 3+1 + this.m + (this.turnsignal ? 1 : 0), 1 };
		} else if (this.hidden > 0 && layer == 1) {
			return new int[] { this.hidden, 1};
		} else if (layer == totalLayerCount - 1) { // Output layer.
			return new int[] { 1+this.m+5, 1}; // 1 Domain (S), 2 TM data, 5 TM control (W,J,L,S,R)
		}
		return null;
	}


	@Override
	public Point[] getNeuronPositions(int layer, int totalLayerCount) {
		switch(this.simulator) {
		case SIMULATOR_TMAZE:
		case SIMULATOR_ROUNDS_TMAZE:
		case SIMULATOR_PERMUTATIONS_TMAZE:
			return tmazeMethodTwo(layer, totalLayerCount);
		case SIMULATOR_COPYTASK:
			return getCopyTaskNeuronPositions(layer, totalLayerCount);
		}
		return null;
	}

	private Point[] getCopyTaskNeuronPositions(int layer, int totalLayerCount) {
		// Coordinates are given in unit ranges and translated to whatever range is specified by the
		// experiment properties.
		int inLayer = getCopyTaskDimensions(layer,totalLayerCount)[0];
		if(inLayer == 0) return null;
		Point[] positions = new Point[inLayer];
		
		if (layer == 0) { // Input layer.
			positions[0] = new Point(0, 0, 0); // start
			positions[1] = new Point(1, 0, 0); // delimiter
			this.setEqually(positions, 2, this.m, 1.0/6, 0); // ct input
			this.setEqually(positions, 2+this.m, this.m, 2.0/6, 0); // tm read

		} else if (this.hidden > 0 && layer == 1) {
			this.setEqually(positions, 0, this.hidden, 0.5, 0.5);
			
		}else if (layer == totalLayerCount - 1) { // Output layer.
			this.setEqually(positions, 0, this.m, 5.0/6, 1.0); // ct output
			this.setEqually(positions, this.m, this.m, 1.0, 1.0); // tm write
			positions[2*this.m] = new Point(0.0, 1.0, 1.0); // write
			positions[2*this.m + 1] = new Point(1.0, 1.0, 1.0); // jump
			positions[2*this.m + 2] = new Point(1.0/4, 1.0, 1.0); // shift L
			positions[2*this.m + 3] = new Point(2.0/4, 1.0, 1.0); // shift S
			positions[2*this.m + 4] = new Point(3.0/4, 1.0, 1.0); // shift R
		}
		return positions;
	}
	
	private void setEqually(Point[] array, int startIndex, int number, double y, double z) {
		double space = 1.0 / (number-1);
		for(int i = startIndex; i < number; i++) {
			array[1+i] = new Point(number == 1 ? 0.5 : space*i, y, z);
		}
	}


	private Point[] tmazeMethodOne(int layer, int totalLayerCount) {
		// Coordinates are given in unit ranges and translated to whatever range is specified by the
		// experiment properties.
		Point[] positions = null;
		
		if (layer == 0) { // Input layer.
			positions = new Point[4 + this.m + (this.turnsignal ? 1 : 0)];
			positions[0] = new Point(0,   0, 0);	// sensor L
			positions[1] = new Point(0.5, 0, 0);	// sensor S
			positions[2] = new Point(1,   0, 0);	// sensor R
			positions[3] = new Point(0.5, 0.4, 0);	// Reward
			if(this.turnsignal)
				positions[4] = new Point(0.5, 0.1, 0);
			
			double space = 1.0 / (this.m-1);
			for(int i = 0; i < this.m; i++) {		// TM read vector
				positions[4+(this.turnsignal ? 1 : 0)+i] = new Point(this.m == 1 ? 0.5 : space*i, 0.25, 0);
			}

		} else if (this.hidden > 0 && layer == 1) {
			positions = new Point[this.hidden];
			
			double space = 1.0 / (this.hidden-1);
			for(int i = 0; i < this.hidden; i++) {
				positions[i] = new Point(this.hidden == 1 ? 0.5 : space*i, 0.5, 0.5);
			}
			
		}else if (layer == totalLayerCount - 1) { // Output layer.
			positions = new Point[1+5+this.m];
			positions[0] = new Point(0.5,  0.6, 1);			// steer
			
			double space = 1.0 / (this.m-1);
			for(int i = 0; i < this.m; i++) {				// TM write vector
				positions[1+i] = new Point(this.m == 1 ? 0.5 : space*i, 0.75, 1);
			}
			positions[1+this.m  ] = new Point(0,    1, 1);	// write control
			positions[1+this.m+1] = new Point(1,    1, 1);	// jump control
			positions[1+this.m+2] = new Point(0.4,  1, 1);	// shift L
			positions[1+this.m+3] = new Point(0.5,  1, 1);	// shift S
			positions[1+this.m+4] = new Point(0.6,  1, 1);	// shift R
		}
		return positions;
	}
	
	private Point[] tmazeMethodTwo(int layer, int totalLayerCount) {
		// Coordinates are given in unit ranges and translated to whatever range is specified by the
		// experiment properties.
		Point[] positions = null;
		
		if (layer == 0) { // Input layer.
			positions = new Point[4 + this.m + (this.turnsignal ? 1 : 0)];
			positions[0] = new Point(0.25,	0.25, 0);	// sensor L
			positions[1] = new Point(0.5,	0.25, 0);	// sensor S
			positions[2] = new Point(0.75,	0.25, 0);	// sensor R
			positions[3] = new Point(0.25,	0,    0);	// Reward
			if(this.turnsignal)
				positions[4] = new Point(0.75, 0, 0);
			
			double space = 1.0 / (this.m-1);
			for(int i = 0; i < this.m; i++) {		// TM read vector
				positions[4+(this.turnsignal ? 1 : 0)+i] = new Point(this.m == 1 ? 0.5 : space*i, 0.25, 0);
			}

		} else if (this.hidden > 0 && layer == 1) {
			positions = new Point[this.hidden];
			
			double space = 1.0 / (this.hidden-1);
			for(int i = 0; i < this.hidden; i++) {
				positions[i] = new Point(this.hidden == 1 ? 0.5 : space*i, 0.5, 1);
			}
			
		}else if (layer == totalLayerCount - 1) { // Output layer.
			positions = new Point[1+5+this.m];
			positions[0] = new Point(0.5,  1, 1);			// steer
			
			double space = 1.0 / (this.m-1);
			for(int i = 0; i < this.m; i++) {				// TM write vector
				positions[1+i] = new Point(this.m == 1 ? 0.5 : space*i, 0.75, 1);
			}
			positions[1+this.m  ] = new Point(0.25,	1, 1);	// write control
			positions[1+this.m+1] = new Point(0.75,	1, 1);	// jump control
			positions[1+this.m+2] = new Point(0.25,	0.75, 1);	// shift L
			positions[1+this.m+3] = new Point(0.5,	0.75, 1);	// shift S
			positions[1+this.m+4] = new Point(0.75,	0.75, 1);	// shift R
		}
		System.out.println(positions.toString());
		return positions;
	}
}
