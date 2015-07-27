package dk.itu.ejuuragr.fitness;

import java.util.Arrays;

import com.anji.integration.Activator;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;

/**
 * Abstracts the flow of evaluating away from subclasses
 * who just have to handle their part of the process
 * @author Emil
 *
 */
public abstract class BaseController implements Controller {
	
	protected Simulator sim;
	protected int iterations;

	public BaseController(Properties props, Simulator sim){
		this.sim = sim;
		this.iterations = props.getIntProperty("controller.iterations", 10);
	}
	
	@Override
	public Simulator getSimulator() {
		return sim;
	}

	@Override
	public int evaluate(Activator nn) {
		int totalScore = 0;
		sim.reset();
		
		// For each iteration
		for(int i = 0; i < iterations; i++) {
			this.reset();
			sim.restart();
			
			double[] controllerOutput = this.getInitialInput();
			double[] simOutput = sim.getInitialObservation();
			
			while(!sim.isTerminated()){
				double[] nnOutput = this.activateNeuralNetwork(nn, simOutput, controllerOutput);
				
				simOutput = this.getSimulationResponse(Arrays.copyOfRange(nnOutput, 0, sim.getInputCount()));
				controllerOutput = this.getControllerResponse(Arrays.copyOfRange(nnOutput, sim.getInputCount(), nnOutput.length));
			}

			totalScore += sim.getCurrentScore();
		}
		
		int result = Math.max(0, totalScore);
		return result;
	}
	
	@Override
	public int getMaxScore() {
		return sim.getMaxScore() * iterations;
	}

	protected double[] activateNeuralNetwork(Activator nn, double[] domainInput, double[] controllerInput) {
//		System.out.println("Activate from Domain: "+Utilities.toString(domainInput));
//		System.out.println("Activate from Controller: "+Utilities.toString(controllerInput));
//		System.out.println("Wanted length = "+inputTotal);
		
		double[] input = new double[domainInput.length + controllerInput.length];
		Utilities.copy(domainInput,input,0);
		Utilities.copy(controllerInput,input,domainInput.length);
		
		// Cap values at 1
		cleanInput(input);
		
		return nn.next(input);
	}
	
	private void cleanInput(double[] array) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] > 1.0) {
//				System.out.printf("WARNING: Input value (%f, index=%d) to NN is greater than 1.0, truncating.",array[i],i);
//				System.out.println();
				array[i] = 1.0;
			}
		}
	}

	protected double[] getSimulationResponse(double[] neuralNetworkDomainOutput){
		return sim.performAction(neuralNetworkDomainOutput);
	}
	
	protected double[] getControllerResponse(double[] neuralNetworkControllerOutput){
		return processOutputs(neuralNetworkControllerOutput);
	}
	
	public abstract double[] processOutputs(double[] fromNN);
	
	public abstract double[] getInitialInput();

}
