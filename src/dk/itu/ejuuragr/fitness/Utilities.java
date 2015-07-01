package dk.itu.ejuuragr.fitness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;


public class Utilities {

	/**
	 * Will instantiate an object using a constructor with the given parameter types and values.
	 * @param className The full qualifying name of the Class to instantiate.
	 * @param params The list of parameters for the constructor, or null to use the no-args
	 * constructor.
	 * @param constructor The actual types required by the constructor (the given params should
	 * be of these types or subtypes thereof). Can be left as null if the wanted constructor
	 * matches the actual types of the given parameters.
	 * @return The instantiated object which can then be cast to its actual type.
	 */
	public static Object instantiateObject(String className, Object[] params, Class<?>[] constructor) {
		Object result = null;
		try {
			if(params == null || params.length == 0) {
				result = Class.forName(className).newInstance();
			} else {
				Constructor<?> con = Class.forName(className).getDeclaredConstructor(constructor == null ? getClasses(params) : constructor);
				result = con.newInstance(params);
			}
		} catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException | InstantiationException 
				| IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static Class<?>[] getClasses(Object[] objects) {
		Class<?>[] result = new Class[objects.length];
		for(int i = 0; i < objects.length; i++)
			result[i] = objects[i].getClass();
		return result;
	}
	
	/**
	 * Takes a 2D array and returns the same elements
	 * in a 1D array structure.
	 * @param arrays The 2D array to flatten.
	 * @return A 1D array of those arrays appended.
	 */
	public static double[] flatten(double[][] arrays){
		int offset = 0;
		double[] result = new double[totalLength(arrays)];
		for(int i = 0; i < arrays.length; i++) {
			System.arraycopy(arrays[i], 0, result, offset, arrays[i].length);
			offset += arrays[i].length;
		}
		return result;
	}
	
	/**
	 * Copies everything from fromArray to toArray starting.
	 * @param fromArray The array to copy every element from.
	 * @param toArray The array to insert the elements into.
	 * @param offset The index to start at in the toArray.
	 */
	public static void copy(double[] fromArray, double[] toArray, int offset) {
//		System.out.println(Arrays.toString(fromArray)+", "+Arrays.toString(toArray)+" offset="+offset);
		System.arraycopy(fromArray,0,toArray,offset,fromArray.length);
	}

	/**
	 * Count the total number of elements in a 2 dimensional matrix
	 * @param arrays The 2d matrix to count
	 * @return The total number of elements in the 2d matrix
	 */
	public static int totalLength(double[][] arrays) {
		int count = 0;
		for(int i = 0; i < arrays.length; i++)
			count += arrays[i].length;
		return count;
	}
	
	/**
	 * Find the index of the element with the highest value
	 * @param array The array to search through
	 * @return The index of the element with the highest value
	 */
	public static int maxPos(double[] array) {
		int maxpos = 0;
		double value = Double.MIN_VALUE;
		for(int i = 0; i < array.length; i++) {
			if(array[i] > value) {
				maxpos = i;
				value = array[i];
			}
		}
		return maxpos;
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
	 * Normalized manhattan distance:
	 * Compares two vectors and calculates a similarity between them.
	 * Only works for strictly positive numbers each between 0.0 and 1.0.
	 * @param v1 the first vector
	 * @param v2 the second vector
	 * @return A number between 0.0 and 1.0 of how similar the two vectors
	 * are (in the space of each variable being between 0.0 and 1.0).
	 */
	public static double emilarity(double[] v1, double[] v2){
		if(v1.length != v2.length)
			throw new IllegalArgumentException("The arrays must be of the same length");
		
		double numerator = 0;
		for(int i = 0; i < v1.length; i++)
			numerator += Math.abs(v1[i] - v2[i]);
		
		return 1.0 - (numerator / v1.length);
	}
	
	public static double euclideanDistance(double[] v1, double[] v2) {
		double sqSum = 0;
		for(int i = 0; i < v1.length; i++) {
			sqSum += Math.pow(v1[i] - v2[i], 2);
		}
		return Math.sqrt(sqSum);
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
		
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v2)+": "+emilarity(v1,v2));
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v3)+": "+emilarity(v1,v3));
		System.out.println(Arrays.toString(v2)+" VS "+Arrays.toString(v3)+": "+emilarity(v2,v3));
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v1)+": "+emilarity(v1,v1));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v3)+": "+emilarity(v3,v3));
		System.out.println(Arrays.toString(v1)+" VS "+Arrays.toString(v4)+": "+emilarity(v1,v4));
		System.out.println(Arrays.toString(v3)+" VS "+Arrays.toString(v4)+": "+emilarity(v1,v4));
		System.out.println(Arrays.toString(v4)+" VS "+Arrays.toString(v5)+": "+emilarity(v4,v5));
		System.out.println(Arrays.toString(v5)+" VS "+Arrays.toString(v4)+": "+emilarity(v5,v4));
	}
}
