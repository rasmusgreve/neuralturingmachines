package dk.itu.ejuuragr.replay;

/**
 * A specific TimeStep for the ANN input
 * and output.
 * 
 * @author Rasmus
 *
 * @param <T>
 */
public class TimeStep<T extends TuringTimeStep>{
	T turingStep;
	double[] domainInput;
	double[][] turingMachineContent;
	double[] domainOuput; //TODO: Might be interesting at some point
	
	public TimeStep(){
		
	}
	
	public TimeStep(T turingStep){
		this.turingStep = turingStep;
	}
	
	public T getTuringStep() {
		return turingStep;
	}
	public void setTuringMachineContent(double[][] content){
		turingMachineContent = content;
	}
	public double[][] getTuringMachineContent(){
		return turingMachineContent;
	}
	public void setTuringStep(T turingStep) {
		this.turingStep = turingStep;
	}
	public double[] getDomainInput() {
		return domainInput;
	}
	public double[] getDomainOutput() {
		return domainOuput;
	}
	public void setDomainInput(double[] domainInput) {
		this.domainInput = domainInput;
	}
	public void setDomainOutput(double[] domainOutput){
		this.domainOuput = domainOutput;
	}
	
	
}