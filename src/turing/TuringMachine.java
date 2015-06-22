package turing;

import java.util.ArrayList;

import turing.TuringMachine.HeadVariables.Head;

public class TuringMachine {
	
	/*int getInputCount();
	int getOutputCount();*/
	
	private ArrayList<double[]> tape;
	private int m;
	private int n;

	public TuringMachine(int n, int m){
		this.n = n;
		this.m = m;
		tape = new ArrayList<double[]>(n);
		for(int i = 0; i < n; i++)
			tape.add(new double[m]);
	}

	public double[][] processInput(HeadVariables vars){
		// First all WRITES
		double[][] weightings = new double[vars.getWrite().size()][];
		
		// Erase
		for(int i = 0; i < vars.getWrite().size(); i++){
			Head current = vars.getWrite().get(i);
			weightings[i] = weighting(current);
			
			for(int k = 0; k < tape.size(); k++){
				for(int j = 0; j < m; j++){
					tape.get(k)[j] *= (1.0 - weightings[i][k] * current.getErase()[j]);
				}
			}
		}
		// Add
		for(int i = 0; i < vars.getWrite().size(); i++){
			Head current = vars.getWrite().get(i);
			
			for(int k = 0; k < tape.size(); k++){
				for(int j = 0; j < m; j++){
					tape.get(k)[j] += weightings[i][k] * current.getAdd()[j];
				}
			}
		}
		
		// prepare result
		double[][] result = new double[vars.getRead().size()][];
		
		// perform READS and get result
		for(int i = 0; i < vars.getRead().size(); i++){
			double[] readWeighting = weighting(vars.getRead().get(i));
			double[] readM = new double[m];
			for(int j = 0; j < m; j++){
				for(int k = 0; k < tape.size(); k++){
					readM[j] += readWeighting[k] * tape.get(k)[j]; 
				}
			}
			
			result[i] = readM;
		}
		
		return result;
	}
	
	// Wrapper of variables for heads.
	
	private double[] weighting(Head current) {
		// TODO Auto-generated method stub
		return null;
	}

	public class HeadVariables {
		
		private ArrayList<Head> read;
		private ArrayList<Head> write;

		public HeadVariables() {
			this.read = new ArrayList<Head>();
			this.write = new ArrayList<Head>();
		}
		
		public void addRead(double[] key, double keyStrength, double interpolation, double shift, double sharp){
			read.add(new Head(null,null,key,keyStrength,interpolation,shift,sharp));
		}
		
		public void addWrite(double[] erase, double[] add, double[] key, double keyStrength, double interpolation, double shift, double sharp){
			write.add(new Head(erase,add,key,keyStrength,interpolation,shift,sharp));
		}
		
		public ArrayList<Head> getRead() {
			return read;
		}

		public ArrayList<Head> getWrite() {
			return write;
		}



		public class Head {
			
			private double[] erase;
			private double[] add;
			private double[] key;
			private double keyStrength;
			private double interpolation;
			private double shift;
			private double sharp;

			public Head(double[] erase, double[] add, double[] key, double keyStrength, double interpolation, double shift, double sharp) {
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

			public double getShift() {
				return shift;
			}

			public double getSharp() {
				return sharp;
			}
		}
	}
}
