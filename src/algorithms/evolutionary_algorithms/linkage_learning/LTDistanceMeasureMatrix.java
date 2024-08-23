package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

import java.util.ArrayList;
import java.util.List;

public class LTDistanceMeasureMatrix {
    double[][] matrix; //symmetric matrix, only upper right part has values
    int noOfGenes;

    public double getMatrixElement(int gi, int gj) {
        if(gi > gj) {
            return matrix[gi][gj];
        }
        System.err.println("LTDistanceMeasureMatrix, asking for genes from the other half of the matrix, " + gi + ", " + gj);
        return -666.0;
    }
    public void calculate(List<BaseIndividual<Integer, TTP>> population) {
        if(!population.isEmpty()) {
            noOfGenes = population.getFirst().getProblem().getSplitPoint();
        } else {
            System.err.println("MutualInformation: population size is " + population.size());
        }
        matrix = new double[noOfGenes][noOfGenes];

        for(int i = 0; i < noOfGenes; i++) {
            for(int j = 0; j < noOfGenes; j++) {
                if(i > j) {
                    matrix[i][j] = LTDistance.getDistanceForSingleton(population, i, j);
                }
            }
        }
    }

    public void toFile(String fileName, String path) {
        MatrixUtils.toFile(this.toString(), this.matrix, "Distance Matrix", fileName, path);
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

    public List<Integer> getGeneIndexes() {
        List<Integer> returnList = new ArrayList<>(this.matrix.length);
        for(int i = 0; i < this.matrix.length; i++) {
            returnList.add(i);
        }

        return returnList;
    }
}
