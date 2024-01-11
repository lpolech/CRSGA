package algorithms.evolutionary_algorithms.genetic_algorithm;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.genetic_algorithm.utils.Direction;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BNSGAIII<PROBLEM extends BaseProblemRepresentation> extends NondominatedSortingGA<PROBLEM> {

  private int numObjectives;
  private int ffe;
  private int alpha;

  private List<Direction<PROBLEM>> referenceDirections;

  public BNSGAIII(PROBLEM problem, int populationSize, int generationLimit,
                  ParameterSet<Integer, PROBLEM> parameters, double mutationProbability,
                  double crossoverProbability, int alpha) {
    super(problem, populationSize, generationLimit, parameters,
        mutationProbability, crossoverProbability);
    this.numObjectives = parameters.evaluator.getNumObjectives();
    this.ffe = 1;
    this.alpha = alpha;
    referenceDirections = calculateReferenceDirections(populationSize);
  }

  public List<BaseIndividual<Integer, PROBLEM>> optimize() {
    List<BaseIndividual<Integer, PROBLEM>> newPopulation = new ArrayList<>();
    List<BaseIndividual<Integer, PROBLEM>> combinedPopulation;
    List<BaseIndividual<Integer, PROBLEM>> allIndividuals = new ArrayList<>();
    Map<Integer, List<BaseIndividual<Integer, PROBLEM>>> fronts;
    List<Direction<PROBLEM>> emptyDirections;
    Direction<PROBLEM> direction;
    List<BaseIndividual<Integer, PROBLEM>> firstFront;
    BaseIndividual<Integer, PROBLEM> solution;

    population = parameters.initialPopulation.generate(problem, populationSize,
        parameters.evaluator, parameters);
    for (BaseIndividual<Integer, PROBLEM> individual : population) {
      individual.buildSolution(individual.getGenes(), parameters);
    }

    allIndividuals.addAll(population);
    allIndividuals = removeDuplicates(allIndividuals);
    allIndividuals = getNondominated(allIndividuals);

    boolean needsLS = true;
    while (ffe < generationLimit) {

      fronts = nondominatedSorting(population);
      for (List<BaseIndividual<Integer, PROBLEM>> front : fronts.values()) {
        crowdingDistance(front);
      }
      newPopulation = makeNewPop(population, allIndividuals);

      if (needsLS) {
        // Phase 1
        hillClimbing(population, newPopulation);
        needsLS = false;
      } else {

        combinedPopulation = new ArrayList<>();
        combinedPopulation.addAll(population);
        combinedPopulation.addAll(newPopulation);

        fronts = nondominatedSorting(combinedPopulation);
        for (List<BaseIndividual<Integer, PROBLEM>> front : fronts.values()) {
          crowdingDistance(front);
        }

        normalize(combinedPopulation);

        List<BaseIndividual<Integer, PROBLEM>> bestWithinNiche = getBestWithinNiche(combinedPopulation);

        emptyDirections = referenceDirections.stream().filter(d -> d.getSurrounding().size() == 0).collect(Collectors.toList());

        for (int i = 0; i < alpha; ++i) {
          if (emptyDirections.size() != 0) {
            // Phase 2
            double currentDistance = Double.MAX_VALUE;
            direction = emptyDirections.get(parameters.random.nextInt(emptyDirections.size()));
            firstFront = fronts.get(fronts.keySet().iterator().next());
            solution = firstFront.get(0);
            for (BaseIndividual<Integer, PROBLEM> individual : firstFront) {
              double distance = direction.getClusteringDistance(individual);
              if (distance < currentDistance) {
                currentDistance = distance;
                solution = individual;
              }
            }
            emptyDirections.remove(direction);

          } else {
            // Phase 3

            // TODO: kktpm
          }

          // TODO: ls
        }

        allIndividuals.addAll(population);
        allIndividuals = removeDuplicates(allIndividuals);
        allIndividuals = getNondominated(allIndividuals);

        ffe += populationSize;
      }

    }

    allIndividuals = removeDuplicates(allIndividuals);
    List<BaseIndividual<Integer, PROBLEM>> pareto = getNondominated(allIndividuals);

    return pareto;
  }

  private void hillClimbing(List<BaseIndividual<Integer, PROBLEM>> population, List<BaseIndividual<Integer, PROBLEM>> newPopulation) {
    for (int i = 0; i < numObjectives; ++i) {
      BaseIndividual<Integer, PROBLEM> extremePoint = findExtreme(population, i);
      double extremeValue = getBiasedWeightedSum(extremePoint, i);
      boolean isImprovement = true;
      while (isImprovement && ffe < generationLimit) {
        isImprovement = false;
        List<BaseIndividual<Integer, PROBLEM>> neighbourhood = new ArrayList<>(parameters.neighbourhoodSize);
        for (int j = 0; j < parameters.neighbourhoodSize && ffe < generationLimit; ++j) {
          BaseIndividual<Integer, PROBLEM> neighbour = new BaseIndividual<>(extremePoint.getProblem(), extremePoint.getGenes(), extremePoint.getEvaluator());
          neighbour.setGenes(parameters.mutation.mutate(population, 1.0, neighbour.getGenes(), 0, populationSize, parameters));
          neighbour.buildSolution(neighbour.getGenes(), parameters);
          neighbourhood.add(neighbour);
          ++ffe;
        }
        double contestantValue;
        for (int j = 0; j < neighbourhood.size(); ++j) {
          contestantValue = getBiasedWeightedSum(neighbourhood.get(j), i);
          if (contestantValue < extremeValue) {
            extremePoint = neighbourhood.get(j);
            extremeValue = contestantValue;
            isImprovement = true;
          }
        }
      }
      int index = parameters.random.nextInt(populationSize);
      newPopulation.set(index, extremePoint);
    }
  }

  private BaseIndividual<Integer, PROBLEM> findExtreme(List<BaseIndividual<Integer, PROBLEM>> population, int objective) {
    BaseIndividual<Integer, PROBLEM> extreme = population.get(0);
    double extremeValue = getBiasedWeightedSum(extreme, objective);
    double contestantValue;
    for (int i = 1; i < populationSize; ++i) {
      contestantValue = getBiasedWeightedSum(population.get(i), objective);
      if (contestantValue < extremeValue) {
        extreme = population.get(i);
        extremeValue = contestantValue;
      }
    }
    return extreme;
  }

  private double getBiasedWeightedSum(BaseIndividual<Integer, PROBLEM> individual, int objective) {
    double value = 0.0d;
    double ratio = 1.0d / (numObjectives - 1);
    for (int i = 0; i < numObjectives; ++i) {
      if (i == objective) {
        value += 0.01d * individual.getNormalObjectives()[i];
      } else {
        value += ratio * individual.getNormalObjectives()[i];
      }
    }
    return value;
  }

  private List<Direction<PROBLEM>> calculateReferenceDirections(int K) {
    List<Direction<PROBLEM>> result = new ArrayList<>();

    for (int j = 0; j < K; ++j) {
      List<Double> objectives = new ArrayList<>();
      double s = 0.0d;
      for (int k = 1; k <= numObjectives; ++k) {
        if (k < numObjectives) {
          double rand = parameters.random.nextDouble();
          double objective = (1.0 - s) * (1 - Math.pow(rand, 1.0 / (numObjectives - k) ));
          objectives.add(objective);
          s += objective;
        } else {
          double objective = 1.0 - s;
          objectives.add(objective);
        }
      }
      result.add(new Direction<PROBLEM>(objectives));
    }

    return result;
  }

  // TODO: extract to a Normalizer class
  private void normalize(List<BaseIndividual<Integer, PROBLEM>> population) {
    double[] mins = new double[numObjectives];
    double[] maxs = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      mins[i] = Double.MAX_VALUE;
      maxs[i] = Double.MIN_VALUE;
    }

    for (BaseIndividual<Integer, PROBLEM> individual : population) {
      for (int i = 0; i < numObjectives; ++i) {
        double value = individual.getObjectives()[i];
        if (value < mins[i]) {
          mins[i] = value;
        }
        if (value > maxs[i]) {
          maxs[i] = value;
        }
      }
    }

    for (BaseIndividual<Integer, PROBLEM> individual : population) {
      double[] objectives = individual.getObjectives();
      double[] normalObjectives = new double[numObjectives];
      for (int i = 0; i < numObjectives; ++i) {
        normalObjectives[i] = (objectives[i] - mins[i]) / (maxs[i] - mins[i]);
      }
      individual.setNormalObjectives(normalObjectives);
    }
  }

  public List<BaseIndividual<Integer, PROBLEM>> getBestWithinNiche(List<BaseIndividual<Integer, PROBLEM>> population) {
    List<BaseIndividual<Integer, PROBLEM>> best = new ArrayList<>();

    for (BaseIndividual<Integer, PROBLEM> individual : population) {
      double currentDistance = Double.MAX_VALUE;
      Direction<PROBLEM> currentDirection = referenceDirections.get(0);
      for (Direction<PROBLEM> d : referenceDirections) {
        double distance = d.getClusteringDistance(individual);
        if (distance < currentDistance) {
          currentDistance = distance;
          currentDirection = d;
          individual.setDistance(currentDistance); // TODO: verify, might be risky to override the crowding distance
        }
      }
      currentDirection.addToSurrounding(individual);
    }

    for (Direction<PROBLEM> d : referenceDirections) {
      BaseIndividual<Integer, PROBLEM> currentIndividual = null;
      if (d.getSurrounding().size() > 0) {
        currentIndividual = d.getSurrounding().get(0);
      }
      for (BaseIndividual<Integer, PROBLEM> individual : d.getSurrounding()) {
        if (individual.getRank() < currentIndividual.getRank() ||
            (individual.getRank() == currentIndividual.getRank() && individual.getDistance() < currentIndividual.getDistance())
        ) {
          currentIndividual = individual;
        }
      }
      if (currentIndividual != null) {
        d.setNicheIndividual(currentIndividual);
        best.add(currentIndividual);
      }
    }

    return best;
  }

}
