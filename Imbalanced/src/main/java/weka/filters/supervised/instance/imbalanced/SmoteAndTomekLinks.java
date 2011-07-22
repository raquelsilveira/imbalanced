package weka.filters.supervised.instance.imbalanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import weka.core.Instance;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.filters.supervised.instance.SMOTE;

/**
 * 
 * This class is a filter which resamples a dataset by applying 
 * the Synthetic Minority Oversampling TEchnique (SMOTE), 
 * and then applying Tomek Links (pairs from different classes which 
 * are closest to each other than to any other sample) and removing them
 * 
 * @author Ayelet and Roni
 *
 */
public class SmoteAndTomekLinks extends SMOTE
{


	private static final long serialVersionUID = -6214097953334039028L;

	/**
	 * Returns a string describing this classifier.
	 * 
	 * @return 		a description of the classifier suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Resamples a dataset by applying the Synthetic Minority Oversampling TEchnique (SMOTE), and then applying Tomek Links (pairs from different classes which are closest to each other than to any other sample) and removing them." +
		" The original dataset must fit entirely in memory." +
		" The amount of SMOTE and number of nearest neighbors may be specified." +
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
		
		//perform SMOTE phase
		super.batchFinished();
		
		//get all the instances from SMOTE
		HashSet<Instance> instances = new HashSet<Instance>();
		
		Instance currInstance = output();
		while (currInstance != null)
		{
			instances.add(currInstance);
			currInstance = output();
		}
		
		//find the nearest neighbor of each instance
		HashMap<Instance, Instance> nearestNeighborMap = getNearestNeighborsMap(instances);
		
		//identify all the Tomek Links
		HashSet<TomekLink> tomekLinks = getTomekLinks(nearestNeighborMap);
		
		//remove all the instances of the Tomek Links
		for (TomekLink currTomekLink : tomekLinks)
		{
			instances.remove(currTomekLink.getInstance(0));
			instances.remove(currTomekLink.getInstance(1));
		}
		
		//push all the remaining instances to the output
		for (Instance instance : instances)
		{
			push(instance);
		}
		
		return (numPendingOutput() != 0);
		
	}



	
}
