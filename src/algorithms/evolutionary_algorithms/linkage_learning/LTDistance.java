package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

import java.util.List;

public class LTDistance {

    public static double getDistance(List<BaseIndividual<Integer, TTP>> population, int i, int j) {
        double H = MutualInformation.calculateH(population, i, j);
        double I = MutualInformation.calculateI(population, i, j);

        double returnVal = (H - I)/H;

        return returnVal;
    }
}
