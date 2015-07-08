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
	protected int inputTotal;
	protected int iterations;

	public BaseController(Properties props, Simulator sim){
		this.sim = sim;
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
		sim.reset();
		
		// For each iteration
		for(int i = 0; i < iterations; i++) {
			this.reset();
			sim.restart();
			
			double[] controllerOutput = this.getInitialInput();
			double[] simOutput = sim.getInitialObservation();
			
			while(!sim.isTerminated() /*&& step < maxSteps*/){
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

	protected double[] activateNeuralNetwork(Activator nn, double[] domainInput, double[] controllerInput){
		double[] input = new double[inputTotal];
		Utilities.copy(domainInput,input,0);
		Utilities.copy(controllerInput,input,domainInput.length);
		
		return nn.next(input);
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
