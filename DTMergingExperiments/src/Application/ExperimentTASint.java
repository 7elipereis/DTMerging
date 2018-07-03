package Application;

import java.util.ArrayList;

import Utils.GraphVizHelper;
import Utils.Matlab;
import Utils.Streamer;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class ExperimentTASint extends ExperimentStream {

	ArrayList<ClassifiedSet> classifiedMergedStreams = new ArrayList<ClassifiedSet>();
	ArrayList<ClassifiedSet> classifiedCurrentStreams = new ArrayList<ClassifiedSet>();
	

	public ExperimentTASint(String name, Matlab matlab, GraphVizHelper gvh) {
		super(name, matlab, gvh);
	}

	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {

		this.streamer = new Streamer(originaldataset_file, this, 20, 70);
		while(this.streamer.getCurrentTransformation() <= 360) {
			J48 lastModel = null;
			J48 currentModel = null;

			Instances lastModelGeneratedInstances = null;
			Instances currentModelGeneratedInstances = null;
			Instances joinedGeneratedInstances = null;			
			TrainingTestSet tts = new TrainingTestSet(streamer.NextStreamFromRotation().getDataset(), training_percentage, streamer);
			currentModel = createClassifier(tts);
			classifiedCurrentStreams.add(new ClassifiedSet(tts, currentModel, streamer.getStreamID()));
			currentModelGeneratedInstances = currentModel.generateInstances(500, this);
			
			if(!classifiers.isEmpty()) {
				lastModel = classifiers.get(classifiers.size()-1);
				lastModelGeneratedInstances = lastModel.generateInstances(500, this);
				classifiedMergedStreams.add(new ClassifiedSet(tts, lastModel, streamer.getStreamID()));
			}

			if(currentModelGeneratedInstances!=null) joinedGeneratedInstances = new Instances(currentModelGeneratedInstances, 0, currentModelGeneratedInstances.size());
			
			if(lastModelGeneratedInstances!=null) {
				for(int i = 0; i< lastModelGeneratedInstances.size(); i++) {
					joinedGeneratedInstances.add(lastModelGeneratedInstances.get(i));				
				}
			}
			TrainingTestSet ttsJoin = new TrainingTestSet(joinedGeneratedInstances, training_percentage, streamer);
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
		
		//matlab.plotStrEvalsComp(evalsmerged, evalscurrent, "Totally Augmented Experiment", this.getExperiment_directory() + "CurrentMergedEvalsComp", "Merged DT x Current Batch evaluation", "Current DT x Current Batch evaluation");
		this.setResults(evalsmerged);
		matlab.plotStrEvalsCompBar(evalsmerged, evalscurrent, "Totally Augmented Experiment", this.getExperiment_directory() + "CurrentMergedEvalsComp", "Merged DT x Current Batch evaluation", "Current DT x Current Batch evaluation");
		
	}	

	

}
