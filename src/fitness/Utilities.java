package fitness;

import java.util.Arrays;


public class Utilities {

	public static double[] flatten(double[][] arrays){
		int offset = 0;
		double[] result = new double[totalLength(arrays)];
		for(int i = 0; i < arrays.length; i++) {
			System.arraycopy(arrays[i], 0, result, offset, arrays[i].length);
			offset += arrays[i].length;
		}
		return result;
	}
	
	public static void copy(double[] fromArray, double[] toArray, int offset) {
		System.arraycopy(fromArray,0,toArray,offset,fromArray.length);
	}
	
	/*public static void copy(double[] fromArray, double[] toArray, int offset) {
		if(toArray.length < fromArray.length + offset)
			throw new IndexOutOfBoundsException("Too much content in fromArray for the toArray and offset");
		
		for(int i = 0; i < fromArray.length; i++){
			toArray[offset + i] = fromArray[i];
		}
	}
	
	public static void copy(double[][] fromArrays, double[] toArray, int offset) {
		if(toArray.length < totalLength(fromArrays) + offset)
			throw new IndexOutOfBoundsException("Too much content in fromArrays for the toArray and offset");
		
		int index = offset;
		for(int i = 0; i < fromArrays.length; i++){
			for(int j = 0; i < fromArrays[i].length; j++){
				toArray[index++] = fromArrays[i][j];
			}
		}
	}*/

	public static int totalLength(double[][] arrays) {
		int count = 0;
		for(int i = 0; i < arrays.length; i++)
			count += arrays[i].length;
		return count;
	}

	/**
	 * Return a new array of the same length where all
	 * elements sum to 1.0 and the relation between the
	 * original elements are preserved.
	 * @param array
	 * @return
	 */
	public static double[] normalize(double[] array) {
		double[] result = new double[array.length];
		double sum = 0.0;
		for(int i = 0; i < array.length; i++)
			sum += array[i];
		for(int i = 0; i < array.length; i++)
			result[i] = array[i] / sum;
		return result;
	}
	
	/**
	 * Compares two vectors and calculates a similarity between them.
	 * Only works for strictly positive numbers each between 0.0 and 1.0.
	 * @param v1 the first vector
	 * @param v2 the second vector
	 * @return A number between 0.0 and 1.0 of how similar the two vectors
	 * are (in the space of each variable being between 0.0 and 1.0).
	 */
	public static double simpleSimilarity(double[] v1, double[] v2){
		if(v1.length != v2.length)
			throw new IllegalArgumentException("The arrays must be of the same length");
		
		double numerator = 0;
		for(int i = 0; i < v1.length; i++)
			numerator += Math.abs(v1[i] - v2[i]);
		
		return 1.0 - (numerator / 5);
	}
	
	/**
	 * Just testing some stuff
	 * @param args
	 */
	public static void main(String[] args) {
		double[] v1 = new double[]{0,0,0,0,0};
		double[] v2 = new double[]{0.5,0.5,0.5,0.5,0.5};
		double[] v3 = new double[]{1,1,1,1,1};
		double[] v4 = new double[]{0,0.25,0.5,0.75,1};
		double[] v5 = new double[]{1,0.75,0.5,0.25,0};
		
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v2)+": "+simpleSimilarity(v1,v2));
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v3)+": "+simpleSimilarity(v1,v3));
		System.out.println(Arrays.toString(v2)+" VS "+Arrays.toString(v3)+": "+simpleSimilarity(v2,v3));
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v1)+": "+simpleSimilarity(v1,v1));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v3)+": "+simpleSimilarity(v3,v3));
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v4)+": "+simpleSimilarity(v1,v4));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v4)+": "+simpleSimilarity(v1,v4));
		System.out.println(Arrays.toString(v4)+" VS "+Arrays.toString(v5)+": "+simpleSimilarity(v4,v5));
		System.out.println(Arrays.toString(v5)+" VS "+Arrays.toString(v4)+": "+simpleSimilarity(v5,v4));
	}
}
