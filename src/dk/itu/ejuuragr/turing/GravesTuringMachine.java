package dk.itu.ejuuragr.turing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.anji.util.Properties;

import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.replay.Replayable;
import dk.itu.ejuuragr.replay.TuringTimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.HeadVariables.Head;
import dk.itu.ejuuragr.turing.GravesTuringMachine.GravesTuringMachineTimeStep;

/**
 * The Turing Machine for integration with ANNs as described
 * by Graves et.al.
 * 
 * @author Emil
 *
 */
public class GravesTuringMachine implements TuringMachine, Replayable<GravesTuringMachineTimeStep> {
	
	private ArrayList<double[]> tape;
	private int m;
	private int n;
	private double[][] readWeightings;
	private double[][] writeWeightings;
	private int shiftLength;
	private int readHeads;
	private int writeHeads;
	private int readHeadOutputs;
	private int writeHeadOutputs;
	
	private double sharpeningFactor = 1;
	
	private boolean recordTimeSteps = false;
	private GravesTuringMachineTimeStep currentStep;
	private boolean enabled = true;

	
	/**
	 * The required constructur for automatic instantiation
	 * of the TM through reflection
	 * @param props The properties to take attributes for.
	 */
	public GravesTuringMachine(Properties props) {
		this(	props.getIntProperty("tm.n", 10),
				props.getIntProperty("tm.m", 3),
				props.getIntProperty("tm.heads.read", 1),
				props.getIntProperty("tm.heads.write", 1),
				props.getIntProperty("tm.shift.length" , 3));
		this.sharpeningFactor = props.getDoubleProperty("tm.sharpening.factor", sharpeningFactor);
		this.enabled = props.getBooleanProperty("tm.enabled", true);
	}

	/**
	 * Instantiating the TM with the necessary parameters.
	 * @param n The number of memory locations in the FINITE tape.
	 * @param m The length of the memory at each location.
	 * @param readHeads The number of read heads in the TM.
	 * @param writeHeads The number of write heads in the TM.
	 * @param shiftLength The maximum distance you can jump with shifting.
	 */
	public GravesTuringMachine(int n, int m, int readHeads, int writeHeads, int shiftLength){
		this.n = n;
		this.m = m;
		this.readHeads = readHeads;
		this.writeHeads = writeHeads;
		this.shiftLength = shiftLength;
		
		// sizes for array handling
		this.readHeadOutputs = 3 + m + shiftLength;
		this.writeHeadOutputs = 3 + 3 * m + shiftLength;
		
		this.reset();
	}
	
	public void setRecordTimeSteps(boolean recordTimeSteps){
		this.recordTimeSteps = true;
	}
	
	/**
	 * Reset the TM to its default state.
	 */
	@Override
	public void reset() {
		// Initialize memory tape
		tape = new ArrayList<double[]>(n);
		for(int i = 0; i < n; i++)
			tape.add(new double[m]);
		
		// save weightings for future iterations
		readWeightings = new double[readHeads][];
		for (int i = 0; i < readHeads; i++){
			readWeightings[i] = new double[n];
			readWeightings[i][0] = 1.0; // ASSUMING THAT HEAD IS AT FIRST ELEMENT AT BEGINNING
		}
		writeWeightings = new double[writeHeads][];
		for (int i = 0; i < writeHeads; i++) {
			writeWeightings[i] = new double[n];
			writeWeightings[i][0] = 1.0; // ASSUMING THAT HEAD IS AT FIRST ELEMENT AT BEGINNING
		}
	}
	
	/**
	 * Gets the number of read heads specified in initialization.
	 * @return
	 */
	@Override
	public int getReadHeadCount() {
		return readHeads;
	}
	
	/**
	 * Gets the number of write heads specified in initialization.
	 * @return
	 */
	@Override
	public int getWriteHeadCount() {
		return writeHeads;
	}

	@Override
	public int getInputCount() {
		return this.getReadHeadCount() * this.readHeadOutputs + this.getWriteHeadCount() * this.writeHeadOutputs;
	}

	@Override
	public int getOutputCount() {
		return this.getReadHeadCount() * this.m;
	}
	
	@Override
	public double[][] getTapeValues() {
		return Utilities.deepCopy(tape.toArray(new double[tape.size()][]));
	}
	
	/**
	 * Gets the read of the default state.
	 * @return An array for each read head
	 * with an array of the memory location size (M).
	 */
	@Override
	public double[][] getDefaultRead() {
		double[][] result = new double[readWeightings.length][];
		for(int i = 0; i < readWeightings.length; i++) {
			result[i] = new double[m];
		}
		return result;
	}

	/**
	 * Processes the input for the TM and gets all the
	 * reads out.
	 * @param flatVars The flat 1d array version of the
	 * inputs for the TM (i.e. directly from a NN)
	 * @return An array for each read head with the m
	 * elements read from the TM.
	 */
	@Override
	public double[][] processInput(double[] flatVars) {
		if(!enabled)
			return getDefaultRead();
		
		return processInput(translateToHeadVars(flatVars));
	}
	
