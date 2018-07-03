package Application;

import java.util.ArrayList;

import Utils.GraphVizHelper;
import Utils.Matlab;
import Utils.Streamer;
import Utils.Writer;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.J48;

public class ExperimentStream extends Experiment {
	
	public int maxRotation = 2*16*90;
	public int anglerotation = 90;
	public int driftshift = 5;
	public int driftpercentage = 25;
	public int noisepercentage = 25;

	public ExperimentStream(String name, Matlab matlab, GraphVizHelper gvh) {
		super(name, matlab, gvh);
		this.streamer = new Streamer(this);
		// TODO Auto-generated constructor stub
	}

	ArrayList<ClassifiedSet> classifiedStreams = new ArrayList<ClassifiedSet>();
	ArrayList<J48> classifiers = new ArrayList<J48>();


	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {


		streamer = new Streamer(originaldataset_file, this, 20, training_percentage);
		super.streamer = streamer;

		while(streamer.getCurrentTransformation()<=maxRotation) {

			TrainingTestSet trainingTestSet = new TrainingTestSet(streamer.NextStreamFromRotation().getDataset(), training_percentage, streamer);
			String classifierLabel = "Classifier for Stream " + streamer.getStreamID() + ". ";
			pool_summary.append("Stream " + streamer.getStreamID() + "started." +nl);
			pool_summary.append("Classifiers in the pool= " + classifiers.size() +nl);
			ClassifiedSet classifiedSet = new ClassifiedSet(trainingTestSet, callClassifier(trainingTestSet), streamer.getStreamID());
			pool_summary.append("Stream " + streamer.getStreamID() + " classified with " + classifiedSet.getPctCorrect() + "% correct." +nl);

			if(classifiedSet.getPctCorrect()<80) {				
				experiment_summary.append("classifier pct correct under 80%. classifier pct correct ="+ classifiedSet.getPctCorrect() +nl);
				J48 newClassifier = ClassifierParametrization(classifiedSet.getTrainingTestSet());
				classifiedSet.setClassifier(newClassifier);
				pool_summary.append("New classifier created for Stream " + streamer.getStreamID() + " classified with " + classifiedSet.getPctCorrect() + "% correct."+nl);				
				classifiers.add(newClassifier);
				pool_summary.append("--------------- Stream Finished! -------------"+nl);

			}
			else {
				if(streamer.getStreamID()!=1)classifierLabel += "Reused from Stream " + (streamer.getStreamID()-1);

				experiment_summary.append("classifier reused. classifier pct correct="+classifiedSet.getPctCorrect()+nl);
				pool_summary.append("Classifier reused for Stream " + streamer.getStreamID() + " classified with " + classifiedSet.getPctCorrect() + "% correct."+nl);
				pool_summary.append("--------------- Stream Finished! -------------"+nl);
			}
			classifiedStreams.add(classifiedSet);

			streamer.printClassifier(classifiedSet.getClassifier(), classifierLabel);
		}

		printPctCorrectlyClassified(classifiedStreams);

		//Writes the summary to a file
		Writer summaryWriter = new Writer();
		summaryWriter.writeSummary(this.experiment_directory.getPath() + "/" + "summary.txt", experiment_summary.toString());
		summaryWriter.writeSummary(this.experiment_directory.getPath() + "/" + "pool_summary.txt", pool_summary.toString());


		/*// read original dataset and copy it to folder datasets
		//Instances dataset = readDataSet(originaldataset_file);
		TrainingTestSet trainingTestSet = new TrainingTestSet(readDataSet(originaldataset_file), training_percentage, this,"InitialSet");

		// Builds a C4.5 classifier
		J48 classifier = ClassifierParametrization(trainingTestSet);

		//evaluate first testset from original dataset 30%
		Evaluation eval = new Evaluation(trainingTestSet.getTrainingdata());	
		eval.evaluateModel(classifier,trainingTestSet.getTestdata());*/
	}

	J48 callClassifier(TrainingTestSet trainingTestSet) {

		J48 last = null;
		if(!classifiers.isEmpty()){
			last = classifiers.get(classifiers.size()-1);
			pool_summary.append("last classified requested"+nl);
		}
		else {
			last = ClassifierParametrization(trainingTestSet);
			classifiers.add(last);
			experiment_summary.append("classifier created. classifiers pool size: " + classifiers.size() +nl);
			pool_summary.append("pool EMPTY first classifier created."+nl);
		}
		return last;
	}

	public void plotAllEvals() {

	}
	public void plotAllEvalsComp(double[][] eval1, double[][] eval2) {
		

	}
	
	public double[][] getResults(ArrayList<ClassifiedSet> classifiedSetList){
		double[][] r = new double[classifiedSetList.size()][];
		
		
		for(ClassifiedSet cs : classifiedSetList) {
			int i = cs.getStreamID();
			double[] t = new double[2];
			t[0] = (double)i;
			t[1] = cs.getPctCorrect();
			r[i-1] = t;
		}
		
		return r;
	}
}
