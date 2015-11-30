package dk.itu.ejuuragr.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.persistence.FilePersistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import dk.itu.ejuuragr.domain.Simulator;
import dk.itu.ejuuragr.fitness.Controller;
import dk.itu.ejuuragr.fitness.Utilities;

/**
 * Visualizes the evaluation of a Copytask
 * agent such that the expected sequence
 * and the one reproduced by the ANN can
 * be compared.
 * 
 * @author Rasmus
 *
 */
public class CopyTaskEvaluationVisualizer {


	protected JFrame frame;
	public static final int[] sequenceLengths = new int[]{10, 20, 30, 50, 120};
	ComparisonComponent component;
	
	
	public CopyTaskEvaluationVisualizer(){
		frame = new JFrame();
		
		frame.setSize(3200,1500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(component = new ComparisonComponent());
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S){
					BufferedImage bi = new BufferedImage(3800, 1500, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = bi.createGraphics();
					component.paint(g2);
					try {
						ImageIO.write(bi, "png", new File("render.png"));
						System.out.println("Written to render.png");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	public void show(){
		frame.setVisible(true);
	}
	
	public void addResult(double[][] target, double[][] output){
		component.addResult(target, output);
		component.repaint();
	}
	
	private class ComparisonComponent extends JComponent{
		private static final long serialVersionUID = 1L;
		
		private class Result{
			public final double[][] target, output;
			public Result(double[][] target, double[][] output){
				this.target = target;
				this.output = output;
			}
		}
		
		private List<Result> results = new ArrayList<Result>();
		
		public void addResult(double[][] target, double[][] output){
			results.add(new Result(target, output));
		}

		@Override
		public void paint(Graphics arg0) {
			Graphics2D g = (Graphics2D) arg0;
			g.setColor(Color.white);
			g.fillRect(0, 0, 50000, 50000);
			int pixelSize = 25;
			int borderWidth = 83;
			
			int maxWidth = 0;
			int totalHeight = 0;
			int rows = 0;
			int x = pixelSize;
			int y = pixelSize*2;
			drawLabels(g, x, y);
			
			x += 220;
			
			Dimension dim = new Dimension(pixelSize, pixelSize);
			for (Result result : results){
				dim = drawResult(g, result, x, y, pixelSize);
				x += borderWidth + dim.width;
				maxWidth = Math.max(maxWidth, x);
				if (x > 2100){
					totalHeight += dim.height + borderWidth;
					y += borderWidth + dim.height;
					x = pixelSize;
					drawLabels(g, x, y);
					x += 220;
					rows++;
				}
			}
			
			drawLegend(g, maxWidth , pixelSize*2, pixelSize * 2, totalHeight - borderWidth - 15);
			
		}
		
		private void drawLabels(Graphics2D g, int x, int y){
			g.setColor(Color.black);
			Font font = new Font("Verdana", Font.PLAIN, 55);
			FontMetrics fm = getFontMetrics(font);
			g.setFont(font);
			int targetWidth = fm.stringWidth("Target");
			g.drawString("Target", x + 180 - targetWidth, y + 125);
			int outputWidth = fm.stringWidth("Output");
			g.drawString("Output", x + 180 - outputWidth, y + 125 + 220);
//			int diffWidth = fm.stringWidth("Diff.");
//			g.drawString("Diff.", x + 180 - diffWidth, y + 125 + 220 * 2);
		}
		
		private Dimension drawResult(Graphics2D g, Result result, int x, int y, int pixelSize){
			Dimension targetDimension = drawData(g, result.target, x, y, pixelSize);
			Dimension outputDimension = drawData(g, result.output, x, y + targetDimension.height + pixelSize, pixelSize);
//			Dimension diffDimension = drawDiff(g, result, x, y + targetDimension.height + outputDimension.height + 2 * pixelSize, pixelSize);
			return new Dimension(
					targetDimension.width, 
					targetDimension.height + outputDimension.height + /*diffDimension.height +*/ pixelSize * 2);
		}
		
		private Dimension drawData(Graphics2D g, double[][] data, int x, int y, int pixelSize){
			for (int round = 0; round < data.length; round++){
				for (int point = 0; point < data[round].length; point++){
					g.setColor(valueToColor(data[round][point]));
					g.fillRect(x+round*pixelSize, y+point*pixelSize, pixelSize, pixelSize);
				}
			}

			g.setColor(Color.black);
			g.drawRect(x, y, data.length*pixelSize, data[0].length*pixelSize);
			return new Dimension(data.length*pixelSize, data[0].length*pixelSize);
		}
		
		private Dimension drawDiff(Graphics2D g, Result r, int x, int y, int pixelSize){
			for (int round = 0; round < r.target.length; round++){
				for (int point = 0; point < r.target[round].length; point++){
					g.setColor(diffToColor(Math.abs(r.output[round][point] - r.target[round][point])));
					g.fillRect(x+round*pixelSize, y+point*pixelSize, pixelSize, pixelSize);
				}
			}
			g.setColor(Color.black);
			g.drawRect(x, y, r.target.length*pixelSize, r.target[0].length*pixelSize);
			return new Dimension(r.target.length*pixelSize, r.target[0].length*pixelSize);
		}
		
		
		private void drawLegend(Graphics2D g, int x, int y, int w, int h){
			
			for (int i = 0; i < h;i++){
				g.setColor(valueToColor(i/(double)h));
				g.fillRect(x, y+h-i, w, 1);
			}
			
			Font font = new Font("Verdana", Font.PLAIN, 30);
			g.setFont(font);
			g.setColor(Color.black);
			for (int i = 0; i <= 10;i++){
				g.drawLine(x+w-(w/4), (int)(y + h - ((h / 10.0) * i) + 0), x+w, (int)(y + h - ((h / 10.0) * i) + 0));
				g.drawString(String.format(Locale.US, "%.1f", i / 10.0), x+w+5, (int)(y + h - ((h / 10.0) * i) + 5));
			}
			g.setColor(Color.black);
			g.drawRect(x, y, w, h);
		}
		
	}
	
	public static Color valueToColor(double value){
		value = 1 - value;
		value *= 0.65;
		return Color.getHSBColor((float)value, 1f, 1f);
	}
	
	public static Color diffToColor(double weight){
//		weight *= 10000;
		weight = 1 - weight;
		double gamma = .43;
		double corrected = Math.pow(weight, gamma);
		int gsv = (int)(corrected*255);
		return new Color(gsv, gsv, gsv);
	}
	
	
	
	
	
	public static void main(String[] args) throws IOException, TranscriberException{
		Properties props = new Properties("copytask.properties");
		props.setProperty("base.dir", "./db");
		Chromosome chrom = loadChromosome("2388977", props);
		
		//Setup activator
		ActivatorTranscriber activatorFactory = (ActivatorTranscriber) props.singletonObjectProperty(ActivatorTranscriber.class);
		Activator activator = activatorFactory.newActivator(chrom);
	
		//Setup viz
		CopyTaskEvaluationVisualizer viz = new CopyTaskEvaluationVisualizer();
		
		for (int length : sequenceLengths){
		
			props.setProperty("simulator.copytask.length.rule", "fixed");
			props.setProperty("simulator.copytask.length.max", length + "");
			
			CopyTaskResultGrabber copyTask = new CopyTaskResultGrabber(props);
			copyTask.reset();
			copyTask.restart();
	
			Controller controller = (Controller) Utilities.instantiateObject(props.getProperty("controller.class"),new Object[]{props,copyTask}, new Class<?>[]{Properties.class,Simulator.class});
			controller.reset();
			
			controller.evaluate(activator);
			
			viz.addResult(copyTask.getTargets(), copyTask.getOutputs());
		}
		viz.show();
		
	}
	
	
	
	
	private static String prompt(String string) {
		System.out.println(string);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Chromosome loadChromosome(String id, Properties props){
		FilePersistence db = new FilePersistence();
		db.init(props);
		return db.loadChromosome(id, new DummyConfiguration());
	}
	
	
}
