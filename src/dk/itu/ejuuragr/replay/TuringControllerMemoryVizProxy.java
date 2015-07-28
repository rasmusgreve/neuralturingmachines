package dk.itu.ejuuragr.replay;

import java.util.ArrayList;
import java.util.List;

import com.anji.integration.Activator;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.turing.GravesTuringMachine;
import dk.itu.ejuuragr.turing.GravesTuringMachineSimplified;
import dk.itu.ejuuragr.turing.TuringController;

public class TuringControllerMemoryVizProxy extends TuringController{

	List<TimeStep> timeSteps = new ArrayList<TimeStep>();
	
	TimeStep currentTimeStep = new TimeStep();
	
	public TuringControllerMemoryVizProxy(Properties props, Simulator sim) {
		super(props, sim);
		iterations = 1; //Overwrite iteration count when replaying, since we don't care about properties
		
		// TODO: HACK TO ONLY WORK FOR GRAVES
		if (tm instanceof GravesTuringMachine){
			GravesTuringMachine gtm = (GravesTuringMachine)tm;
			gtm.setRecordTimeSteps(true);
			
			TimeStep first = new TimeStep();
			first.setTuringStep(gtm.getInitialTimeStep());
			first.setDomainInput(sim.getInitialObservation());
			first.setTuringMachineContent(tm.getTapeValues());
			timeSteps.add(first);
		}
		
		if (tm instanceof GravesTuringMachineSimplified){
			GravesTuringMachineSimplified gtm = (GravesTuringMachineSimplified)tm;
			gtm.setRecordTimeSteps(true);
			
			TimeStep first = new TimeStep();
			first.setTuringStep(gtm.getInitialTimeStep());
			first.setDomainInput(sim.getInitialObservation());
			first.setTuringMachineContent(tm.getTapeValues());
			timeSteps.add(first);
		}
	}
	
	public List<TimeStep> getSteps(){
		return timeSteps;
	}
	
	@Override
	public double[] processOutputs(double[] fromNN) {
		double[] result = super.processOutputs(fromNN);
		
		//Catch tm step
		if (tm instanceof GravesTuringMachine){
			currentTimeStep.setTuringStep(((GravesTuringMachine) tm).getLastTimeStep());
		}
		if (tm instanceof GravesTuringMachineSimplified){
			currentTimeStep.setTuringStep(((GravesTuringMachineSimplified) tm).getLastTimeStep());
		}
			
		currentTimeStep.setTuringMachineContent(tm.getTapeValues());
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
