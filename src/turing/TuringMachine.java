package turing;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.linear.*;

import fitness.Utilities;
import turing.TuringMachine.HeadVariables.Head;

public class TuringMachine {
	
	private ArrayList<double[]> tape;
	private int m;
	private int n;
	private double[][] readWeightings;
	private double[][] writeWeightings;
	private int shiftLength;
	private int readHeads;
	private int writeHeads;

	public TuringMachine(int n, int m, int readHeads, int writeHeads, int shiftLength){
		this.n = n;
		this.m = m;
		this.readHeads = readHeads;
		this.writeHeads = writeHeads;
		this.shiftLength = shiftLength;
		
		this.reset();
	}
	
	public void reset() {
		// Initialize memory tape
		tape = new ArrayList<double[]>(n);
		for(int i = 0; i < n; i++)
			tape.add(new double[m]);
		
		// save weightings for future iterations
		readWeightings = new double[readHeads][];
		for (int i = 0; i < readHeads; i++)
			readWeightings[i] = new double[n];
		writeWeightings = new double[writeHeads][];
		for (int i = 0; i < writeHeads; i++)
			writeWeightings[i] = new double[n];
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
	
	public double[][] processInput(double[] flatVars) {
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
		
		// Actually handle it
		return processInput(vars);
	}
	
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
		
		return result;
	}
	
	// Wrapper of variables for heads.
	
	private double[] weighting(Head current, double[] oldWeight) {
		double[] result = new double[n];
		
		// Focusing by Content
		double sum = 0.0;
		for(int i = 0; i < n; i++){
			result[i] = Math.exp(current.getKeyStrength() * cosineSim(current.getKey(),tape.get(i)));
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
			for(int j = 0; j < n; j++){
				int idx = (shift.length*n + i - j) % shift.length;
				tempWeight[i] += result[j] * shift[idx]; 
				//TODO Check if this is right
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

	private double cosineSim(double[] key, double[] inMemory) {
		//0 test
		boolean allZeros = true;
		for (double d : key) if (d != 0) allZeros = false;
		for (double d : inMemory) if (d != 0) allZeros = false;
		if (allZeros) return 1;
		
		ArrayRealVector vecA = new ArrayRealVector(key);
		ArrayRealVector vecB = new ArrayRealVector(inMemory);
		
		return vecA.dotProduct(vecB) / vecA.getNorm() * vecB.getNorm();
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
