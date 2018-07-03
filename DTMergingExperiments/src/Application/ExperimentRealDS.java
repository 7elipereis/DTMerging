package Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import Utils.DataSetGenerator;
import Utils.DataSetWriter;
import Utils.GraphVizHelper;
import Utils.Matlab;
import Utils.Rule;
import Utils.Streamer;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;

public class ExperimentRealDS extends Experiment {
	
	

	public ExperimentRealDS(String name, Matlab matlab, GraphVizHelper gvh) {
		super(name, matlab, gvh);
		
	}
	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {
		
		Instances dataset = readDataSet(originaldataset_file);
		dataset.setClassIndex(dataset.numAttributes()-1);		
		this.streamer = new Streamer(this);
		this.streamer.setDataset(dataset);
		dataset.randomize(new Random(100));
		int trainingPercentage = training_percentage;		
		int testPercentage = 100-trainingPercentage;
		
		Instances trainingdataset = new Instances(dataset, 0, getTrainSize(trainingPercentage,dataset.numInstances()));
		Instances testdataset = new Instances(dataset, trainingdataset.numInstances(), getTestSize(testPercentage, dataset.numInstances()));
		
		// Builds a C4.5 classifier
		J48 j48classifier = new J48();
		


		//parameter selection
		CVParameterSelection ps = new CVParameterSelection();
		ps.setClassifier(j48classifier);
		ps.setNumFolds(10);
		//-M(minimum number of instances) parameter varying from 100 to 1000 in 10 steps (100, 200, 300 ..., 1000)
		ps.addCVParameter("M 100 1000 10");
		//-C (confidence) parameter varying from 0.1 to 0.9 in 9 steps (0.1, 0.2, 0.3, ..., 0.9)
		ps.addCVParameter("C 0.1 0.9 9");
		ps.buildClassifier(trainingdataset);
		StringBuilder options = new StringBuilder();
		//-J (for do not use of MDL correction) parameter. 
		options.append("-J ");
		for (String o : ps.getBestClassifierOptions()) {
			if(!o.equals("")) options.append(o + " ");
		}
		j48classifier.setOptions(options.toString().split(" "));
		build(j48classifier,trainingdataset);
		
		Evaluation eval1 = new Evaluation(trainingdataset);
		eval1.evaluateModel(j48classifier, testdataset);
		System.out.println(eval1.toSummaryString());
		
		
		ArrayList<Rule> rules = j48classifier.getRules();
		System.out.println(j48classifier.graph());
		DataSetGenerator dsg = new DataSetGenerator(j48classifier, 1000, this);
		ArrayList<String[]> generated_data = dsg.GenerateNominal(streamer, "generated for Experiment experiment");
		DataSetWriter dsw = new DataSetWriter(streamer);
		String[] attnames = new String[streamer.getDataset().numAttributes()-1];
		for(Attribute att: Collections.list(streamer.getDataset().enumerateAttributes())) {
			if(att.name()!= null)
			attnames[att.index()]=att.name();
		}
		
		
		
		Instances g = readDataSet(dsw.arffWriter("generatedreal", generated_data, attnames));
		
		double size = g.size();
		
		
		g.randomize(new Random(100));
		
		
		Instances trainingdatasetg = new Instances(g, 0, getTrainSize(trainingPercentage,g.numInstances()));
		Instances testdatasetg = new Instances(g, trainingdatasetg.numInstances(), getTestSize(testPercentage, g.numInstances()));
		
		// Builds a C4.5 classifier
		J48 j48classifierg = new J48();
		


		//parameter selection
		ps = new CVParameterSelection();
		ps.setClassifier(j48classifierg);
		ps.setNumFolds(10);
		//-M(minimum number of instances) parameter varying from 100 to 1000 in 10 steps (100, 200, 300 ..., 1000)
		ps.addCVParameter("M 100 1000 10");
		//-C (confidence) parameter varying from 0.1 to 0.9 in 9 steps (0.1, 0.2, 0.3, ..., 0.9)
		ps.addCVParameter("C 0.1 0.9 9");
		ps.buildClassifier(trainingdatasetg);
		options = new StringBuilder();
		//-J (for do not use of MDL correction) parameter. 
		options.append("-J ");
		for (String o : ps.getBestClassifierOptions()) {
			if(!o.equals("")) options.append(o + " ");
		}
		j48classifierg.setOptions(options.toString().split(" "));
		build(j48classifierg,trainingdatasetg);
		
		//Evaluation eval = new Evaluation(trainingdatasetg);
		eval1.evaluateModel(j48classifier, testdatasetg);
		System.out.println(eval1.toSummaryString());
		
		Evaluation evalg = new Evaluation(trainingdatasetg);
		evalg.evaluateModel(j48classifierg, testdataset);
		System.out.println(evalg.toSummaryString());
		
	}

}
