package domain;

/**
 * A Simulator should have a constructor taking a single Properties object.
 * This way it can be used by simply referencing it in the properties file.
 * @author Emil
 *
 */
public interface Simulator {
	int getInputCount();
	int getOutputCount();
	
	void reset();
	double[] getInitialObservation();
	
	/**
	 * @param action
	 * @return new bbservation
	 */
	double[] performAction(double[] action);
	int getCurrentScore();
	int getMaxScore();
	boolean isTerminated();
}
