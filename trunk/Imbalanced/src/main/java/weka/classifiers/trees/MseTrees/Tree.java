package weka.classifiers.trees.MseTrees;

import java.io.Serializable;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.core.Instance;
import weka.core.Instances;

public class Tree implements Serializable{
	private static final long serialVersionUID = 1975792212344277339L;
	protected Classifier m_classifier;
	/** minimum number of instances at which a node is considered for splitting */
	protected int m_minNumInstances;
	protected Split m_split = new Split();
	protected ArrayList<Tree> m_childNodes = new ArrayList();
	protected ArrayList<Instances> m_sonsInstances;
	protected Instances myInstances;

	/**
	 * @param minNumInstances
	 * @param data
	 */
	public Tree(int minNumInstances, Instances data, Classifier classifier) {
		myInstances = data;
		//LinearRegression simpleLinearRegression = new LinearRegression();
		this.m_minNumInstances = minNumInstances;
		this.m_split = new Split();
		m_classifier = classifier;
		buildTree(data, classifier);
	}

	public void buildTree(Instances currentData, Classifier classifier) {
		try {
			classifier.buildClassifier(currentData);
			
		} catch (Exception e) {

			e.printStackTrace();
		}

		// Stopping condition
		if (currentData.numInstances() > m_minNumInstances) {
			findBestSplit(currentData);
			m_sonsInstances = splitByAttribute(currentData, m_split);
			for (int i = 0; i < m_sonsInstances.size(); i++) {
				Classifier sonClassifier = createNewClassifier();
				// Recursive call
				Tree childNode = new Tree(m_minNumInstances, m_sonsInstances.get(i), sonClassifier);
				m_childNodes.add(childNode);
			}
		}
	}

	private Classifier createNewClassifier()
	{
		if(m_classifier instanceof SimpleLinearRegression)
		{
			return new SimpleLinearRegression();
		}
		else
		{
			return new LinearRegression();			
		}
		
	}

	private ArrayList<Instances> splitByAttribute(Instances data, Split split) {

		ArrayList<Instances> sonsInstances = new ArrayList<Instances>();

		if (data.attribute(split.getAttribute()).isNominal()) {
			sonsInstances = splitByInstance_NominalAttribute(data,
					split.getAttribute());
		} else {
			sonsInstances = splitByInstance_NumericalAttribute(data,
					split.getAttribute(), split.getInstance(),
					split.getAttrValue());
		}

		return sonsInstances;
	}

	private void findBestSplit(Instances data) {

		double bestMse = 0;
		for (int j = 0; j < data.numAttributes() - 1; j++) {

			for (int i = 0; i < data.numInstances(); i++) {
				Instance inst = data.instance(i);
				ArrayList<Instances> currentSplit;
				if (data.attribute(j).isNominal()) {
					currentSplit = splitByInstance_NominalAttribute(data, j);
				} else {
					currentSplit = splitByInstance_NumericalAttribute(data, j,
							i, inst.value(j));
				}

				double weightedSquareError = 0;
				double MSE;

				for (Instances instances : currentSplit) {
					if (instances.numInstances() > 0) {
						weightedSquareError += calculateMSE(instances)
								* instances.numInstances();
					}
				}

				// MSE = weightedSquareError / data.numInstances();
				MSE = currentSplit.size()
						* (weightedSquareError / data.numInstances());

				if (MSE < bestMse || bestMse == 0) {
					bestMse = MSE;
					m_split.setAttribute(j);
					m_split.setAttrValue(inst.value(j));
					m_split.setAttrName(inst.attribute(j).name());
					m_split.setInstance(i);
					m_split.setNumeric(inst.attribute(j).isNumeric());
					m_split.setM_bestmse(bestMse);
				}
			}
		}
		// //System.out.println("mse: " + bestMse);
		// System.out.println("splittingAttribute: " + m_split.getAttribute());
		// System.out.println("size_right: " + size_right + " size_left: " +
		// size_left);

	}

	private ArrayList<Instances> splitByInstance_NumericalAttribute(
			Instances data, int AttributeNum, int InstanceNum,
			double splittingValue) {
		ArrayList<Instances> children = new ArrayList<Instances>();
		children.add(new Instances(data, 0, 0));
		children.add(new Instances(data, 0, 0));
		// Actual Values of the current attribute
		double[] attributeInstanceValues = data
				.attributeToDoubleArray(AttributeNum);

		for (int k = 0; k < attributeInstanceValues.length; k++) {
			if (attributeInstanceValues[k] > splittingValue) {
				children.get(0).add(data.instance(k));
			} else {
				children.get(1).add(data.instance(k));
			}
		}
		return children;
	}

