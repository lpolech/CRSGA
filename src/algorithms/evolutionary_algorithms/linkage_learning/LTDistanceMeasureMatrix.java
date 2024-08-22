package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

import java.util.List;

public class LTDistanceMeasureMatrix {
    double[][] matrix;
    int noOfGenes;

    public void calculate(List<BaseIndividual<Integer, TTP>> population) {
        if(!population.isEmpty()) {
            noOfGenes = population.getFirst().getProblem().getSplitPoint();
        } else {
            System.err.println("MutualInformation: population size is " + population.size());
        }
        matrix = new double[noOfGenes][noOfGenes];

        for(int i = 0; i < noOfGenes; i++) {
            for(int j = 0; j < noOfGenes; j++) {
                if(i > j) { //symmetric matrix, only upper right part has values
                    matrix[i][j] = getDistance(population, i, j);
                }
            }
        }
    }

    public double getDistance(List<BaseIndividual<Integer, TTP>> population, int i, int j) {
        double H = MutualInformation.calculateH(population, i, j);
        double I = MutualInformation.calculateI(population, i, j);

        double returnVal = (H - I)/H;

        return returnVal;
    }
}
