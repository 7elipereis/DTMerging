package Application;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import Utils.DataSetGenerator;
import Utils.DataSetWriter;
import Utils.GraphVizHelper;
import Utils.Matlab;
import Utils.Streamer;
import Utils.Writer;
import models.ClassifiedSet;
import models.TrainingTestSet;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;


public class Experiment {

	public Matlab matlab;
	public GraphVizHelper gvh;
	protected File experiment_directory;
	public File getExperiment_directory() {
		return experiment_directory;
	}


	double[][] results;

	public double[][] getResults() {
		return results;
	}

	public void setResults(double[][] results) {
		this.results = results;
	}


	public File experiment_datasets_directory;
	public File getExperiment_datasets_directory() {
		return experiment_datasets_directory;
	}


	private File experiment_classifiers_directory;
	private String name;
	public static StringBuilder experiment_summary;
	public StringBuilder pool_summary;
	public final static String nl = System.lineSeparator();

	Streamer streamer;

	public Streamer getStreamer() {
		return streamer;
	}

	public void setStreamer(Streamer streamer) {
		this.streamer = streamer;
	}

	public Experiment(String name, Matlab matlab, GraphVizHelper gvh) {

		//experiment name for later reference
		this.setName(name);
		this.matlab = matlab;
		this.gvh = gvh;


		//experiment folder. files and datasets will be saved there
		experiment_directory = new File("Experiments/"+ name);
		experiment_directory.mkdirs();

		experiment_datasets_directory = new File(experiment_directory + "/datasets");
		experiment_datasets_directory.mkdirs();

		experiment_classifiers_directory = new File(experiment_directory + "/classifiers");
		experiment_classifiers_directory.mkdirs();

		//log of the experiment
		experiment_summary = new StringBuilder();
		experiment_summary.append("Experiment Name: " + name + " When: " + (new Date()).toString() + nl);

		pool_summary = new StringBuilder();
		pool_summary.append("Experiment Name: " + name + " When: " + (new Date()).toString() + nl);
	}

