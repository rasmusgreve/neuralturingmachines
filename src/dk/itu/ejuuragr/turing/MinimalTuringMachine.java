package dk.itu.ejuuragr.turing;

import java.util.LinkedList;

import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.BaseController;
import dk.itu.ejuuragr.fitness.Utilities;

public class MinimalTuringMachine extends BaseController {

	private static final boolean DEBUG = false;

	private LinkedList<double[]> tape;
	private int pointer;
	private int m;
	private int shiftLength;

	public MinimalTuringMachine(Properties props, Simulator sim) {
		super(props, sim);
		this.m = props.getIntProperty("tm.m");
		this.shiftLength = props.getIntProperty("tm.shift.length");

		tape = new LinkedList<double[]>();
	}

	@Override
	public void reset() {
		tape.clear();
		tape.add(new double[m]);
		pointer = 0;
	}

	@Override
	public double[] processOutputs(double[] fromNN) {
		if (DEBUG) printState();

		// Should be M + 1 + S elements
		write(Utilities.copy(fromNN, 0, this.m), fromNN[this.m]);
		moveHead(Utilities.copy(fromNN, this.m + 1, this.shiftLength));

		return getRead();
	}

	@Override
	public double[] getInitialInput() {
		return getRead();
	}

	// PRIVATE HELPER METHODS

	private void printState() {
		System.out.println("TM: " + Utilities.toString(tape.toArray(new double[tape.size()][]))+" pointer="+pointer);
	}

	private void write(double[] content, double interp) {
		tape.set(pointer, interpolate(content, tape.get(pointer), interp));
	}

	private double[] interpolate(double[] first, double[] second, double interp) {
		double[] result = new double[first.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = interp * first[i] + (1 - interp) * second[i];
		}
		return result;
	}

	private void moveHead(double[] shift) {
		int highest = Utilities.maxPos(shift);
		int offset = highest - (shift.length / 2);

		while (offset != 0) {
			if (offset > 0) {
				pointer++;
				if (pointer >= tape.size()) {
					tape.addLast(new double[this.m]);
				}
			} else {
				pointer--;
				if (pointer < 0) {
					tape.addFirst(new double[this.m]);
					pointer = 0;
				}
			}

			offset = offset > 0 ? offset - 1 : offset + 1; // Go closer to 0
		}
	}

	private double[] getRead() {
		return tape.get(pointer).clone();
	}

}
