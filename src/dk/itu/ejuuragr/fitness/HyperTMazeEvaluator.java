package dk.itu.ejuuragr.fitness;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.jgapcustomised.Allele;
import org.jgapcustomised.Chromosome;
import org.jgapcustomised.ChromosomeMaterial;

import com.anji_ahni.integration.Activator;
import com.anji_ahni.neat.ConnectionAllele;
import com.anji_ahni.neat.NeuronAllele;
import com.anji_ahni.neat.NeuronType;
import com.ojcoleman.ahni.evaluation.BulkFitnessFunctionMT;
import com.ojcoleman.ahni.hyperneat.Properties;
import com.ojcoleman.ahni.util.Point;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.Utilities.ActivatorProxy;

public class HyperTMazeEvaluator extends BulkFitnessFunctionMT {

	private static final long serialVersionUID = 1L;
//	private RoundsTMaze tmaze;
//	private TuringController controller;
	private Properties properties;
	private com.anji.util.Properties anjiProps;
	private int m;
	private int hidden;
	
	
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
		
		this.m = props.getIntProperty("tm.m");
		this.hidden = props.getIntProperty("ann.topology.num.hidden.neurons", 0);
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
		if (layer == 0){ // Input layer.
			// 3 range sensors plus reward plus TM 1+2 (or whatever m is).
			return new int[] { 3+1+this.m, 1 };
		} else if (this.hidden > 0 && layer == 1) {
			return new int[] { this.hidden, 1};
		} else if (layer == totalLayerCount - 1) { // Output layer.
			return new int[] { 1+this.m+5, 1}; // 1 Domain (S), 2 TM data, 5 TM control (W,J,L,S,R)
		}
		return null;
	}

	@Override
	public Point[] getNeuronPositions(int layer, int totalLayerCount) {
		// Coordinates are given in unit ranges and translated to whatever range is specified by the
		// experiment properties.
		Point[] positions = null;
		
		if (layer == 0) { // Input layer.
			positions = new Point[4 + this.m];
			positions[0] = new Point(0,   0, 0);
			positions[1] = new Point(0.5, 0, 0);
			positions[2] = new Point(1,   0, 0);
			positions[3] = new Point(0.5, 0.4, 0);
			
			double space = 1.0 / (this.m-1);
			for(int i = 0; i < this.m; i++) {
				positions[4+i] = new Point(this.m == 1 ? 0.5 : space*i, 0.25, 0);
			}

		} else if (this.hidden > 0 && layer == 1) {
			positions = new Point[this.hidden];
			
			double space = 1.0 / (this.hidden-1);
			for(int i = 0; i < this.hidden; i++) {
				positions[i] = new Point(this.hidden == 1 ? 0.5 : space*i, 0.5, 0);
			}
			
		}else if (layer == totalLayerCount - 1) { // Output layer.
			positions = new Point[1+5+this.m];
			positions[0] = new Point(0.5,  0.6, 0);
			
			double space = 1.0 / (this.m-1);
			for(int i = 0; i < this.m; i++) {
				positions[1+i] = new Point(this.m == 1 ? 0.5 : space*i, 0.75, 0);
			}
			positions[1+this.m] = new Point(0,    1, 0);
			positions[1+this.m+1] = new Point(1,    1, 0);
			positions[1+this.m+2] = new Point(0.4,  1, 0);
			positions[1+this.m+3] = new Point(0.5,  1, 0);
			positions[1+this.m+4] = new Point(0.6,  1, 0);

		}
		return positions;
	}

}
