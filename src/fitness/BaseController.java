package fitness;

import java.util.Arrays;

import com.anji.integration.Activator;
import com.anji.util.Properties;

import fitness.Utilities;
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
	private int maxSteps;
	private int iterations;

	public BaseController(Properties props, Simulator sim){
		this.sim = sim;
		this.maxSteps = props.getIntProperty("controller.steps.max");
		this.iterations = props.getIntProperty("controller.iterations");
		
		// how many inputs for controller and simulator?
		this.inputTotal = props.getIntProperty("stimulus.size");
	}
	
	@Override
	public Simulator getSimulator() {
		return sim;
	}

	@Override
	public int evaluate(Activator nn) {
		int totalScore = 0;
		
		// For each iteration
		for(int i = 0; i < iterations; i++) {
			this.reset();
			sim.reset();
			
			double[] controllerOutput = this.getInitialInput();
			double[] simOutput = sim.getInitialObservation();
			
			int step = 0;
			while(!sim.isTerminated() && step < maxSteps){
				// 1: Take input from sim and controller
				double[] input = new double[inputTotal];
				Utilities.copy(simOutput,input,0);
				Utilities.copy(controllerOutput,input,simOutput.length);
				
				// 2: Activate
				double[] nnOutput = nn.next(input);
				
				// 3: Take output of nn to sim and controller respectively
				simOutput = sim.performAction(Arrays.copyOfRange(nnOutput, 0, sim.getInputCount()));
				controllerOutput = this.processOutputs(Arrays.copyOfRange(nnOutput, sim.getInputCount(), nnOutput.length));
				
				step++;
			}

			totalScore += sim.getCurrentScore();
		}
		return Math.max(0, totalScore / iterations);
	}
	
	public abstract double[] processOutputs(double[] fromNN);
	
	public abstract double[] getInitialInput();

}
