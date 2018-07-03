package Application;

import Utils.DataSetGenerator;
import Utils.GraphVizHelper;
import Utils.Matlab;
import Utils.Streamer;
import Utils.Writer;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class ExperimentStreamClassifierFusion extends ExperimentStream {


	
	public ExperimentStreamClassifierFusion(String name, Matlab matlab, GraphVizHelper gvh) {		
		super(name, matlab, gvh);
	}
	
	
	void CreateExperiment(String originaldataset_file, int training_percentage) {
		
		streamer = new Streamer(originaldataset_file,this,20, training_percentage);
		super.setStreamer(streamer);
		
		while(streamer.getCurrentTransformation()<=360) {
			
			TrainingTestSet trainingTestSet = streamer.NextStreamFromRotation();
			pool_summary.append("Stream " + streamer.getStreamID() + "started." +nl);
			pool_summary.append("Classifiers in the pool= " + classifiers.size() +nl);
			ClassifiedSet classifiedSet = new ClassifiedSet(trainingTestSet, callClassifier(trainingTestSet), streamer.getStreamID());			
			pool_summary.append("Stream " + streamer.getStreamID() + " FUSION classified with " + classifiedSet.getPctCorrect() + "% correct." +nl);
			pool_summary.append("--------------- Stream Finished! -------------"+nl);
			streamer.printClassifier(classifiedSet.getClassifier(), streamer.getStreamTitle());
			classifiedStreams.add(classifiedSet);
			
			streamer.printClassifier(classifiedSet.getClassifier(), "Fused classifier Stream " + streamer.getStreamID());
		}
		
		printPctCorrectlyClassified(classifiedStreams);
		//Writes the summary to a file
		Writer summaryWriter = new Writer();
		summaryWriter.writeSummary(this.experiment_directory.getPath() + "/" + "summary.txt", experiment_summary.toString());
		summaryWriter.writeSummary(this.experiment_directory.getPath() + "/" + "pool_summary.txt", pool_summary.toString());
	}
	
	J48 callClassifier(TrainingTestSet trainingTestSet) {

		J48 last = null;
		J48 fused = null;
		if(!classifiers.isEmpty()){
			last = classifiers.get(classifiers.size()-1);			
			pool_summary.append("Fusion started for Stream " + streamer.getStreamID() +nl);			
			fused = fusionClassifier(last, ClassifierParametrization(trainingTestSet));
			classifiers.add(fused);
		}
		else {
			last = ClassifierParametrization(trainingTestSet);
			classifiers.add(last);
			fused = last;
			experiment_summary.append("classifier created. classifiers pool size: " + classifiers.size() +nl);
			pool_summary.append("pool EMPTY first classifier created."+nl);

		}		
		return fused;

	}
	
	J48 fusionClassifier(J48 firstClassifier, J48 secondClassifier) {
		J48 unionClassifier = null;
		
		DataSetGenerator dsgFirst = new DataSetGenerator(firstClassifier, 500, this);
		Instances dsFirst = readArrayToInstances(dsgFirst.Generate(streamer, "Last_Classifier"), experiment_datasets_directory + "FusionDSFirst", streamer);		
		DataSetGenerator dsgSecond = new DataSetGenerator(secondClassifier, 500, this);
		Instances dsSecond = readArrayToInstances(dsgSecond.Generate(streamer, "New_Classifier"), experiment_datasets_directory + "FusionDSSecond", streamer);
		
		Instances union = new Instances(dsFirst);
		for (Instance instance : dsSecond) {
			union.add(instance);
		}
		
		TrainingTestSet set = new TrainingTestSet(union, 70, streamer);
		unionClassifier = ClassifierParametrization(set);		
		
		return unionClassifier;
	}

}
