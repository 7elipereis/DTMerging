package Utils;

import models.DataAttribute;

public class Condition {
	
	
	
	DataAttribute attribute;
	//public String attribute;
	public NodeSide nodeSide;
	public double threshold;
	public boolean isCategorical;
	
	public Condition(DataAttribute attribute) {
		this.attribute = attribute;
		this.isCategorical = false;
	}
	public Condition(DataAttribute attribute, boolean iscategorical) {
		this.attribute = attribute;
		this.isCategorical = iscategorical;
	}
	
	@Override
	public String toString() {
		
		StringBuilder result = new StringBuilder();
		
		result.append(attribute.name() + " ");
		if(nodeSide!=null) {
			switch (nodeSide) {
			case Left:
				result.append("<=");
				break;
			case Right:
				result.append(">");
				break;
			}
		}
		result.append(threshold);
		
		return result.toString();
	}
}
