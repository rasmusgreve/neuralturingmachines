package domain.tmaze;

import domain.Simulator;

public class ForwardSimulator implements Simulator {

	static final int MAXSCORE = 10;
	double[] position;
	
	public ForwardSimulator(){
		reset();
	}
	
	@Override
	public int getInputCount() {
		return 2;
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	@Override
	public double[] initialObservation() {
		//return new double[]{1d};
		return position;
	}

	@Override
	public double[] performAction(double[] action) {
		position[0] += action[0] - action[1];
		//return new double[]{1d};
		return position;
	}

	@Override
	public int getCurrentScore() {
		return (int)position[0];
	}

	@Override
	public boolean isTerminated() {
		return position[0] >= MAXSCORE;
	}

	@Override
	public int getMaxScore() {
		return MAXSCORE;
	}

	@Override
	public void reset() {
		position = new double[]{0};
	}


}