	/**
	 * Processes the input for the TM and gets all the
	 * reads out.
	 * @param vars The input for the different heads in
	 * the TM.
	 * @return An array for each read head with the m
	 * elements read from the TM.
	 */
	public double[][] processInput(HeadVariables vars){
		if(vars.getRead().size() != getReadHeadCount() 
				|| vars.getWrite().size() != getWriteHeadCount())
			throw new IllegalArgumentException("You must define as many read and write heads as when the TM was created.");

		// First all WRITES
		
		// Erase
		for(int i = 0; i < vars.getWrite().size(); i++){
			Head current = vars.getWrite().get(i);
			writeWeightings[i] = weighting(current,writeWeightings[i]);
			
			for(int k = 0; k < tape.size(); k++){
				for(int j = 0; j < m; j++){
					tape.get(k)[j] *= (1.0 - writeWeightings[i][k] * current.getErase()[j]);
				}
			}
		}
		
		// Add
		for(int i = 0; i < vars.getWrite().size(); i++){
			Head current = vars.getWrite().get(i);
			
			for(int k = 0; k < tape.size(); k++){
				for(int j = 0; j < m; j++){
					tape.get(k)[j] += writeWeightings[i][k] * current.getAdd()[j];
				}
			}
		}
		
		//System.out.println("Reading");
		// prepare result
		double[][] result = new double[vars.getRead().size()][];
		
		// perform READS and get result
		for(int i = 0; i < vars.getRead().size(); i++){
			readWeightings[i] = weighting(vars.getRead().get(i),readWeightings[i]);
			double[] readM = new double[m];
			for(int j = 0; j < m; j++){
				for(int k = 0; k < tape.size(); k++){
					readM[j] += readWeightings[i][k] * tape.get(k)[j]; 
				}
			}
			
			result[i] = readM;
		}
		
		if (recordTimeSteps){
			currentStep = new GravesTuringMachineTimeStep();
			for (int i = 0; i < getWriteHeadCount(); i++){
				currentStep.getWriteHeads().add(new HeadTimeStep(
						writeWeightings[i], 
						vars.getWrite().get(i).getAdd(),
						vars.getWrite().get(i).getKey(),
						vars.getWrite().get(i).getKeyStrength(),
						vars.getWrite().get(i).getInterpolation()));
			}
			
			for (int i = 0; i < getReadHeadCount(); i++){
				currentStep.getReadHeads().add(new HeadTimeStep(
						readWeightings[i], 
						result[i],
						vars.getRead().get(i).getKey(),
						vars.getRead().get(i).getKeyStrength(),
						vars.getRead().get(i).getInterpolation()));
			}
		}
		
		return result;
	}

	public static class GravesTuringMachineTimeStep implements TuringTimeStep{
		
		List<HeadTimeStep> writeHeads = new ArrayList<HeadTimeStep>();
		List<HeadTimeStep> readHeads = new ArrayList<HeadTimeStep>();
		//TODO: Possible store a snapshot of the tape
		
		public GravesTuringMachineTimeStep(){}

		public List<HeadTimeStep> getWriteHeads() {
			return writeHeads;
		}

		public List<HeadTimeStep> getReadHeads() {
			return readHeads;
		}
		
		
	}
	
	public static class HeadTimeStep{
		public final double[] weights; //length: N
		public final double[] value; //length: M
		public final double[] key;
		public final double keyStrength;
		public final double interpolation;
		
		
		public HeadTimeStep(double[] weights, double[] value, double[] key, double keyStrength, double interpolation){
			this.weights = new double[weights.length];
			System.arraycopy(weights, 0, this.weights, 0, weights.length);
			
			this.value = new double[value.length];
			System.arraycopy(value, 0, this.value, 0, value.length);
			
			this.key = new double[key.length];
			System.arraycopy(key, 0, this.key, 0, key.length);
			
			this.keyStrength = keyStrength;
			
			this.interpolation = interpolation;
		}
	}
	
	public GravesTuringMachineTimeStep getLastTimeStep(){
		return currentStep;
	}

	public GravesTuringMachineTimeStep getInitialTimeStep(){
		GravesTuringMachineTimeStep timeStep = new GravesTuringMachineTimeStep();
		
		for (int i = 0; i < getWriteHeadCount(); i++){
			timeStep.getWriteHeads().add(new HeadTimeStep(writeWeightings[i], new double[m], new double[m], 0, 1));
		}
		double[][] defaultRead = getDefaultRead();
		for (int i = 0; i < getReadHeadCount(); i++){
			timeStep.getReadHeads().add(new HeadTimeStep(readWeightings[i], defaultRead[i], new double[m], 0, 1));
		}
		
		return timeStep;
	}
	
	// Wrapper of variables for heads.
	
