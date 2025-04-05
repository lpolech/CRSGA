package internal_measures;

import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Node;
import common.CommonQualityMeasure;
import common.Utils;

import java.util.HashMap;

public class VarianceDeviation2 extends CommonQualityMeasure {

	@Override
	public double getMeasure(Hierarchy h) {
		double sumOfVarianceRatios = 0.0;
		int dataDim = Integer.MIN_VALUE;
		HashMap<Node, Double[]> nodesWithVariances = new HashMap<>(h.getNumberOfGroups(), 1.0f);
		for(Node n: h.getGroups())
		{
			nodesWithVariances.put(n, Utils.nodeSubtreeVariance(n, true));
		}
		
		for(Node n: h.getGroups())
		{
			if(n.getParent() != null)
			{
				Double[] parentVar = nodesWithVariances.get(n.getParent());
				Double[] childVar = nodesWithVariances.get(n);
				dataDim = parentVar.length;
				for(int i = 0; i < dataDim; i++)
				{
					sumOfVarianceRatios += parentVar[i] == 0.0? 0.0: childVar[i]/parentVar[i];
				}
			}
		}
		
		sumOfVarianceRatios /= ((h.getNumberOfGroups()-1) * dataDim);
		
		return sumOfVarianceRatios;
	}

	@Override
	public double getDesiredValue() {//the desired value should be as small as possible, but not to small
		return Double.NaN;
	}

	@Override
	public double getNotDesiredValue() {
		return Double.MAX_VALUE;
	}

	@Override
	public boolean isFirstMeasureBetterThanSecond(double firstMeasure, double secondMeasure) {
		return firstMeasure < secondMeasure;
	}

	@Override
	public boolean shouldMeasureBeMaximised() {
		return false;
	}

	@Override
	public String getName() {
		return "VD2";
	}

}
