//package weka.classifiers.trees;
//
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.Vector;
//
//import weka.classifiers.Classifier;
//import weka.core.Instance;
//import weka.core.Instances;
//
//import weka.classifiers.Classifier;
//import weka.classifiers.trees.REPTree.Tree;
//
//import weka.core.Instances;
//
//import weka.core.Option;
//
//import weka.core.Utils;
//
//public class Ayelet extends Classifier
//{
//	public class MSETree
//	/** Number of folds for reduced error pruning. */
//
//	protected int m_NumFolds = 3;
//	/** The Tree object */
//	protected Tree m_Tree = null;
//	
//
//	@Override
//	public void buildClassifier(Instances data) throws Exception
//	{
//
//		// Split data into training and test set
//
//		Instances train = data;
//
//		Instances test = null;
//		
//		int count = train.numInstances();
//		double sum = 0;
//		double average = 0;
//		double[] sums = null;
//		double[] sumSquared = null;
//		for (int j = 0; j < train.numAttributes(); j++)
//		{
//		
//			for (int i = 0; i < train.numInstances(); i++)
//			{
//				Instance inst = train.instance(i);
//	
//				//ArrayList<Integer> a = new ArrayList();
//				
//				sum += inst.value(j);
//				
//				
//			}
//			System.out.print(sum+ "\n");
//		}
//		// train = data.trainCV(m_NumFolds, 0);
//		// test = data.testCV(m_NumFolds, 0);
//		
//		double var = variance(sums, sumSquared);
//		
//		
//		
//		throw new Exception("xxxx" + m_NumFolds);
//
//	}
//
//	protected double variance(double[] s, double[] sS)
//	{
//
//		double var = 0;
//
//		for (int i = 0; i < s.length; i++)
//		{
//			
//				var += singleVariance(s[i], sS[i]);
//			
//		}
//
//		return var;
//	}
//	
//	
//	protected double singleVariance(double s, double sS)
//	{
//
//		return sS - ((s * s) );
//	}
//
//	
//	public String globalInfo()
//	{
//
//		return "ayeletttttttt. sdfsdfsdfsdf";
//
//	}
//
//	public String[] getOptions()
//	{
//
//		String[] options = new String[12];
//
//		int current = 0;
//
//		options[current++] = "-N";
//
//		options[current++] = "" + 3;
//
//		while (current < options.length)
//		{
//
//			options[current++] = "";
//
//		}
//
//		return options;
//
//	}
//
//	public void setOptions(String[] options) throws Exception
//	{
//
//		String numFoldsString = Utils.getOption('N', options);
//
//		if (numFoldsString.length() != 0)
//		{
//
//			m_NumFolds = Integer.parseInt(numFoldsString);
//
//		}
//		else
//		{
//
//			m_NumFolds = 3;
//
//		}
//
//		Utils.checkForRemainingOptions(options);
//
//	}
//
//	/**
//	 * <!-- options-start -->
//	 * 
//	 * Valid options are:
//	 * <p/>
//	 * 
//	 * 
//	 * 
//	 * <pre>
//	 * -M &lt;minimum number of instances&gt;
//	 * 
//	 *  Set minimum number of instances per leaf (default 2).
//	 * </pre>
//	 * 
//	 * 
//	 * 
//	 * <pre>
//	 * -V &lt;minimum variance for split&gt;
//	 * 
//	 *  Set minimum numeric class variance proportion
//	 * 
//	 *  of train variance for split (default 1e-3).
//	 * </pre>
//	 * 
//	 * 
//	 * 
//	 * <pre>
//	 * -N &lt;number of folds&gt;
//	 * 
//	 *  Number of folds for reduced error pruning (default 3).
//	 * </pre>
//	 * 
//	 * 
//	 * 
//	 * <pre>
//	 * -S &lt;seed&gt;
//	 * 
//	 *  Seed for random data shuffling (default 1).
//	 * </pre>
//	 * 
//	 * 
//	 * 
//	 * <pre>
//	 * -P
//	 * 
//	 *  No pruning.
//	 * </pre>
//	 * 
//	 * 
//	 * 
//	 * <pre>
//	 * -L
//	 * 
//	 *  Maximum tree depth (default -1, no maximum)
//	 * </pre>
//	 * 
//	 * 
//	 * 
//	 * <!-- options-end -->
//	 **/
//
//	public Enumeration listOptions()
//	{
//
//		Vector newVector = new Vector(1);
//
//		newVector.
//
//		addElement(new Option("\tNumber of folds for reduced error pruning " +
//
//		"(default 3 roni roni).",
//
//		"N", 1, "-N <number of folds>"));
//
//		return newVector.elements();
//
//	}
//
//}
