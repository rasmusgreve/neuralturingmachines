package turing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;

import turing.TuringMachine.HeadVariables.Head;
import fitness.Utilities;

public class TuringMachine {
	
	private ArrayList<double[]> tape;
	private int m;
	private int n;
	private double[][] readWeightings;
	private double[][] writeWeightings;
	private int shiftLength;
	private int readHeads;
	private int writeHeads;
	
	private boolean recordTimeSteps = false;
	private TuringTimeStep currentStep;

	public TuringMachine(int n, int m, int readHeads, int writeHeads, int shiftLength){
		this.n = n;
		this.m = m;
		this.readHeads = readHeads;
		this.writeHeads = writeHeads;
		this.shiftLength = shiftLength;
		
		this.reset();
	}
	
	public void setRecordTimeSteps(boolean recordTimeSteps){
		this.recordTimeSteps = true;
	}
	
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
	
	public int getReadHeadCount() {
		return readHeads;
	}
	
	public int getWriteHeadCount() {
		return writeHeads;
	}

	public double[][] getDefaultRead() {
		double[][] result = new double[readWeightings.length][];
		for(int i = 0; i < readWeightings.length; i++) {
			result[i] = new double[m];
		}
		return result;
	}
	
	public HeadVariables translateToHeadVars(double[] flatVars){
		HeadVariables vars = new HeadVariables();
		int offset = 0;
		
		// Package for TM
		for(int i = 0; i < getReadHeadCount(); i++) {
			vars.addRead(Arrays.copyOfRange(flatVars,offset,offset+m), 
					flatVars[offset+m], 
					flatVars[offset+m+1], 
					Utilities.normalize(Arrays.copyOfRange(flatVars,offset+m+2,offset+m+2+shiftLength)), 
					flatVars[offset+m+2+shiftLength]);
			offset += m+3+shiftLength;
		}
		
		for(int i = 0; i < getWriteHeadCount(); i++) {
			vars.addWrite(Arrays.copyOfRange(flatVars, offset, offset+m), 
					Arrays.copyOfRange(flatVars, offset+m, offset+2*m), 
					Arrays.copyOfRange(flatVars, offset+2*m, offset+3*m),  
					flatVars[offset+3*m], 
					flatVars[offset+3*m+1], 
					Utilities.normalize(Arrays.copyOfRange(flatVars,offset+3*m+2,offset+3*m+2+shiftLength)), 
					flatVars[offset+3*m+2+shiftLength]);
			offset += 3*m+3+shiftLength;
		}
		return vars;
	}
	
	public double[][] processInput(double[] flatVars) {
		return processInput(translateToHeadVars(flatVars));
	}
	
	public double[][] processInput(HeadVariables vars){
		if(vars.getRead().size() != getReadHeadCount() 
				|| vars.getWrite().size() != getWriteHeadCount())
			throw new IllegalArgumentException("You must define as many read and write heads as when the TM was created.");

		// First all WRITES
		//System.out.println("Erasing");
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
		//System.out.println("Adding");
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
			currentStep = new TuringTimeStep();
			for (int i = 0; i < getWriteHeadCount(); i++){
				currentStep.getWriteHeads().add(new HeadTimeStep(writeWeightings[i], vars.getWrite().get(i).getAdd()));
			}
			
			for (int i = 0; i < getReadHeadCount(); i++){
				currentStep.getReadHeads().add(new HeadTimeStep(readWeightings[i], result[i]));
			}
		}
		
		return result;
	}
	/*
	public double[][] getReadWeightings() {
		return readWeightings;
	}

	public double[][] getWriteWeightings() {
		return writeWeightings;
	}
	
	public ArrayList<double[]> getTape(){
		return tape;
	}
	
	public int getM(){
		return m;
	}
	
	public int getN(){
		return n;
	}
	*/
	public static class TuringTimeStep{
		
		List<HeadTimeStep> writeHeads = new ArrayList<HeadTimeStep>();
		List<HeadTimeStep> readHeads = new ArrayList<HeadTimeStep>();
		//TODO: Possible store a snapshot of the tape
		
		private TuringTimeStep(){}

		public List<HeadTimeStep> getWriteHeads() {
			return writeHeads;
		}

		public List<HeadTimeStep> getReadHeads() {
			return readHeads;
		}
		
		
	}
	
	public static class HeadTimeStep{
		public final double[] weights;
		public final double[] value;
		
		public HeadTimeStep(double[] weights, double[] value){
			this.weights = new double[weights.length];
			System.arraycopy(weights, 0, this.weights, 0, weights.length);
			this.value = new double[value.length];
			System.arraycopy(value, 0, this.value, 0, value.length);
		}
	}
	
	public TuringTimeStep getLastTimeStep(){
		return currentStep;
	}
	
	// Wrapper of variables for heads.
	
	private double[] weighting(Head current, double[] oldWeight) {
		double[] result = new double[n];
		
		// Focusing by Content
		double sum = 0.0;
		for(int i = 0; i < n; i++){
			double similarity =  Utilities.simpleSimilarity(current.getKey(),tape.get(i)); //cosineSim(
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

	public static double cosineSim(double[] key, double[] inMemory) {
		//0 test
		boolean allZeros = true;
		for (double d : key) if (d != 0) allZeros = false;
		for (double d : inMemory) if (d != 0) allZeros = false;
		if (allZeros) return 1;
		
		ArrayRealVector vecA = new ArrayRealVector(key);
		ArrayRealVector vecB = new ArrayRealVector(inMemory);
		
		double similarity = vecA.dotProduct(vecB) / (vecA.getNorm() * vecB.getNorm());
		
		return similarity;
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
