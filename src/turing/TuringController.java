package turing;

import java.util.Arrays;

import com.anji.util.Properties;

import domain.Simulator;
import fitness.BaseController;
import fitness.Utilities;

public class TuringController extends BaseController {

	private TuringMachine tm;

	public TuringController(Properties props, Simulator sim) {
		super(props,sim);
		// Initialize everything (using properties)
		int n = props.getIntProperty("tm.n");
		int m = props.getIntProperty("tm.m");
		int shiftLength = props.getIntProperty("tm.shift.length");
		int readHeads = props.getIntProperty("tm.heads.read");
		int writeHeads = props.getIntProperty("tm.heads.write");
		this.tm = new TuringMachine(n,m,readHeads,writeHeads,shiftLength);
	}

	@Override
	public double[] processOutputs(double[] fromNN) {
		double[] result = Utilities.flatten(tm.processInput(fromNN));
		
		//System.out.println("fromNN: "+Arrays.toString(fromNN));
		//System.out.println("toNN: "+Arrays.toString(result));
		
		return result;
	}

	@Override
	public double[] getInitialInput() {
		return Utilities.flatten(tm.getDefaultRead());
	}

	@Override
	public void reset() {
		System.out.println("---------- RESET ----------");
		this.tm.reset();
	}
}