	void CreateExperiment(String originaldataset_file, int training_percentage) throws Exception {
		streamer = new Streamer(originaldataset_file, this, 20, training_percentage);
		// read original dataset and copy it to folder datasets
		Instances dataset = readDataSet(originaldataset_file);

		matlab.plotDS(dataSetToArray(dataset), experiment_datasets_directory.getPath() + "/" + dataset.relationName() + "_plot.png", streamer.getStreamTitle());
		copyOriginalDataSet(originaldataset_file);		

		//randomizes the original dataset for the spliting(training and test) process.
		//Necessary if the data is sorted in a clustered way
		dataset.randomize(new Random(100));

		//Defining dataset splitting sizes		
		int trainingPercentage = training_percentage;		
		int testPercentage = 100-trainingPercentage;

		//dataset spliting
		Instances trainingdataset = new Instances(dataset, 0, getTrainSize(trainingPercentage,dataset.numInstances()));
		Instances testdataset = new Instances(dataset, trainingdataset.numInstances(), getTestSize(testPercentage, dataset.numInstances()));

		//writes down the training dataset
		CSVSaver csvTraining = new CSVSaver();
		csvTraining.setInstances(trainingdataset);
		try {
			csvTraining.setFile(new File(experiment_datasets_directory.getPath() + "/" + dataset.relationName() +"_train.csv"));
			csvTraining.writeBatch();
			matlab.plotDS(dataSetToArray(trainingdataset), experiment_datasets_directory.getPath() + "/" + dataset.relationName() + "_train_plot"+streamer.getStreamID()+".png", streamer.getStreamTitle());
			experiment_summary.append("training dataset created and ploted"+ nl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		//writes down the test dataset
		CSVSaver csvTest = new CSVSaver();
		csvTest.setInstances(testdataset);
		try {
			csvTest.setFile(new File(experiment_datasets_directory.getPath() + "/" +dataset.relationName()+"_test.csv"));
			csvTest.writeBatch();
			matlab.plotDS(dataSetToArray(testdataset), experiment_datasets_directory.getPath() + "/" + dataset.relationName() + "_test_plot"+streamer.getStreamID()+".png", streamer.getStreamTitle());
			experiment_summary.append("test dataset created and ploted"+ nl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


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
		experiment_summary.append("Classifier's parameter selection: " + options.toString() + nl);

		//builds the tree with the specified parameters
		build(j48classifier,trainingdataset);

		//generates data based on the rules extracted from the j48tree considering the total number of instances per leaf
		DataSetGenerator dsg = new DataSetGenerator(j48classifier, 1000, this);
		double[][] generated_data = dsg.Generate(streamer, "generated for Experiment experiment");		
		DataSetWriter dsw = new DataSetWriter(streamer);
		dsw.csvWriter(experiment_datasets_directory.getPath() + "/" + dataset.relationName(), generated_data);
		String attributeNames[] = {"A", "B"};
		dsw.arffWriter(experiment_datasets_directory.getPath() + "/"+ dataset.relationName(), generated_data, attributeNames);

		Instances generated = new Instances(new FileReader(experiment_datasets_directory.getPath() + "/" + dataset.relationName()+"_generated.arff"));
		generated.setClassIndex(trainingdataset.numAttributes()-1);

		matlab.plotDS(generated_data, experiment_directory.getPath() + "/plot_file_param"+streamer.getStreamID()+".png", streamer.getStreamTitle());




		//evaluate with testdataset
		evaluate(j48classifier, testdataset);

		//evaluate with generated dataset
		evaluate(j48classifier, generated);


		///Classifier evaluation against datasets
		ArrayList<Instances> datasetList = new ArrayList<Instances>();		
		datasetList.add(testdataset);

		//Instances originaldataset = readDataSet( experiment_datasets_directory.getPath() +"/"+ "original.arff");
		Instances originaldataset = dataset;		
		datasetList.add(originaldataset);



		Instances rotateddataset = matlab.RotateDS(readDataSetToArray(originaldataset),20, streamer);





		datasetList.add(rotateddataset);

		datasetList.add(generated);

		double[][] evaldatastats = new double[4][8];
		double[][] evaldatapcts = new double[4][8];

		for(Instances ds :  datasetList) {
			Evaluation e = evaluate(j48classifier, ds);			
			evaldatastats[datasetList.indexOf(ds)][0] = e.pctCorrect();
			evaldatastats[datasetList.indexOf(ds)][1] = e.pctIncorrect();
		}

		for(Instances ds : datasetList) {			
			Evaluation e = evaluate(j48classifier, ds);
			evaldatastats[datasetList.indexOf(ds)][0] = e.kappa();
			evaldatastats[datasetList.indexOf(ds)][1] = e.meanAbsoluteError();
			evaldatastats[datasetList.indexOf(ds)][2] = e.rootMeanSquaredError();
			evaldatastats[datasetList.indexOf(ds)][3] = e.relativeAbsoluteError();
			evaldatastats[datasetList.indexOf(ds)][4] = e.rootRelativeSquaredError();

		}

		Writer wr = new Writer();
		wr.csvWriter(dataset.relationName() + "_eval_stats", evaldatastats);
		wr.csvWriter(dataset.relationName() + "_eval_pcts", evaldatapcts);


		//Writes the summary to a file
		Writer summaryWriter = new Writer();
		summaryWriter.writeSummary(experiment_directory.getPath() + "/" + "summary.txt", experiment_summary.toString());
	}

	public HoeffdingTree HTClassifierParametrization(TrainingTestSet set) {
		HoeffdingTree ht = new HoeffdingTree();
		CVParameterSelection psht = new CVParameterSelection();
		psht.setClassifier(ht);
		try {
			psht.addCVParameter("G 20 200 1");
			psht.addCVParameter("M 0.01 0.1 1");
			psht.addCVParameter("H 0.05 0.1 1");
			psht.buildClassifier(set.getTrainingdata());
			StringBuilder options = new StringBuilder();
			for (String o : psht.getBestClassifierOptions()) {
				if(!o.equals("")) options.append(o + " ");
			}
			ht.setOptions(options.toString().split(" "));

			ht.buildClassifier(set.getTrainingdata());
			
			

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return ht;
	}

	public J48 ClassifierParametrization(TrainingTestSet set) {

		J48 j48classifier = new J48();

		//parameter selection
		CVParameterSelection ps = new CVParameterSelection();
		ps.setClassifier(j48classifier);
		try {
			//ps.setNumFolds(10);
			//-M(minimum number of instances) parameter varying from 100 to 1000 in 10 steps (100, 200, 300 ..., 1000)

			ps.addCVParameter("M 10 100 10");
			//-C (confidence) parameter varying from 0.1 to 0.9 in 9 steps (0.1, 0.2, 0.3, ..., 0.9)
			ps.addCVParameter("C 0.1 0.5 2");
			ps.buildClassifier(set.getTrainingdata());
			StringBuilder options = new StringBuilder();
			//-J (for do not use of MDL correction) parameter. 
			options.append("-J ");
			for (String o : ps.getBestClassifierOptions()) {
				if(!o.equals("")) options.append(o + " ");
			}
			j48classifier.setOptions(options.toString().split(" "));		
			experiment_summary.append("Classifier's parameter selection: " + options.toString() + nl);
			//builds the tree with the specified parameters
			build(j48classifier,set.getTrainingdata());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return j48classifier;

	}

	protected void build(Classifier classifier, Instances dataset) throws Exception {
		try {
			((Classifier)classifier).buildClassifier(dataset);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//experiment_summary.append(((Drawable)classifier).graph() + nl);
		System.out.println();
	}



	private static Evaluation evaluate(Classifier classifier, Instances dataset) throws Exception {
		Evaluation eval = new Evaluation(dataset);
		eval.evaluateModel(classifier, dataset);

		//Writer wr = new Writer();
		//wr.Results(dataset.relationName(), eval.toSummaryString());
		//evaluation.append(eval.toSummaryString());
		//evaluation.append(System.lineSeparator());
		System.out.print(eval.toSummaryString());

		return eval;
	}
	public static Instances readDataSet(String file) {

		Instances dataset = null;		
		try {
			dataset = new Instances(new FileReader(file));
			dataset.setClassIndex(dataset.numAttributes()-1);			
		} catch (FileNotFoundException e1) {			
			e1.printStackTrace();
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
		return dataset;
	}
	public static double[][] readDataSetToArray(String file) {
		Instances dataset = null;
		double[][] data = null;
		try {
			dataset = new Instances(new FileReader(file));
			data = new double[dataset.size()][dataset.numAttributes()];
			for (Instance instance : dataset) {
				data[dataset.indexOf(instance)] = instance.toDoubleArray();
			}			
		} catch (FileNotFoundException e1) {			
			e1.printStackTrace();
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
		return data;
	}
	public static Instances readArrayToInstances(double[][] data, String filename, Streamer streamer) {

		Instances dataset = null;

		DataSetWriter dsw = new DataSetWriter(streamer);
		String[] attNames = new String[] {"A", "B"};
		String fileGeneratedDataSet = dsw.arffWriter(filename, data, attNames);
		dataset = readDataSet(fileGeneratedDataSet);


		return dataset;
	}
	public static double[][] readDataSetToArray(Instances instances) {
		Instances dataset = null;
		double[][] data = null;
		dataset = instances;
		data = new double[dataset.size()][dataset.numAttributes()];

		for (Instance instance : dataset) {
			data[dataset.indexOf(instance)] = instance.toDoubleArray();
		}

		return data;
	}
	public static double[][] dataSetToArray(Instances dataset){
		double[][] data = new double[dataset.size()][dataset.numAttributes()];
		for (Instance instance : dataset) {
			data[dataset.indexOf(instance)] = instance.toDoubleArray();
		}
		return data;
	}

	protected void copyOriginalDataSet(String file) {
		// TODO Auto-generated method stub
		File source = new File(file);
		File destination = new File(experiment_datasets_directory.getPath() +"/original.arff");
		try {
			//destination.createNewFile();
			if(!destination.exists())Files.copy(source.toPath(), destination.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getTrainSize(int trainpercentage, int datasize) {
		int result = 0;		
		result = Math.round((trainpercentage*datasize)/100);		
		return result;
	}
	public static int getTestSize(int testpercentage, int datasize) {
		int result = 0;
		result = Math.round((datasize*testpercentage)/100);
		return result;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	static StringBuilder evaluation = new StringBuilder();


	public void printPctCorrectlyClassified(ArrayList<ClassifiedSet> set) {

		double[] pctdata = new double[set.size()];

		for (ClassifiedSet s : set) {
			pctdata[set.indexOf(s)] = s.getPctCorrect();
		}
		this.pool_summary.append(pctdata.toString()+nl);

		matlab.plotPctCorrectStream(pctdata, this.experiment_directory.getPath() + "/Streamer/pctCorrectlyClassifiedStream.png", "PCT of Instances Correctly Classified per Stream");
	}

	public J48 createClassifier(TrainingTestSet trainingTestSet) {
		J48 model = null;
		model = ClassifierParametrization(trainingTestSet);
		/*GraphVizHelper gvh = new GraphVizHelper();
		try {
			gvh.print2PNG(model.graph(), "classifier stream " + this.streamer.getStreamID(), this.streamer.getStreamIDPath() + "/classifier.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return model;
	}
	public J48 createClassifier(TrainingTestSet trainingTestSet, String name) {
		J48 model = null;
		model = ClassifierParametrization(trainingTestSet);
		try {
			gvh.print2PNG(model.graph(), name, this.streamer.getStreamIDPath()+ "/" + "Classifier" + name + ".png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return model;
	}

	public Classifier createClassifier(TrainingTestSet trainingTestSet, String name, Class<?> type) {
		
		Classifier classifier = null;
		if(type.getName().equals(HoeffdingTree.class.getName())) {
			classifier= HTClassifierParametrization(trainingTestSet);
		}
		if(type.getName().equals(J48.class.getName())) {
			type = (Class<?>) type.cast(ClassifierParametrization(trainingTestSet));
		}
		
		return classifier;
	}




}
