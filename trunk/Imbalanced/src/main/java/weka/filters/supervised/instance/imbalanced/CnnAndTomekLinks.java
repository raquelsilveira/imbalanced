package weka.filters.supervised.instance.imbalanced;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.TechnicalInformation;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.filters.DistanceBasedFilter;

/**
 *  A filter which undersamples a dataset by applying CNN
 *  (choosing a subset which classifys correctly all the samples in the set 
 *  according to one nearest neighbors - starting with the minority samples, 
 *  and iteratively adding to the subset only samples from the majority class 
 *  which were not classifying correctly according to the current subset), 
 *  and then finding Tomek Links (pairs from different classes
 *  which are closest to each other than to any other sample) and removing 
 *  the majority class instance from each Tomek Link.
 * 
 * @author Ayelet and Roni
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
	
	
	/**
	 * Returns a string describing this classifier.
	 * 
	 * @return 		a description of the classifier suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Undersamples a dataset by applying CNN" + 
		"(choosing a subset which classifys correctly all the samples in the set according to one nearest neighbors - " +
		" starting with the minority samples, and iteratively adding to the subset only samples from the majority class which" +
		" were not classifying correctly according to the current subset), and then finding Tomek Links (pairs from different classes" +
		" which are closest to each other than to any other sample) and removing the majority class instance from each Tomek Link." +
		" The original dataset must fit entirely in memory." +		
		" For more information, see \n\n" 
		+ getTechnicalInformation().toString();
	}
	
	
	/**
	 * Returns an instance of a TechnicalInformation object, containing 
	 * detailed information about the technical background of this class,
	 * e.g., paper reference or book this class is based on.
	 * 
	 * @return 		the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result = new TechnicalInformation(Type.ARTICLE);

		result.setValue(Field.AUTHOR, "G E A P A Batista, R C Prati, M C Monard");
		result.setValue(Field.TITLE, "A study of the behavior of several methods for balancing machine learning training data");
		result.setValue(Field.JOURNAL, "Sigkdd Explorations");
		result.setValue(Field.YEAR, "2004");

		return result;
	}
	
	
	/** 
	 * Returns the Capabilities of this filter.
	 *
	 * @return            the capabilities of this object
	 * @see               Capabilities
	 */
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();

		// attributes
		result.enableAllAttributes();
		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);

		return result;
	}
	
	
	/**
	 * Signify that this batch of input to the filter is finished. 
	 * If the filter requires all instances prior to filtering,
	 * output() may now be called to retrieve the filtered instances.
	 *
	 * @return 		true if there are instances pending output
	 * @throws IllegalStateException if no input structure has been defined
	 * @throws Exception 	if provided options cannot be executed 
	 * 			on input instances
	 */
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
	
	
	/**
	 * Input an instance for filtering. Filter requires all
	 * training instances be read before producing output.
	 *
	 * @param instance 		the input instance
	 * @return 			true if the filtered instance may now be
	 * 				collected with output().
	 * @throws IllegalStateException if no input structure has been defined
	 */
	public boolean input(Instance instance) {
		if (getInputFormat() == null) {
			throw new IllegalStateException("No input instance format defined");
		}
		if (m_NewBatch) {
			resetQueue();
			m_NewBatch = false;
		}
		if (m_FirstBatchDone) {
			push(instance);
			return true;
		} else {
			bufferInput(instance);
			return false;
		}
	}
	
		
	
}
