package weka.classifiers.trees.MseTrees;

import java.io.Serializable;

public class Split implements Serializable
{
	protected int instance;
	protected int attribute;
	protected String attrName;
	protected double attrValue;
	protected boolean numeric;
	protected double m_bestmse;

	public double getM_bestmse() {
		return m_bestmse;
	}

	public void setM_bestmse(double m_bestmse) {
		this.m_bestmse = m_bestmse;
	}

	public boolean getNumeric() {
		return numeric;
	}

	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}

	public double getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(double attrValue) {
		this.attrValue = attrValue;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public int getInstance()
	{
		return instance;
	}

	public void setInstance(int instance)
	{
		this.instance = instance;
	}

	public int getAttribute()
	{
		return attribute;
	}

	public void setAttribute(int attribute)
	{
		this.attribute = attribute;
	}

}
