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
		frame.setSize(pixelSize*(steps.size()+1), 500);
		frame.setVisible(true);
		
		MyComponent comp = new MyComponent();
		comp.setSteps(steps);
		frame.add(comp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public class MyComponent extends JComponent{
		
		private static final long serialVersionUID = 1L;
		
		List<TimeStep> steps;
		
		public void setSteps(List<TimeStep> steps){
			this.steps = steps;
		}
		
		@Override
		public void paint(Graphics arg0) {
			Graphics2D g = (Graphics2D) arg0;
			
			//Clear
			g.setColor(Color.white);
			g.fillRect(0, 0, 500, 500);
			
			for (int timeStep = 0; timeStep < steps.size(); timeStep++){
				TimeStep step = steps.get(timeStep);
				
				HeadTimeStep hst = step.getTuringStep().getWriteHeads().get(0);
				for (int memoryLocation = 0; memoryLocation < hst.weights.length; memoryLocation++){
					double weight = hst.weights[memoryLocation];
					
					//Apply gamma correction to improve readability
					double gamma = .43;
					double corrected = Math.pow(weight, gamma);
					
					int gsv = (int)(corrected*255);
					g.setColor(new Color(gsv, gsv, gsv));
					g.fillRect(timeStep*pixelSize, memoryLocation*pixelSize, pixelSize, pixelSize);
				}
				
			}
			
			
		}
	}
	
}
