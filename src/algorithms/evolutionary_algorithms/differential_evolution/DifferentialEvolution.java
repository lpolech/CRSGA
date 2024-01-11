package algorithms.evolutionary_algorithms.differential_evolution;

import algorithms.evolutionary_algorithms.EvolutionaryAlgorithm;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.scheduling.Schedule;
import algorithms.quality_measure.BaseMeasure;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the differential evolution approach.
 */
public class DifferentialEvolution<PROBLEM extends BaseProblemRepresentation> extends EvolutionaryAlgorithm<Double, PROBLEM> {

  private List<BaseIndividual<Double, PROBLEM>> population;

  // mutation parameter
  private double f;
  // crossover parameter
  private double cr;
  // limits max number of generations without improvement
  private int staleLimit;
  // limits max number of clones in population
  private int maxClones;

  private List<BaseMeasure> measures;

  private NondominatedSorter<BaseIndividual<Double, Schedule>> sorter;

  public DifferentialEvolution(PROBLEM problem, int populationSize, double f, double cr,
                               int generationLimit, int staleLimit, int maxClones, ParameterSet<Double, PROBLEM> parameters,
                               List<BaseMeasure> measures) {
    this.problem = problem;
    this.parameters = parameters;
    this.populationSize = populationSize;
    this.f = f;
    this.cr = cr;
    this.generationLimit = generationLimit;
    this.staleLimit = staleLimit;
    this.maxClones = maxClones;
    this.measures = measures;

    sorter = new NondominatedSorter<>();
  }

  /**
   * Runs single objective version of differential evolution.
   *
   * @return best found individual
   */
  public List<BaseIndividual<Double, PROBLEM>> optimize() {
    int generation = 0;
    int lastUpdate = 0;
    int numClones = 0;
    BaseIndividual<Double, PROBLEM> best;
    List<Double> donor;
    BaseIndividual<Double, PROBLEM> current;
    List<Double> trialGenes;
    BaseIndividual<Double, PROBLEM> trialIndividual;

    population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);
    for (BaseIndividual<Double, PROBLEM> individual : population) {
      individual.buildSolution(individual.getGenes(), parameters);
    }
    best = findBestIndividual(population);

    while ((generation < generationLimit) && (generation - lastUpdate < staleLimit) &&
        (numClones < maxClones)) {

      for (int i = 0; i < populationSize; ++i) {
        // Perform mutation
        donor = parameters.mutation.mutate(population, f, null, i, populationSize, parameters);

        // Perform crossover
        current = population.get(i);
        // there is only one child
        trialGenes = parameters.crossover.crossover(cr, current.getGenes(), donor, parameters).get(0);

        // Create and evaluate trialGenes individual and perform selections
        trialIndividual = new BaseIndividual<>(problem, trialGenes, parameters.evaluator);
        trialIndividual.buildSolution(trialIndividual.getGenes(), parameters);
        population.set(i, parameters.selection.select(population, null, i, current, trialIndividual, parameters));

        // Set current best individual
        if (trialIndividual.getEvalValue() < best.getEvalValue()) {
          best = trialIndividual;
          lastUpdate = generation;
        }
        // TODO: correct place?
        numClones = parameters.cloneHandler.handleClones(population);
      }
      ++generation;
    }

    this.problem = best.getProblem();
    List<BaseIndividual<Double, PROBLEM>> results = new ArrayList<>();
    results.add(best);
    return results;
  }

  /**
   * Finds an individual with the lowest value of
   * evaluation function
   *
   * @param population population, in which we search
   * @return best individual
   */
  private BaseIndividual<Double, PROBLEM> findBestIndividual(List<BaseIndividual<Double, PROBLEM>> population) {
    BaseIndividual<Double, PROBLEM> best = population.get(0);
    double eval = best.getEvalValue();
    BaseIndividual<Double, PROBLEM> trial;
    for (int i = 1; i < population.size(); ++i) {
      trial = population.get(i);
      if (trial.getEvalValue() < eval) {
        best = trial;
        eval = trial.getEvalValue();
      }
    }

    return best;
  }

}