	private ArrayList<Instances> splitByInstance_NominalAttribute(
			Instances data, int AttributeNum) {
		ArrayList<Instances> children = new ArrayList<Instances>();

		for (int i = 0; i < data.attribute(AttributeNum).numValues(); i++) {
			children.add(new Instances(data, 0, 0));
		}

		// Actual Values of the current attribute
		double[] attributeInstanceValues = data
				.attributeToDoubleArray(AttributeNum);

		for (int k = 0; k < attributeInstanceValues.length; k++) {
			children.get((int) attributeInstanceValues[k])
					.add(data.instance(k));
		}
		return children;
	}

	private double calculateMSE(Instances subsetInstances) {

		double error = 0;
		double squaredError = 0;
		double predicted = 0;
		double actual = 0;
		double mse = 0;

		Classifier classifier = new LinearRegression();
		try {
			classifier.buildClassifier(subsetInstances);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			for (int i = 0; i < subsetInstances.numInstances(); i++) {
				predicted = classifier.classifyInstance(subsetInstances
						.instance(i));
				actual = subsetInstances.instance(i).classValue();
				error = actual - predicted;
				squaredError = squaredError + (error * error);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mse = squaredError / subsetInstances.numInstances();
		return mse;
	}

	public String printModel(int level, Tree parent) {
		StringBuffer text = new StringBuffer();
		if (m_childNodes.size() == 0) {
			text.append("\nModel for leaf " + 	parent.getM_split().getAttrName() + " <= " + parent.getM_split().getAttrValue()); 
			text.append(m_classifier.toString());
		}
		else
		{
//			text.append(level + "yyyy");
			text.append(m_childNodes.get(0).printModel(level + 1 , this));
			text.append(m_childNodes.get(1).printModel(level + 1 , this));
//			text.append("\n");
//			text.append(m_childNodes.get(0).toString(level + 1, this));
//			text.append(m_childNodes.get(1).toString(level + 1, this));
//			
		}

		return text.toString();
	}
	
	public String toString(int level, Tree parent) {
		StringBuffer text = new StringBuffer();
		try {
			// tree.getChildNodes()
			text.append("MSE = " + calculateMSE(myInstances));
			if (m_childNodes.size() == 0) {
				// its a leaf
				text.append(" (This leaf contains "
						+ myInstances.numInstances() + " instances)");
				

			} else {
				// its a node

				if (m_split.getNumeric()) {
					text.append(toStringNumeric(level, parent));
				} else {
					text.append(toStringNominal(level, parent));
				}

			}
			
			 
		} catch (Exception e) {
			e.printStackTrace();
			text.append("Can't print mse tree.");
		}

		return text.toString();
	}

	public String toStringNominal(int level, Tree parent) {
		StringBuffer text = new StringBuffer();
		//
		for (int i = 0; i < myInstances.attribute(0).numValues(); i++) {
			text.append("\n");
			for (int j = 0; j < level; j++) {
				text.append("|   ");
			}
			text.append(m_split.getAttrName() + " <= ");
			text.append(m_split.getAttrValue());
			text.append("   (mse=" + m_split.getM_bestmse() + ")");
			text.append(m_childNodes.get(i).toString(level + 1, this));
			text.append("\n");

			// children.add(new Instances(data, 0, 0));
		}

		return text.toString();
	}

	public String toStringNumeric(int level, Tree parent) {
		StringBuffer text = new StringBuffer();
		text.append("\n");
		for (int j = 0; j < level; j++) {
			text.append("|   ");
		}
		text.append(m_split.getAttrName() + " <= ");
		text.append(m_split.getAttrValue());
		//text.append("   (MSE=" + m_split.getM_bestmse() + ")");
		text.append(m_childNodes.get(0).toString(level + 1, this));
		text.append("\n");
		for (int j = 0; j < level; j++) {
			text.append("|   ");
		}
		text.append(m_split.getAttrName() + " > ");
		text.append(m_split.getAttrValue());
		//text.append("   (MSE=" + m_split.getM_bestmse() + ")");
		text.append(m_childNodes.get(1).toString(level + 1, this));
		return text.toString();
	}

	/***************************************************************************
	 * Getters & Setters for Tree members *
	 **************************************************************************/
	public Split getM_split() {
		return m_split;
	}

	public void setM_split(Split m_split) {
		this.m_split = m_split;
	}

	public Classifier getM_classifier() {
		return m_classifier;
	}

	public void setM_classifier(Classifier m_classifier) {
		this.m_classifier = m_classifier;
	}

	public ArrayList<Tree> getChildNodes() {
		return m_childNodes;
	}

	public void setChildNodes(ArrayList<Tree> childNodes) {
		this.m_childNodes = childNodes;
	}

}
