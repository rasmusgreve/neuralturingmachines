package graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import run.Replay.TimeStep;
import turing.TuringMachine.HeadTimeStep;

public class ReplayVisualizer {

	public final static int pixelSize = 20;
	
	public void show(List<TimeStep> steps){
		JFrame frame = new JFrame();
		frame.setSize(pixelSize*(steps.size()+1), 800);
		frame.setVisible(true);
		
		MyComponent comp = new MyComponent();
		comp.setSteps(steps);
		frame.add(comp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	
	private Color weightToColor(double weight){
		double gamma = .43;
		double corrected = Math.pow(weight, gamma);
		int gsv = (int)(corrected*255);
		return new Color(gsv, gsv, gsv);
	}
	
	private Color valueToColor(double value){
		value *= 0.7;
		return Color.getHSBColor((float)value, 1, 1);
	}
	
	private enum Type{WRITE,READ;}
	
	public class MyComponent extends JComponent{
		
		private static final long serialVersionUID = 1L;
		
		List<TimeStep> steps;
		
		public void setSteps(List<TimeStep> steps){
			this.steps = steps;
		}
		
		
		//returns height of what is drawn
		private int drawFocus(Graphics2D g, Type headType, int headIndex, int startX, int startY){
			g.setColor(Color.black);
			g.drawString(headType + " head #" + headIndex + " focus", startX, startY+20);
			startY += 30;
			
			int height = 0;
			for (int timeStep = 0; timeStep < steps.size(); timeStep++){
				TimeStep step = steps.get(timeStep);
				
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
			
			return height / steps.size() + 30;
		}
		
		private int drawValue(Graphics2D g, Type headType, int headIndex, int startX, int startY){
			g.setColor(Color.black);
			g.drawString(headType + " head #" + headIndex + " value", startX, startY+20);
			startY += 30;
			
			int height = 0;
			for (int timeStep = 0; timeStep < steps.size(); timeStep++){
				TimeStep step = steps.get(timeStep);
				
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
			return height / steps.size() + 30;
		}
		
		
		@Override
		public void paint(Graphics arg0) {
			Graphics2D g = (Graphics2D) arg0;
			

			//Clear
			g.setColor(Color.white);
			g.fillRect(0, 0, 5000, 5000);
			
			int yshift = 0;
			
			yshift += drawFocus(g, Type.WRITE, 0, 0, yshift);
			yshift += drawValue(g, Type.WRITE, 0, 0, yshift);
			
			yshift += drawFocus(g, Type.READ, 0, 0, yshift);
			yshift += drawValue(g, Type.READ, 0, 0, yshift);
			
		}
	}
	
}
