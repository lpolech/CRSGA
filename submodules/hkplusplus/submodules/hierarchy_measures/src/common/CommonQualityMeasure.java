package common;

import basic_hierarchy.interfaces.Hierarchy;
import interfaces.QualityMeasure;
import internal_measures.statistics.AvgWithStdev;

import java.util.ArrayList;

public abstract class CommonQualityMeasure implements QualityMeasure {
    @Override
    public abstract double getMeasure(Hierarchy h);

    @Override
    public AvgWithStdev getMeasure(ArrayList<Hierarchy> hierarchies, boolean calculatePopulationStdev)
    {
        double[] values = new double[hierarchies.size()];
        for(int i = 0; i < hierarchies.size(); i++)
        {
            values[i] = this.getMeasure(hierarchies.get(i));
        }
        return new AvgWithStdev(Utils.mean(values), Utils.stdev(values, calculatePopulationStdev));
    }

    @Override
    public abstract double getDesiredValue();

    @Override
    public abstract double getNotDesiredValue();
}
