package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.evolutionary_algorithms.linkage_learning.LinkageTree.LTNode;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.TTP;

import java.util.List;

public class LTDistance<PROBLEM extends BaseProblemRepresentation> {
    MutualInformation mi = new MutualInformation();

    public double getDistanceForSingleton(List<BaseIndividual<Integer, PROBLEM>> population, int i, int j) {
        double H = mi.calculateH(population, i, j);
        double I = mi.calculateI(population, i, j);

        double returnVal = 0;

        if(H != 0.0) {
            returnVal = (H - I) / H;
        }
        return returnVal;
    }

    // TODO: recursive implementation, might not be the most effective
    public double getDistance(LTNode Ck, LTNode Csum, LTDistanceMeasureMatrix distanceMatrix) {
        if(Csum.getGeneIndexes().size() > 1) {
            if(Csum.getChildren().size() != 2) {
                System.err.println("LTDistance.LTDistance NOT exactly 2 children! Size: " + Csum.getChildren().size());
            }
            LTNode Ci = Csum.getChildren().get(0);
            LTNode Cj = Csum.getChildren().get(1);

            double CiSize = Ci.getGeneIndexes().size();
            double CjSize = Cj.getGeneIndexes().size();

            double firstElemFirstFactor = (CiSize/(CiSize + CjSize));
            double firstElemSecondFactor = getDistance(Ck, Ci, distanceMatrix);

            double secondElemFirstFactor = (CjSize/(CiSize + CjSize));
            double secondElemSecondFactor = getDistance(Ck, Cj, distanceMatrix);

            return firstElemFirstFactor * firstElemSecondFactor
                    + secondElemFirstFactor * secondElemSecondFactor;
        } else if(Ck.getGeneIndexes().size() > 1) {
            return getDistance(Csum, Ck, distanceMatrix); // Swap nodes
        } else if(Ck.getGeneIndexes().size() == 1 && Csum.getGeneIndexes().size() == 1) {
            int firstGeneIndex = Ck.getGeneIndexes().get(0);
            int secondGeneIndex = Csum.getGeneIndexes().get(0);
            return distanceMatrix.getMatrixElement(Math.max(firstGeneIndex, secondGeneIndex), Math.min(firstGeneIndex, secondGeneIndex));
        }

        System.err.println("LTDistance.getDistance we should have never come here! Ck.getGeneIndexes().size()"
                            + Ck.getGeneIndexes().size() + ", Csum.getGeneIndexes().size() " + Csum.getGeneIndexes().size());
        return -123;
    }
}
