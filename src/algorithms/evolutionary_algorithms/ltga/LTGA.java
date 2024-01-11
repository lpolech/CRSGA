package algorithms.evolutionary_algorithms.ltga;


import algorithms.evolutionary_algorithms.EvolutionaryAlgorithm;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Linkage Tree Genetic Algorithm
 */
public class LTGA<PROBLEM extends BaseProblemRepresentation> extends EvolutionaryAlgorithm<Integer, PROBLEM> {

  protected List<BaseIndividual<Integer, PROBLEM>> population;

  private int highestUpperBound;
  private double[][] singularProbabilities;

  public LTGA(PROBLEM problem, int populationSize, int generationLimit, ParameterSet<Integer, PROBLEM> parameters) {
    this.problem = problem;
    this.parameters = parameters;
    this.populationSize = populationSize;
    this.generationLimit = generationLimit;
    setHighestUpperBound();
    singularProbabilities = new double[populationSize][highestUpperBound];
  }

  public List<BaseIndividual<Integer, PROBLEM>> optimize() {
    int generation = 0;
    List<BaseIndividual<Integer, PROBLEM>> newPopulation;

    population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);
    for (BaseIndividual<Integer, PROBLEM> individual : population) {
      individual.buildSolution(individual.getGenes(), parameters);
    }

    while (generation < generationLimit) {
      newPopulation = new ArrayList<>();
      setSingularProbabilities();

      for (int i = 0; i < populationSize; ++i) {



      }

      population = newPopulation;
      ++generation;
    }

    return population;
  }

  private void setHighestUpperBound() {
    final int[] upperBounds = parameters.upperBounds;
    highestUpperBound = upperBounds[0];
    for (int i = 0; i < upperBounds.length; ++i) {
      if (upperBounds[i] > highestUpperBound) {
        highestUpperBound = upperBounds[i];
      }
    }
  }

  private void setSingularProbabilities() {
    final int[] upperBounds = parameters.upperBounds;
    for (int i = 0; i < populationSize; ++i) {
      List<Integer> genes = population.get(i).getGenes();
      for (int j = 0; j < upperBounds.length; ++j) {
        ++singularProbabilities[i][genes.get(j)];
      }
      for (int j = 0; j < singularProbabilities[i].length; ++j) {
        singularProbabilities[i][j] /= genes.size();
      }
    }
  }

}
