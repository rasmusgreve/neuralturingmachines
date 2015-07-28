package dk.itu.ejuuragr.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import dk.itu.ejuuragr.fitness.Utilities;
import dk.itu.ejuuragr.replay.TimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.HeadTimeStep;

public class ReplayStepVisualizer {

public final static int pixelSize = 50;
	
	public void show(final List<TimeStep> steps){
		JFrame frame = new JFrame();
		frame.setSize(800, 800);
		frame.setVisible(true);
		
		final MyComponent comp = new MyComponent();
		frame.add(comp);
		comp.setStep(0, steps.get(0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(new KeyListener() {
			int step = 0;
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					step = Math.max(0, step-1);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					step = Math.min(steps.size()-1, step +1);
				comp.setStep(step, steps.get(step));
			}
		});
		
	}

	private static Color weightToColor(double weight){
		double gamma = .43;
		double corrected = Math.pow(weight, gamma);
		int gsv = (int)(corrected*255);
		return new Color(gsv, gsv, gsv);
	}
	
	private static Color valueToColor(double value){
		value *= 0.7;
		return Color.getHSBColor((float)value, 1, 1);
	}
	

	private enum Type{WRITE,READ;}
	
	
	public class MyComponent extends JComponent{
			
		private static final long serialVersionUID = 1L;
		
		int stepIndex;
		TimeStep step;
		
		public void setStep(int stepIndex, TimeStep step){
			this.step = step;
			this.stepIndex = stepIndex;
			repaint();
		}
		
		@Override
		public void paint(Graphics arg0) {
			Graphics2D g = (Graphics2D) arg0;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			//Clear
			g.setColor(Color.white);
			g.fillRect(0, 0, 5000, 5000);
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			g.drawString("ID: " + stepIndex, 20, 20);
			
			double[][] tmContent = step.getTuringMachineContent();
			
			final int M = tmContent[0].length;
			final int N = tmContent.length;
			
			
			//Memory content
			for (int n = 0; n < N; n++){
				for (int m = 0; m < M; m++){
					double value = tmContent[n][m];
					g.setColor(valueToColor(value));
					g.fillRect(n*pixelSize, m*pixelSize, pixelSize, pixelSize);

					g.setColor(Color.black);
					g.drawString(String.format("%.2f", value).substring(0), n*pixelSize+2, m*pixelSize + pixelSize - 5);
				}
			}
			
			//Write focus
			g.setColor(Color.black);
			g.drawString("Write focus:", 10, (M+1) * pixelSize - 5);
			
			HeadTimeStep hst = step.getTuringStep().getWriteHeads().get(0);
			
			for (int n = 0; n < N; n++){
				double weight = hst.weights[n];
				g.setColor(weightToColor(weight));
				g.fillRect(n * pixelSize, (M+1) * pixelSize, pixelSize, pixelSize);
			}
			
			//Write value
			g.setColor(Color.black);
			g.drawString("Write value", N * pixelSize, (M) * pixelSize + 30);
			
			for (int m = 0; m < M; m++){
				double value = hst.value[m];
				g.setColor(valueToColor(value));
				g.fillRect((N+1) * pixelSize, m*pixelSize, pixelSize, pixelSize);
				g.setColor(Color.black);
				g.drawString(String.format("%.2f", value).substring(0), (N+1)*pixelSize+2, m*pixelSize + pixelSize - 5);
			}
			
			//Read focus
			g.setColor(Color.black);
			g.drawString("Read focus:", 10, (M+3) * pixelSize - 5);
			
			hst = step.getTuringStep().getReadHeads().get(0);
			
			for (int n = 0; n < N; n++){
				double weight = hst.weights[n];
				g.setColor(weightToColor(weight));
				g.fillRect(n * pixelSize, (M+3) * pixelSize, pixelSize, pixelSize);
			}
			
			//Read value
			g.setColor(Color.black);
			g.drawString("Read value", (N+3) * pixelSize, (M) * pixelSize + 30);
			
			for (int m = 0; m < M; m++){
				double value = hst.value[m];
				g.setColor(valueToColor(value));
				g.fillRect((N+3) * pixelSize, m*pixelSize, pixelSize, pixelSize);
				g.setColor(Color.black);
				g.drawString(String.format("%.2f", value).substring(0), (N+3)*pixelSize+2, m*pixelSize + pixelSize - 5);
			}
			
			//Step #
			g.setColor(Color.black);
			g.drawString("Idx: " + stepIndex, (N+3) * pixelSize, (M+3) * pixelSize);
			
			//System.out.println(Utilities.toString(step.getDomainInput()));
			
			
			
			int yshift = 0;
			
			//TODO: Hardcoded that there is exactly one write and one read head
			/*
			yshift += drawFocus(g, Type.WRITE, 0, 0, yshift);
			yshift += drawValue(g, Type.WRITE, 0, 0, yshift);
			
			yshift += drawFocus(g, Type.READ, 0, 0, yshift);
			yshift += drawValue(g, Type.READ, 0, 0, yshift);
			*/
		}
	}
}
