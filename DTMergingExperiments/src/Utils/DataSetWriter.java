package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Application.Experiment;

public class DataSetWriter {



	private final String line = System.lineSeparator();
	Streamer streamer;

	public DataSetWriter(Streamer streamer) {this.streamer=streamer;}

	public void csvWriter(String filename, double[][] data) {
		try {
			FileWriter writer = new FileWriter(new File(filename + "_generated" + ".csv"));
			writeData(writer, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public String arffWriter(String filename, double[][] data, String[] attributeNames) {

		double totalInstances = 0;
		String filepathresult = "";
		File arffFile = null;

		try {
			FileWriter writer = new FileWriter(new File(filename + "_generated" + ".arff"));			
			writer.write("@relation " + " generated" + line);

			for(int i = 0; i < attributeNames.length; i++) {
				writer.write("@attribute " + attributeNames[i] + " numeric" + line);
			}

			writer.write("@attribute class {0,1}" + line );
			writer.write(line);
			writer.write("@data"+ line);

			writeData(writer, data);

			arffFile = new File(filename + "_generated" + ".arff");
			filepathresult = arffFile.getPath();
			//streamer.experiment.matlab.plotDS(Experiment.readDataSetToArray(arffFile.getPath()), filename + "_generated_plot"+streamer.getStreamID()+".png", streamer.getStreamTitle());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filepathresult;
	}

	public String arffWriter(String filename, ArrayList<String[]> data, String[] attributeNames) {

		double totalInstances = 0;
		String filepathresult = "";
		File arffFile = null;

		try {
			FileWriter writer = new FileWriter(new File(filename + "_generated" + ".arff"));			
			writer.write("@relation " + filename + "_generated" + line);

			for(int i = 0; i < attributeNames.length; i++) {
				writer.write("@attribute " + attributeNames[i] + " numeric" + line);
			}

			writer.write("@attribute class {DOWN,UP}" + line );
			writer.write(line);
			writer.write("@data"+ line);

			writeData(writer, data);

			arffFile = new File(filename + "_generated" + ".arff");
			filepathresult = arffFile.getPath();
			//streamer.experiment.matlab.plotDS(Experiment.readDataSetToArray(arffFile.getPath()), filename + "_generated_plot"+streamer.getStreamID()+".png", streamer.getStreamTitle());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filepathresult;
	}

	private void writeData(FileWriter writer, double[][] data) throws IOException {

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				//writer.write(data[i][j]);

				//writer.write(Double.toString(data[i][j]));

				if(j<(data[i].length - 1)) {

					writer.write(Double.toString(data[i][j]));
					writer.write(",");
				}
				else {					
					writer.write(Long.toString(Math.round(data[i][j])));
				}
			}
			writer.write(line);				
		}		
		writer.close();
	}
	private void writeData(FileWriter writer, ArrayList<String[]> data) throws IOException {
		
		for(String[] instance: data) {
			for (int i = 0; i < instance.length; i++) {
				if(i<(instance.length)) {
					writer.write(instance[i]);
					writer.write(",");
				}
				else {
					writer.write(instance[i]);
				}
				
			}
			writer.write(line);
		}

		/*for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				//writer.write(data[i][j]);

				//writer.write(Double.toString(data[i][j]));

				if(j<(data[i].length - 1)) {

					writer.write(Double.toString(data[i][j]));
					writer.write(",");
				}
				else {					
					writer.write(Long.toString(Math.round(data[i][j])));
				}
			}
			writer.write(line);				
		}	*/	
		writer.close();
	}
}
