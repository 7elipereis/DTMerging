package Application;

import java.util.ArrayList;
import java.util.Random;

import Utils.GraphVizHelper;
import Utils.Matlab;
import Utils.Streamer;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class ExperimentSASintetic extends ExperimentStream {
	
	ArrayList<ClassifiedSet> classifiedMergedStreams = new ArrayList<ClassifiedSet>();
	ArrayList<ClassifiedSet> classifiedCurrentStreams = new ArrayList<ClassifiedSet>();

	public ExperimentSASintetic(String name, Matlab matlab, GraphVizHelper gvh) {
		super(name, matlab, gvh);
		// TODO Auto-generated constructor stub
	}
	
	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {

		this.streamer = new Streamer(originaldataset_file, this, 20, 70);
		while(this.streamer.getCurrentTransformation() <= 360) {
			J48 lastModel = null;
			J48 currentModel = null;

			Instances lastModelGeneratedInstances = null;
			Instances currentModelGeneratedInstances = null;
			Instances joinedInstances = null;
			Instances currentStream = null;
			Instances currentStreamSampleCurrentModelGenerated = null;
			
			currentStream = streamer.NextStreamFromRotation().getDataset();
			TrainingTestSet tts = new TrainingTestSet(currentStream, training_percentage, streamer);
			int sizeToGenerate = tts.getDataset().size()/2;
			currentModel = createClassifier(tts);
			classifiedCurrentStreams.add(new ClassifiedSet(tts, currentModel, streamer.getStreamID()));
			
			if(!classifiers.isEmpty()) {
				lastModel = classifiers.get(classifiers.size()-1);
				lastModelGeneratedInstances = lastModel.generateInstances(500, this);
				classifiedMergedStreams.add(new ClassifiedSet(tts, lastModel, streamer.getStreamID()));
				sizeToGenerate = tts.getDataset().size()/4;
			}			
			
			currentModelGeneratedInstances = currentModel.generateInstances(sizeToGenerate, this);
			
			
			
			
			
			joinedInstances = new Instances(currentModelGeneratedInstances, 0, currentModelGeneratedInstances.size());
			
			currentStreamSampleCurrentModelGenerated = new Instances(currentStream.resample(new Random(100)), 0, sizeToGenerate);
			
			
			for(int i = 0; i< currentStreamSampleCurrentModelGenerated.size(); i++) {
				joinedInstances.add(currentStreamSampleCurrentModelGenerated.get(i));				
			}
			
			if(lastModelGeneratedInstances!=null) {
				for(int i = 0; i< lastModelGeneratedInstances.size(); i++) {
					joinedInstances.add(lastModelGeneratedInstances.get(i));				
				}
			}
			TrainingTestSet ttsJoin = new TrainingTestSet(joinedInstances, training_percentage, streamer);
			classifiers.add(createClassifier(ttsJoin));
		}
		
		double[][] evalscurrent = new double[streamer.getStreamID()][];
		double[][] evalsmerged = new double[streamer.getStreamID()][];
		
		for(ClassifiedSet cs : classifiedCurrentStreams) {
			int i = cs.getStreamID();
			double[] t = new double[2];
			t[0] = (double)i;
			t[1] = cs.getPctCorrect();
			evalscurrent[i-1] = t;
		}
		for(ClassifiedSet cs : classifiedMergedStreams) {
			int i = cs.getStreamID();
			double[] t = new double[2];
			t[0] = (double)i;
			t[1] = cs.getPctCorrect();
			evalsmerged[i-1] = t;
		}
		
		matlab.plotStrEvalsComp(evalsmerged, evalscurrent, "Semi-Augmented Sintetic Experiment", this.getExperiment_directory() + "CurrentMergedEvalsComp", "Merged DT x Current Batch evaluation", "Current DT x Current Batch evaluation");
		this.setResults(evalsmerged);
		matlab.plotStrEvalsCompBar(evalsmerged, evalscurrent, "Semi-Augmented Sintetic Experiment", this.getExperiment_directory() + "CurrentMergedEvalsComp", "Merged DT x Current Batch evaluation", "Current DT x Current Batch evaluation");
		
	}	

}
