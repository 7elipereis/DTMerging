package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Writer {
	
	public void writeSummary(String filename, String resultSummary) {
		try {
			FileWriter writer = new FileWriter(new File(filename +"_results.txt"));
			writer.write(resultSummary);
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void csvWriter(String filename, double[][] data) {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		DecimalFormat df = (DecimalFormat)nf;
		df.setMinimumIntegerDigits(1);
		df.setMaximumFractionDigits(2);
		df.setGroupingUsed(false);
			
		try {
			FileWriter writer = new FileWriter(new File("Experiments/" +filename+"_results.csv"));
			for(int i =0; i<data.length; i++) {
				for(int j = 0; j< data[i].length; j++) {
					writer.write(df.format(data[i][j]));
					if(j<data[i].length) {
						writer.write(",");
					}
					if(j==data[i].length-1) {
						writer.write(System.lineSeparator());
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
