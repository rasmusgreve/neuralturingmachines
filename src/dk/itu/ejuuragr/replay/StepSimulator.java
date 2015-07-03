package dk.itu.ejuuragr.replay;

import dk.itu.ejuuragr.domain.Simulator;

public class StepSimulator implements Simulator {

	Simulator simulator;
	private Stepper stepper;
	
	public StepSimulator(Simulator simulator) {
		this.simulator = simulator;
	}
	
	public void setStepper(Stepper stepper){
		this.stepper = stepper;
	}
	
	public interface Stepper{
		public void step();
	}

	@Override
	public int getInputCount() {
		return simulator.getInputCount();
	}

	@Override
	public int getOutputCount() {
		return simulator.getOutputCount();
	}

	@Override
	public double[] getInitialObservation() {
		return simulator.getInitialObservation();
	}

	@Override
	public double[] performAction(double[] action) {
		double[] result = simulator.performAction(action);
		if (stepper != null) stepper.step();
		return result;
	}

	@Override
	public int getCurrentScore() {
		return simulator.getCurrentScore();
	}

	@Override
	public boolean isTerminated() {
		return simulator.isTerminated();
	}

	@Override
	public void reset() {
		simulator.reset();
	}

	@Override
	public int getMaxScore() {
		return simulator.getMaxScore();
	}

	
}
