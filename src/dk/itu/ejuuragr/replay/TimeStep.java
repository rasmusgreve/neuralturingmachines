package dk.itu.ejuuragr.replay;


public class TimeStep<T extends TuringTimeStep>{
	T turingStep;
	double[] domainInput;
	double[][] turingMachineContent;
	//double[] domainOuput; //TODO: Might be interesting at some point
	
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
	public void setDomainInput(double[] domainInput) {
		this.domainInput = domainInput;
	}
	
}