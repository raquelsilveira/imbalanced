package weka.filters.supervised.instance.imbalanced;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import weka.core.Instance;
import weka.core.Option;
import weka.core.TechnicalInformation;
import weka.core.Utils;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.filters.supervised.instance.SMOTE;

/**
 * 
 * A filter which resamples a dataset 
 * by applying the Synthetic Minority Oversampling TEchnique (SMOTE), 
 * and then applying ENN (Wilson's Edited Nearest Neighbor rule) - 
 * Removing each sample which is misclassified by its N nearest 
 * neighbors
 * 
 * @author Ayelet and Roni
 *
 */
public class SmoteAndEnn extends SMOTE
{

	private static final long serialVersionUID = 232299741996551652L;

	protected int m_EnnNumNearestNeighbors = 3;
	
	/**
	 * Get the number of nearest neighbors to use in the ENN phase
	 * @return
	 */
	public int getEnnNumNearestNeighbors()
	{
		return m_EnnNumNearestNeighbors;
	}


	/**
	 * Set the number of nearest neighbors to use in the ENN phase (at least 1)
	 * @param mEnnNumNearestNeighbors
	 */
	public void setEnnNumNearestNeighbors(int mEnnNumNearestNeighbors)
	{
		if (mEnnNumNearestNeighbors >= 1)
		{
			m_EnnNumNearestNeighbors = mEnnNumNearestNeighbors;
		}
		else
		{
			System.err.println("Number of nearest neighbors must be at least 1!");
		}		
	}


	/**
	 * Returns a string describing this classifier.
	 * 
	 * @return 		a description of the classifier suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Resamples a dataset by applying the Synthetic Minority Oversampling TEchnique (SMOTE), and then applying ENN (Wilson's Edited Nearest Neighbor rule) - Removing each sample which is misclassified by its N nearest neighbors" +
		" The original dataset must fit entirely in memory." +
		" The amount of SMOTE and number of nearest neighbors for SMOTE and for ENN may be specified." +
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
	 * Returns an enumeration describing the available options.
	 *
	 * @return an enumeration of all the available options.
	 */
	@SuppressWarnings("unchecked")
	public Enumeration listOptions() {
		
		//get all the options from SMOTE (the parent)
		Vector newVector = new Vector();
		
		Enumeration smoteOptions = super.listOptions();
		while (smoteOptions.hasMoreElements())
		{
			newVector.add(smoteOptions.nextElement());
		}
		
		newVector.addElement(new Option(
				"\tNumber of nearest neighbors for the ENN phase\n"
				+ "\t(default 3)",
				"N", 1, "-N <nearest-neighbors>"));
		
		return newVector.elements();
	
	}
	
	
	/**
	 * Parses a given list of options.
	 * 
   <!-- options-start -->
	 * Valid options are: <p/>
	 * 
	 * <pre> -S &lt;num&gt;
	 *  Specifies the random number seed
	 *  (default 1)</pre>
	 * 
	 * <pre> -P &lt;percentage&gt;
	 *  Specifies percentage of SMOTE instances to create.
	 *  (default 100.0)
	 * </pre>
	 * 
	 * <pre> -K &lt;nearest-neighbors&gt;
	 *  Specifies the number of nearest neighbors to use.
	 *  (default 5)
	 * </pre>
	 * 
	 * <pre> -C &lt;value-index&gt;
	 *  Specifies the index of the nominal class value to SMOTE
	 *  (default 0: auto-detect non-empty minority class))
	 * </pre>
	 * 
	 * <pre> -N &lt;enn-nearest-neighbors&gt;
	 *  Specifies the number of nearest neighbors for the ENN phase
	 *  (default 3: auto-detect non-empty minority class))
	 * </pre>
	 * 
   <!-- options-end -->
	 *
	 * @param options the list of options as an array of strings
	 * @throws Exception if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception 
	{
		super.setOptions(options);
		
		String nnStr = Utils.getOption('N', options);
		if (nnStr.length() != 0) {
			setEnnNumNearestNeighbors(Integer.parseInt(nnStr));
		} else {
			setEnnNumNearestNeighbors(3);
		}
	}
	
	
	/**
	 * Returns the tip text for this property.
	 * 
	 * @return 		tip text for this property suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String ennNumNearestNeighborsTipText() {
		return "The number of nearest neighbors to use for the ENN phase.";
	}
	
	/**
	 * Gets the current settings of the filter.
	 *
	 * @return an array 	of strings suitable for passing to setOptions
	 */
	public String[] getOptions() 
	{
		String[] parentRes = super.getOptions();
		
		ArrayList<String> res = new ArrayList<String>();
		
		for (String str : parentRes)
		{
			res.add(str);
		}
		
		res.add("-N");
		res.add("" + getEnnNumNearestNeighbors());
		
		return res.toArray(new String[res.size()]);
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
		
		//run the SMOTE processing
		super.batchFinished();
		
		//get all the instances after the SMOTE processing
		HashSet<Instance> allInstances = new HashSet<Instance>();
		
		Instance currInstance = output();
		while (currInstance != null)
		{
			allInstances.add(currInstance);
			currInstance = output();
		}
		
		//create a set of instances to remove
		HashSet<Instance> instancesToRemove = new HashSet<Instance>();
		
		for (Instance instanceI : allInstances)
		{
			//find the  nearest neighbors of instance i
			NearestNeighbors nn = new NearestNeighbors(getEnnNumNearestNeighbors());
			for (Instance instanceJ : allInstances)
			{
				if (instanceJ != instanceI)
				{
					nn.addNeighborIfNearer(instanceJ, calculateDistance(instanceI, instanceJ));
				}
			}
			Instance[] kNearestNeighbors = nn.getNearestNeighbors();
			
			double currInstanceClass = instanceI.classValue();
			
			//how many neighbors have a different class?
			int numDifferentClassNeighbors = 0;
			for (int i = 0; i < kNearestNeighbors.length; i++)
			{
				if (kNearestNeighbors[i].classValue() != currInstanceClass)
				{
					numDifferentClassNeighbors++;
				}
			}
			
			//if more than half the neighbors are different, we should remove this instance (ENN)
			if (numDifferentClassNeighbors >= Math.ceil(((double)getEnnNumNearestNeighbors())/2))
			{
				instancesToRemove.add(instanceI);
			}
		}
		
		//remove the instances that we marked in the ENN phase from the set of all instances
		for (Instance instToRemove : instancesToRemove)
		{
			allInstances.remove(instToRemove);
		}
		
		//push all the remaining instances to the output queue
		for (Instance instance : allInstances)
		{
			push(instance);
		}
		
		return (numPendingOutput() != 0);
	}
}
