package Application;


import Utils.GraphVizHelper;
import Utils.Matlab;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class ExperimentBaseLine extends ExperimentStream {

	public ExperimentBaseLine(String name, Matlab matlab, GraphVizHelper gvh) {
		super(name, matlab, gvh);
		// TODO Auto-generated constructor stub
	}

	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {
		
		while(this.streamer.getCurrentTransformation() <maxRotation) {

			TrainingTestSet tts = null;			
			J48 currentModel = null;
			ClassifiedSet recurringConcept = null;
			ClassifiedSet joinedConcepts = null;
			
			
			if((this.streamer.getStreamID()-4)>=0 && classifiedStreams.get(this.streamer.getStreamID()-4)!=null) {
				recurringConcept = classifiedStreams.get(this.streamer.getStreamID()-4);

				//union of datasets: current batch last similar batch.
				tts = this.streamer.NextStreamFromBaseLineBatchDrift(anglerotation, driftshift, driftpercentage, noisepercentage);
				//tts.plotDataSet(streamer.getStreamIDPath() + "/BatchWithDrift", "Drift shift =" + 5 + " Drift percentage =" + driftpercentage + "% Noise percentage =" + noisepercentage + "%");
				recurringConcept.getTrainingTestSet().plotDataSet(streamer.getStreamIDPath() + "/PlotRecurringConceptBatch_" + recurringConcept.getStreamID(), "/D from batch #" + recurringConcept.getStreamID());
				Instances datasetUnion = new Instances(tts.getTrainingdata());				
				for(Instance i : recurringConcept.getTrainingTestSet().getTrainingdata()) 					
					datasetUnion.add(i);

				TrainingTestSet uniontts = new TrainingTestSet(datasetUnion, 70, this.getStreamer());
				uniontts.plotDataSet(streamer.getStreamIDPath() + "/PlotUnionRecurringCurrentBatches", "Plot Union D from batch #"+recurringConcept.getStreamID() +" With D from batch"+ streamer.getStreamID()+ "with drift");
				currentModel = createClassifier(uniontts, "UnionRecurringCurrentBatch");
				//classifiedStreams.add(new ClassifiedSet(uniontts, currentModel, this.getStreamer().getStreamID()));
				classifiedStreams.add(new ClassifiedSet(uniontts, recurringConcept.getTrainingTestSet(), tts, currentModel, this.getStreamer().getStreamID()));
			}
			else {
				tts = this.streamer.NextStreamFromBaseLineBatchDrift(anglerotation, 0, 0,noisepercentage);
				currentModel = createClassifier(tts, "CurrentBatch");
				classifiedStreams.add(new ClassifiedSet(tts, currentModel, this.streamer.getStreamID()));
			}
		}
		
		

	}

}


