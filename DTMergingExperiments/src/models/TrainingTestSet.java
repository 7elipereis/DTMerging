package models;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import Application.Experiment;
import Utils.Streamer;
import weka.core.Instances;
import weka.core.converters.CSVSaver;

public class TrainingTestSet {
	
	Instances trainingdata;
	public Instances getTrainingdata() {
		return trainingdata;
	}

	public void setTrainingdata(Instances trainingdata) {
		this.trainingdata = trainingdata;
	}

	public int getTrainingPercentage() {
		return trainingPercentage;
	}

	public void setTrainingPercentage(int trainingPercentage) {
		this.trainingPercentage = trainingPercentage;
	}

	public Instances getTestdata() {
		return testdata;
	}

	public void setTestdata(Instances testdata) {
		this.testdata = testdata;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Instances getDataset() {
		return dataset;
	}

	public void setDataset(Instances dataset) {
		this.dataset = dataset;
	}

	int trainingPercentage;
	Instances testdata;
	Experiment experiment;
	Instances dataset;
	File dir;
	Streamer streamer;
	
	public void plotDataSet(String filename, String title) {
		
		streamer.experiment.matlab.plotDS(Experiment.readDataSetToArray(this.dataset), filename + "_generated_plot"+streamer.getStreamID()+".png", title + " " + streamer.getStreamTitle());
		
	}
	
	public TrainingTestSet(Instances dataset, int training_percentage, Streamer streamer) {
			
		this.streamer = streamer;		
		this.dataset = dataset;
		this.dataset.setClassIndex(dataset.numAttributes()-1);
		//randomizes the original dataset for the spliting(training and test) process.
		//Necessary if the data is sorted in a clustered way
		this.dataset.randomize(new Random(100));
		//Defining dataset splitting sizes
		trainingPercentage = training_percentage;		
		int testPercentage = 100-trainingPercentage;
		//dataset spliting
		trainingdata = new Instances(dataset, 0, Experiment.getTrainSize(trainingPercentage,dataset.numInstances()));
		trainingdata.setRelationName(streamer.getStreamIDPath() + "/TrainingData");
		trainingdata.setClassIndex(dataset.numAttributes()-1);
		testdata = new Instances(dataset, trainingdata.numInstances(), Experiment.getTestSize(testPercentage, dataset.numInstances()));
		testdata.setRelationName(streamer.getStreamIDPath() + "/TestData");
		testdata.setClassIndex(dataset.numAttributes()-1);
		//writes down the training dataset into files
		//WriteDownSplit();
		//plots the dataset
		//String plotfilename = dataset.relationName().replace(experiment.experiment_datasets_directory.getPath(), "");
		//plotfilename = experiment.experiment_datasets_directory.getPath() + plotfilename;
		//streamer.experiment.matlab.plotDS(experiment.dataSetToArray(dataset), streamer.getStreamIDPath() + "/DataSetPlot"+streamer.getStreamID()+".png", streamer.getStreamTitle());
		//experiment.copyOriginalDataSet(originaldataset_file);
		
		
	}
	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	void WriteDownSplit() {
		
		//writes down the training dataset
		CSVSaver csvTraining = new CSVSaver();
		csvTraining.setInstances(trainingdata);
		try {
			csvTraining.setFile(new File(streamer.getStreamIDPath()+"/TrainingData.csv"));
			csvTraining.writeBatch();			
			streamer.experiment.matlab.plotDS(Experiment.dataSetToArray(trainingdata), streamer.getStreamIDPath() + "/TrainingDataPlot"+streamer.getStreamID()+".png", streamer.getStreamTitle());
			Experiment.experiment_summary.append("training dataset "+ trainingdata.relationName() +" created and ploted"+ Experiment.nl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//writes down the test dataset
		CSVSaver csvTest = new CSVSaver();
		csvTest.setInstances(testdata);
		try {
			csvTest.setFile(new File(streamer.getStreamIDPath() +"/TestData.csv"));
			csvTest.writeBatch();
			streamer.experiment.matlab.plotDS(experiment.dataSetToArray(testdata), streamer.getStreamIDPath() + "/TestDataPlot"+streamer.getStreamID()+".png", streamer.getStreamTitle());
			streamer.experiment.experiment_summary.append("test dataset "+ testdata.relationName() +" created and ploted"+ experiment.nl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
