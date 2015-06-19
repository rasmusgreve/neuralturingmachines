package domain;

public interface Simulator {
	int getInputCount();
	int getOutputCount();
	
	void reset();
	double[] initialObservation();
	
	/**
	 * @param action
	 * @return new bbservation
	 */
	double[] performAction(double[] action);
	int getCurrentScore();
	int getMaxScore();
	boolean isTerminated();
}
