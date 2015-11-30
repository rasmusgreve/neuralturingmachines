package dk.itu.ejuuragr.graph;

import java.util.Arrays;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.CopyTask;

/**
 * An extension to CopyTask which stores
 * the output from the ANN to be visualized
 * elsewhere.
 * 
 * @author Rasmus
 *
 */
public class CopyTaskResultGrabber extends CopyTask {

	double[][] received;
	int i;
	
	public CopyTaskResultGrabber(Properties props) {
		super(props);
	}
	
	@Override
	public void restart() {
		super.restart();
		received = new double[sequence.length][];
		i = 0;
	}

	@Override
	protected double evaluate(double[] correct, double[] received) {
		this.received[i++] = Arrays.copyOf(received, received.length);
		return super.evaluate(correct, received);
	}

	public double[][] getTargets(){
		return sequence;
	}
	
	public double[][] getOutputs(){
		return received;
	}
	
}
