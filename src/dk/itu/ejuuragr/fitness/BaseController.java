package dk.itu.ejuuragr.fitness;

import java.util.Arrays;

import com.anji.integration.Activator;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;

/**
 * Abstracts the flow of evaluating away from subclasses
 * who just have to handle their part of the process
 * 
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
	public double evaluate(Activator nn) {
		double totalScore = 0;
		sim.reset();
		int steps = 0;
		
		long time = System.currentTimeMillis();
		long nnTime = 0;
		long contTime = 0;
		long simTime = 0;
		
		// For each iteration
		for(int i = 0; i < iterations; i++) {
			this.reset();
			sim.restart();
			
			double[] controllerOutput = this.getInitialInput();
			double[] simOutput = sim.getInitialObservation();
			
			while(!sim.isTerminated()){
				time = System.currentTimeMillis();
				
				double[] nnOutput = this.activateNeuralNetwork(nn, simOutput, controllerOutput);
				
				nnTime += (System.currentTimeMillis() - time);
				time = System.currentTimeMillis();
				
				// CopyTask can rely on the TM acting first
				controllerOutput = this.getControllerResponse(Arrays.copyOfRange(nnOutput, sim.getInputCount(), nnOutput.length));
				
				contTime += (System.currentTimeMillis() - time);
				time = System.currentTimeMillis();
				
				simOutput = this.getSimulationResponse(Arrays.copyOfRange(nnOutput, 0, sim.getInputCount()));
				
				simTime += (System.currentTimeMillis() - time);
				time = System.currentTimeMillis();
				steps++;
			}

			totalScore += sim.getCurrentScore();
		}
		
		double result = Math.max(0.0, totalScore);
		return result;
	}
	
	@Override
	public int getMaxScore() {
		return sim.getMaxScore() * iterations;
	}

	protected double[] activateNeuralNetwork(Activator nn, double[] domainInput, double[] controllerInput) {
		
		double[] input = new double[domainInput.length + controllerInput.length + 1];
		Utilities.copy(domainInput,input,0);
		Utilities.copy(controllerInput,input,domainInput.length);
		input[input.length-1] = 1.0; // Bias node
		
		// Cap values at 1
		cleanInput(input); // FIXME: This might be a symptom in the GTM.
		
		return nn.next(input);
	}
	
	private void cleanInput(double[] array) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] > 1.0) {
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
