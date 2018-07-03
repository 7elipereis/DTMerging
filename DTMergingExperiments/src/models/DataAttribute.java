package models;

import java.util.Random;



import weka.core.Attribute;

public class DataAttribute extends Attribute {
	
	double min;
	double max;
	private Random random = new Random();
	public String fixedValue;
	
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public DataAttribute(String attributeName) {
		super(attributeName);
		setMin(0);
		setMax(100);
	}
	public DataAttribute(String attributeName, double min, double max) {
		super(attributeName);
		
		setMin(min);
		setMax(max);
	}
	public DataAttribute(String attributeName, String value) {
		super(attributeName);
		setStringValue(value);
		fixedValue = value;
		
	}
	
	public String getFixedValue() {
		return fixedValue;
	}
	public double getRandomValue() {
		double result = 0;
		result = min + (max-min)*random.nextDouble();
		return result;
	}
	
	

}
