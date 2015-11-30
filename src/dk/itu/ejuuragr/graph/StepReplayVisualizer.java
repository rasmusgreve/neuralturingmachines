package dk.itu.ejuuragr.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import dk.itu.ejuuragr.replay.Recording;
import dk.itu.ejuuragr.replay.TimeStep;
import dk.itu.ejuuragr.replay.TuringTimeStep;
import dk.itu.ejuuragr.turing.GravesTuringMachine.GravesTuringMachineTimeStep;
import dk.itu.ejuuragr.turing.MinimalTuringMachine.MinimalTuringMachineTimeStep;

/**
 * Enables Visualizers to step around in the replay
 * such that the memory usage can be explored.
 * 
 * @author Rasmus
 *
 */
public class StepReplayVisualizer extends AbstractReplayVisualizer {

	protected int stepIndex = 0;

	public StepReplayVisualizer(final Recording<?> recording) {
		super(recording);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					stepIndex = Math.max(0, stepIndex - 1);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					stepIndex = Math.min(recording.size() - 1, stepIndex + 1);
				repaint();
			}
		});
		pixelSize = 50;
	}

	private int N, M;

	@Override
	protected void paint(Graphics2D g) {
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString("ID: " + stepIndex, 20, 20);
		TimeStep<?> step = recording.get(stepIndex);
		TuringTimeStep turingStep = step.getTuringStep();

		double[][] tmContent = step.getTuringMachineContent();
		M = tmContent[0].length;
		N = tmContent.length;

		// Memory content
		drawMemoryContent(g, step);

		double[] writeWeights = null;
		double[] readWeights = null;
		double[] writeValue = null;
		double[] readValue = null;
		String writeInterp = "-";
		String jumpInterp = "-";
		if (turingStep instanceof GravesTuringMachineTimeStep){
			GravesTuringMachineTimeStep gtmts = (GravesTuringMachineTimeStep)turingStep;
			writeWeights = gtmts.getWriteHeads().get(0).weights;
			readWeights = gtmts.getReadHeads().get(0).weights;
			writeValue = gtmts.getWriteHeads().get(0).value;
			readValue = gtmts.getReadHeads().get(0).value;
		}
		else if (turingStep instanceof MinimalTuringMachineTimeStep){
			MinimalTuringMachineTimeStep mtmts = (MinimalTuringMachineTimeStep)turingStep;
			writeWeights = convertPositionToWeights(mtmts.writePosition, N);
			readWeights = convertPositionToWeights(mtmts.readPosition, N);
			writeValue = mtmts.key;
			readValue = mtmts.read;
			writeInterp = String.format("%.2f", mtmts.writeInterpolation);
			jumpInterp = String.format("%.2f", mtmts.contentJump);
		}

		// Write focus
		drawFocus(g, writeWeights, (M+1) * pixelSize, "Write focus:");

		// Write value
		drawValue(g, writeValue, (N+1) * pixelSize, "Write value");
		
		// Read focus
		drawFocus(g, readWeights, (M+3) * pixelSize, "Read focus:");

		// Read value
		drawValue(g, readValue, (N+3) * pixelSize, "Read value");
		
		// Step #
		g.setColor(Color.black);
		g.drawString("Idx: " + stepIndex, (N + 3) * pixelSize, (M + 3) * pixelSize);
		
		//Write interp
		g.drawString("W?: " + writeInterp, (N+1) * pixelSize + 15, (M+1) * pixelSize + 30);
		//Jump interp
		g.drawString("J?: " + jumpInterp, (N+1) * pixelSize + 15, (M+3) * pixelSize + 30);
		
	}
	
	private double[] convertPositionToWeights(int position, int length) {
		double[] result = new double[length];
		result[position] = 1;
		return result;
	}

	
	private void drawMemoryContent(Graphics2D g, TimeStep<?> timeStep) {
		double[][] tmContent = timeStep.getTuringMachineContent();

		// Memory content
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < M; m++) {
				double value = tmContent[n][m];
				g.setColor(valueToColor(value));
				g.fillRect(n * pixelSize, m * pixelSize, pixelSize, pixelSize);
				g.setColor(Color.black);
				g.drawString(String.format("%.2f", value).substring(0), n * pixelSize + 2, m * pixelSize + pixelSize - 5);
			}
		}
	}


	private void drawFocus(Graphics2D g, double[] weights, int y, String title) {
		g.setColor(Color.black);
		g.drawString(title, 10, y - 5);

		for (int n = 0; n < N; n++) {
			double weight = weights[n];
			g.setColor(weightToColor(weight));
			g.fillRect(n * pixelSize, y, pixelSize, pixelSize);
		}
	}
	
	private void drawValue(Graphics2D g, double[] value, int x, String title){
		g.setColor(Color.black);
		g.drawString(title, x, (M) * pixelSize + 30);

		for (int m = 0; m < M; m++) {
			double v = value[m];
			g.setColor(valueToColor(v));
			g.fillRect(x+20, m * pixelSize, pixelSize, pixelSize);
			g.setColor(Color.black);
			g.drawString(String.format("%.2f", v).substring(0), x + 2+20, m * pixelSize + pixelSize - 5);
		}
	}

	
	
}
