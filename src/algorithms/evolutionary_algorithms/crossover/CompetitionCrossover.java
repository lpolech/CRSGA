package algorithms.evolutionary_algorithms.crossover;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseProblemRepresentation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompetitionCrossover extends BaseCrossover<Integer, BaseProblemRepresentation> {

  @Override
  public List<List<Integer>> crossover(double cr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters) {
    return null;
  }

  /**
   * Crossover specialized for TTP.
   * EDGE for TSP. Single-Point for KP.
   *
   * @param cr cross over rate
   * @param firstParent first parent's genes
   * @param secondParent second parent's genes
   * @param parameters set of parameters
   * @return array of children genes
   */
  @Override
  public List<List<Integer>> crossover(double TSPcr, double KNAPcr, List<Integer> firstParent,
                                       List<Integer> secondParent,
                                       ParameterSet<Integer, BaseProblemRepresentation> parameters) {

    CrossoverResult intermediateResult = null;

    if(parameters.TSPcrossoverVersion == 1) {
      List<Integer> firstChild = new ArrayList<>(firstParent);
      List<Integer> secondChild = new ArrayList<>(secondParent);
      intermediateResult = indWiseEdgeCrossoverTSP(TSPcr, firstParent, secondParent, parameters, firstChild, secondChild);
    } else if(parameters.TSPcrossoverVersion == 2) {
      intermediateResult = indWiseTwoOrSinglePointPMXCrossoverTSP(TSPcr, firstParent, secondParent, parameters, parameters.geneSplitPoint, false); // TWO point as the last param is true
    } else if(parameters.TSPcrossoverVersion == 3) {
      intermediateResult = indWiseTwoOrSinglePointPMXCrossoverTSP(TSPcr, firstParent, secondParent, parameters, parameters.geneSplitPoint, true); // SINGLE point as the last param is true
    } else if(parameters.TSPcrossoverVersion == 4) {
      intermediateResult = indWiseOXCrossoverTSP(TSPcr, firstParent, secondParent, parameters, parameters.geneSplitPoint, false);
    } else if(parameters.TSPcrossoverVersion == 5) {
      intermediateResult = indWiseOXCrossoverTSP(TSPcr, firstParent, secondParent, parameters, parameters.geneSplitPoint, true);
    } else if(parameters.TSPcrossoverVersion == 6) {
      intermediateResult = indWiseCXCrossoverTSP(TSPcr, firstParent, secondParent, parameters, parameters.geneSplitPoint); // BASELINE
    }

//    printTSPGeneDifference(firstParent, secondParent, intermediateResult.firstChild(), intermediateResult.secondChild(), parameters.geneSplitPoint);

    if(parameters.KNAPcrossoverVersion == 1) {
      indWiseUniformCrossoverKNAP(KNAPcr, firstParent, secondParent, parameters, intermediateResult); // BASELINE
    } else if(parameters.KNAPcrossoverVersion == 2) {
      indWiseSinglePointCrossoverKNAP(KNAPcr, firstParent, secondParent, parameters, intermediateResult);
    } else if(parameters.KNAPcrossoverVersion == 3) {
      indWiseTwoPointCrossoverKNAP(KNAPcr, firstParent, secondParent, parameters, intermediateResult);
    }

    List<List<Integer>> result = new ArrayList<>();
    result.add(intermediateResult.firstChild());
    result.add(intermediateResult.secondChild());

    return result;
  }

  private void printTSPGeneDifference(List<Integer> firstParent, List<Integer> secondParent, List<Integer> firstChild, List<Integer> secondChild, int geneSplitPoint) {
    printOoutCommonTSPGenes(firstParent, firstChild, geneSplitPoint, "firstParent", "firstChild");
    printOoutCommonTSPGenes(firstParent, secondChild, geneSplitPoint, "firstParent", "secondChild");
    printOoutCommonTSPGenes(secondParent, firstChild, geneSplitPoint, "secondParent", "firstChild");
    printOoutCommonTSPGenes(secondParent, secondChild, geneSplitPoint, "secondParent", "secondChild");
    System.out.println();
  }

  private static void printOoutCommonTSPGenes(List<Integer> first, List<Integer> second, int geneSplitPoint, String firstGenesName, String secondGenesName) {
    int commonGenes = 0;
    for(int i = 0; i < geneSplitPoint; i++) {
      if(first.get(i) == second.get(i)) {
        commonGenes++;
      }
    }
    System.out.println(firstGenesName + " X " + secondGenesName + " TSP common genes: " + commonGenes);
  }

  private CrossoverResult indWiseCXCrossoverTSP(double TSPcr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters, int geneSplitPoint) {
        if (parameters.random.nextDouble() > TSPcr) {
            return new CrossoverResult(new ArrayList<>(firstParent), new ArrayList<>(secondParent));
        }

        int size = firstParent.size();

        List<Integer> offspring1 = new ArrayList<>(Collections.nCopies(size, -1));
        List<Integer> offspring2 = new ArrayList<>(Collections.nCopies(size, -1));

        // Perform Cycle Crossover
        int currentCityIndex = 0;
        boolean cycleNotReached = true;
        while (offspring1.contains(-1) && cycleNotReached && currentCityIndex < geneSplitPoint) {
            if (offspring1.get(currentCityIndex) == -1) {
                offspring1.set(currentCityIndex, firstParent.get(currentCityIndex));
            }

            int nextCity = secondParent.get(currentCityIndex);
            currentCityIndex = firstParent.indexOf(nextCity);

            cycleNotReached = !offspring1.contains(nextCity);
        }

        currentCityIndex = 0;
        cycleNotReached = true;
        while (offspring2.contains(-1) && cycleNotReached && currentCityIndex < geneSplitPoint) {
          if (offspring2.get(currentCityIndex) == -1) {
            offspring2.set(currentCityIndex, secondParent.get(currentCityIndex));
          }

          int nextCity = firstParent.get(currentCityIndex);
          currentCityIndex = secondParent.indexOf(nextCity);

          cycleNotReached = !offspring2.contains(nextCity);
        }

        // Fill in the remaining positions with cities from the second parent
        for (int i = 0; i < geneSplitPoint; i++) {
            if (!offspring1.contains(secondParent.get(i))) {
                offspring1.set(i, secondParent.get(i));
            }

            if (!offspring2.contains(firstParent.get(i))) {
                offspring2.set(i, firstParent.get(i));
            }
        }

        // fill up the KNAP section
        for(int i = geneSplitPoint; i < size; i++) {
            offspring1.set(i, firstParent.get(i));
            offspring2.set(i, secondParent.get(i));
        }

        return new CrossoverResult(offspring1, offspring2);
    }

  private CrossoverResult indWiseOXCrossoverTSP(double TSPcr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters, int geneSplitPoint, boolean isSinglePoint) {
    if (parameters.random.nextDouble() > TSPcr) {
      return new CrossoverResult(new ArrayList<>(firstParent), new ArrayList<>(secondParent));
    }

    int size = firstParent.size();

    // Determine the two crossover points
    int point1 = parameters.random.nextInt(geneSplitPoint);
    int point2 = parameters.random.nextInt(geneSplitPoint);
    int start = Math.min(point1, point2);
    int end = Math.max(point1, point2);
    if(isSinglePoint) {
      start = 0;
    }

    List<Integer> offspring1 = new ArrayList<>(Collections.nCopies(size, -1));
    List<Integer> offspring2 = new ArrayList<>(Collections.nCopies(size, -1));

    // Copy segment from parents to offspring
    for (int i = start; i <= end; i++) {
      offspring1.set(i, secondParent.get(i));
      offspring2.set(i, firstParent.get(i));
    }

    // Fill in the remaining positions with cities from the first parent
    for (int i = end + 1, j = end + 1; i != start; ) {
      i %= geneSplitPoint;
      j %= geneSplitPoint;
      if (!offspring1.contains(firstParent.get(j))) {
        offspring1.set(i, firstParent.get(j));
        i += 1;
      }
      j += 1;
    }

    // Fill in the remaining positions with cities from the second parent
    for (int i = end + 1, j = end + 1; i != start; ) {
      i %= geneSplitPoint;
      j %= geneSplitPoint;
      if (!offspring2.contains(secondParent.get(j))) {
        offspring2.set(i, secondParent.get(j));
        i += 1;
      }
      j += 1;
    }

    // fill up the rest
    for(int i = geneSplitPoint; i < size; i++) {
      offspring1.set(i, firstParent.get(i));
      offspring2.set(i, secondParent.get(i));
    }

    return new CrossoverResult(offspring1, offspring2);
  }

  private CrossoverResult indWiseTwoOrSinglePointPMXCrossoverTSP(double TSPcr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters, int geneSplitPoint, boolean isSinglePoint) {
    if (parameters.random.nextDouble() > TSPcr) {
      return new CrossoverResult(new ArrayList<>(firstParent), new ArrayList<>(secondParent));
    }

    int size = firstParent.size();

    // Determine the two crossover points
    int point1 = parameters.random.nextInt(geneSplitPoint);
    int point2 = parameters.random.nextInt(geneSplitPoint);
    int end = Math.max(point1, point2);
    int start = Math.min(point1, point2);
    if(isSinglePoint) {
      start = 0;
    }

    List<Integer> offspring1 = new ArrayList<>(Collections.nCopies(size, -1));
    List<Integer> offspring2 = new ArrayList<>(Collections.nCopies(size, -1));

    // Copy segment from parents to offspring
    for (int i = start; i <= end; i++) {
      offspring1.set(i, secondParent.get(i));
      offspring2.set(i, firstParent.get(i));
    }

    // Fill in the remaining positions with mappings
    for (int i = 0; i < geneSplitPoint; i++) {
      if (i < start || i > end) {
        int index1 = secondParent.indexOf(firstParent.get(i));
        int index2 = firstParent.indexOf(secondParent.get(i));

        while (index1 >= start && index1 <= end) { // if the city existis in the already copid sequence
          index1 = secondParent.indexOf(firstParent.get(index1));
        }

        while (index2 >= start && index2 <= end) {
          index2 = firstParent.indexOf(secondParent.get(index2));
        }

        offspring1.set(index1, secondParent.get(i));
        offspring2.set(index2, firstParent.get(i));
      }
    }

    // Fill in the remaining positions with non-mapped cities
    for (int i = 0; i < size; i++) {
      if (offspring1.get(i) == -1) {
        offspring1.set(i, firstParent.get(i));
      }
      if (offspring2.get(i) == -1) {
        offspring2.set(i, secondParent.get(i));
      }
    }

    return new CrossoverResult(offspring1, offspring2);
  }

  private static void indWiseTwoPointCrossoverKNAP(double KNAPcr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters, CrossoverResult intermediateResult) {
    // KNAP TwoPoint Crossover
    if (parameters.random.nextDouble() < KNAPcr) {
      int numGenes = parameters.geneSplitPoint;
      int a = parameters.random.nextInt(firstParent.size() - numGenes) + numGenes;
      int b = parameters.random.nextInt(firstParent.size() - numGenes) + numGenes;
      while(a == b) {
        b = parameters.random.nextInt(firstParent.size() - numGenes) + numGenes;
      }
      int startPoint = Math.min(a, b);
      int endPoint = Math.max(a, b);

      for (int i = numGenes; i < firstParent.size(); ++i) {
        if (i < startPoint || i > endPoint) {
          intermediateResult.firstChild().set(i, firstParent.get(i));
          intermediateResult.secondChild().set(i, secondParent.get(i));
        } else {
          intermediateResult.firstChild().set(i, secondParent.get(i));
          intermediateResult.secondChild().set(i, firstParent.get(i));
        }
      }
    }
  }

  private static void indWiseUniformCrossoverKNAP(double KNAPcr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters, CrossoverResult intermediateResult) {
    // UNIFORM KNAP
    if (parameters.random.nextDouble() < KNAPcr) {
      double random;
      for (int i = parameters.geneSplitPoint; i < firstParent.size(); ++i) {
        random = parameters.random.nextDouble();
        if (random < 0.5) {
          intermediateResult.firstChild().set(i, secondParent.get(i));
        }
        random = parameters.random.nextDouble();
        if (random < 0.5) {
          intermediateResult.secondChild().set(i, firstParent.get(i));
        }
      }
    }
  }

  private static void indWiseSinglePointCrossoverKNAP(double KNAPcr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters, CrossoverResult intermediateResult) {
    // KNAP SinglePoint Crossover
    if (parameters.random.nextDouble() < KNAPcr) {
      int numGenes = parameters.geneSplitPoint;
      int point = parameters.random.nextInt(firstParent.size() - numGenes) + numGenes;

      for (int i = numGenes; i < firstParent.size(); ++i) {
        if (i < point) {
          intermediateResult.firstChild().set(i, firstParent.get(i));
          intermediateResult.secondChild().set(i, secondParent.get(i));
        } else {
          intermediateResult.firstChild().set(i, secondParent.get(i));
          intermediateResult.secondChild().set(i, firstParent.get(i));
        }
      }
    }
  }

  private CrossoverResult indWiseEdgeCrossoverTSP(double TSPcr, List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters, List<Integer> firstChild, List<Integer> secondChild) {
    // EDGE TSP
    if (parameters.random.nextDouble() < TSPcr) {
      List<Set<Integer>> firstNeighbourhood = generateNeighbourhood(firstParent, secondParent, parameters);
      List<Set<Integer>> secondNeighbourhood = copyNeighbourhood(firstNeighbourhood);
      firstChild = getChild(firstChild, firstParent, secondParent, firstNeighbourhood, parameters);
      secondChild = getChild(secondChild, secondParent, firstParent, secondNeighbourhood, parameters);
    }
    CrossoverResult result = new CrossoverResult(firstChild, secondChild);
    return result;
  }

  private record CrossoverResult(List<Integer> firstChild, List<Integer> secondChild) {
  }

  private List<Integer> getChild(List<Integer> child, List<Integer> firstParent, List<Integer> secondParent,
                                 List<Set<Integer>> neighbourhood, ParameterSet<Integer, BaseProblemRepresentation> parameters) {
    int numGenes = parameters.geneSplitPoint;
    List<Integer> availableGenes = IntStream.rangeClosed(0, numGenes - 1)
        .boxed().collect(Collectors.toList());
    int x = firstParent.get(0);
    availableGenes.remove(new Integer(x));
    for (int i = 0; i < numGenes - 1; ++i) {
      child.set(i, x);

      removeFromNeighbourhood(x, neighbourhood);

      if (neighbourhood.get(x).isEmpty()) {
        x = availableGenes.get(parameters.random.nextInt(availableGenes.size()));
        availableGenes.remove(new Integer(x));
      } else {
        Set<Integer> neighbourhoodOfX = neighbourhood.get(x);
        int minSize = neighbourhoodOfX.stream().mapToInt(neighbour -> neighbourhood.get(neighbour).size()).min().orElse(0);
        neighbourhoodOfX = neighbourhoodOfX.stream().filter(neighbour -> neighbourhood.get(neighbour).size() == minSize).collect(Collectors.toSet());
        int random = parameters.random.nextInt(neighbourhoodOfX.size());
        Iterator<Integer> iter = neighbourhoodOfX.iterator();
        for (int j = 0; j < random; j++) {
          iter.next();
        }
        x = iter.next();

        availableGenes.remove(new Integer(x));
      }
    }
    child.set(numGenes - 1, x);

    return child;
  }

  private void removeFromNeighbourhood(final Integer toRemove, List<Set<Integer>> neighbourhood) {
    neighbourhood.forEach(set -> set.remove(toRemove));
  }

  private List<Set<Integer>> generateNeighbourhood(List<Integer> firstParent, List<Integer> secondParent, ParameterSet<Integer, BaseProblemRepresentation> parameters) {
    int numGenes = parameters.geneSplitPoint;
    List<Set<Integer>> neighbourhood = new ArrayList<>();
    for (int i = 0; i < numGenes; ++i) {
      neighbourhood.add(new HashSet<>());
    }
    neighbourhood.get(firstParent.get(0)).add(firstParent.get(1));
    neighbourhood.get(firstParent.get(0)).add(firstParent.get(numGenes - 1));
    neighbourhood.get(secondParent.get(0)).add(secondParent.get(1));
    neighbourhood.get(secondParent.get(0)).add(secondParent.get(numGenes - 1));

    for (int i = 1; i < numGenes - 1; ++i) {
      neighbourhood.get(firstParent.get(i)).add(firstParent.get(i - 1));
      neighbourhood.get(firstParent.get(i)).add(firstParent.get(i + 1));
      neighbourhood.get(secondParent.get(i)).add(secondParent.get(i - 1));
      neighbourhood.get(secondParent.get(i)).add(secondParent.get(i + 1));
    }

    neighbourhood.get(firstParent.get(numGenes - 1)).add(firstParent.get(numGenes - 2));
    neighbourhood.get(firstParent.get(numGenes - 1)).add(firstParent.get(0));
    neighbourhood.get(secondParent.get(numGenes - 1)).add(secondParent.get(numGenes - 2));
    neighbourhood.get(secondParent.get(numGenes - 1)).add(secondParent.get(0));

    return neighbourhood;
  }

  private List<Set<Integer>> copyNeighbourhood(List<Set<Integer>> neighbourhood) {
    List<Set<Integer>> copy = new ArrayList<>();
    for (Set<Integer> set : neighbourhood) {
      Set<Integer> setCopy = new HashSet<>(set);
      copy.add(setCopy);
    }
    return copy;
  }

  private boolean areTSPListsEqual(List<?> list1, List<?> list2, int geneSplitPoint) {
    // If lists are of different sizes, they can't be equal
    if (list1.size() != list2.size()) {
      return false;
    }

    // Compare each element of the lists
    for (int i = 0; i < geneSplitPoint; i++) {
      if (!list1.get(i).equals(list2.get(i))) {
        return false; // If any element differs, lists are not equal
      }
    }

    // If all elements match, lists are equal
    return true;
  }
}