package dk.itu.ejuuragr.replay;

/**
 * The necessary methods for being Replayable
 * which primarily is to be able to privode
 * TuringTimeSteps for each activation.
 * 
 * @author Rasmus
 *
 * @param <T> The specific type of TuingTimeStep
 * that will be provided.
 */
public interface Replayable<T extends TuringTimeStep> {

	public void setRecordTimeSteps(boolean setRecordTimeSteps);
	public T getInitialTimeStep();
	public T getLastTimeStep();
}
