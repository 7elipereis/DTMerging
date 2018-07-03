package Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import Application.Experiment;
import models.DataAttribute;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.AttributeStats;

public class DataSetGenerator {
	
	private Experiment experiment;
	
	private ArrayList<Rule> rules;
	private double totalInstances;
	private int numInstances;
	private ArrayList<Attribute> metadatalist;
	public ArrayList<Attribute> getMetadatalist() {
		return metadatalist;
	}
		

	public void setMetadatalist(ArrayList<Attribute> metadatalist) {
		this.metadatalist = metadatalist;
	}

	private ArrayList<AttributeStats> stats;
	private J48 classifier;
	
	public DataSetGenerator(J48 classifier, int numInstances, Experiment experiment) {
		this.experiment = experiment;
		this.rules = classifier.getRules();
		for (Rule rule : rules) {
			this.totalInstances += rule.total;
		}
		this.stats = classifier.stats;
		this.numInstances = numInstances;
		metadatalist = new ArrayList<Attribute>();
		
		for(Attribute att:Collections.list(this.experiment.getStreamer().getDataset().enumerateAttributes())) {metadatalist.add(att);}
		
		
		this.classifier = classifier;
		//this.classifier
		
	}
	
	public double[][] Generate(Streamer streamer, String name) {
				
		ArrayList<double[]> generatedData = new ArrayList<double[]>();
		double[][] rulesMaxAndMin = new double[rules.size()][4];
		char[][] rulesLeafsLabels = new char[rules.size()][];
		
		for (Rule rule : rules) {
			
			double ruleInfluence = Math.round(rule.getRuleInfluence(totalInstances)*numInstances);
			ArrayList<DataAttribute> attributes = rule.discoverAttributesDataBoundaries(metadatalist, stats);
			
			//rulesMaxAndMin[rules.indexOf(rule)] = new double[]{attributes.get(0).getMin(),attributes.get(0).getMax(),attributes.get(1).getMin(),attributes.get(1).getMax()};
			//String label = rule.data + "(" + rule.total + "/" + rule.incorrect +")";
			//rulesLeafsLabels[rules.indexOf(rule)] = label.toCharArray();
			
			for (int i = 0; i <= ruleInfluence; i++) {				
				double[] instance = new double[attributes.size()+1];				
				for(DataAttribute attribute : attributes) {
					instance[attributes.indexOf(attribute)] = attribute.getRandomValue();					
				}
				
				instance[attributes.size()] = rule.data;								
				generatedData.add(instance);				
			}
		}
		
		double[][] data  = new double[numInstances][generatedData.get(0).length];
		for (int i = 0; i < data.length; i++) {			
			if(generatedData.size()>i) {
				data[i] = generatedData.get(i);
			}
		}
		File fusiondir = new File(streamer.getStreamIDPath()+"/fusion");
		fusiondir.mkdirs();
		
		//experiment.matlab.plotDSWithRuleBoundaries(data, rulesMaxAndMin, rulesLeafsLabels, fusiondir.getPath() + "/GeneratedWithRuleBoundariesStream"+streamer.getStreamID()+ name +".png", streamer.getStreamTitle() + " " +name);
		//experiment.matlab.plotDS(data, fusiondir.getPath() + "/GeneratedStream"+streamer.getStreamID()+ name + ".png", streamer.getStreamTitle());
		
	
		return data;
	}
	
	public ArrayList<String[]> GenerateNominal(Streamer streamer, String name) {
		
		ArrayList<String[]> generatedData = new ArrayList<String[]>();
		//double[][] rulesMaxAndMin = new double[rules.size()][4];
		//char[][] rulesLeafsLabels = new char[rules.size()][];
		
		for (Rule rule : rules) {
			
			double ruleInfluence = Math.round(rule.getRuleInfluence(totalInstances)*numInstances);
			ArrayList<DataAttribute> attributes = rule.discoverAttributesDataBoundaries(metadatalist, stats);
			
			//rulesMaxAndMin[rules.indexOf(rule)] = new double[]{attributes.get(0).getMin(),attributes.get(0).getMax(),attributes.get(1).getMin(),attributes.get(1).getMax()};
			//String label = rule.data + "(" + rule.total + "/" + rule.incorrect +")";
			//rulesLeafsLabels[rules.indexOf(rule)] = label.toCharArray();
			
			for (int i = 0; i <= ruleInfluence; i++) {				
				String[] instance = new String[attributes.size()+1];				
				for(DataAttribute attribute : attributes) {
					
					if(attribute.getMin()==0 && attribute.getMax()==0) {
						instance[attributes.indexOf(attribute)] = "?"; 
					}
					else {
						if((attribute.fixedValue!=null)) {
							instance[attributes.indexOf(attribute)] = attribute.getFixedValue();
						}
						else {
							instance[attributes.indexOf(attribute)] = String.valueOf(attribute.getRandomValue());
						}
						
					}
										
				}
				if(classifier.isNominal()) {
					instance[attributes.size()] = rule.classvalue; 
				}				
				else {
					instance[attributes.size()] = String.valueOf(rule.data);
				}
								
				generatedData.add(instance);				
			}
		}
		
		ArrayList<String[]> data  = new ArrayList<String[]>();
		for(String[] instance : generatedData) {
			data.add(instance);
		}
		File fusiondir = new File(streamer.getStreamIDPath()+"/fusion");
		fusiondir.mkdirs();
		
		//experiment.matlab.plotDSWithRuleBoundaries(data, rulesMaxAndMin, rulesLeafsLabels, fusiondir.getPath() + "/GeneratedWithRuleBoundariesStream"+streamer.getStreamID()+ name +".png", streamer.getStreamTitle() + " " +name);
		//experiment.matlab.plotDS(data, fusiondir.getPath() + "/GeneratedStream"+streamer.getStreamID()+ name + ".png", streamer.getStreamTitle());
		
	
		return data;
	}
}
