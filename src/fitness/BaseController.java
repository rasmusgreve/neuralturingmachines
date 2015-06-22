package fitness;

import com.anji.integration.Activator;
import com.anji.util.Properties;

import domain.Simulator;

/**
 * Abstracts the flow of evaluating away from subclasses
 * who just have to handle their part of the process
 * @author Emil
 *
 */
public abstract class BaseController implements Controller {
	
	private Simulator sim;
	private int inputTotal;
	private int controllerInputs;
	private int outputTotal;
	private int controllerOutputs;
	private int maxIterations;

	public BaseController(Properties props, Simulator sim){
		this.sim = sim;
		this.maxIterations = props.getIntProperty("controller.iterations.max");
		
		// how many inputs for controller and simulator?
		this.inputTotal = props.getIntProperty("stimulus.size");
		this.controllerInputs = inputTotal - sim.getOutputCount();
				
		this.outputTotal = props.getIntProperty("response.size");
		this.controllerOutputs = outputTotal - sim.getInputCount();
	}

	@Override
	public int evaluate(Activator nn) {
		double[] controllerOutput = this.getInitialInput();
		double[] simOutput = sim.getInitialObservation();
		
		// For each iteration
		int iteration = 0;
		while(!sim.isTerminated() && iteration < maxIterations){
			// 1: Take input from sim and turing
			double[] input = new double[inputTotal];
			copy(simOutput,input,0);
			copy(controllerOutput,input,simOutput.length);
			
			// TODO: NÅEDE HERTIL
			
			// 2: Activate
			// 3: Take output of nn to Turing and Sim respectively
			
			iteration++;
		}

		return -1;
	}
	
	private void copy(double[] fromArray, double[] toArray, int offset) {
		if(toArray.length < fromArray.length + offset)
			throw new IndexOutOfBoundsException("Too much content in fromArray for the toArray and offset");
		
		for(int i = 0; i < fromArray.length; i++){
			toArray[offset + i] = fromArray[i];
		}
	}
	
	private void copy(double[][] fromArrays, double[] toArray, int offset) {
		if(toArray.length < totalLength(fromArrays) + offset)
			throw new IndexOutOfBoundsException("Too much content in fromArrays for the toArray and offset");
		
		int index = offset;
		for(int i = 0; i < fromArrays.length; i++){
			for(int j = 0; i < fromArrays[i].length; j++){
				toArray[index++] = fromArrays[i][j];
			}
		}
	}

	private int totalLength(double[][] arrays) {
		int count = 0;
		for(int i = 0; i < arrays.length; i++)
			count += arrays[i].length;
		return count;
	}
	
	public abstract double[] processOutputs(double[] fromNN);
	
	public abstract double[] getInitialInput();

}
