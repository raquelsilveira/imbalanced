package weka.filters.supervised.instance.imbalanced;

import weka.core.Instance;

/**
 * 
 * A class representing a Tomek Link - two instances which are nearest neighbors
 * and are from different classes
 *  
 * @author Ayelet and Roni
 *
 */
public class TomekLink
{
	
	private Instance[] m_instances = new Instance[2];
	
	/**
	 * Constructor for Tomek Link
	 * @param inst1
	 * @param inst2
	 */
	public TomekLink(Instance inst1, Instance inst2)
	{
		m_instances[0] = inst1;
		m_instances[1] = inst2;
	}
	
	/**
	 * Get one of the instances of the Tomek Link
	 * @param index
	 * @return
	 */
	public Instance getInstance(int index)
	{
		if (index >= 2 || index < 0)
		{
			return null;
		}
		return m_instances[index];
	}
	
	
	/**
	 * Test if two Tomek Links are equal (if they have the same instances, even in different order)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof TomekLink))
		{
			return false;
		}	
		
		TomekLink other = (TomekLink)obj;
		
		if(this.getInstance(0).equals(other.getInstance(0)) && 
		   this.getInstance(1).equals(other.getInstance(1)))
		{
			return true;
		}
		if(this.getInstance(0).equals(other.getInstance(1)) && 
		   this.getInstance(1).equals(other.getInstance(0)))
		{
			return true;
		}
		return false;		
	}
	
	@Override
	public int hashCode()
	{
		return m_instances[0].hashCode() * m_instances[1].hashCode();
	}
	
	
}
