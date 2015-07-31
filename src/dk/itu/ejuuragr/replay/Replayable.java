package dk.itu.ejuuragr.replay;



public interface Replayable<T extends TuringTimeStep> {

	public void setRecordTimeSteps(boolean setRecordTimeSteps);
	public T getInitialTimeStep();
	public T getLastTimeStep();
}
