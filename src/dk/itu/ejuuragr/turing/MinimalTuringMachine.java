package dk.itu.ejuuragr.turing;

import java.util.LinkedList;
import java.util.Queue;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.replay.Replayable;
import dk.itu.ejuuragr.replay.TuringTimeStep;
import dk.itu.ejuuragr.turing.MinimalTuringMachine.MinimalTuringMachineTimeStep;

public class MinimalTuringMachine implements TuringMachine, Replayable<MinimalTuringMachineTimeStep> {

	private static final boolean DEBUG = false;

	private LinkedList<double[]> tape;
	private int pointer;
	private int m;
	private int shiftLength;
	private String shiftMode;
	private int contentKeySize;
	private boolean enabled;

	private boolean recordTimeSteps = false;
	private MinimalTuringMachineTimeStep lastTimeStep;
	private boolean increasedSizeDown = false;

	private double[][] initialRead;



	public MinimalTuringMachine(Properties props) {
		this.m = props.getIntProperty("tm.m");
		this.shiftLength = props.getIntProperty("tm.shift.length");
		this.shiftMode = props.getProperty("tm.shift.mode", "multiple");
		this.contentKeySize = props.getIntProperty("tm.content.key.length", this.m);
		this.enabled = props.getBooleanProperty("tm.enabled", true);

		tape = new LinkedList<double[]>();
		
		this.reset();
		initialRead = new double[][]{getRead()};
	}

	@Override
	public void reset() {
		tape.clear();
		tape.add(new double[m]);
		pointer = 0;
		if (DEBUG) printState();
	}

	/**
	 * Operation order:
	 * 	write
	 *	jump
	 *	shift
	 *	read
	 */
	@Override
	public double[][] processInput(double[] fromNN) {
		if(!enabled)
			return initialRead;
		
		Queue<Double> queue = new LinkedList<Double>();
		for(double d : fromNN) queue.add(d);
		
		// Should be M + 2 + S elements
		double[] writeKey = take(queue,this.m);
		double interp = queue.poll();
		double content = queue.poll();
		double[] shift = take(queue,getShiftInputs());
		
		int writePosition = pointer;
		increasedSizeDown = false;
		
		if (DEBUG) {
			System.out.println("------------------- MINIMAL TURING MACHINE -------------------");
			System.out.println("Write="+Utilities.toString(writeKey)+" Interp="+interp);
			System.out.println("Content?="+content+" Shift="+Utilities.toString(shift));
		}
		
		write(writeKey, interp);
		moveHead(content, writeKey, shift);
		
		double[] result = getRead();
		
		if (recordTimeSteps){
			int readPosition = pointer;
			if (increasedSizeDown) writePosition++;
			lastTimeStep = new MinimalTuringMachineTimeStep(writeKey, interp, content, shift, result, writePosition, readPosition);
		}
		
		if (DEBUG) {
			printState();
			System.out.println("Sending to NN: "+Utilities.toString(result));
			System.out.println("--------------------------------------------------------------");
		}
		
		return new double[][]{result};
	}
	
	public MinimalTuringMachineTimeStep getLastTimeStep(){
		return lastTimeStep;
	}
	

	@Override
	public void setRecordTimeSteps(boolean setRecordTimeSteps) {
		recordTimeSteps = setRecordTimeSteps;
	}

	@Override
	public MinimalTuringMachineTimeStep getInitialTimeStep() {
		return new MinimalTuringMachineTimeStep(new double[m], 0, 0, new double[shiftLength], new double[m],0,0);
	}

	
	public static class MinimalTuringMachineTimeStep implements TuringTimeStep{
		public final double[] key;
		public final double writeInterpolation, contentJump;
		public final double[] shift;
		public final double[] read;
		public final int writePosition;
		public final int readPosition;
		
		public MinimalTuringMachineTimeStep(double[] key, double write, double jump, double[] shift, double[] read, int writePosition, int readPosition){
			this.key = key;
			writeInterpolation = write;
			contentJump = jump;
			this.shift = shift;
			this.read = read;
			this.writePosition = writePosition;
			this.readPosition = readPosition;
		}
		
	}

	@Override
	public double[][] getDefaultRead() {
		if(DEBUG) printState();
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
		return this.m + 2 + getShiftInputs();
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
	
	private static double[] take(Queue<Double> coll, int amount) {
		double[] result = new double[amount];
		for(int i = 0; i < amount; i++)
			result[i] = coll.poll();
		return result;
	}
	
	private int getShiftInputs() {
		switch(shiftMode) {
			case "single": return 1;
			default: return this.shiftLength;
		}
	}

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
			for(int i = 0; i < tape.size() + 1; i++) {
				double[] keySection = this.contentKeySize < this.m ? Utilities.copy(key, 0, this.contentKeySize) : key;
				double[] tapeSection = this.contentKeySize < this.m ? Utilities.copy(tape.get(i), 0, this.contentKeySize) : tape.get(i);
				
				double curSim = Utilities.emilarity(keySection, tapeSection);
				if(curSim > similarity) {
					similarity = curSim;
					bestPos = i;
				}
			}
			
			this.pointer = bestPos;
		}

		// SHIFTING
		int highest;
		switch(shiftMode){
			case "single": highest = (int) (shift[0] * this.shiftLength); break; // single
			default: highest = Utilities.maxPos(shift); break; // multiple
		}
		
		int offset = highest - (this.shiftLength / 2);
		
//		System.out.println("Highest="+highest);
//		System.out.println("Offset="+offset);

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
					increasedSizeDown = true;
				}
			}

			offset = offset > 0 ? offset - 1 : offset + 1; // Go closer to 0
		}
	}

	private double[] getRead() {
		return tape.get(pointer).clone();
	}

}
