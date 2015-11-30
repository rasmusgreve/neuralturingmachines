package dk.itu.ejuuragr.replay;

import java.util.Arrays;

import com.anji.integration.Activator;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.turing.TuringController;

/**
 * A proxy for TuringController which saves
 * the time steps for the processes with the
 * Turing Machine to be replayed later.
 * 
 * @author Rasmus
 *
 */
public class TuringControllerRecorder extends TuringController{

	Recording recording = new Recording();
	
	@SuppressWarnings("rawtypes")
	TimeStep currentTimeStep = new TimeStep<>();
	
	public TuringControllerRecorder(Properties props, Simulator sim) {
		super(props, sim);
		iterations = 1; //Overwrite iteration count when replaying, since we don't care about properties
		
		if (tm instanceof Replayable<?>)
		{
			((Replayable<?>)tm).setRecordTimeSteps(true);	
			TimeStep<?> first = new TimeStep<>(((Replayable<?>)tm).getInitialTimeStep());
			first.setDomainInput(sim.getInitialObservation());
			first.setTuringMachineContent(tm.getTapeValues());
//			recording.add(first);
		}
	}
	
	public Recording getRecording(){
		return recording;
	}
	
	@Override
	public double[] processOutputs(double[] fromNN) {
		double[] result = super.processOutputs(fromNN);
		
		//Catch tm step
		if (tm instanceof Replayable<?>){
			currentTimeStep.setTuringStep(((Replayable<?>) tm).getLastTimeStep());
		}
			
		currentTimeStep.setTuringMachineContent(tm.getTapeValues());
		//Store and get ready for next step
		recording.add(currentTimeStep);
		currentTimeStep = new TimeStep();
		
		return result;
	}
	
	@Override
	protected double[] activateNeuralNetwork(Activator nn, double[] domainInput, double[] controllerInput) {
		double[] neuralNetworkOutput = super.activateNeuralNetwork(nn, domainInput, controllerInput);
		
		currentTimeStep.setDomainInput(domainInput); //Catch domain input
		currentTimeStep.setDomainOutput(Arrays.copyOfRange(neuralNetworkOutput, 0, sim.getInputCount()));
		
		return neuralNetworkOutput;
	};

}
