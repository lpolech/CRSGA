package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.TTP;

import java.util.List;

public class MutualInformation<PROBLEM extends BaseProblemRepresentation> {

    public double calculateI(List<BaseIndividual<Integer, PROBLEM>> population, int i, int j) {
        if(population.isEmpty()) {
            System.err.println("MutualInformation: population size is " + population.size());
            return -666;
        }

        int splitPoint = ((TTP)population.get(0).getProblem()).getSplitPoint();
        double p00 = calculateTwoGenes(population, i, j, 0, 0, splitPoint);
        double p01 = calculateTwoGenes(population, i, j, 0, 1, splitPoint);
        double p10 = calculateTwoGenes(population, i, j, 1, 0, splitPoint);
        double p11 = calculateTwoGenes(population, i, j, 1, 1, splitPoint);
        double pi0 = calculateSingleGene(population, i, 0, splitPoint);
        double pi1 = calculateSingleGene(population, i, 1, splitPoint);
        double pj0 = calculateSingleGene(population, j, 0, splitPoint);
        double pj1 = calculateSingleGene(population, j, 1, splitPoint);

        double elem00 = 0.0;
        if(p00 != 0.0 && pi0 != 0.0 && pj0 != 0.0) {
            elem00 = p00*Math.log(p00/(pi0*pj0));
        }
        double elem01 = 0.0;
        if(p01 != 0.0 && pi0 != 0.0 && pj1 != 0.0) {
            elem01 = p01*Math.log(p01/(pi0*pj1));
        }
        double elem10 = 0.0;
        if(p10 != 0.0 && pi1 != 0.0 && pj0 != 0.0) {
            elem10 = p10*Math.log(p10/(pi1*pj0));
        }
        double elem11 = 0.0;
        if(p11 != 0.0 && pi1 != 0.0 && pj1 != 0.0) {
            elem11 = p11*Math.log(p11/(pi1*pj1));
        }

        double returnVal = elem00 + elem01 + elem10 + elem11;

        return returnVal;
    }

    public double calculateH(List<BaseIndividual<Integer, PROBLEM>> population, int i, int j) {
        if(population.isEmpty()) {
            System.err.println("MutualInformation: population size is " + population.size());
            return -666;
        }

        int splitPoint = ((TTP)population.get(0).getProblem()).getSplitPoint();
        double p00 = calculateTwoGenes(population, i, j, 0, 0, splitPoint);
        double p01 = calculateTwoGenes(population, i, j, 0, 1, splitPoint);
        double p10 = calculateTwoGenes(population, i, j, 1, 0, splitPoint);
        double p11 = calculateTwoGenes(population, i, j, 1, 1, splitPoint);

        double elem00 = 0.0;
        if(p00 != 0.00) {
            elem00 = p00*Math.log(p00);
        }
        double elem01 = 0.0;
        if(p01 != 0.0) {
            elem01 = p01*Math.log(p01);
        }
        double elem10 = 0.0;
        if(p10 != 0.0) {
            elem10 = p10 * Math.log(p10);
        }
        double elem11 = 0.0;
        if(p11 != 0.0) {
            elem11 = p11*Math.log(p11);
        }

        double returnVal = elem00 + elem01 + elem10 + elem11;

        return -returnVal;
    }

    private double calculateSingleGene(List<BaseIndividual<Integer, PROBLEM>> population, int geneIndex, int genePattern, int splitPoint) {
        int counter = 0;

        for (BaseIndividual<Integer, PROBLEM> ind : population) {
            int gVal = ind.getGenes().get(splitPoint + geneIndex);
            if (gVal == genePattern) {
                counter++;
            }
        }

        return counter/(double)population.size();
    }

    private double calculateTwoGenes(List<BaseIndividual<Integer,PROBLEM>> population, int i, int j, int giPattern, int gjPattern, int splitPoint) {
        int counter = 0;

        for (BaseIndividual<Integer, PROBLEM> ind : population) {
            int giVal = ind.getGenes().get(splitPoint + i);
            int gjVal = ind.getGenes().get(splitPoint + j);
            if (giVal == giPattern && gjVal == gjPattern) {
                counter++;
            }
        }

        return counter/(double)population.size();
    }
}
