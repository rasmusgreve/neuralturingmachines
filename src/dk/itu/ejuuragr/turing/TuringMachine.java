package dk.itu.ejuuragr.turing;

/**
 * The required interface for a Turing Machine
 * implemented for our framework. It needs to
 * define various getter methods for its sizes
 * and be able to process some input from an ANN
 * and give a "read" result back.
 * 
 * @author Emil
 *
 */
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
