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
	
	public static int totalSum(double[] array) {
		int result = 0;
		for(double d : array) result += d;
		return result;
	}
	
	public static double min(double[] array) {
		double lowest = Double.MAX_VALUE;
		for(double d : array)
			if(d < lowest)
				lowest = d;
		return lowest;
	}
	
	private static double simpleSimilarity(double[] v1, double[] v2){
		if(v1.length != v2.length)
			throw new IllegalArgumentException("The arrays must be of the same length");
		
		if(Utilities.totalSum(v1) + Utilities.totalSum(v2) == 0)
			return 1;
		
		double numerator = 0;
		for(int i = 0; i < v1.length; i++)
			numerator += Math.abs(v1[i] - v2[i]);
		
		double min = Math.min(Utilities.min(v1), Utilities.min(v2));
		
		double denominator = Utilities.totalSum(v1) + Utilities.totalSum(v2);
		if(min < 0.0)
			denominator += -2.0 * min * v1.length;
		
		return 1.0 - (numerator / denominator);
	}
	
	/**
	 * Just testing some stuff
	 * @param args
	 */
	public static void main(String[] args) {
		double[] v1 = new double[]{0,1,2,3,4};
		double[] v2 = new double[]{4,3,2,1,0};
		double[] v3 = new double[]{0,0,0,0,0};
		double[] v4 = new double[]{-1,-1,-1,-1,-1};
		double[] v5 = new double[]{1,1,1,1,1};
		double[] v6 = new double[]{2,2,2,2,2};
		double[] v7 = new double[]{4,4,4,4,4};
		
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v2)+": "+simpleSimilarity(v1,v2));
		System.out.println(Arrays.toString(v2)+" VS "+Arrays.toString(v3)+": "+simpleSimilarity(v2,v3));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v2)+": "+simpleSimilarity(v3,v2));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v4)+": "+simpleSimilarity(v3,v4));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v5)+": "+simpleSimilarity(v3,v5));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v3)+": "+simpleSimilarity(v3,v3));
		System.out.println(Arrays.toString(v5)+" VS "+Arrays.toString(v5)+": "+simpleSimilarity(v5,v5));
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v4)+": "+simpleSimilarity(v1,v4));
		System.out.println(Arrays.toString(v5)+" VS "+Arrays.toString(v6)+": "+simpleSimilarity(v5,v6));
		System.out.println(Arrays.toString(v6)+" VS "+Arrays.toString(v7)+": "+simpleSimilarity(v6,v7));
	}
}
