package dk.itu.ejuuragr.turing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.replay.Replayable;
import dk.itu.ejuuragr.replay.TuringTimeStep;
import dk.itu.ejuuragr.turing.MinimalTuringMachine.MinimalTuringMachineTimeStep;

/**
 * Our simplified version of a Turing Machine for
 * use with a neural network. It uses the general
 * TuringMachine interface so can be used in the
 * same contexts as the GravesTuringMachine.
 * 
 * @author Emil
 *
 */
public class MinimalTuringMachine implements TuringMachine, Replayable<MinimalTuringMachineTimeStep> {

	private static final boolean DEBUG = false;

	private LinkedList<double[]> tape;
	private int[] pointers;
	private int m;
	private int n;
	private int shiftLength;
	private String shiftMode;
	private boolean enabled;
	private int heads;

	private boolean recordTimeSteps = false;
	private MinimalTuringMachineTimeStep lastTimeStep;
	private MinimalTuringMachineTimeStep internalLastTimeStep;
	private boolean increasedSizeDown = false;
	private int zeroPosition = 0;

	private double[][] initialRead;

	public MinimalTuringMachine(Properties props) {
		this.m = props.getIntProperty("tm.m");
		this.n = props.getIntProperty("tm.n", -1);
		this.shiftLength = props.getIntProperty("tm.shift.length");
		this.shiftMode = props.getProperty("tm.shift.mode", "multiple");
		this.enabled = props.getBooleanProperty("tm.enabled", true);
		this.heads = props.getIntProperty("tm.heads.readwrite", 1);

		tape = new LinkedList<double[]>();
		
		this.reset();
		initialRead = new double[heads][];
		for(int i = 0; i < heads; i++) {
			initialRead[i] = getRead(i);
		}
	}

	@Override
	public void reset() {
		tape.clear();
		tape.add(new double[m]);
		pointers = new int[heads];

		if (recordTimeSteps){
			internalLastTimeStep = new MinimalTuringMachineTimeStep(new double[m], 0, 0, new double[shiftLength], new double[m],0,0,0,0,0,0); 
			lastTimeStep = new MinimalTuringMachineTimeStep(new double[m], 0, 0, new double[shiftLength], new double[m],0,0,0,0,0,0);
		}
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
		
		double[][] result = new double[heads][];
		
		double[][] writeKeys = new double[heads][];
		double[] interps = new double[heads];
		double[] contents = new double[heads];
		double[][] shifts = new double[heads][];
		
		// First all writes
		for(int i = 0; i < heads; i++) {
			// Should be M + 2 + S elements
			writeKeys[i] = take(queue,this.m);
			interps[i] = queue.poll();
			contents[i] = queue.poll();
			shifts[i] = take(queue,getShiftInputs());
			
			if (DEBUG) {
				System.out.println("------------------- MINIMAL TURING MACHINE (HEAD "+(i+1)+") -------------------");
				System.out.println("Write="+Utilities.toString(writeKeys[i], "%.4f")+" Interp="+interps[i]);
				System.out.println("Content?="+contents[i]+" Shift="+Utilities.toString(shifts[i],"%.4f"));
			}
			
			write(i, writeKeys[i], interps[i]);
		}
		
		// Perform content jump
		for(int i = 0; i < heads; i++) {
			performContentJump(i, contents[i], writeKeys[i]);
		}
		
		// Shift and read (no interaction)
		for(int i = 0; i < heads; i++) {
			int writePosition = pointers[i];
			increasedSizeDown = false;
			moveHead(i, shifts[i]);
			
			double[] headResult = getRead(i); // Show me what you've got! \cite{rickEtAl2014}
			result[i] = headResult;
			
			if (recordTimeSteps){
				int readPosition = pointers[i];
				int correctedWritePosition = writePosition - zeroPosition;
				
				if (increasedSizeDown) {
					writePosition++;
					zeroPosition++;
				}
				int correctedReadPosition = readPosition - zeroPosition;
				lastTimeStep = new MinimalTuringMachineTimeStep(writeKeys[i], interps[i]  , contents[i], shifts[i]     , headResult, writePosition    , readPosition    , zeroPosition         , zeroPosition          , correctedWritePosition, correctedReadPosition);
//				                                               (double[] key, double write, double jump, double[] shift, double[] read, int writePosition, int readPosition, int writeZeroPosition, int readZeroPosition  , int correctedWritePosition, int correctedReadPosition){
			
//				correctedReadPosition = readPosition - zeroPosition;
				lastTimeStep =         new MinimalTuringMachineTimeStep(writeKeys[i], interps[i], contents[i], shifts[i], headResult, writePosition    , readPosition    , zeroPosition, zeroPosition, correctedWritePosition, correctedReadPosition);
				internalLastTimeStep = new MinimalTuringMachineTimeStep(writeKeys[i], interps[i], contents[i], shifts[i], headResult, writePosition    , readPosition    , zeroPosition, zeroPosition, correctedWritePosition, correctedReadPosition);
			}
		}
		
		if (DEBUG) {
			printState();
			System.out.println("Sending to NN: "+Utilities.toString(result, "%.4f"));
			System.out.println("--------------------------------------------------------------");
		}
//		return new double[1][result.length];
		return result;
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
		return lastTimeStep = new MinimalTuringMachineTimeStep(new double[m], 0, 0, new double[shiftLength], new double[m],0,0,0,0,0,0);
	}

	
	public static class MinimalTuringMachineTimeStep implements TuringTimeStep{
		public final double[] key;
		public final double writeInterpolation, contentJump;
		public final double[] shift;
		public final double[] read;
		public final int writePosition;
		public final int readPosition;
		public final int writeZeroPosition;
		public final int readZeroPosition;
		public final int correctedWritePosition;
		public final int correctedReadPosition;
		public MinimalTuringMachineTimeStep(double[] key, double write, double jump, double[] shift, double[] read, int writePosition, int readPosition, int writeZeroPosition, int readZeroPosition, int correctedWritePosition, int correctedReadPosition){
			this.key = key;
			writeInterpolation = write;
			contentJump = jump;
			this.shift = shift;
			this.read = read;
			this.writePosition = writePosition;
			this.readPosition = readPosition;
			this.writeZeroPosition = writeZeroPosition;
			this.readZeroPosition = readZeroPosition;
			this.correctedWritePosition = correctedWritePosition;
			this.correctedReadPosition = correctedReadPosition;
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
		return this.heads * (this.m + 2 + getShiftInputs());
	}

