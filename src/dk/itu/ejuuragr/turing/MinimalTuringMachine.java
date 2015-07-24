package dk.itu.ejuuragr.turing;

import java.util.LinkedList;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;

public class MinimalTuringMachine implements TuringMachine {

	private static final boolean DEBUG = false;

	private LinkedList<double[]> tape;
	private int pointer;
	private int m;
	private int shiftLength;

	private double[][] initialRead;

	public MinimalTuringMachine(Properties props) {
		this.m = props.getIntProperty("tm.m");
		this.shiftLength = props.getIntProperty("tm.shift.length");

		tape = new LinkedList<double[]>();
		
		this.reset();
		initialRead = new double[][]{getRead()};
	}

	@Override
	public void reset() {
		tape.clear();
		tape.add(new double[m]);
		pointer = 0;
	}

	@Override
	public double[][] processInput(double[] fromNN) {
		if (DEBUG) printState();

		// Should be M + 1 + S elements
		write(Utilities.copy(fromNN, 0, this.m), fromNN[this.m]);
		moveHead(Utilities.copy(fromNN, this.m + 1, this.shiftLength));

		return new double[][]{getRead()};
	}

	@Override
	public double[][] getDefaultRead() {
		return initialRead;
	}
	
	@Override
	public int getReadHeadCount() {
		return 1;
	}

	@Override
	public int getWriteHeadCount() {
		return 1;
	}

	@Override
	public int getInputCount() {
		return this.m + 1 + this.shiftLength;
	}

	@Override
	public int getOutputCount() {
		return this.m;
	}
	
	@Override
	public double[][] getTapeValues() {
		return Utilities.deepCopy(tape.toArray(new double[tape.size()][]));
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(Utilities.toString(tape.toArray(new double[tape.size()][])));
		b.append("\n");
		b.append("Pointer=");
		b.append(pointer);
		return b.toString();
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
