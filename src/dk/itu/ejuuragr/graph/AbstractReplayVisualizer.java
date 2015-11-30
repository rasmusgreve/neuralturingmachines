package dk.itu.ejuuragr.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import dk.itu.ejuuragr.replay.Recording;

/**
 * Basic functioanlity for visualizing a replay of
 * a chromosome evolved through NEAT.
 * 
 * @author Rasmus
 *
 */
public abstract class AbstractReplayVisualizer {

	public static int pixelSize = 25;
	protected final JFrame frame;
	protected Recording<?> recording;
	private RecordingComponent component;
	
	public AbstractReplayVisualizer(Recording<?> recording){
		frame = new JFrame();
		frame.setSize(800, 800);
		this.recording = recording;
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
					BufferedImage bi = new BufferedImage(3800, 2000, BufferedImage.TYPE_INT_RGB);
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

	public static Color weightToColor(double weight){
		double gamma = .43;
		double corrected = Math.pow(weight, gamma);
		int gsv = (int)(corrected*255);
		return new Color(gsv, gsv, gsv);
	}
	
	public static Color valueToColor(double value){
		value *= 0.7;
		return Color.getHSBColor((float)value, 1, 1);
	}
	
	public void show(){
		frame.add(component = new RecordingComponent());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	protected abstract void paint(Graphics2D g);
	protected void repaint(){
		if (component != null) component.repaint();
	}
	
	private class RecordingComponent extends JComponent{
		private static final long serialVersionUID = 1L;
		
		@Override
		public void paint(Graphics arg0) {
			Graphics2D g = (Graphics2D) arg0;
			AbstractReplayVisualizer.this.paint(g);
		}
	}
	
	
}
