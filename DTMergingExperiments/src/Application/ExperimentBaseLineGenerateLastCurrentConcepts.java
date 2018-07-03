package Application;


import Utils.GraphVizHelper;
import Utils.Matlab;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class ExperimentBaseLineGenerateLastCurrentConcepts extends ExperimentStream {

	public ExperimentBaseLineGenerateLastCurrentConcepts(String name, Matlab matlab, GraphVizHelper gvh) {
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
				
				Instances generatedLastConcept = ((J48)recurringConcept.getClassifier()).generateInstances(1000, this);
				matlab.plotDS(this.dataSetToArray(generatedLastConcept), streamer.getStreamIDPath() + "/PlotGeneratedFromRecurringConcept.png", "Generated From Recurring Concept");
				
				tts = this.streamer.NextStreamFromBaseLineBatchDrift(anglerotation, driftshift, driftpercentage, noisepercentage);
				Instances generatedCurrentConcept = createClassifier(tts).generateInstances(1000, this);
				matlab.plotDS(this.dataSetToArray(generatedCurrentConcept), streamer.getStreamIDPath() + "/generated_CurrentConcept.png", "generated current");
				
				//union of datasets: current batch last similar batch.
				Instances datasetUnion = new Instances(generatedCurrentConcept);				
				for(Instance i : generatedLastConcept) 					
					datasetUnion.add(i);
				
				TrainingTestSet uniontts = new TrainingTestSet(datasetUnion, 70, this.getStreamer());
				uniontts.plotDataSet(streamer.getStreamIDPath() + "/UnionRecurringCurrentBatches", "Union Recurring and Current Generated From Models");
				currentModel = createClassifier(uniontts, "UnionRecurringCurrent");
				//classifiedStreams.add(new ClassifiedSet(uniontts, currentModel, this.getStreamer().getStreamID()));
				classifiedStreams.add(new ClassifiedSet(uniontts, recurringConcept.getTrainingTestSet(), tts, currentModel, this.getStreamer().getStreamID()));
			}
			else {
				tts = this.streamer.NextStreamFromBaseLineBatchDrift(anglerotation, 0, 0, noisepercentage);
				currentModel = createClassifier(tts, "CurrentBatch");
				//Instances generatedLastConpcept = currentModel.generateInstances(1000, this);
				//matlab.plotDS(this.dataSetToArray(generatedLastConpcept), streamer.getStreamIDPath() + "/generated_WithCurrentModel.png", "generated");
				classifiedStreams.add(new ClassifiedSet(tts, currentModel, this.streamer.getStreamID()));
			}
		}
	}

}
