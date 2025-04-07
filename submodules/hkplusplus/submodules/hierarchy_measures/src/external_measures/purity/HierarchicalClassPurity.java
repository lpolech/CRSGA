package external_measures.purity;

import java.util.HashMap;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;
import common.CommonQualityMeasure;
import interfaces.DistanceMeasure;
import interfaces.QualityMeasure;
import common.Utils;

public class HierarchicalClassPurity extends CommonQualityMeasure {

	@Override
	public double getMeasure(Hierarchy h) {
		int sumOfClassPurity = 0;
		
		Node[] allNodes = h.getGroups();//allGroups
		String[] allClasses = h.getClasses();
		HashMap<String, Integer> indexesClasses = new HashMap<>(allClasses.length, 1.0f);//classesWithIndices
		HashMap<Node, Integer[]> indexedPartials = new HashMap<>(allNodes.length, 1.0f);//nodesWithEveryClassPartialPurity
		
		for(int i = 0; i < allClasses.length; i++)
		{
			indexesClasses.put(allClasses[i], i);
		}
		
		for(Node n: allNodes)
		{
			indexedPartials.put(n, new Integer[allClasses.length]);
		}
		
		bottomUpCalculation(h.getRoot(), allClasses, indexesClasses, indexedPartials);
		for(int i = 0; i < allClasses.length; i++)
		{
			sumOfClassPurity = Math.max(indexedPartials.get(h.getRoot())[i], sumOfClassPurity);			
		}
		
		return sumOfClassPurity/(double)h.getOverallNumberOfInstances();
	}

	@Override
	public double getDesiredValue() {
		return 1.0;
	}

	@Override
	public double getNotDesiredValue() {
		return 0;
	}

	@Override
	public boolean isFirstMeasureBetterThanSecond(double firstMeasure, double secondMeasure) {
		return firstMeasure > secondMeasure;
	}

	@Override
	public boolean shouldMeasureBeMaximised() {
		return true;
	}

	@Override
	public String getName() {
		return "HCP";
	}

	private void bottomUpCalculation(Node currentNode/*node*/, String[] allClasses, HashMap<String, Integer> indexedClasses, HashMap<Node, Integer[]> indexedPartials)
	{
		for(Node child: currentNode.getChildren())
		{
			bottomUpCalculation(child, allClasses, indexedClasses, indexedPartials);
		}
		
		for(String candidate: allClasses)
		{
			int accumulator = 0;//currentNodeAcc
			for(Instance i: currentNode.getNodeInstances())
			{
				if(i.getTrueClass().equals(candidate))
				{
					accumulator++;
				}
			}
			
			for(Node child: currentNode.getChildren())
			{
				int partialAccumulator = 0;//childNodeAcc
				for(String candidateChild: allClasses)//candidateChildClass
				{
					if(Utils.isTheSameOrSubclass(candidate, candidateChild))
					{
						partialAccumulator = Math.max(partialAccumulator, indexedPartials.get(child)[indexedClasses.get(candidateChild)]);
					}
				}
				accumulator += partialAccumulator;
			}
			indexedPartials.get(currentNode)[indexedClasses.get(candidate)] = accumulator;
		}
	}
}
