package weka.filters.supervised.instance.imbalanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import weka.core.Instance;
import weka.filters.supervised.instance.SMOTE;

public class SmoteAndTomekLinks extends SMOTE
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6214097953334039028L;

	public boolean batchFinished() throws Exception {
		
		super.batchFinished();
		
		HashSet<Instance> instances = new HashSet<Instance>();
		
		Instance currInstance = output();
		while (currInstance != null)
		{
			instances.add(currInstance);
			currInstance = output();
		}
		
		HashMap<Instance, Instance> nearestNeighborMap = getNearestNeighborsMap(instances);
		
		HashSet<TomekLink> tomekLinks = getTomekLinks(nearestNeighborMap);
		
		for (TomekLink currTomekLink : tomekLinks)
		{
			instances.remove(currTomekLink.getInstance(0));
			instances.remove(currTomekLink.getInstance(1));
		}
		
		for (Instance instance : instances)
		{
			push(instance);
		}
		
		return (numPendingOutput() != 0);
		
	}



	
}
