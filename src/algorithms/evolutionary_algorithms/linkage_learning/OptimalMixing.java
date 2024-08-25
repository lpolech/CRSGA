package algorithms.evolutionary_algorithms.linkage_learning;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.crossover.BaseCrossover;
import algorithms.evolutionary_algorithms.linkage_learning.LinkageTree.LTMask;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.TTP;

import java.util.List;

public class OptimalMixing {
    public static <PROBLEM extends BaseProblemRepresentation> boolean crossover(LTMask mask, BaseIndividual<Integer, PROBLEM> donor, List<Integer> sourceGenesCopy) {
        int splitPoint = ((TTP)donor.getProblem()).getSplitPoint();
        boolean sourceGenotypeChanged = false;
        for(Integer geneNum: mask.getMask()) {
            int geneIndex = splitPoint + geneNum;
            Integer gene = donor.getGenes().get(geneIndex);
            sourceGenotypeChanged = (gene != sourceGenesCopy.get(geneIndex));
            sourceGenesCopy.set(geneIndex, gene);
        }
        return sourceGenotypeChanged;
    }


}
