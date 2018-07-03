package Application;

import Utils.GraphVizHelper;
import Utils.Matlab;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.HoeffdingTree;
import weka.core.Instance;

public class ExperimentBaseLineUpdateLastConcept extends ExperimentStream {

	public ExperimentBaseLineUpdateLastConcept(String name, Matlab matlab, GraphVizHelper gvh) {
		super(name, matlab, gvh);
		// TODO Auto-generated constructor stub
	}
	
	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {	
		
		while(this.streamer.getCurrentTransformation() <maxRotation) {
			
			TrainingTestSet tts = null;
			ClassifiedSet recurringConcept = null;
			ClassifiedSet joinedConcepts = null;

			if((this.streamer.getStreamID()-4)>=0 && classifiedStreams.get(this.streamer.getStreamID()-4)!=null) {
				recurringConcept = classifiedStreams.get(this.streamer.getStreamID()-4);
				HoeffdingTree updatedModel = (HoeffdingTree)recurringConcept.getClassifier();
				
				tts = this.streamer.NextStreamFromBaseLineBatchDrift(anglerotation, driftshift, driftpercentage, noisepercentage);
				
				for(Instance i: tts.getTrainingdata()) {
					updatedModel.updateClassifier(i);
				}
				classifiedStreams.add(new ClassifiedSet(tts, updatedModel, streamer.getStreamID()));				
				//classifiedStreams.add(new ClassifiedSet(uniontts, recurringConcept.getTrainingTestSet(), tts, currentModel, this.getStreamer().getStreamID()));
				try {
					gvh.print2PNG(updatedModel.graph(), "HT Recurring Model updated with batch from stream " + this.streamer.getStreamID(),  this.streamer.getStreamIDPath() + "/" + "Updatedupdateable_classifier.png");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			else {
				
				tts = this.streamer.NextStreamFromBaseLineBatchDrift(anglerotation, 0, 0, noisepercentage);
				HoeffdingTree currentModel = HoeffdingTree.class.cast(createClassifier(tts, "currentBatchOnly_classifier", new HoeffdingTree().getClass()));
				classifiedStreams.add(new ClassifiedSet(tts, currentModel, this.streamer.getStreamID()));
				
				try {
					gvh.print2PNG(currentModel.graph(), "HT Current batch from stream " + this.streamer.getStreamID(),  this.streamer.getStreamIDPath() + "/" + "CurrentBatchupdateable_classifier.png");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 
			}
		}
	}

}