	private double[] weighting(Head current, double[] oldWeight) {
		double[] result = new double[n];
		
		// Focusing by Content
		double sum = 0.0;
		for(int i = 0; i < n; i++){
			double similarity =  Utilities.emilarity(current.getKey(),tape.get(i)); //cosineSim(	 // Utilities.euclideanDistance(
			result[i] = Math.exp(current.getKeyStrength() * similarity);
			sum += result[i];
		}
		for(int i = 0; i < n; i++){
			result[i] /= sum;
		}
		
		// Interpolation Gate
		double interpolation = current.getInterpolation();
		for(int i = 0; i < n; i++){
			result[i] = interpolation * result[i] + (1 - interpolation) * oldWeight[i];
		}
		
		// Focusing by Location
		double[] shift = current.getShift();
		double[] tempWeight = new double[n];
		for(int i = 0; i < n; i++) {
			/*for(int j = 0; j < n; j++){
				int idx = (shift.length*n + i - j) % shift.length;
				tempWeight[i] += result[j] * shift[idx]; 
				//TODO Check if this is right
			}*/
			// Simpler convolution so elements don't appear multiple times
			for(int j = 0; j < shift.length; j++) {
				int k = (i - shift.length/2 + j +n) % n;
				tempWeight[i] += result[k] * shift[j];
			}
		}
		result = tempWeight;
		
		// Sharpening
		sum = 0.0;
		for(int i = 0; i < n; i++){
			result[i] = Math.pow(result[i], current.getSharp());
			sum += result[i];
		}
		for(int i = 0; i < n; i++){
			result[i] /= sum;
		}
		
		// Return to sender
		return result;
	}
	
	/**
	 * Translates a flat array of values to the default
	 * positions in the HeadVariables object matching the
	 * number of read and write heads.
	 * @param flatVars The 1d array of variables. First all
	 * the read heads and then all the write heads in order.
	 * @return A HeadVariables object with all the values properly
	 * packed.
	 */
	private HeadVariables translateToHeadVars(double[] flatVars){
		int varsNeeded = getInputCount();
		if(flatVars.length != varsNeeded)
			throw new IllegalArgumentException("The number of elements ("+flatVars.length+") to the TM doesn't match the needed ("+varsNeeded+")");
		
		HeadVariables vars = new HeadVariables();
		int offset = 0;
		
		// Package for TM
		for(int i = 0; i < getReadHeadCount(); i++) {
			vars.addRead(Arrays.copyOfRange(flatVars,offset,offset+m), 	// Key (k)
					flatVars[offset+m], 								// Key Strength (beta)
					flatVars[offset+m+1], 								// Interpolation (g)
					Utilities.normalize(Arrays.copyOfRange(flatVars,offset+m+2,offset+m+2+shiftLength)), // Shifting (s)
					1 + this.sharpeningFactor * flatVars[offset+m+2+shiftLength]); 				// Sharpening (gamma)
			offset += this.readHeadOutputs;
		}
		
		for(int i = 0; i < getWriteHeadCount(); i++) {
			vars.addWrite(Arrays.copyOfRange(flatVars, offset, offset+m), 	// Erase Vector (e)
					Arrays.copyOfRange(flatVars, offset+m, offset+2*m),   	// Add Vector (a)
					Arrays.copyOfRange(flatVars, offset+2*m, offset+3*m),	// Key (k)
					flatVars[offset+3*m], 									// Key Strength (beta)
					flatVars[offset+3*m+1], 								// Interpolation (g)
					Utilities.normalize(Arrays.copyOfRange(flatVars,offset+3*m+2,offset+3*m+2+shiftLength)), // Shifting (s)
					1 + this.sharpeningFactor * flatVars[offset+3*m+2+shiftLength]);				// Sharpening (gamma)
			offset += this.writeHeadOutputs;
		}
		return vars;
	}

	public static class HeadVariables {
		
		private ArrayList<Head> read;
		private ArrayList<Head> write;

		public HeadVariables() {
			this.read = new ArrayList<Head>();
			this.write = new ArrayList<Head>();
		}
		
		public void addRead(double[] key, double keyStrength, double interpolation, double[] shift, double sharp){
			read.add(new Head(null,null,key,keyStrength,interpolation,shift,sharp));
		}
		
		public void addWrite(double[] erase, double[] add, double[] key, double keyStrength, double interpolation, double[] shift, double sharp){
			write.add(new Head(erase,add,key,keyStrength,interpolation,shift,sharp));
		}
		
		public ArrayList<Head> getRead() {
			return read;
		}

		public ArrayList<Head> getWrite() {
			return write;
		}



		public static class Head {
			
			private double[] erase;
			private double[] add;
			private double[] key;
			private double keyStrength;
			private double interpolation;
			private double[] shift;
			private double sharp;

			public Head(double[] erase, double[] add, double[] key, double keyStrength, double interpolation, double shift[], double sharp) {
				this.erase = erase;
				this.add = add;
				this.key = key;
				this.keyStrength = keyStrength;
				this.interpolation = interpolation;
				this.shift = shift;
				this.sharp = sharp;
			}

			public double[] getErase() {
				return erase;
			}

			public double[] getAdd() {
				return add;
			}

			public double[] getKey() {
				return key;
			}

			public double getKeyStrength() {
				return keyStrength;
			}

			public double getInterpolation() {
				return interpolation;
			}

			public double[] getShift() {
				return shift;
			}

			public double getSharp() {
				return sharp;
			}
		}
	}


}
