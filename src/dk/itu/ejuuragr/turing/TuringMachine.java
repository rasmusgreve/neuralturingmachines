package dk.itu.ejuuragr.turing;

public interface TuringMachine {

	public void reset();
	public int getReadHeadCount();
	public int getWriteHeadCount();
	public int getInputCount();
	public int getOutputCount();
	public double[][] processInput(double[] input);
	public double[][] getDefaultRead();
	
	// Get the info saved
	public double[][] getTapeValues();
}
