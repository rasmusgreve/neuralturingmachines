package dk.itu.ejuuragr.replay;

import java.util.Random;

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
		if (stepper != null) stepper.step();
		double[] result = simulator.performAction(action);
		return result;
	}

	@Override
	public double getCurrentScore() {
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

	@Override
	public void restart() {
		simulator.restart();
	}

	@Override
	public Random getRandom() {
		return simulator.getRandom();
	}

	@Override
	public void setRandomOffset(int offset) {
		simulator.setRandomOffset(offset);
	}
}
