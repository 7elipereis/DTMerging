package Utils;

import java.io.File;
import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import Application.Experiment;
import DataMergingMatlabUtils.DatasetGenerator;
import DataMergingMatlabUtils.DatasetRotator;
import DataMergingMatlabUtils.Plotter;
import weka.core.Instances;

public class Matlab {

	DatasetGenerator dsg;
	DatasetRotator dsr;
	Plotter plotter;
	public Matlab() {
		try {
			dsg = new DatasetGenerator();
			dsr = new DatasetRotator();
			plotter = new Plotter();
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Instances generateDS2(double totalinstances,double angle, double driftshift, double driftpercentage, double noisepercentage, Streamer streamer) {

		Instances result = null;
		Object[] ds = null;
		try {			
			ds = dsr.BaseLineBatchDrift(1, totalinstances, angle, driftshift, noisepercentage);



		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if(ds.length > 0) {
			double[][] resultsingle = (double[][])((MWArray)ds[0]).toArray();

			double[][] rotatedarray = new double[resultsingle.length][];
			for(int i = 0; i < resultsingle.length; i++) {
				rotatedarray[i] = new double[] {Double.valueOf(resultsingle[i][0]),Double.valueOf(resultsingle[i][1]),Double.valueOf(resultsingle[i][2])};
			}

			DataSetWriter dsw = new DataSetWriter(streamer);
			String attributeNames[] = {"A","B"};
			String rotatedDSArff = dsw.arffWriter(streamer.getStreamIDPath() +"/"+ angle + "_DegreesRotated_", rotatedarray, attributeNames);
			//String rotatedDSArff = dsw.arffWriter(streamer.dir.getPath() + "/"+ streamer.getStreamID() +"/"+ angle + "_DegreesRotated_", rotatedarray, attributeNames);
			result = Experiment.readDataSet(rotatedDSArff);
			//(new File(rotatedDSArff)).delete();
		}

		return result;

	}


	public Instances generateDS2(int totalinstances,int angle, double drift, Streamer streamer) {

		Instances result = null;
		Object[] ds = null;
		try {

			ds = dsr.genRot2DDrift(1, totalinstances, angle, drift);



		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if(ds.length > 0) {
			float[][] resultsingle = (float[][])((MWArray)ds[0]).toArray();

			double[][] rotatedarray = new double[resultsingle.length][];
			for(int i = 0; i < resultsingle.length; i++) {
				rotatedarray[i] = new double[] {Double.valueOf(resultsingle[i][0]),Double.valueOf(resultsingle[i][1]),Double.valueOf(resultsingle[i][2])};
			}

			DataSetWriter dsw = new DataSetWriter(streamer);
			String attributeNames[] = {"A","B"};
			String rotatedDSArff = dsw.arffWriter(streamer.getStreamIDPath() +"/"+ angle + "_DegreesRotated_", rotatedarray, attributeNames);
			//String rotatedDSArff = dsw.arffWriter(streamer.dir.getPath() + "/"+ streamer.getStreamID() +"/"+ angle + "_DegreesRotated_", rotatedarray, attributeNames);
			result = Experiment.readDataSet(rotatedDSArff);
			(new File(rotatedDSArff)).delete();
		}

		return result;

	}

	public Instances generateDS2(int totalinstances,int angle, double driftshift, int driftpercentage, Streamer streamer) {

		Instances result = null;
		Object[] ds = null;
		try {

			ds = dsr.genRot2DDrift(1, totalinstances, angle, driftshift);



		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if(ds.length > 0) {
			float[][] resultsingle = (float[][])((MWArray)ds[0]).toArray();

			double[][] rotatedarray = new double[resultsingle.length][];
			for(int i = 0; i < resultsingle.length; i++) {
				rotatedarray[i] = new double[] {Double.valueOf(resultsingle[i][0]),Double.valueOf(resultsingle[i][1]),Double.valueOf(resultsingle[i][2])};
			}

			DataSetWriter dsw = new DataSetWriter(streamer);
			String attributeNames[] = {"A","B"};
			String rotatedDSArff = dsw.arffWriter(streamer.getStreamIDPath() +"/"+ angle + "_DegreesRotated_", rotatedarray, attributeNames);
			//String rotatedDSArff = dsw.arffWriter(streamer.dir.getPath() + "/"+ streamer.getStreamID() +"/"+ angle + "_DegreesRotated_", rotatedarray, attributeNames);
			result = Experiment.readDataSet(rotatedDSArff);
			(new File(rotatedDSArff)).delete();
		}

		return result;

	}

	public  Instances RotateDS(double[][] data,int transformation, Streamer streamer) {

		Instances dataset = null;
		Object[] rotated = null;

		try {

			MWNumericArray ds = new MWNumericArray(data, MWClassID.DOUBLE);
			rotated = dsr.rot(1, ds, transformation);



		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(rotated.length > 0) {
			float[][] resultsingle = (float[][])((MWArray)rotated[0]).toArray();

			double[][] rotatedarray = new double[resultsingle.length][];
			for(int i = 0; i < resultsingle.length; i++) {
				rotatedarray[i] = new double[] {Double.valueOf(resultsingle[i][0]),Double.valueOf(resultsingle[i][1]),Double.valueOf(resultsingle[i][2])};
			}

			DataSetWriter dsw = new DataSetWriter(streamer);
			String attributeNames[] = {"A","B"};
			String rotatedDSArff = dsw.arffWriter(streamer.dir.getPath() + "/"+ streamer.getStreamID() +"/"+ transformation + "_DegreesRotated_", rotatedarray, attributeNames);
			dataset = Experiment.readDataSet(rotatedDSArff);
			(new File(rotatedDSArff)).delete();
		}
		return dataset;

	}

	public  void plotDS(double[][] data, String filepath, String title) {

		try {

			MWNumericArray ds = new MWNumericArray(data, MWClassID.DOUBLE);
			plotter.plotDS(ds, filepath, title);
			ds.dispose();



		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  void plotDS(String[][] data, String filepath, String title) {

		try {

			MWNumericArray ds = new MWNumericArray(data, MWClassID.DOUBLE);

			plotter.plotDS(ds, filepath, title);
			ds.dispose();



		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  void plotDSWithRuleBoundaries(double[][] data, double[][] boundaries, char[][] labels, String filepath, String title) {

		try {

			MWNumericArray ds = new MWNumericArray(data, MWClassID.DOUBLE);
			MWCharArray l = new MWCharArray(labels);
			MWNumericArray b = new MWNumericArray(boundaries, MWClassID.DOUBLE);

			plotter.plotWithRuleBoundaries(ds, b,l,filepath, title);
			ds.dispose();


		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void plotPctCorrectStream(double[] data, String filepath, String title) {

		try {

			MWNumericArray ds = new MWNumericArray(data, MWClassID.DOUBLE);
			plotter.printPctCorrectStream(ds,filepath,title);

		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  Instances generateRotated(int size, int transformation,Streamer streamer) {

		Instances dataset = null;
		Object[] result = null;

		try {

			result = dsr.genRot(1, size, transformation);

		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(result.length > 0) {
			float[][] resultsingle = (float[][])((MWArray)result[0]).toArray();

			double[][] rotatedarray = new double[resultsingle.length][];
			for(int i = 0; i < resultsingle.length; i++) {
				rotatedarray[i] = new double[] {Double.valueOf(resultsingle[i][0]),Double.valueOf(resultsingle[i][1]),Double.valueOf(resultsingle[i][2])};
			}

			DataSetWriter dsw = new DataSetWriter(streamer);
			String attributeNames[] = {"A","B"};
			String rotatedDSArff = dsw.arffWriter(streamer.getStreamIDPath() +"/"+ transformation + "_DegreesRotated_", rotatedarray, attributeNames);
			dataset = Experiment.readDataSet(rotatedDSArff);
			(new File(rotatedDSArff)).delete();

		}

		return dataset;
	}

	public void clean() {

		// TODO Auto-generated method stub

	}
	public void plotStrEvalsComp(double[][] d1, double[][] d2, String name, String filename, String firstlinelegend, String secondlinelegend) {
		try {			


			MWNumericArray ds1 = new MWNumericArray(d1, MWClassID.DOUBLE);
			MWNumericArray ds2 = new MWNumericArray(d2, MWClassID.DOUBLE);

			//plotter.printPctCorrectStream(ds,filepath,title);
			plotter.plotStrEvalsComp(ds1,ds2,name,filename, firstlinelegend, secondlinelegend);

		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void plotStrEvalsComp3(double[][] d1, double[][] d2, double[][] d3, double[][] d4, String name, String filename, String firstlinelegend, String secondlinelegend, String thirdlegend, String fourthlegend, String xlabeltext, String ylabeltext) {
		try {			


			MWNumericArray ds1 = new MWNumericArray(d1, MWClassID.DOUBLE);
			MWNumericArray ds2 = new MWNumericArray(d2, MWClassID.DOUBLE);
			MWNumericArray ds3 = new MWNumericArray(d3, MWClassID.DOUBLE);
			MWNumericArray ds4 = new MWNumericArray(d4, MWClassID.DOUBLE);

			//plotter.printPctCorrectStream(ds,filepath,title);
			plotter.plotStrEvalsComp3(ds1,ds2,ds3,ds4,name,filename, firstlinelegend, secondlinelegend, thirdlegend, fourthlegend,xlabeltext,ylabeltext);
			

		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void plotStrEvalsCompBar(double[][] d1, double[][] d2, String name, String filename, String firstlinelegend, String secondlinelegend) {
		try {

			double[][] finald1 = d1;
			double[][] finald2 = d2;

			if(d1.length!=d2.length) {
				double[] zero = new double[2];
				zero[0] = 0;
				zero[1] = 0;
				int difference =  d1.length - d2.length;				
				if(difference>0) {
					double[][] newd2 = new double[d1.length][];
					for(int i = 0; i <= Math.abs(difference); i++) {
						newd2[i] = zero ;
					}
					for(int i = Math.abs(difference); i <= d1.length; i++) {
						newd2[i] = d2[i];
					}
					finald2 = newd2;
				}
				else {
					if(difference<0) {
						double[][] newd1 = new double[d2.length][];
						for(int i = 0; i <= Math.abs(difference); i++) {
							newd1[i] = zero ;
						}
						for(int i = Math.abs(difference); i <= d2.length; i++) {
							newd1[i] = d2[i];
						}
						finald1 = newd1;
					}
				}
			}

			MWNumericArray ds1 = new MWNumericArray(finald1, MWClassID.DOUBLE);
			MWNumericArray ds2 = new MWNumericArray(finald2, MWClassID.DOUBLE);

			//plotter.printPctCorrectStream(ds,filepath,title);
			//plotter.plotStrEvalsComp(ds1,ds2,name,filename, firstlinelegend, secondlinelegend);
			plotter.plotStrEvalsCompBar(ds1,ds2,name,filename + "bar", firstlinelegend, secondlinelegend);



		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
