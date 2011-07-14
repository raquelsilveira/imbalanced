package weka.filters.supervised.instance.imbalanced;

import weka.core.Instance;

public class TomekLink
{
	
	private Instance[] m_instances = new Instance[2];
	
	public TomekLink(Instance inst1, Instance inst2)
	{
		m_instances[0] = inst1;
		m_instances[1] = inst2;
	}
	
	public Instance getInstance(int index)
	{
		if (index >= 2 || index < 0)
		{
			return null;
		}
		return m_instances[index];
	}
	
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
