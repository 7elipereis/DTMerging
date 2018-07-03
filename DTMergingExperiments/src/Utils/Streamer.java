package Utils;

import java.io.File;

import Application.Experiment;
import models.TrainingTestSet;
import weka.classifiers.Classifier;
import weka.core.Drawable;
import weka.core.Instances;

public class Streamer {

	Instances dataset;
	int currentTransformation;
	int transformationStep;
	int trainingPercentage;
	public Experiment experiment;
	int streamID;
	File dir;

	public int getStreamID() {
		return streamID;
	}
	public void setStreamID(int streamID) {
		this.streamID = streamID;
	}
	public File getDir() {
		return dir;
	}
	public void setDir(File dir) {
		this.dir = dir;
	}
	public String getStreamTitle() {
		return "Stream " + this.getStreamID();
	}
	public int batchsize = 0;
	public Streamer(Experiment experiment) {
		this.dir = new File(experiment.getExperiment_directory().getPath() + "/Streamer");
		this.dir.mkdirs();
		this.experiment = experiment;
	}

	public Streamer(Experiment experiment, int transformationStep, int trainingPercentage) {
		this.dir = new File(experiment.getExperiment_directory().getPath() + "/Streamer");
		this.dir.mkdirs();
		this.experiment = experiment;
		this.transformationStep = transformationStep;
		this.trainingPercentage = trainingPercentage;
	}


	public Streamer(String datasetfilepath, Experiment experiment, int transformationStep, int trainingPercentage) {		
		this.dir = new File(experiment.getExperiment_directory().getPath() + "/Streamer");
		this.dataset = Experiment.readDataSet(datasetfilepath);
		this.dir.mkdirs();		
		this.streamID = 0;		
		this.currentTransformation = 0;
		this.transformationStep = transformationStep;
		this.experiment = experiment;
		this.trainingPercentage = trainingPercentage;		
	}



	public TrainingTestSet NextStreamFromRotation() {
		streamID+=1;
		TrainingTestSet set = null;
		Instances dataset = experiment.matlab.generateRotated(1000, currentTransformation, this);
		set = new TrainingTestSet(dataset, 70, this);
		currentTransformation+=transformationStep;		
		return set;
	}
	public TrainingTestSet NextStreamFromRotation2D(int anglestep) {

		streamID+=1;
		TrainingTestSet set = null;
		Instances dataset = experiment.matlab.generateDS2(1000, ( streamID-1)*anglestep, 1, this);
		set = new TrainingTestSet(dataset, 70, this);
		currentTransformation = (streamID-1)*anglestep;

		if(this.dataset==null) {
			this.dataset = dataset;
		}

		return set;
	}
	public TrainingTestSet NextStreamFromBaseLineBatchDrift(double anglestep, double driftshift, double driftpercentage, double noisepercentage) {

		streamID+=1;
		int angle = ((int)anglestep) * (streamID-1);
		int angleminlim =0;
		int anglemaxlim = 360;
		
		int anglewidth = anglemaxlim - angleminlim;
		int angleoffset = angle - angleminlim;
		
		int normalangle = (angleoffset- ((angleoffset/anglewidth) * anglewidth) + angleminlim);
		TrainingTestSet set = null;		
		Instances dataset = experiment.matlab.generateDS2(1000, normalangle, driftshift, driftpercentage, noisepercentage, this);
		
		set = new TrainingTestSet(dataset, 70, this);
		currentTransformation = (streamID-1)* ((int) anglestep);

		if(this.dataset==null) {
			this.dataset = dataset;
		}
		
		set.plotDataSet(experiment.getStreamer().getStreamIDPath() + "/BatchWithDrift", "Drift shift =" + driftshift + " Drift percentage =" + driftpercentage + "% Noise percentage =" + noisepercentage + "%");

		return set;
	}
	public TrainingTestSet NextStreamFromRotation2D(int anglestep, double drift) {

		streamID+=1;
		TrainingTestSet set = null;
		Instances dataset = experiment.matlab.generateDS2(1000, ( streamID-1)*anglestep, drift, this);
		set = new TrainingTestSet(dataset, 70, this);
		currentTransformation = (streamID-1)*anglestep;

		if(this.dataset==null) {
			this.dataset = dataset;
		}

		return set;
	}
	public TrainingTestSet NextStreamFromRotation2D(int anglestep, double driftshift, int driftpercentage) {

		streamID+=1;
		TrainingTestSet set = null;
		Instances dataset = experiment.matlab.generateDS2(1000, ( streamID-1)*anglestep, driftshift, this);
		set = new TrainingTestSet(dataset, 70, this);
		currentTransformation = (streamID-1)*anglestep;

		if(this.dataset==null) {
			this.dataset = dataset;
		}

		return set;
	}

	public TrainingTestSet NextStreamFromFile() {
		streamID+=1;	

		TrainingTestSet set = null;

		int firstInstanceIndex = 0;
		int lastInstanceIndex = 0;
		int totalInstances = this.getDataset().size();
		int batchSize = totalInstances / transformationStep;
		int nBatches = totalInstances / batchSize;

		if(nBatches<streamID)return null;

		lastInstanceIndex = (streamID*batchSize) - 1;		
		firstInstanceIndex = (lastInstanceIndex+1) - batchSize;	

		Instances ds = new Instances(this.getDataset(), firstInstanceIndex, batchSize);
		set = new TrainingTestSet(ds, 70, this);
		currentTransformation+=transformationStep;		
		return set;
	}

	public void printClassifier(Classifier classifier, String label) {
		
		try {
			this.experiment.gvh.print2PNG(((Drawable)classifier).graph(), label, getStreamIDPath() + "/" + "StreamClassifier" + getStreamID() + ".png");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Instances getDataset() {

		if(dataset!=null) {
			return dataset;
		}
		else {
			return experiment.matlab.generateDS2(0, 0, 1, this);
		}

	}
	public void setDataset(Instances dataset) {
		this.dataset = dataset;
	}
	public int getCurrentTransformation() {
		return currentTransformation;
	}
	public void setCurrentTransformation(int currentTransformation) {
		this.currentTransformation = currentTransformation;
	}
	public int getTransformationStep() {
		return transformationStep;
	}
	public void setTransformationStep(int transformationStep) {
		this.transformationStep = transformationStep;
	}
	public int getTrainingPercentage() {
		return trainingPercentage;
	}
	public void setTrainingPercentage(int trainingPercentage) {
		this.trainingPercentage = trainingPercentage;
	}
	public Experiment getExperiment() {
		return experiment;
	}
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	public String getStreamIDPath() {
		File streamIDDir = new File(getDir().getPath()+"/"+getStreamID());
		if(!streamIDDir.exists())streamIDDir.mkdirs();
		return streamIDDir.getPath();
	}
}
