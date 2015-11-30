package dk.itu.ejuuragr.graph;

import java.awt.Color;
import java.awt.Graphics2D;

import dk.itu.ejuuragr.replay.Recording;
import dk.itu.ejuuragr.replay.TimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.GravesTuringMachineTimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.HeadTimeStep;

/**
 * This visualizer is only suited for visualizing Graves Turing Machine recordings
 * because the main feature of this visualization is that it shows the read/write focus
 * which is only present in Graves.
 * 
 * If you try to visualize another recording you will get a ClassCastException
 * 
 * @author Rasmus
 * 
 */

public class StaticReplayVisualizer extends AbstractReplayVisualizer{
	
	public StaticReplayVisualizer(Recording<?> recording) {
		super(recording);
	}


	private enum Type{WRITE,READ;}
	
	//returns height of what is drawn
	private int drawFocus(Graphics2D g, Type headType, int headIndex, int startX, int startY){
		g.setColor(Color.black);
		g.drawString(headType + " head #" + headIndex + " focus", startX, startY+20);
		startY += 30;
		
		int height = 0;
		for (int timeStep = 0; timeStep < recording.size(); timeStep++){
			TimeStep<GravesTuringMachineTimeStep> step = (TimeStep<GravesTuringMachineTimeStep>)recording.get(timeStep);
			
			//Get correct head
			HeadTimeStep hst;
			if (headType == Type.WRITE)
				hst = step.getTuringStep().getWriteHeads().get(headIndex);
			else
				hst = step.getTuringStep().getReadHeads().get(headIndex);
			
			
			for (int memoryLocation = 0; memoryLocation < hst.weights.length; memoryLocation++){
				double weight = hst.weights[memoryLocation];
				
				g.setColor(weightToColor(weight));
				g.fillRect(startX + timeStep * pixelSize, startY + memoryLocation * pixelSize, pixelSize, pixelSize);
				height += pixelSize;
			}
		}
		
		return height / recording.size() + 30;
	}
	
	private int drawValue(Graphics2D g, Type headType, int headIndex, int startX, int startY){
		g.setColor(Color.black);
		g.drawString(headType + " head #" + headIndex + " value", startX, startY+20);
		startY += 30;
		
		int height = 0;
		for (int timeStep = 0; timeStep < recording.size(); timeStep++){
			TimeStep<GravesTuringMachineTimeStep> step = (TimeStep<GravesTuringMachineTimeStep>)recording.get(timeStep);
			
			//Get correct head
			HeadTimeStep hst;
			if (headType == Type.WRITE)
				hst = step.getTuringStep().getWriteHeads().get(headIndex);
			else
				hst = step.getTuringStep().getReadHeads().get(headIndex);
			
			for (int memoryLocation = 0; memoryLocation < hst.value.length; memoryLocation++){
				double value = hst.value[memoryLocation];
				
				g.setColor(valueToColor(value));
				g.fillRect(timeStep*pixelSize, startY+ memoryLocation*pixelSize, pixelSize, pixelSize);
				
				height += pixelSize;
				
				g.setColor(Color.black);
				g.drawString(String.format("%.1f", value).substring(0), timeStep*pixelSize+2, startY+memoryLocation*pixelSize + pixelSize - 5);
			}
			
		}
		return height / recording.size() + 30;
	}
	

	@Override
	protected void paint(Graphics2D g) {
		int yshift = 0;
		
		yshift += drawFocus(g, Type.WRITE, 0, 0, yshift);
		yshift += drawValue(g, Type.WRITE, 0, 0, yshift);
		
		yshift += drawFocus(g, Type.READ, 0, 0, yshift);
		yshift += drawValue(g, Type.READ, 0, 0, yshift);
	}
	
}
