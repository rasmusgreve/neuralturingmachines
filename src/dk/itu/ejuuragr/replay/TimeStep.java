package dk.itu.ejuuragr.replay;

import dk.itu.ejuuragr.turing.GravesTuringMachine.TuringTimeStep;

public class TimeStep{
	TuringTimeStep turingStep;
	double[] domainInput;
	//double[] domainOuput; //TODO: Might be interesting at some point
	public TuringTimeStep getTuringStep() {
		return turingStep;
	}
	public void setTuringStep(TuringTimeStep turingStep) {
		this.turingStep = turingStep;
	}
	public double[] getDomainInput() {
		return domainInput;
	}
	public void setDomainInput(double[] domainInput) {
		this.domainInput = domainInput;
	}
	
}