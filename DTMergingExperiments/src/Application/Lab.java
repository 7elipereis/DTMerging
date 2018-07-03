package Application;

import Utils.GraphVizHelper;
import Utils.Matlab;

public class Lab {

	public static String experiment_directory;
	public static Matlab matlab;
	public static GraphVizHelper gvh;
	
	
	public static void main(String[] args) throws Exception {
		matlab = new Matlab();
		gvh = new GraphVizHelper();
		// TODO Auto-generated method stub

		//Experiment experiment = new Experiment("TestRotated");

		//experiment.CreateExperiment("DataSet/D4FNS_R20.arff", 70);

		//ExperimentStream experiment = new ExperimentStream("TestStream5");
		//experiment.CreateExperiment("DataSet/D4FNS.arff", 70);

		/*int i = 0;
		while(i<=5) {
			ExperimentStreamClassifierFusion experiment = new ExperimentStreamClassifierFusion("FusionExperimentStream_"+i);
			//experiment.CreateExperiment("DataSet/D4FNS.arff", 70);

			//ExperimentStream experiment = new ExperimentStream("ExperimentStream_" + i);
			experiment.CreateExperiment("DataSet/D4FNS.arff", 70);
			i++;
		}*/

		//		ExperimentTASint experiment = new ExperimentTASint("TotallyAugmentedSintetic");
		//		experiment.CreateExperiment("DataSet/D4FNS.arff", 70);
		//		
		//		ExperimentSASintetic experiment1 = new ExperimentSASintetic("SemiAugmentedSintetic");
		//		experiment1.CreateExperiment("DataSet/D4FNS.arff", 70);
		//		
		//		Matlab matlab = new Matlab();
		//		double[][] er1 = experiment.getResults();
		//		double[][] er2 = experiment1.getResults();
		//		
		//		matlab.plotStrEvalsCompBar(experiment.getResults(), experiment1.getResults(), "Totally Augmented X Semi-Augmented Experiment", "totallyXsemi-sintetic", "merging accuracy totally augmented data", "merging accuracy semi augmented data");
		//		
		//ExperimentRealDS experiment = new ExperimentRealDS("Elec");
		//experiment.CreateExperiment("DataSet/elecNormNew.arff", 70);

		//ExperimentTAElec exelec = new ExperimentTAElec("TAElec");
		//exelec.CreateExperiment("DataSet/elecNormNew.arff", 70);

		


		ExperimentBaseLine ebl = new ExperimentBaseLine("Experiment BaseLine", matlab, gvh);
		ebl.CreateExperiment("", 70);		
		double[][] eblr = ebl.getResults(ebl.classifiedStreams);


		ExperimentBaseLineUpdateLastConcept eblulc = new ExperimentBaseLineUpdateLastConcept("Experiment Base Line Update Last Concept", matlab, gvh);
		eblulc.CreateExperiment("", 70);
		double[][] eblulcr = eblulc.getResults(eblulc.classifiedStreams);

				
		ExperimentBaseLineGenerateLastConcept eblglc = new ExperimentBaseLineGenerateLastConcept("Experiment BaseLine Generate Last Concept", matlab, gvh);
		eblglc.CreateExperiment("", 70);
		double[][] eblglcr = eblglc.getResults(eblglc.classifiedStreams);
		
		ExperimentBaseLineGenerateLastCurrentConcepts eblglcc = new ExperimentBaseLineGenerateLastCurrentConcepts("Experiment BaseLine Generate Last And Current Concepts", matlab, gvh);
		eblglcc.CreateExperiment("", 70);
		double[][] eblglccr = eblglcc.getResults(eblglcc.classifiedStreams);
		
		matlab.plotStrEvalsComp(eblr, eblglcr, "(Di U Dj) x (Di' U Dj)", "Experiments/baselineXlastgenerated", "(Di U Dj)", "(Di' U Dj)");
		
		matlab.plotStrEvalsComp(eblr, eblglccr, "(Di U Dj) x (Di' U Dj')", "Experiments/baselineXlastCurrentgenerated", "(Di U Dj)", "(Di' U Dj')");
		
		matlab.plotStrEvalsComp(eblr, eblulcr, "(Di U Dj) x update(DTi, Dj)", "Experiments/baselineXUpdateLastConcept", "(Di U Dj)", "update(DTi, Dj)");
		
		matlab.plotStrEvalsComp(eblulcr, eblglcr, "update(DTi, Dj) x (Di' U Dj)", "Experiments/baselineXUpdateLastConcept", "update(DTi, Dj)", "(Di' U Dj)");
		
		
		
		
		matlab.plotStrEvalsComp3(eblr, eblulcr, eblglcr, eblglccr, " All approaches comparison" , "Experiments/themall", "(Di U Dj)", "(Di' U Dj)", "(Di' U Dj')", "update(DTi, Dj)","# Batches (1K Instances/Batch)","% Correctly Classified Instances");
		
	}
	

	
}
