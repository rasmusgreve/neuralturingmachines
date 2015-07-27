package dk.itu.ejuuragr.turing;

import java.util.Arrays;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.BaseController;
import dk.itu.ejuuragr.fitness.Utilities;

/**
 * A Controller which adds a Turing Machine to the 
 * simulation and uses outputs from the NN to to TM (write)
 * and gives its output (read) to the inputs of the NN.
 * This way the NN has the capability to save data in
 * memory.
 * @author Emil
 *
 */
public class TuringController extends BaseController {

	protected TuringMachine tm;

	/**
	 * The required constructor for instantiation via reflection
	 * from the properties file.
	 * @param props The properties where it can read everything.
	 * @param sim The simulator of the domain to behave in.
	 */
	public TuringController(Properties props, Simulator sim) {
		super(props,sim);
		// Initialize everything (using properties)
		this.tm = (TuringMachine) Utilities.instantiateObject(props.getProperty("tm.class"), new Object[]{props}, null);
	}

	@Override
	public double[] processOutputs(double[] fromNN) {
		double[] result = Utilities.flatten(tm.processInput(fromNN));
		
//		System.out.println("fromNN: "+Arrays.toString(fromNN));
//		System.out.println("toNN: "+Arrays.toString(result));
		
		return result;
	}

	@Override
	public double[] getInitialInput() {
		return Utilities.flatten(tm.getDefaultRead());
	}

	@Override
	public void reset() {
		this.tm.reset();
	}
	
	public TuringMachine getTuringMachine() {
		return tm;
	}
}
