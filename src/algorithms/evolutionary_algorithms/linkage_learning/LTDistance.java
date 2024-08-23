package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.evolutionary_algorithms.linkage_learning.LinkageTree.LTNode;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

import java.util.List;

public class LTDistance {

    public static double getDistanceForSingleton(List<BaseIndividual<Integer, TTP>> population, int i, int j) {
        double H = MutualInformation.calculateH(population, i, j);
        double I = MutualInformation.calculateI(population, i, j);

        double returnVal = (H - I)/H;

        return returnVal;
    }

    // TODO: recursive implementation, might not be the most effective
    public static double getDistance(LTNode Ck, LTNode Csum, LTDistanceMeasureMatrix distanceMatrix) {
        if(Csum.getGeneIndexes().size() > 1) {
            if(Csum.getChildren().size() != 2) {
                System.err.println("LTDistance.LTDistance NOT exactly 2 children! Size: " + Csum.getChildren().size());
            }
            LTNode Ci = Csum.getChildren().getFirst();
            LTNode Cj = Csum.getChildren().getLast();

            double CiSize = Ci.getGeneIndexes().size();
            double CjSize = Cj.getGeneIndexes().size();

            double firstElemFirstFactor = (CiSize/(CiSize + CjSize));
            double dirstElemSecondFactor = LTDistance.getDistance(Ck, Ci, distanceMatrix);

            double secondElemFirstFactor = (CjSize/(CiSize + CjSize));
            double secondElemSecondFactor = LTDistance.getDistance(Ck, Cj, distanceMatrix);

            return firstElemFirstFactor * dirstElemSecondFactor
                    + secondElemFirstFactor * secondElemSecondFactor;
        } else if(Ck.getGeneIndexes().size() > 1) {
            return LTDistance.getDistance(Csum, Ck, distanceMatrix); // Swap nodes
        } else if(Ck.getGeneIndexes().size() == 1 && Csum.getGeneIndexes().size() == 1) {
            int firstGeneIndex = Ck.getGeneIndexes().getFirst();
            int secondGeneIndex = Csum.getGeneIndexes().getFirst();
            return distanceMatrix.getMatrixElement(Math.max(firstGeneIndex, secondGeneIndex), Math.min(firstGeneIndex, secondGeneIndex));
        }

        System.err.println("LTDistance.getDistance we should have never come here! Ck.getGeneIndexes().size()"
                            + Ck.getGeneIndexes().size() + ", Csum.getGeneIndexes().size() " + Csum.getGeneIndexes().size());
        return -123;
    }
}
