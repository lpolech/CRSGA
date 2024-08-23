package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

import java.util.List;

public class DependencyStructureMatrix {
    double[][] matrix; //symmetric matrix, only upper right part has values
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
                    matrix[i][j] = MutualInformation.calculateI(population, i, j);
                }
            }
        }
    }

    public void toFile(String fileName, String path) {
        MatrixUtils.toFile(this.toString(), this.matrix, "DSM", fileName, path);
    }

    public String toString() {
        StringBuilder toStringVal = new StringBuilder();

        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                toStringVal.append(matrix[i][j] + ";");
            }
            toStringVal.append("\n");
        }

        return toStringVal.toString();
    }
}
