package Application;

import java.util.ArrayList;

import Utils.GraphVizHelper;
import Utils.Matlab;
import Utils.Streamer;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class ExperimentTAElec extends ExperimentStream {

	public ExperimentTAElec(String name, Matlab matlab, GraphVizHelper gvh) {
		super(name, matlab, gvh);
		// TODO Auto-generated constructor stub
	}

	ArrayList<ClassifiedSet> classifiedMergedStreams = new ArrayList<ClassifiedSet>();
	ArrayList<ClassifiedSet> classifiedCurrentStreams = new ArrayList<ClassifiedSet>();

	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {

		this.streamer = new Streamer(originaldataset_file, this, 5, 70);
		while(this.streamer.getCurrentTransformation() <= 360) {
			J48 lastModel = null;
			J48 currentModel = null;

			Instances lastModelGeneratedInstances = null;
			Instances currentModelGeneratedInstances = null;
			Instances joinedGeneratedInstances = null;




			//TrainingTestSet tts = new TrainingTestSet(streamer.NextStreamFromFile().getDataset(), training_percentage, streamer);
			TrainingTestSet tts = streamer.NextStreamFromFile();
			if(tts == null) {
				break;
			}
			currentModel = createClassifier(tts);
			classifiedCurrentStreams.add(new ClassifiedSet(tts, currentModel, streamer.getStreamID()));
			currentModelGeneratedInstances = currentModel.generateInstancesNominal(tts.getDataset().size()/2, this);

			if(!classifiers.isEmpty()) {
				lastModel = classifiers.get(classifiers.size()-1);
				lastModelGeneratedInstances = lastModel.generateInstancesNominal(tts.getDataset().size()/2, this);
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


			System.out.println("stream id "+ streamer.getStreamID());
			System.out.println(""+ classifiedCurrentStreams.get(classifiedCurrentStreams.size()-1).getEvaluation().toSummaryString());
			System.out.println();
			System.out.println();
		}

		double[][] evalscurrent = new double[streamer.getStreamID()-1][];
		double[][] evalsmerged = new double[streamer.getStreamID()-1][];

//		for(int i = 0; i<streamer.getStreamID()-1;i++) {
//
//			if(classifiedCurrentStreams.get(i)!=null) {
//				evalscurrent[i] = new double[2];
//				evalscurrent[i][0] = (double)classifiedCurrentStreams.get(i).getStreamID();
//				evalscurrent[i][1] = classifiedCurrentStreams.get(i).getPctCorrect();
//			}
//			else {
//				evalscurrent[i] = new double[2];
//				evalscurrent[i][0] = i;
//				evalscurrent[i][1] = 0;
//			}
//
//			if(classifiedMergedStreams.size()>i) {
//				if(classifiedMergedStreams.get(i)!=null) {
//					evalsmerged[i] = new double[2];
//					evalsmerged[i][0] = (double)classifiedMergedStreams.get(i).getStreamID();
//					evalsmerged[i][1] = classifiedMergedStreams.get(i).getPctCorrect();
//				}
//				else {
//					evalsmerged[i] = new double[2];
//					evalscurrent[i][0] = i;
//					evalscurrent[i][1] = 0;
//				}
//				
//			}
//			else {
//				evalsmerged[i] = new double[2];
//				evalscurrent[i][0] = 0;
//				evalscurrent[i][1] = 0;
//			}
//			
//		}

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
