package algorithms.evolutionary_algorithms.mutation;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompetitionMutation extends BaseMutation<Integer, BaseProblemRepresentation> {

  @Override
  public List<Integer> mutate(List<BaseIndividual<Integer, BaseProblemRepresentation>> population, double f, List<Integer> genesToMutate, int current, int populationSize, ParameterSet<Integer, BaseProblemRepresentation> parameters) {
    return null;
  }

  /**
   * Swaps
   *
   * @param population population to process
   * @param KNAPmutationProbability probability that the gene should be mutated
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

    if(parameters.TSPmutationVersion == 1) {
      indWiseReverseMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint); // BASELINE
    } else if(parameters.TSPmutationVersion == 2) {
      indWiseReverseFixedLengthMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint, 40);
    } else if(parameters.TSPmutationVersion == 3) {
      geneWiseReverseMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 4) {
      indWiseDisplacementMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 5) {
      indWiseInversionDisplacementMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 6) {
      indWiseScrambleDisplacementMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 7) {
      indWiseScrambleMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 8) {
      indWiseInsertionMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 9) {
      geneWiseInsertionMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 10) {
      geneWiseBitSwapMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.TSPmutationVersion == 11) {
      indWiseBitSwapMutationTSP(TSPmutationProbability, genesToMutate, parameters, splitPoint);
    }

    if(parameters.KNAPmutationVersion == 1) {
      geneWiseBitFlipMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint); // BASELINE
    } else if(parameters.KNAPmutationVersion == 2) { // NEW FAVOURITE
      indWiseBitFlipMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 1); // NEW FAVOURITE
    } else if(parameters.KNAPmutationVersion == 3) {
      indWiseReverseMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 4) {
      indWiseReverseFixedLengthMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 30);
    } else if(parameters.KNAPmutationVersion == 5) {
      geneWiseReverseMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 6) {
      indWiseDisplacementMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 7) {
      indWiseInversionDisplacementMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 8) {
      indWiseScrambleDisplacementMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 9) {
      indWiseScrambleMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 10) {
      indWiseInsertionMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 11) {
      geneWiseInsertionMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 12) {
      geneWiseBitSwapMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 13) {
      indWiseBitSwapMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 14) {
      indWiseBitFlipSeqMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 15) {
      indWiseBitFlipSeqFixedLengthMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 40);
    } else if(parameters.KNAPmutationVersion == 16) {
      indWiseBitFlipSeqDisplacementMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 17) {
      indWiseInversionDisplacementMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 18) {
      indWiseBitFlipInsertionMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 19) {
      geneWiseBitFlipInsertionMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 20) {
      geneWiseInsertionMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint);
    } else if(parameters.KNAPmutationVersion == 21) {
      indWiseBitFlipMutationDynKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 2);
    } else if(parameters.KNAPmutationVersion == 22) {
      indWiseBitFlipMutationDynKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 3);
    } else if(parameters.KNAPmutationVersion == 23) {
      indWiseBitFlipMutationDynKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 4);
    }  else if(parameters.KNAPmutationVersion == 24) {
      indWiseBitFlipMutationDynKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 5);
    } else if(parameters.KNAPmutationVersion == 25) {
      indWiseBitFlipMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 2);
    } else if(parameters.KNAPmutationVersion == 26) {
      indWiseBitFlipMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 3);
    } else if(parameters.KNAPmutationVersion == 27) {
      indWiseBitFlipMutationKNAP(KNAPmutationProbability, genesToMutate, parameters, splitPoint, 4);
    }
    return genesToMutate;
  }


  private static void geneWiseReverseMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // gene-wise reverse mutation TSP
    for(int i = 0; i < splitPoint; i++) {
      if (parameters.random.nextDouble() < TSPmutationProbability) {
        int firstGene = i;//parameters.random.nextInt(splitPoint - 1) + 1;
        int secondGene = parameters.random.nextInt(splitPoint);
        while (firstGene == secondGene) {
          secondGene = parameters.random.nextInt(splitPoint);
        }
        if (firstGene < secondGene) {
          Collections.reverse(genesToMutate.subList(firstGene, secondGene));
        } else {
          Collections.reverse(genesToMutate.subList(secondGene, firstGene));
        }
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

  private static void indWiseBitSwapMutationTSP(double TSPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
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

  private static void geneWiseBitFlipMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // KNAP gene-wise mutation
    for(int i = splitPoint; i < genesToMutate.size(); i++) {
      if (parameters.random.nextDouble() < KNAPmutationProbability) {
        genesToMutate.set(i, genesToMutate.get(i) ^ 1);
      }
    }
  }

  private static void indWiseBitFlipMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint, int numberOfSwaps) {
    //      individual-wise knapsack mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      for(int i = 0; i < numberOfSwaps; i++) {
        int random = parameters.random.next(genesToMutate.size() - splitPoint) + splitPoint;
        genesToMutate.set(random, genesToMutate.get(random) ^ 1);
//        System.out.println(i + ", " + random);
      }
//      System.out.println("");
    }
  }
  private static void indWiseBitFlipMutationDynKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint, int numberOfPotentialSwaps) {
    //      individual-wise knapsack mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int numberOdGeneFlips = parameters.random.next(numberOfPotentialSwaps) + 1; // min 1 swap

      for (int i = 0; i < numberOdGeneFlips; i++) {
        int random = parameters.random.next(genesToMutate.size() - splitPoint) + splitPoint;
        genesToMutate.set(random, genesToMutate.get(random) ^ 1);
//        System.out.println(i + ", " + random);
      }
//      System.out.println("");
    }
  }

  private static void geneWiseReverseMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // gene-wise reverse mutation KNAP
    for(int i = splitPoint; i < genesToMutate.size(); i++) {
      if (parameters.random.nextDouble() < KNAPmutationProbability) {
        int firstGene = i;
        int secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
        while (firstGene == secondGene) {
          secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
        }
        if (firstGene < secondGene) {
          Collections.reverse(genesToMutate.subList(firstGene, secondGene));
        } else {
          Collections.reverse(genesToMutate.subList(secondGene, firstGene));
        }
      }
    }
  }

  private static void indWiseBitFlipSeqMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // Individual - wise reverse mutation KNAP
    if (parameters.random.nextDouble() < KNAPmutationProbability) {

      int a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int b = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (a == b) {
        b = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      Function<Integer, Integer> function = bit -> bit ^ 1;
      int finalSecondGene = secondGene;
      IntStream.range(0, genesToMutate.size())
              .mapToObj(i -> i >= firstGene && i <= finalSecondGene ? genesToMutate.set(i, function.apply(genesToMutate.get(i))) : genesToMutate.get(i))
              .collect(Collectors.toList());
    }
  }

  private static void indWiseReverseMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // Individual - wise reverse mutation KNAP
    if (parameters.random.nextDouble() < KNAPmutationProbability) {

      int firstGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (firstGene == secondGene) {
        secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
//      Collections.swap(genesToMutate, firstGene, secondGene);
      if (firstGene < secondGene) {
        Collections.reverse(genesToMutate.subList(firstGene, secondGene));
      } else {
        Collections.reverse(genesToMutate.subList(secondGene, firstGene));
      }

    }
  }

  private static void indWiseBitSwapMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    //     ind-wise swap bit mutation KNAP
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int firstGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (firstGene == secondGene) {
        secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
      Collections.swap(genesToMutate, firstGene, secondGene);
    }
  }


  private static void geneWiseBitSwapMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // gene-wise swap bit mutation TSP
    for(int i = splitPoint; i < genesToMutate.size(); i++) {
      if (parameters.random.nextDouble() < KNAPmutationProbability) {
        int secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
        while (i == secondGene) {
          secondGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
        }
        Collections.swap(genesToMutate, i, secondGene);
      }
    }
  }

  private static void geneWiseBitFlipInsertionMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // gene-wise KNAP insertion mutation
    for(int i = splitPoint; i < genesToMutate.size(); i++) {
      if (parameters.random.nextDouble() < KNAPmutationProbability) {
        int geneNumberToMove = i;
        int whereToInsert = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;

        int geneToInsert = genesToMutate.get(geneNumberToMove);
        geneToInsert = geneToInsert ^ 1;
        genesToMutate.remove(geneNumberToMove);
        genesToMutate.add(whereToInsert, geneToInsert);
      }
    }
  }

  private static void geneWiseInsertionMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // gene-wise KNAP insertion mutation
    for(int i = splitPoint; i < genesToMutate.size(); i++) {
      if (parameters.random.nextDouble() < KNAPmutationProbability) {
        int geneNumberToMove = i;
        int whereToInsert = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;

        int geneToInsert = genesToMutate.get(geneNumberToMove);
        genesToMutate.remove(geneNumberToMove);
        genesToMutate.add(whereToInsert, geneToInsert);
      }
    }
  }

  private static void indWiseBitFlipInsertionMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // ind-wise KNAP insertion mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int geneNumberToMove = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int whereToInsert = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;

      int geneToInsert = genesToMutate.get(geneNumberToMove);
      geneToInsert = geneToInsert ^ 1;
      genesToMutate.remove(geneNumberToMove);
      genesToMutate.add(whereToInsert, geneToInsert);
    }
  }

  private static void indWiseInsertionMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // ind-wise KNAP insertion mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int geneNumberToMove = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int whereToInsert = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;

      int geneToInsert = genesToMutate.get(geneNumberToMove);
      genesToMutate.remove(geneNumberToMove);
      genesToMutate.add(whereToInsert, geneToInsert);
    }
  }


  private static void indWiseScrambleMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // KNAP Scramble mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int b = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (a == b) {
        a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> subList = genesToMutate.subList(firstGene, secondGene);
      Collections.shuffle(subList);
    }
  }


  private static void indWiseScrambleDisplacementMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // KNAP Scramble displacement mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int b = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (a == b) {
        a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> sublistToBeDisplaced = removeAndReturnSubList(genesToMutate, firstGene, secondGene);
      int displacementLocation = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      Collections.shuffle(sublistToBeDisplaced);
      genesToMutate.addAll(displacementLocation, sublistToBeDisplaced);
    }
  }



  private static void indWiseBitFlipSeqDisplacementMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // KNAP Inversion displacement mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int b = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (a == b) {
        a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> sublistToBeDisplaced = removeAndReturnSubList(genesToMutate, firstGene, secondGene);
      int displacementLocation = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      Function<Integer, Integer> function = bit -> bit ^ 1;
      int finalSecondGene = secondGene;
      IntStream.range(0, sublistToBeDisplaced.size())
              .mapToObj(i -> sublistToBeDisplaced.set(i, function.apply(sublistToBeDisplaced.get(i))))
              .collect(Collectors.toList());
      genesToMutate.addAll(displacementLocation, sublistToBeDisplaced);
    }
  }

  private static void indWiseInversionDisplacementMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // KNAP Inversion displacement mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int b = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (a == b) {
        a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> sublistToBeDisplaced = removeAndReturnSubList(genesToMutate, firstGene, secondGene);
      int displacementLocation = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      Collections.reverse(sublistToBeDisplaced);
      genesToMutate.addAll(displacementLocation, sublistToBeDisplaced);
    }
  }



  private static void indWiseDisplacementMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint) {
    // KNAP displacement mutation
    if (parameters.random.nextDouble() < KNAPmutationProbability) {
      int a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int b = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      while (a == b) {
        a = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      }
      int firstGene = Math.min(a, b);
      int secondGene = Math.max(a, b);

      List<Integer> sublistToBeDisplaced = removeAndReturnSubList(genesToMutate, firstGene, secondGene);
      int displacementLocation = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      genesToMutate.addAll(displacementLocation, sublistToBeDisplaced);
    }
  }


  private static void indWiseBitFlipSeqFixedLengthMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint, int length) {
    // Individual - wise reverse with fixed length mutation KNAP
    if (parameters.random.nextDouble() < KNAPmutationProbability) {

      int firstGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int secondGene = Math.min(firstGene + length, genesToMutate.size() - 1);
      Function<Integer, Integer> function = bit -> bit ^ 1;
      int finalSecondGene = secondGene;
      IntStream.range(0, genesToMutate.size())
              .mapToObj(i -> i >= firstGene && i <= finalSecondGene ? genesToMutate.set(i, function.apply(genesToMutate.get(i))) : genesToMutate.get(i))
              .collect(Collectors.toList());
    }
  }

  private static void indWiseReverseFixedLengthMutationKNAP(double KNAPmutationProbability, List<Integer> genesToMutate, ParameterSet<Integer, BaseProblemRepresentation> parameters, int splitPoint, int length) {
    // Individual - wise reverse with fixed length mutation KNAP
    if (parameters.random.nextDouble() < KNAPmutationProbability) {

      int firstGene = parameters.random.nextInt(genesToMutate.size() - splitPoint) + splitPoint;
      int secondGene = Math.min(firstGene + length, genesToMutate.size() - 1); //parameters.random.nextInt(splitPoint);
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
