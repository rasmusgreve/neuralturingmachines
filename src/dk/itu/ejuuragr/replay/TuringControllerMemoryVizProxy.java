package dk.itu.ejuuragr.replay;

import java.util.ArrayList;
import java.util.List;

import com.anji.integration.Activator;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.turing.GravesTuringMachine;
import dk.itu.ejuuragr.turing.TuringController;

public class TuringControllerMemoryVizProxy extends TuringController{

	List<TimeStep> timeSteps = new ArrayList<TimeStep>();
	
	TimeStep currentTimeStep = new TimeStep();
	
	public TuringControllerMemoryVizProxy(Properties props, Simulator sim) {
		super(props, sim);
		iterations = 1; //Overwrite iteration count when replaying, since we don't care about properties
		((GravesTuringMachine) tm).setRecordTimeSteps(true);
		// TODO: HACK TO ONLY WORK FOR GRAVES
	}
	
	public List<TimeStep> getSteps(){
		return timeSteps;
	}
	
	@Override
	public double[] processOutputs(double[] fromNN) {
		double[] result = super.processOutputs(fromNN);
		
		//Catch tm step
		currentTimeStep.setTuringStep(((GravesTuringMachine) tm).getLastTimeStep());
		
		//Store and get ready for next step
		timeSteps.add(currentTimeStep);
		currentTimeStep = new TimeStep();
		
		return result;
	}
	
	@Override
	protected double[] activateNeuralNetwork(Activator nn, double[] domainInput, double[] controllerInput) {
		double[] neuralNetworkOutput = super.activateNeuralNetwork(nn, domainInput, controllerInput);
		
		currentTimeStep.setDomainInput(domainInput); //Catch domain input
		
		return neuralNetworkOutput;
	};

}
