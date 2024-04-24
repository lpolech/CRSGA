package algorithms.evolutionary_algorithms.mutation;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.Collections;
import java.util.List;

public class CompetitionMutation extends BaseMutation<Integer, BaseProblemRepresentation> {

  @Override
  public List<Integer> mutate(List<BaseIndividual<Integer, BaseProblemRepresentation>> population, double f, List<Integer> genesToMutate, int current, int populationSize, ParameterSet<Integer, BaseProblemRepresentation> parameters) {
    return null;
  }

  /**
   * Swaps
   *
   * @param population population to process
   * @param mutationProbability probability that the gene should be mutated
   * @param genesToMutate list of genes to mutate
   * @param current index of an individual to mutate
   * @param populationSize size of the population
   * @param parameters set of parameters
   * @return
   */
  @Override
  public List<Integer> mutate(List<BaseIndividual<Integer, BaseProblemRepresentation>> population,
                              double TSPmutationProbability, double KNAPmutationProbability, List<Integer> genesToMutate,
                              int current, int populationSize, ParameterSet<Integer, BaseProblemRepresentation> parameters) {
    int splitPoint = parameters.geneSplitPoint;

    // Individual - wise reverse mutation TSP
    if (parameters.random.nextDouble() < TSPmutationProbability) {

      int firstGene = parameters.random.nextInt(splitPoint);
      int secondGene = parameters.random.nextInt(splitPoint);
      while (firstGene == secondGene) {
        secondGene = parameters.random.nextInt(splitPoint);
      }
//      Collections.swap(genesToMutate, firstGene, secondGene);
      if (firstGene < secondGene) {
        Collections.reverse(genesToMutate.subList(firstGene, secondGene));
      } else {
        Collections.reverse(genesToMutate.subList(secondGene, firstGene));
      }

    }

    // gene-wise swap bit mutation TSP
//    for(int i  = 0; i < splitPoint; i++) {
//      if (parameters.random.nextDouble() < TSPmutationProbability) {
//        int secondGene = parameters.random.nextInt(splitPoint);
//        while (i == secondGene) {
//          secondGene = parameters.random.nextInt(splitPoint);
//        }
//        Collections.swap(genesToMutate, i, secondGene);
//      }
//    }

    // gene-wise reverse mutation TSP
//    for(int i  = 0; i < splitPoint; i++) {
//      if (parameters.random.nextDouble() < TSPmutationProbability) {
//        int firstGene = i;//parameters.random.nextInt(splitPoint - 1) + 1;
//        int secondGene = parameters.random.nextInt(splitPoint - 1) + 1;
//        while (firstGene == secondGene) {
//          secondGene = parameters.random.nextInt(splitPoint - 1) + 1;
//        }
//        if (firstGene < secondGene) {
//          Collections.reverse(genesToMutate.subList(firstGene, secondGene));
//        } else {
//          Collections.reverse(genesToMutate.subList(secondGene, firstGene));
//        }
//      }
//    }

    //individual-wise knapsack mutation
//    if (parameters.random.nextDouble() < mutationProbability) {
//      int random = parameters.random.next(genesToMutate.size() - splitPoint) + splitPoint;
//      genesToMutate.set(random, genesToMutate.get(random) ^ 1);
//    }

    // KNAP gene-wise mutation
    for(int i  = splitPoint; i < genesToMutate.size(); i++) {
      if (parameters.random.nextDouble() < KNAPmutationProbability) {
        genesToMutate.set(i, genesToMutate.get(i) ^ 1);
      }
    }

    return genesToMutate;
  }

}
