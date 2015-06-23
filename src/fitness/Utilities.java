package fitness;


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
}
