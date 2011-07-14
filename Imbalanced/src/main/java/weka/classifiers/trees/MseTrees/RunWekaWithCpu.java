package weka.classifiers.trees.MseTrees;

import java.io.BufferedReader;
import java.io.FileReader;

import weka.classifiers.trees.MseTree;
import weka.core.Instances;

public class RunWekaWithCpu {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("start");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("c://cpu.arff"));

			Instances data = new Instances(reader);
			reader.close();
			// setting class attribute
			data.setClassIndex(data.numAttributes() - 1);
			
			MseTree mseTree = new MseTree();
			mseTree.buildClassifier(data);
			
			
			
			String strTree = mseTree.toString();
			System.out.println(strTree);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
