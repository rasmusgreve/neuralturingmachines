package com.anji.nn;

public class SigmoidSteepActivationFunction extends SigmoidActivationFunction {
	
	/**
	 * identifying string
	 */
	public final static String NAME = ActivationFunctionType.SIGMOID_STEEP.toString();
	
	public SigmoidSteepActivationFunction() {
		this.SLOPE = 8.0d;
	}
}
