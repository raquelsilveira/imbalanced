package weka.filters.supervised.instance.imbalanced;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.DistanceBasedFilter;

/**
 * 
 * @author uayelet
 *
 */
public class CnnAndTomekLinks extends DistanceBasedFilter
{

	protected HashMap<Double, HashSet<Instance>> instancesOfEachClass;
	
	/**
	 * Sets the format of the input instances.
	 *
	 * @param instanceInfo 	an Instances object containing the input 
	 * 				instance structure (any instances contained in 
	 * 				the object are ignored - only the structure is required).
	 * @return 			true if the outputFormat may be collected immediately
	 * @throws Exception 		if the input format can't be set successfully
	 */
	public boolean setInputFormat(Instances instanceInfo) throws Exception {
		super.setInputFormat(instanceInfo);
		super.setOutputFormat(instanceInfo);
		return true;
	}
	
	public boolean batchFinished() throws Exception {
		if (getInputFormat() == null) {
			throw new IllegalStateException("No input instance format defined");
		}
	
		if (!m_FirstBatchDone) {
			doCnnAndTomekLinks();
		}
		flushInput();

		m_NewBatch = true;
		m_FirstBatchDone = true;
		return (numPendingOutput() != 0);
	}

	/**
	 * Main function of this class which performs the CNN and then Tomek Links
	 */
	private void doCnnAndTomekLinks()
	{	
		//a set of instances per class value
		instancesOfEachClass = new HashMap<Double, HashSet<Instance>>();
		
		Enumeration instanceEnum = getInputFormat().enumerateInstances();
		
		//loop over all the instances and add each one to the matching set according to its class
		while(instanceEnum.hasMoreElements()) {
			Instance instance = (Instance) instanceEnum.nextElement();
			
			double classValue = instance.classValue();
			
			if (!instancesOfEachClass.containsKey(classValue))
			{
				instancesOfEachClass.put(classValue, new HashSet<Instance>());
			}
			instancesOfEachClass.get(classValue).add(instance);
		}
		
		//find the minority class
		double minorityClassValue = findMinorityClass();
		
		//at first, the CNN output is all the minority class instances
		HashSet<Instance> cnnOutput = instancesOfEachClass.get(minorityClassValue);
		
		//next, add only the majority instances which are not classified correctly by one nearest neighbor
		for (Double currClassValue : instancesOfEachClass.keySet())
		{
			if (currClassValue != minorityClassValue)
			{
				addMajorityInstancesToCnnOutput(cnnOutput, currClassValue);
			}
		}
		
		//perform tomek links and remove only the majority class instances
		HashMap<Instance, Instance> nearestNeighborMap = getNearestNeighborsMap(cnnOutput);
		HashSet<TomekLink> tomekLinks = getTomekLinks(nearestNeighborMap);
		
		//loop over the tomek links and remove the majority class instances from CNN output
		for (TomekLink currTomekLink : tomekLinks)
		{
			for (int i = 0; i < 2; i++)
			{
				if (currTomekLink.getInstance(i).classValue() != minorityClassValue)
				{
					cnnOutput.remove(currTomekLink.getInstance(i));
				}
			}
		}
		
		//push all the remaining instances out to the output queue
		for (Instance instance : cnnOutput)
		{
			push(instance);
		}
		
	}

	/**
	 * Find the value of the minority class
	 * @return
	 */
	protected double findMinorityClass()
	{
		int minorityClassSize = Integer.MAX_VALUE;
		double minorityClassValue = 0;
		
		//loop over all the classes
		for (Double classValue : instancesOfEachClass.keySet())
		{
			//check if the current class is smaller than the minority class found so far
			int currClassSize = instancesOfEachClass.get(classValue).size();
			if (currClassSize < minorityClassSize)
			{
				minorityClassSize = currClassSize;
				minorityClassValue = classValue;
			}
		}
		return minorityClassValue;
	}

	/**
	 * This function adds the instances of the majority class to the CNN output
	 * It only adds the instances which are not classified correctly by one nearest neighbor
	 * @param cnnOutput - the subset in the CNN algorithm
	 * @param majorityClassValue - the value of the majority class
	 */
	private void addMajorityInstancesToCnnOutput(HashSet<Instance> cnnOutput,
			Double majorityClassValue)
	{
		//loop over all the majority class instances
		for (Instance majorityClassInstance : instancesOfEachClass.get(majorityClassValue))
		{
			//find the nearest neighbor of the current majority class instance within the current CNN output
			Instance nearestNeighbor = findNearestNeighbor(majorityClassInstance, cnnOutput);
			
			//if the nearest neighbor is from a different class it means 
			//that the current instance is not classified correctly
			//so we need to add it the CNN output
			if (nearestNeighbor.classValue() != majorityClassValue)
			{
				cnnOutput.add(majorityClassInstance);
			}
		}
	}
		
	
}
