package Utils;

import java.util.ArrayList;
import models.DataAttribute;
import weka.core.Attribute;
import weka.core.AttributeStats;

public class Rule {

	public double total;
	public ArrayList<Condition> conditions;
	public double data;
	public double incorrect;
	public String classvalue;
	public boolean isCategorical;



	public boolean isCategorical() {
		return isCategorical;
	}
	public void setCategorical(boolean isCategorical) {
		this.isCategorical = isCategorical;
	}
	public String getClassvalue() {
		return classvalue;
	}
	public void setClassvalue(String classvalue) {
		this.classvalue = classvalue;
	}
	public Rule() {
		conditions = new ArrayList<Condition>();
		incorrect = 0;
	}
	public Rule(double total, double incorrect) {
		conditions = new ArrayList<Condition>();
		this.incorrect = incorrect;
		this.total = total;
	}

	public double getData() {
		return this.data;
	}

	@Override
	public String toString() {

		StringBuilder result =  new StringBuilder();
		result.append("Number of instances: " + total + " ");
		result.append("Conditions: ");
		for (Condition condition : conditions) {
			result.append(" (");
			result.append(condition.toString());
			result.append(") ");
		}
		result.append("Class: " + data + " " + classvalue);

		return result.toString();
	}

	public double getRuleInfluence(double totalInstances) {
		double result = 0;		
		result = (total/totalInstances);		
		return result;
	}

	private ArrayList<Condition> filterConditions(){
		ArrayList<Condition> filteredConditions = new ArrayList<Condition>();
		ArrayList<DataAttribute> dataattributes = new ArrayList<DataAttribute>();

		for (Condition condition : conditions) {
			Condition hold = condition;
			if(!condition.isCategorical) {
				for(Condition c : conditions) {
					if(condition.nodeSide.equals(c.nodeSide) && condition.attribute.name().equals(c.attribute.name())) {
						switch (condition.nodeSide) {
						case Left:
							if(hold.threshold >= c.threshold) {hold=c;}
							break;
						case Right:
							if(hold.threshold <= c.threshold) {hold=c;}
							break;
						}
					}
				} 
			}
			if(!filteredConditions.contains(hold))filteredConditions.add(hold);
		}

		//creates empty-valued conditions for attributes which are not in the rule




		return filteredConditions;
	}


	public ArrayList<DataAttribute> discoverAttributesDataBoundaries(ArrayList<Attribute> metadatalist, ArrayList<AttributeStats> stats){
		ArrayList<Condition> conditions = filterConditions();
		ArrayList<Condition> boundaries = new ArrayList<Condition>();
		
		ArrayList<DataAttribute> attributelist = new ArrayList<DataAttribute>();
		for(Attribute a: metadatalist) {
			DataAttribute da = new DataAttribute(a.name()); 
			
			
			for(Condition c: conditions) {
				if(!c.isCategorical) {
					if(c.attribute.name().equals(a.name())) {
						switch (c.nodeSide) {
						case Left:
							da.setMax(c.threshold);
							break;
						case Right:
							da.setMin(c.threshold);
							break;

						default:
							break;
						}
					}
				}
				else {
					da.setMax(Double.parseDouble(c.attribute.getFixedValue()));
					da.setMin(Double.parseDouble(c.attribute.getFixedValue()));
					
				}
			}
			attributelist.add(da);
		}
		
		
		
		
		
		
		
		
		/*for(Condition condition : conditions) {	
			
			if(!condition.isCategorical) {
				switch (condition.nodeSide) {
				case Left:
					if(attributeExist(condition.attribute.name(), boundaries)) {
						for(Condition c : boundaries) {
							if(condition.attribute.name().equals(c.attribute.name())) {
								c.attribute.setMax(condition.threshold);
							}
						}					
					}
					else {
						condition.attribute.setMax(condition.threshold);
						boundaries.add(condition);
					}
					break;
				case Right:
					if(attributeExist(condition.attribute.name(), boundaries)) {
						for(Condition c : boundaries) {
							if(condition.attribute.name().equals(c.attribute.name())) {
								c.attribute.setMin(condition.threshold);							
							}
						}
					}
					else {
						condition.attribute.setMin(condition.threshold);
						boundaries.add(condition);
					}
					break;
				}
			}
			else {
				condition.attribute.setMax(Double.parseDouble(condition.attribute.getFixedValue()));
				condition.attribute.setMin(condition.attribute.getMax());
				boundaries.add(condition);
			}
		}
*/

		for(Attribute att : metadatalist) {

			if(!attributeExist(att.name(), conditions)) {
				double min =0,max =0;
				if(stats.get(att.index()).numericStats!=null) {
					min = stats.get(att.index()).numericStats.min;
					max = stats.get(att.index()).numericStats.max;
				}
				for(DataAttribute dataattribute: attributelist) {
					if(dataattribute.name().equals(att.name())) {
						dataattribute.setMax(max);
						dataattribute.setMin(min);
					}
				}
				//DataAttribute da = new DataAttribute(att.name(), min, max);

				//boundaries.add(new Condition(da));

			}

		}

		/*if(boundaries.size()>4) {
			DataAttribute[] attributesOrder = new DataAttribute[boundaries.size()];		
			for(Condition condition : boundaries) {

				switch (condition.attribute.name()) {
				case "date":
					
					attributesOrder[0] = condition.attribute;
					break;
				case "day":
					attributesOrder[1] = condition.attribute;
					break;
				case "period":
					attributesOrder[2] = condition.attribute;
					break;
				case "nswprice":
					attributesOrder[3] = condition.attribute;
					break;
				case "nswdemand":
					attributesOrder[4] = condition.attribute;
					break;
				case "vicprice":
					attributesOrder[5] = condition.attribute;
					break;
				case "vicdemand":
					attributesOrder[6] = condition.attribute;
					break;
				case "transfer":
					attributesOrder[7] = condition.attribute;
					break;
				case "class":
					attributesOrder[8] = condition.attribute;
					break;

				default:
					break;
				}
				

			}
			ArrayList<DataAttribute> attributes = new ArrayList<DataAttribute>();
			for(int i = 0; i< attributesOrder.length; i++)attributes.add(attributesOrder[i]);
			if(attributes.size()!=2 && !attributes.isEmpty()) {
				if(attributes.get(0).name().equals("A")) {
					attributes.add(new DataAttribute("B",0,100));
				}
				if(attributes.get(0).name().equals("B")) {
					attributes.add(new DataAttribute("A",0,100));
				}
			}

			return attributes;
		}
		else {
			ArrayList<DataAttribute> attributes = new ArrayList<DataAttribute>();
			for(Condition c: boundaries) {
				attributes.add(c.attribute);
			}
			return attributes;
		}*/
		
		return attributelist;
		
	}

	private boolean attributeExist(String name, ArrayList<Condition> conditions) {
		boolean result = false;
		for(Condition condition : conditions) {
			if(condition.attribute.name().equals(name)) {result = true; break;}
		}
		return result;
	}

}
