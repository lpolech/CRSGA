package external_measures.statistical_hypothesis;

import basic_hierarchy.interfaces.Hierarchy;
import common.CommonQualityMeasure;
import interfaces.Hypotheses;
import interfaces.QualityMeasure;

public class FowlkesMallowsIndex extends CommonQualityMeasure {
	private Hypotheses hypothesesCalculator;

	private FowlkesMallowsIndex() {}
	
	public FowlkesMallowsIndex(Hypotheses hypothesesCalculator)
	{
		this.hypothesesCalculator = hypothesesCalculator;
	}
	
	@Override
	public double getMeasure(Hierarchy h) {
		this.hypothesesCalculator.calculate(h);
		double numerator = this.hypothesesCalculator.getTP()*this.hypothesesCalculator.getTP();
		double denominator = (this.hypothesesCalculator.getTP() + this.hypothesesCalculator.getFP())
                * (this.hypothesesCalculator.getTP() + this.hypothesesCalculator.getFN());
        return Math.sqrt(numerator/denominator);
	}

	@Override
	public double getDesiredValue() {
		return 1;
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
		return "FowMI";
	}

}
