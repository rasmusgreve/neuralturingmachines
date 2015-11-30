package dk.itu.ejuuragr.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A helper class for creating an image from an ANN structure.
 * 
 * @author Rasmus
 *
 */
public class ChromosomeGrapher {

	private final static String outputFolder = "graphs";
	private final static String chromosomeFolder = "db/chromosome";
	private final static String dotExeLocation = "./graphviz2.38/bin/dot.exe";
	
	/**
	 * Run this with the first argument being an integer with the
	 * ID of the NN chromosome to visualize.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		processSingleByStdIn();
	}
	
	
	private static void processSingleByStdIn() throws Exception{
		System.out.println("Enter chromosome id: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		processSingleChromosome(br.readLine());
	}
	
	private static void processFamilyByStdIn() throws Exception{
		File folder = new File("./"+outputFolder+"/");
		folder.mkdirs();
		File[] files = folder.listFiles();
		if (files != null)
			for (File f : files)
				f.delete();
		
		
		System.out.println("Enter chromosome id: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String id = br.readLine();
		while (id != null)
			id = processSingleChromosome(id);
		System.out.println("All done!");
	}
	
	
	/**
	 * Processes a single chromosome
	 * @param id The id of the chromosome to process
	 * @return The id of the parent or null if no parent
	 */
	private static String processSingleChromosome(String id) throws Exception{
		try {
			System.out.print("Processing " + id + "... ");
			XMLReader reader = XMLReaderFactory.createXMLReader();
			ChromosomeHandler handler = new ChromosomeHandler();
			reader.setContentHandler(handler);
			reader.parse(chromosomeFolder+"/chromosome"+id+".xml");
			
			String gvDot = handler.getString();
			FileWriter fw = new FileWriter("tmp.gv");
			fw.write(gvDot);
			fw.flush();
			fw.close();
			
			System.out.print("GV -> "+outputFolder+"/Graph" + id + ".png...");
			
			new File("./"+outputFolder+"/").mkdirs();
			ProcessBuilder pb = new ProcessBuilder(dotExeLocation,  "-Tpng", "tmp.gv");
			pb.redirectOutput(Redirect.to(new File(outputFolder+"/Graph"+id+".png")));
			pb.redirectError(Redirect.INHERIT);
			int exitCode = pb.start().waitFor();
			if (exitCode == 0)
				System.out.println("OK!");
			else
				System.out.println("Failed: " + exitCode);
			
			return handler.getParentId();
		} catch (SAXException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	private static class ChromosomeHandler extends DefaultHandler {
		ArrayList<String> ins = new ArrayList<String>();
		ArrayList<String> outs = new ArrayList<String>();
		HashMap<String,Double> edges = new HashMap<String,Double>();
		String parentChromosomeId;
		
		public void startElement(String uri, String localName, String qName, Attributes atts) {
			if (qName.equals("neuron")) {
				String name = atts.getValue("id");
				if ("in".equals(atts.getValue("type"))) ins.add(name);
				if ("out".equals(atts.getValue("type"))) outs.add(name);
			}
			
			if (qName.equals("connection")) {
				String from = atts.getValue("src-id");
				String to = atts.getValue("dest-id");
				edges.put(from + " -> " + to, Double.parseDouble(atts.getValue("weight")));
			}
			
			if (qName.equals("chromosome"))
			{
				parentChromosomeId = atts.getValue("primary-parent-id");
			}
		}
		
		public String getParentId(){
			return parentChromosomeId;
		}
		
		public String getString(){
			StringBuilder sb = new StringBuilder();
			
			//header
			sb.append("digraph G {\n")
			.append("\trankdir=LR;\n")
			.append("\tdpi=200\n")
			.append("\tnode [height=.6, width=.6]\n")
			.append("\n")
			
			//ins
			.append("\tsubgraph ins {\n")
			.append("\t\trank=same;\n")
			.append("\t\tnode [color=\"#BBBBFF\", style=filled, shape=circle];\n");
			for (String in : ins)
				sb.append("\t\t").append(in).append(";\n");
			sb.append("\t}\n")
			.append("\n")
			
			//outs
			.append("\tsubgraph outs {\n")
			.append("\t\trank=same;\n")
			.append("\t\tnode [color=\"#FFBBBB\", style=filled, shape=circle];\n");
			for (String in : outs)
				sb.append("\t\t").append(in).append(";\n");
			sb.append("\t}\n")
			.append("\n")
			
			//edges
			.append("\tnode [shape=circle];\n")
			.append("\n");
			for (String edge : edges.keySet())
				sb.append("\t").append(edge).append(String.format(" [label=\"%.2f\"];\n",edges.get(edge)));
			sb.append("\n")
			
			//in and out ordering
			.append("\tedge [style=invis]\n");
			
			//in ordering
			for (int i = 0; i < ins.size() -1; i++)
				for (int j = i+1; j < ins.size(); j++){
					sb.append("\t" + ins.get(i) +" -> " + ins.get(j) + ";\n");
				}
			
			//out ordering
			for (int i = 0; i < outs.size() -1; i++)
				for (int j = i+1; j < outs.size(); j++){
					sb.append("\t" + outs.get(i) +" -> " + outs.get(j) + ";\n");
				}
			
			
			
			//footer
			sb.append("\n}\n");
			
			return sb.toString();
		}
	}


}
