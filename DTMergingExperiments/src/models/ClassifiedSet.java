package models;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;

public class ClassifiedSet {

	TrainingTestSet trainingTestSet;
	TrainingTestSet recurringConcept;
	TrainingTestSet batchTestSet;
	public TrainingTestSet getRecurringConcept() {
		return recurringConcept;
	}

	public void setRecurringConcept(TrainingTestSet recurringConcept) {
		this.recurringConcept = recurringConcept;
	}
	//J48 classifier;
	Classifier classifier;
	Evaluation evaluation;
	double pctCorrect;
	int streamID;

	public int getStreamID() {
		return streamID;
	}

	public void setStreamID(int streamID) {
		this.streamID = streamID;
	}

	public ClassifiedSet(TrainingTestSet trainingTestSet, Classifier classifier, int streamID) {
		this.trainingTestSet = trainingTestSet;
		this.streamID = streamID;
		this.classifier = classifier;
		try {
			evaluation = new Evaluation(trainingTestSet.getTrainingdata());
			evaluation.evaluateModel(classifier, trainingTestSet.getTestdata());
			pctCorrect = evaluation.pctCorrect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public ClassifiedSet(TrainingTestSet trainingTestSet, TrainingTestSet reccuringConcept, TrainingTestSet batchtestset, Classifier classifier, int streamID) {
		this.trainingTestSet = trainingTestSet;
		this.setRecurringConcept(reccuringConcept);
		this.batchTestSet = batchtestset;
		this.streamID = streamID;
		this.classifier = classifier;
		try {
			evaluation = new Evaluation(trainingTestSet.getTrainingdata());
			evaluation.evaluateModel(classifier, batchtestset.getTestdata());
			pctCorrect = evaluation.pctCorrect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClassifiedSet(TrainingTestSet trainingTestSet, TrainingTestSet reccuringConcept, Classifier classifier, int streamID) {
		this.trainingTestSet = trainingTestSet;
		this.setRecurringConcept(reccuringConcept);
		this.streamID = streamID;
		this.classifier = classifier;
		try {
			evaluation = new Evaluation(trainingTestSet.getTrainingdata());
			evaluation.evaluateModel(classifier, trainingTestSet.getTestdata());
			pctCorrect = evaluation.pctCorrect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public TrainingTestSet getTrainingTestSet() {
		return trainingTestSet;
	}

	public void setTrainingTestSet(TrainingTestSet trainingTestSet) {
		this.trainingTestSet = trainingTestSet;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(J48 classifier) {
		this.classifier = classifier;
		try {
			evaluation = new Evaluation(trainingTestSet.getTrainingdata());
			evaluation.evaluateModel(classifier, trainingTestSet.getTestdata());
			pctCorrect = evaluation.pctCorrect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}
	public double getPctCorrect() {
		return pctCorrect;
	}
	public void setPctCorrect(double pctCorrect) {
		this.pctCorrect = pctCorrect;
	}


}
