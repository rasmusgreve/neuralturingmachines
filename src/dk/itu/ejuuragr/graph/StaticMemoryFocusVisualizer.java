package dk.itu.ejuuragr.graph;

import java.awt.Color;
import java.awt.Graphics2D;

import dk.itu.ejuuragr.replay.Recording;
import dk.itu.ejuuragr.replay.TimeStep;
import dk.itu.ejuuragr.replay.TuringTimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.GravesTuringMachineTimeStep;
import dk.itu.ejuuragr.turing.MinimalTuringMachine.MinimalTuringMachineTimeStep;

/**
 * Visualizes the memory usage for a memory
 * model which does not expand runtime.
 * 
 * @author Rasmus
 *
 */
public class StaticMemoryFocusVisualizer extends AbstractReplayVisualizer {

	
	public StaticMemoryFocusVisualizer(Recording<?> recording) {
		super(recording);
		frame.setSize(3000, 1500);
	}

	
	
	
	@Override
	protected void paint(Graphics2D g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, 20000, 20000);
		
		
		int pixelSize = 35;
		
		//Write
		for (int i = 0; i < recording.size();i++){
			TimeStep<?> t = recording.get(i); 
			int y = pixelSize;
			int x = pixelSize;
			y += drawBWLine(g, t.getDomainInput(), x+ i * pixelSize, y, pixelSize) + pixelSize;
			y += drawColorLine(g, getWrite(t.getTuringStep()), x + i * pixelSize, y, pixelSize) + pixelSize;
			y += drawBWLine(g, getWriteFocus(t.getTuringStep()), x + i * pixelSize, y, pixelSize) + pixelSize;
		}
		
		//Borders
		g.setColor(Color.black);
		int borderX = pixelSize;
		int borderY = pixelSize;
		int borderW = recording.size()*pixelSize;
		int borderH = recording.get(0).getDomainInput().length * pixelSize;
		g.drawRect(borderX, borderY, borderW, borderH);
		borderY += borderH + pixelSize;
		borderH = getWrite(recording.get(0).getTuringStep()).length * pixelSize;
		g.drawRect(borderX, borderY, borderW, borderH);
		borderY += borderH + pixelSize;
		borderH = getWriteFocus(recording.get(0).getTuringStep()).length * pixelSize;
		g.drawRect(borderX, borderY, borderW, borderH);
		
		//Read
		for (int i = 0; i < recording.size();i++){
			TimeStep<?> t = recording.get(i); 
			int y = pixelSize * 13;//* 3;
			int x = pixelSize ;//* (recording.size() + 2); //TODO
			y += drawBWLine(g, t.getDomainOutput(), x + i * pixelSize, y, pixelSize) + pixelSize;
			y += drawColorLine(g, getRead(t.getTuringStep()), x + i * pixelSize, y, pixelSize) + pixelSize;
			y += drawBWLine(g, getReadFocus(t.getTuringStep()), x + i * pixelSize, y, pixelSize) + pixelSize;
		}
		
		//Borders
		g.setColor(Color.black);
		borderX = borderW + pixelSize * 2;
		borderY = pixelSize * 3;
		
		borderX = pixelSize;
		borderY = pixelSize * 13;
		
		borderW = recording.size()*pixelSize;
		borderH = recording.get(0).getDomainOutput().length * pixelSize;
		g.drawRect(borderX, borderY, borderW, borderH);
		borderY += borderH + pixelSize;
		borderH = getRead(recording.get(0).getTuringStep()).length * pixelSize;
		g.drawRect(borderX, borderY, borderW, borderH);
		borderY += borderH + pixelSize;
		borderH = getReadFocus(recording.get(0).getTuringStep()).length * pixelSize;
		g.drawRect(borderX, borderY, borderW, borderH);
		
	}
	
	private double[] getWriteFocus(TuringTimeStep step){
		if (step instanceof MinimalTuringMachineTimeStep){
			MinimalTuringMachineTimeStep ts = (MinimalTuringMachineTimeStep)step;
			int N = recording.get(recording.size()-1).getTuringMachineContent().length;
			int zeroPosition = ((MinimalTuringMachineTimeStep)recording.get(recording.size()-1).getTuringStep()).writeZeroPosition;
			return convertPositionToWeights(ts.correctedWritePosition + zeroPosition,N);
		}
		else if (step instanceof GravesTuringMachineTimeStep){
			GravesTuringMachineTimeStep ts = (GravesTuringMachineTimeStep)step;
			return ts.getWriteHeads().get(0).weights; //Note: Assumes single write head
		}
		else {
			throw new IllegalArgumentException("Unknown time step type: " + step.getClass().getName());
		}
	}
	
	private double[] getReadFocus(TuringTimeStep step){
		if (step instanceof MinimalTuringMachineTimeStep){
			MinimalTuringMachineTimeStep ts = (MinimalTuringMachineTimeStep)step;
			int N = recording.get(recording.size()-1).getTuringMachineContent().length;
			int zeroPosition = ((MinimalTuringMachineTimeStep)recording.get(recording.size()-1).getTuringStep()).readZeroPosition;
			return convertPositionToWeights(ts.correctedReadPosition + zeroPosition,N);
		}
		else if (step instanceof GravesTuringMachineTimeStep){
			GravesTuringMachineTimeStep ts = (GravesTuringMachineTimeStep)step;
			return ts.getReadHeads().get(0).weights; //Note: Assumes single write head
		}
		else {
			throw new IllegalArgumentException("Unknown time step type: " + step.getClass().getName());
		}
	}
	
	private double[] getWrite(TuringTimeStep step){
		if (step instanceof MinimalTuringMachineTimeStep){
			MinimalTuringMachineTimeStep ts = (MinimalTuringMachineTimeStep)step;
			return ts.key;
		}
		else if (step instanceof GravesTuringMachineTimeStep){
			GravesTuringMachineTimeStep ts = (GravesTuringMachineTimeStep)step;
			return ts.getWriteHeads().get(0).value; //Note: Assumes single write head
		}
		else {
			throw new IllegalArgumentException("Unknown time step type: " + step.getClass().getName());
		}
	}
	
	private double[] getRead(TuringTimeStep step){
		if (step instanceof MinimalTuringMachineTimeStep){
			MinimalTuringMachineTimeStep ts = (MinimalTuringMachineTimeStep)step;
			return ts.read;
		}
		else if (step instanceof GravesTuringMachineTimeStep){
			GravesTuringMachineTimeStep ts = (GravesTuringMachineTimeStep)step;
			return ts.getReadHeads().get(0).value; //Note: Assumes single write head
		}
		else {
			throw new IllegalArgumentException("Unknown time step type: " + step.getClass().getName());
		}
	}
	

	private double[] convertPositionToWeights(int position, int length) {
		double[] result = new double[length];
		result[position] = 1;
		return result;
	}
	
	
	private int drawBWLine(Graphics2D g, double[] data, int x, int y, int pixelSize){
		for (int i = 0; i < data.length; i++){
			g.setColor(weightToColor(data[i]));
			g.fillRect(x, y+i*pixelSize, pixelSize, pixelSize);
		}
		return data.length * pixelSize;
	}
	
	private int drawColorLine(Graphics2D g, double[] data, int x, int y, int pixelSize) {
		for (int i = 0; i < data.length; i++){
			g.setColor(valueToColor(data[i]));
			g.fillRect(x, y+i*pixelSize, pixelSize, pixelSize);
			g.setColor(Color.black);
			g.drawString(String.format("%.3f",data[i]), x, y+i*pixelSize+pixelSize);
		}
		return data.length * pixelSize;
	}
	
	
	
	
	
}
