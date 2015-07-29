package dk.itu.ejuuragr.turing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

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
		Queue<Double> queue = new LinkedList<Double>();
		for(double d : fromNN) queue.add(d);
		
		// Should be M + 2 + S elements
		double[] writeKey = take(queue,this.m);
		
		write(writeKey, queue.poll());
		moveHead(queue.poll(), writeKey, take(queue,this.shiftLength));

		return new double[][]{getRead()};
	}
	
	private static double[] take(Queue<Double> coll, int amount) {
		double[] result = new double[amount];
		for(int i = 0; i < amount; i++)
			result[i] = coll.poll();
		return result;
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
		// WriteKey, Interpolation, ToContentJump, Shift
		return this.m + 2 + this.shiftLength;
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

	private void moveHead(double contentJump, double[] key, double[] shift) {
		if(contentJump >= 0.5) {
			// JUMPING POINTER TO BEST MATCH
			int bestPos = 0;
			double similarity = -1d;
			for(int i = 0; i < tape.size(); i++) {
				double curSim = Utilities.emilarity(key, tape.get(i));
				if(curSim > similarity) {
					similarity = curSim;
					bestPos = i;
				}
			}
			this.pointer = bestPos;
		}

		// SHIFTING
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
