package internal_measures;

import basic_hierarchy.interfaces.Hierarchy;
import interfaces.DistanceMeasure;

/**
 * This class was created in order to be used with Hierarchical Internal Measure.
 */
public class FlatReversedDunn4 extends FlatDunn4 {

    public FlatReversedDunn4(DistanceMeasure dist)
    {
        super(dist);
    }

    @Override
    public double getMeasure(Hierarchy h) {
        return calculate(h, true);
    }

    @Override
    public double getDesiredValue() {
        return 0;
    }

    @Override
    public double getNotDesiredValue() {
        return Double.MAX_VALUE;
    }

    @Override
    public boolean isFirstMeasureBetterThanSecond(double firstMeasure, double secondMeasure) {
        return firstMeasure < secondMeasure;
    }
}
