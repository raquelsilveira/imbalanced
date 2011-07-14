package weka.filters.supervised.instance.imbalanced;

import java.util.HashSet;

import weka.core.Instance;
import weka.filters.supervised.instance.SMOTE;

public class SmoteAndEnn extends SMOTE
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 232299741996551652L;


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
			//find the 3 nearest neighbors of instance i
			NearestNeighbors nn = new NearestNeighbors(3);
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
			
			//if 2 or more neighbors are different, we should remove this instance (ENN)
			if (numDifferentClassNeighbors >= 2)
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
