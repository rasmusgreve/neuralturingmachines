package turing;

import com.anji.util.Properties;

import domain.Simulator;
import fitness.BaseController;

public class TuringController extends BaseController {

	private TuringMachine tm;

	public TuringController(Properties props, Simulator sim) {
		super(props,sim);
		// Initialize everything (using properties)
		int n = props.getIntProperty("tm.n");
		int m = props.getIntProperty("tm.m");
		int readHeads = props.getIntProperty("tm.heads.read");
		int writeHeads = props.getIntProperty("tm.heads.write");
		this.tm = new TuringMachine(n,m,readHeads,writeHeads);
	}

	@Override
	public double[] processOutputs(double[] fromNN) {
		//TODO: Not implemented
		return null;
	}

	@Override
	public double[] getInitialInput() {
		return flatten(tm.getDefaultRead());
	}

	private double[] flatten(double[][] arrays){
		//TODO: Implement
		return null;
	}
}
