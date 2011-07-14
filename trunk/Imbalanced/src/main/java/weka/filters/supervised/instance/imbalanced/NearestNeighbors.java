package weka.filters.supervised.instance.imbalanced;

import weka.core.Instance;

/**
 * Class for maintaining the nearest neighbors of some instance
 * @author uayelet
 *
 */
public class NearestNeighbors
{
	
	
	private int k;
	
	private Instance[] m_neighbors;
	private double[] m_distances;
	private int m_furthestNeighborIndex;
	
	/**
	 * Constructor for nearest neighbors class
	 * @param instance - the instance with the neighbors
	 * @param k - how many neighbors to hold
	 */
	public NearestNeighbors(int k)
	{
		this.k = k;
		
		m_neighbors = new Instance[k];
		m_distances = new double[k];
		
		for (int i = 0; i < k; i++)
		{
			m_distances[i] = Double.MAX_VALUE;
		}
		m_furthestNeighborIndex = 0;
		
	}
	
	/**
	 * Add a new neighbor and check if it's one of the nearest neighbors
	 * @param neighbor - the instance to check
	 * @param distance - the distance between the original instance and the neighbor
	 */
	public void addNeighborIfNearer(Instance neighbor, double distance)
	{
		if (distance < m_distances[m_furthestNeighborIndex])
		{
			m_distances[m_furthestNeighborIndex] = distance;
			m_neighbors[m_furthestNeighborIndex] = neighbor;
			
			double newMaxDistance = Double.MIN_VALUE;
			for (int i = 0; i < k; i++)
			{
				if (m_distances[i] > newMaxDistance)
				{
					newMaxDistance = m_distances[i];
					m_furthestNeighborIndex = i;
				}
			}
		}
	}
	
	/**
	 * Get the k nearest neighbors from all the neighbors sent to addNeighbor()
	 * @return
	 */
	public Instance[] getNearestNeighbors()
	{
		return m_neighbors;
	}

}
