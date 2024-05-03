package algorithms.evolutionary_algorithms.mutation;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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

    indWiseReverseMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    indWiseReverseFixedLengthMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint, 40);
//    indWiseDisplacementMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    indWiseInversionDisplacementMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    indWiseScrambleDisplacementMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    indWiseScrambleMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    indWiseInsertionMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    geneWiseInsertionMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    geneWiseBitSwapMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    indWIseBitSwapMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
//    geneWiseReverseMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);

//    indWiseBitFlipMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    geneWiseBitFlipMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);

    return genesToMutate;
  }

  private static void geneWiseBitFlipMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // KNAP gene-wise mutation
    for(int i = splitPoint; i < genesToMutate.size(); i++) {
      if (parameters.random.nextDouble() < KNAPmutationProbability) {
        genesToMutate.set(i, genesToMutate.get(i) ^ 1);
      }
    }
  }

  private static void indWiseBitFlipMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    //      individual-wise knapsack mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int random = parameters.random.next(genesToMutate.size() - splitPoint) + splitPoint;
      genesToMutate.set(random, genesToMutate.get(random) ^ 1);
    }
  }

  private static void geneWiseReverseMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
      // gene-wise reverse mutation TSP
      for(int i = 0; i < splitPoint; i++) {
        if (parameters.random.nextDouble() < TSPmutationProbability) {
          int firstGene = i;//parameters.random.nextInt(splitPoint - 1) + 1;
          int secondGene = parameters.random.nextInt(splitPoint - 1) + 1;
          while (firstGene == secondGene) {
            secondGene = parameters.random.nextInt(splitPoint - 1) + 1;
          }
          if (firstGene < secondGene) {
            Collections.reverse(genesToMutate.subList(firstGene, secondGene));
          } else {
            Collections.reverse(genesToMutate.subList(secondGene, firstGene));
          }
        }
      }
    }

  private static void indWIseBitSwapMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    //     ind-wise swap bit mutation TSP
    if (parameters.random.nextDouble() < TSPmutationProbability) {
      int firstGene = parameters.random.nextInt(splitPoint);
      int secondGene = parameters.random.nextInt(splitPoint);
      while (firstGene == secondGene) {
        secondGene = parameters.random.nextInt(splitPoint);
      }
      Collections.swap(genesToMutate, firstGene, secondGene);
    }
  }

  private static void geneWiseBitSwapMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // gene-wise swap bit mutation TSP
    for(int i = 0; i < splitPoint; i++) {
      if (parameters.random.nextDouble() < TSPmutationProbability) {
        int secondGene = parameters.random.nextInt(splitPoint);
        while (i == secondGene) {
          secondGene = parameters.random.nextInt(splitPoint);
        }
        Collections.swap(genesToMutate, i, secondGene);
      }
    }
  }

  private static void geneWiseInsertionMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // gene-wise TSP insertion mutation
    for(int i = 0; i < splitPoint; i++) {
      if (parameters.random.nextDouble() < TSPmutationProbability) {
        int geneNumberToMove = i;
        int whereToInsert = parameters.random.nextInt(splitPoint - 1);

        int geneToInsert = genesToMutate.get(geneNumberToMove);
        genesToMutate.remove(geneNumberToMove);
        genesToMutate.add(whereToInsert, geneToInsert);
      }
    }
  }

  private static void indWiseInsertionMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // ind-wise TSP insertion mutation
    if (parameters.random.nextDouble() < TSPmutationProbability) {
      int geneNumberToMove = parameters.random.nextInt(splitPoint);
      int whereToInsert = parameters.random.nextInt(splitPoint - 1);

      int geneToInsert = genesToMutate.get(geneNumberToMove);
      genesToMutate.remove(geneNumberToMove);
      genesToMutate.add(whereToInsert, geneToInsert);
    }
  }

  private static void indWiseScrambleMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // TSP Scramble mutation
    if (parameters.random.nextDouble() < TSPmutationProbability) {
      int a = parameters.random.nextInt(splitPoint);
      int b = parameters.random.nextInt(splitPoint);
      while (a == b) {
        a = parameters.random.nextInt(splitPoint);
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> subList = genesToMutate.subList(firstGene, secondGene);
      Collections.shuffle(subList);
    }
  }

  private static void indWiseScrambleDisplacementMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // TSP Scramble displacement mutation
    if (parameters.random.nextDouble() < TSPmutationProbability) {
      int a = parameters.random.nextInt(splitPoint);
      int b = parameters.random.nextInt(splitPoint);
      while (a == b) {
        a = parameters.random.nextInt(splitPoint);
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> sublistToBeDisplaced = removeAndReturnSubList(genesToMutate, firstGene, secondGene);
      int displacementLocation = parameters.random.nextInt(splitPoint - sublistToBeDisplaced.size());
      Collections.shuffle(sublistToBeDisplaced);
      genesToMutate.addAll(displacementLocation, sublistToBeDisplaced);
    }
  }

  private static void indWiseInversionDisplacementMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // TSP Inversion displacement mutation
    if (parameters.random.nextDouble() < TSPmutationProbability) {
      int a = parameters.random.nextInt(splitPoint);
      int b = parameters.random.nextInt(splitPoint);
      while (a == b) {
        a = parameters.random.nextInt(splitPoint);
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> sublistToBeDisplaced = removeAndReturnSubList(genesToMutate, firstGene, secondGene);
      int displacementLocation = parameters.random.nextInt(splitPoint - sublistToBeDisplaced.size());
      Collections.reverse(sublistToBeDisplaced);
      genesToMutate.addAll(displacementLocation, sublistToBeDisplaced);
    }
  }

  private static void indWiseDisplacementMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // TSP displacement mutation
    if (parameters.random.nextDouble() < TSPmutationProbability) {
      int a = parameters.random.nextInt(splitPoint);
      int b = parameters.random.nextInt(splitPoint);
      while (a == b) {
        a = parameters.random.nextInt(splitPoint);
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> sublistToBeDisplaced = removeAndReturnSubList(genesToMutate, firstGene, secondGene);
      int displacementLocation = parameters.random.nextInt(splitPoint - sublistToBeDisplaced.size());
      genesToMutate.addAll(displacementLocation, sublistToBeDisplaced);
    }
  }

  private static void indWiseReverseFixedLengthMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint, int length) {
    // Individual - wise reverse with fixed length mutation TSP
    if (parameters.random.nextDouble() < TSPmutationProbability) {

      int firstGene = parameters.random.nextInt(splitPoint);
      int secondGene = Math.min(firstGene + length, splitPoint -1); //parameters.random.nextInt(splitPoint);
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
  }

  private static void indWiseReverseMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
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
  }

  public static List<Integer> removeAndReturnSubList(List<Integer> list, int startIndex, int endIndex) {
    if (startIndex < 0 || endIndex >= list.size() || startIndex > endIndex) {
      throw new IllegalArgumentException("Invalid start or end index");
    }

    List<Integer> subList = new ArrayList<>(list.subList(startIndex, endIndex));
    list.subList(startIndex, endIndex).clear();

    return subList;
  }
}
