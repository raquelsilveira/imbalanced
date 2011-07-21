package weka.filters;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import weka.core.Attribute;
import weka.core.Instance;
import weka.filters.supervised.instance.imbalanced.TomekLink;

public abstract class DistanceBasedFilter extends Filter
{

	/** Value Distance Metric matrices for nominal features */
	protected Map m_VdmMap;
	
	protected void calcValueDistanceMetric()
	{
		Enumeration instanceEnum;
		// compute Value Distance Metric matrices for nominal features
		//Ayelet & Roni: moved m_VdmMap to class member
		m_VdmMap = new HashMap();
		Enumeration attrEnum = getInputFormat().enumerateAttributes();
		while(attrEnum.hasMoreElements()) {
			Attribute attr = (Attribute) attrEnum.nextElement();
			if (!attr.equals(getInputFormat().classAttribute())) {
				if (attr.isNominal() || attr.isString()) {
					double[][] vdm = new double[attr.numValues()][attr.numValues()];
					m_VdmMap.put(attr, vdm);
					int[] featureValueCounts = new int[attr.numValues()];
					int[][] featureValueCountsByClass = new int[getInputFormat().classAttribute().numValues()][attr.numValues()];
					instanceEnum = getInputFormat().enumerateInstances();
					while(instanceEnum.hasMoreElements()) {
						Instance instance = (Instance) instanceEnum.nextElement();
						int value = (int) instance.value(attr);
						int classValue = (int) instance.classValue();
						featureValueCounts[value]++;
						featureValueCountsByClass[classValue][value]++;
					}
					for (int valueIndex1 = 0; valueIndex1 < attr.numValues(); valueIndex1++) {
						for (int valueIndex2 = 0; valueIndex2 < attr.numValues(); valueIndex2++) {
							double sum = 0;
							for (int classValueIndex = 0; classValueIndex < getInputFormat().numClasses(); classValueIndex++) {
								double c1i = (double) featureValueCountsByClass[classValueIndex][valueIndex1];
								double c2i = (double) featureValueCountsByClass[classValueIndex][valueIndex2];
								double c1 = (double) featureValueCounts[valueIndex1];
								double c2 = (double) featureValueCounts[valueIndex2];
								double term1 = c1i / c1;
								double term2 = c2i / c2;
								sum += Math.abs(term1 - term2);
							}
							vdm[valueIndex1][valueIndex2] = sum;
						}
					}
				}
			}
		}
	}

	protected double calculateDistance(Instance instanceI, Instance instanceJ)
	{
		Enumeration attrEnum;
		double distance = 0;
		attrEnum = getInputFormat().enumerateAttributes();
		while(attrEnum.hasMoreElements()) {
			Attribute attr = (Attribute) attrEnum.nextElement();
			if (!attr.equals(getInputFormat().classAttribute())) {
				double iVal = instanceI.value(attr);
				double jVal = instanceJ.value(attr);
				if (attr.isNumeric()) {
					distance += Math.pow(iVal - jVal, 2);
				} else {
					distance += ((double[][]) m_VdmMap.get(attr))[(int) iVal][(int) jVal];
				}
			}
		}
		distance = Math.pow(distance, .5);
		return distance;
	}
	
	protected HashMap<Instance, Instance> getNearestNeighborsMap(
			HashSet<Instance> instances)
	{
		//for each instance, we calculate its nearest neighbor
		HashMap<Instance, Instance> nearestNeighborMap = new HashMap<Instance, Instance>();
		
		for(Instance instanceI : instances)
		{
			Instance nearestNeighbor = findNearestNeighbor(instanceI, instances); 
			nearestNeighborMap.put(instanceI, nearestNeighbor);
		}
		return nearestNeighborMap;
	}

	protected Instance findNearestNeighbor(Instance instance,
			HashSet<Instance> neighbors)
	{
		double minDistance = Double.MAX_VALUE;
		Instance nearestNeighbor = null;
		for (Instance instanceJ : neighbors)
		{
			if (instance != instanceJ)
			{
				double currDistance = calculateDistance(instance, instanceJ);
				if (currDistance < minDistance)
				{
					minDistance = currDistance;
					nearestNeighbor = instanceJ;
				}
			}				
		}
		return nearestNeighbor;
	}
	
	protected HashSet<TomekLink> getTomekLinks(HashMap<Instance, Instance> nearestNeighborMap)
	{
		HashSet<TomekLink> tomekLinks = new HashSet<TomekLink>();
		
		for(Instance instance : nearestNeighborMap.keySet())
		{
			Instance neighbor = nearestNeighborMap.get(instance);
						
			if (neighbor != null)
			{
				if (instance.classValue() != neighbor.classValue()
						&& nearestNeighborMap.get(neighbor) == instance)
				{
					tomekLinks.add(new TomekLink(instance, neighbor));
				}
			}

		}
		return tomekLinks;
	}
	
	
}
