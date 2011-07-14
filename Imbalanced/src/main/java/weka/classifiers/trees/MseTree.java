package weka.classifiers.trees;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.classifiers.trees.MseTrees.Tree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;


public class MseTree extends Classifier implements OptionHandler, Serializable

{
	private static final long serialVersionUID = 1975792242734277339L;
	protected int m_minNumInstances = 30;
	protected Classifier m_classifier = new LinearRegression();
	private static final int SMALLER_OR_EQUALE = 1;
	private static final int BIGGER = 0;
	private Tree tree;
	
	//protected Classifier m_classifier1;

	public int getM_minNumInstances() {
		return m_minNumInstances;
	}

	public void setM_minNumInstances(int mMinNumInstances) {
		m_minNumInstances = mMinNumInstances;
	}

	public Classifier getM_Classifier() {
		return m_classifier;
	}

	public void setM_Classifier(Classifier mClassifier) {
		m_classifier = mClassifier;
	}

	public static void main() {
		MseTree tree = new MseTree();
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {
		/** Total number of training instances. */
		LinearRegression linearRegression = new LinearRegression();
		setM_Classifier(linearRegression);
		//setM_minNumInstances(30);
		tree = new Tree(getM_minNumInstances(), data, m_classifier);

	}

	@Override
	public double classifyInstance(Instance instance) throws Exception {

		double classify = -1;
		int splitAttr = tree.getM_split().getAttribute();
		
		try{
			if (tree.getChildNodes().size() == 0) 
			{
				// we reached a leaf. let's use it's model
				classify = tree.getM_classifier().classifyInstance(instance);
			}
			else 
			{
				// we are in a node
				if (instance.attribute(splitAttr).isNumeric())
				{
					classify = classifyNumericInstance(instance);
				}
				else
				{
					classify = classifyNominalInstance(instance);
				}
			}
		}catch (Exception e) {
			System.out.println("Error in classifyInstance()");
		}
		return classify;
	}
	
	private double classifyNumericInstance(Instance instance)
	{
		double classify = -1;
		int splitAttr = tree.getM_split().getAttribute();
		double splitValue 	= tree.getM_split().getAttrValue();			
		double currentValue = instance.value(splitAttr);
		
		Tree child;
		if (currentValue >  splitValue)
		{
			child = tree.getChildNodes().get(BIGGER);
		}
		else
		{
			child = tree.getChildNodes().get(SMALLER_OR_EQUALE);	
		}
		
		try {
			classify = child.getM_classifier().classifyInstance(instance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classify;
	}
	
	private double classifyNominalInstance(Instance instance)
	{
		double classify = -1;
		int splitAttr = tree.getM_split().getAttribute();
		double splitValue 	= tree.getM_split().getAttrValue();			
		int currentValue = (int) instance.value(splitAttr);

		Tree child = tree.getChildNodes().get(currentValue-1);
		try {
			classify = child.getM_classifier().classifyInstance(instance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return classify;

	}
	
		
			

	public String toString() {
		StringBuffer output = new StringBuffer("");
		output.append("\nMSE Tree\n============\n");
		output.append("Minimum number of instances="+ getM_minNumInstances() + "\n");
		output.append(tree.toString(0,null));
		output.append("\n"+ tree.printModel(0,null));
		//output.append("\n" + "\nSize of the tree : " + "numNodes()");
		return output.toString();
	}
	


	  @Override
	  public Enumeration<Option> listOptions() 
	  {

		  Vector<Option> newVector = new Vector<Option>(2);

		  newVector.addElement(new Option(
				  "The type of regression model to use (Linear or Simple)",
				  "M", 1, "-M <ModelType>"));
		  newVector.addElement(new Option(
				  "The minimum number of instances required for a split",
				  "S", 1, "-S <MinNumInstances>"));
		  return newVector.elements();
	  }
	  
	  /**
	   * Parses a given list of options. Valid options are:<p>
	   *
   		<!-- options-start -->
   		

      	<!-- options-end -->

	   *
	   * @param options the list of options as an array of strings
	   * @exception Exception if an option is not supported
	   */
	  @Override
	  public void setOptions(String[] options) throws Exception {

		  String model = Utils.getOption('M', options);
		  
		  if (model.equals("Linear"))
		  {
			  m_classifier = new LinearRegression();
		  }
		  else if (model.equals("Simple"))
		  {
			  m_classifier = new SimpleLinearRegression();
		  }
		  
		  String minInstances = Utils.getOption('S', options);
		  m_minNumInstances = Integer.parseInt(minInstances);
	  }
	  
	  /**
	   * Gets the current settings of the Classifier.
	   *
	   * @return an array of strings suitable for passing to setOptions
	   */
	  @Override
	  public String [] getOptions() {

	    String [] options = new String[4];
	    options[0] = "-S";
	    options[1] = Integer.toString(m_minNumInstances);
	    options[2] = "-M";
	    options[3] = m_classifier instanceof LinearRegression ? "Linear Regression" : "Simple Linear Regression";

	    return options;
	  }
	  

	public String globalInfo() {

		return "Exercise 2. Learning Algorithms Lior Rokach";

	}

}