	@Override
	public int getOutputCount() {
		return this.m * this.heads;
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
		b.append("Pointers=");
		b.append(Arrays.toString(pointers));
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
		System.out.println("TM: " + Utilities.toString(tape.toArray(new double[tape.size()][]))+" pointers="+Arrays.toString(pointers));
	}

	private void write(int head, double[] content, double interp) {
		tape.set(pointers[head], interpolate(content, tape.get(pointers[head]), interp));
	}

	private double[] interpolate(double[] first, double[] second, double interp) {
		double[] result = new double[first.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = interp * first[i] + (1 - interp) * second[i];
		}
		return result;
	}
	
	private void performContentJump(int head, double contentJump, double[] key) {
		if(contentJump >= 0.5) {
			// JUMPING POINTER TO BEST MATCH
			int bestPos = 0;
			double similarity = -1d;
			for(int i = 0; i < tape.size(); i++) {				
				double curSim = Utilities.emilarity(key, tape.get(i));
				if(DEBUG) System.out.println("Pos "+i+": sim ="+curSim+(curSim > similarity ? " better" : ""));
				if(curSim > similarity) {
					similarity = curSim;
					bestPos = i;
				}
			}
			
			if(DEBUG) System.out.println("PERFORMING CONTENT JUMP! from "+this.pointers[head]+" to "+bestPos);
			
			this.pointers[head] = bestPos;
			
		}
	}

	private void moveHead(int head, double[] shift) {
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
				if(this.n > 0 && tape.size() >= this.n) {
					pointers[head] = 0;
				}else {
					pointers[head] = pointers[head] + 1;
					
					if (pointers[head] >= tape.size()) {
						tape.addLast(new double[this.m]);
					}
				}
	
			} else {
				if(this.n > 0 && tape.size() >= this.n) {
					pointers[head] = tape.size()-1;
				} else {
					pointers[head] = pointers[head] - 1;
					if (pointers[head] < 0) {
						tape.addFirst(new double[this.m]);
						pointers[head] = 0;
						
						// Moving all other heads accordingly
						for(int i = 0; i < heads; i++) {
							if(i != head)
								pointers[i] = pointers[i] + 1;
						}
						
						increasedSizeDown = true;
					}
				}

			}

			offset = offset > 0 ? offset - 1 : offset + 1; // Go closer to 0
		}
	}

	private double[] getRead(int head) {
		return tape.get(pointers[head]).clone();
	}

}
