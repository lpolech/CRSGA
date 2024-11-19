package algorithms.evolutionary_algorithms.initial_population;

import algorithms.evaluation.BaseEvaluator;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomTTPInitialPopulation<PROBLEM extends BaseProblemRepresentation> extends BaseInitialPopulation<Integer, PROBLEM> {

  private int multiplicationFactor = 1;

  /**
   * Creates a a population by randomizing
   * each individual
   *
   * @param problem problem, for which to generate a population
   * @param populationSize size of the population
   * @param evaluator list of evaluators
   * @param parameters set of parameters
   * @return population - list of individuals
   */
  @Override
  public List<BaseIndividual<Integer, PROBLEM>> generate(PROBLEM problem, int populationSize,
                                                      BaseEvaluator<Integer, PROBLEM> evaluator, ParameterSet<Integer, PROBLEM> parameters) {
    populationSize *= multiplicationFactor;
    List<BaseIndividual<Integer, PROBLEM>> population = new ArrayList<>(populationSize);
    int numGenes = problem.getNumGenes();
    int splitPoint = parameters.geneSplitPoint;

    List<Integer> genes = new ArrayList<>(Collections.nCopies(numGenes, null));
    for (int i = 0; i < populationSize; ++i) {

      for (int j = 0; j < splitPoint; ++j) {
        genes.set(j, j);
      }
      Collections.shuffle(genes.subList(1, splitPoint), parameters.random.getRandom());

      for (int j = splitPoint; j < numGenes; ++j) {
        genes.set(j, parameters.random.next(parameters.upperBounds[j]));
      }

      population.add(new BaseIndividual<>(problem, genes, evaluator));
    }

    return population;
  }

  public void setMultiplicationFactor(int multiplicationFactor) {
    this.multiplicationFactor = multiplicationFactor;
  }

}
