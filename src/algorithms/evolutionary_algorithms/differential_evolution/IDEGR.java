package algorithms.evolutionary_algorithms.differential_evolution;

import algorithms.evaluation.BaseEvaluator;
import algorithms.evaluation.WeightedEvaluator;
import algorithms.evolutionary_algorithms.EvolutionaryAlgorithm;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;
import algorithms.quality_measure.BaseMeasure;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the island differential evolution approach.
 */
public class IDEGR extends EvolutionaryAlgorithm<Double, Schedule> {

  // TODO: maybe population should be another object?
  private List<List<BaseIndividual<Double, Schedule>>> populations;

  // mutation parameter
  private double f;
  // crossover parameter
  private double cr;
  // limits max number of generations without improvement
  private int staleLimit;
  // limits max number of clones in population
  private int maxClones;
  // number of populations
  private int numPopulations;

  private List<BaseMeasure> measures;

  private NondominatedSorter<BaseIndividual<Double, Schedule>> sorter;

  // TODO: all populations use the same parameters, fix that!
  public IDEGR(Schedule schedule, int populationSize, int numPopulations, double f, double cr,
               int generationLimit, int maxClones, ParameterSet<Double, Schedule> parameters,
               List<BaseMeasure> measures) {
    this.problem = schedule;
    this.parameters = parameters;
    this.populationSize = populationSize;
    this.numPopulations = numPopulations;
    this.f = f;
    this.cr = cr;
    this.generationLimit = generationLimit;
    this.maxClones = maxClones;
    this.measures = measures;

    sorter = new NondominatedSorter<>();
    populations = new ArrayList<>();
  }

  /**
   * Runs single objective version of differential evolution.
   *
   * @return best found individual
   */
  public List<BaseIndividual<Double, Schedule>> optimize() {
    int numIterations = populationSize - parameters.migrationSize;
    int generation = 0;
    int numClones = 0;
    List<Double> donor;
    BaseIndividual<Double, Schedule> current;
    List<Double> trialGenes;
    BaseIndividual<Double, Schedule> trialIndividual;
    List<BaseIndividual<Double, Schedule>> allIndividuals = new ArrayList<>();
    List<BaseEvaluator<Double, Schedule>> evaluators = new ArrayList<>();

    double evalRateDivider = numPopulations - 1;
    for (int i = 0; i < numPopulations; ++i) {
      BaseEvaluator<Double, Schedule> evaluator = new WeightedEvaluator<>(i / evalRateDivider);
      evaluators.add(evaluator);
      List<BaseIndividual<Double, Schedule>> population =
          parameters.initialPopulation.generate(problem, populationSize, evaluator, parameters);
      for (BaseIndividual<Double, Schedule> individual : population) {
        buildSchedule(individual);
      }
      populations.add(population);
      allIndividuals.addAll(population);
    }

    while ((generation < generationLimit) && (numClones < maxClones)) {
      populations = parameters.migration.migrate(populations, generation);
      // TODO: it should be either unnecessary or optimized to run only for changed individuals
      for (List<BaseIndividual<Double, Schedule>> population : populations) {
        for (BaseIndividual<Double, Schedule> individual : population) {
          buildSchedule(individual);
        }
      }

      for (int populationIndex = 0; populationIndex < numPopulations; ++populationIndex) {
        List<BaseIndividual<Double, Schedule>> population = populations.get(populationIndex);

        for (int i = 0; i < numIterations; ++i) {
          // Perform mutation
          donor = parameters.mutation.mutate(population, f, null, i, populationSize, parameters);

          // Perform crossover
          current = population.get(i);
          // there is only one child
          trialGenes = parameters.crossover.crossover(cr, current.getGenes(), donor, parameters).get(0);

          // Create and evaluate trialGenes individual and perform selections
          trialIndividual = new BaseIndividual<>(problem, trialGenes, evaluators.get(populationIndex));
          buildSchedule(trialIndividual);
          trialIndividual = parameters.selection.select(population, null, i, current, trialIndividual, parameters);
          if (population.get(i) != trialIndividual) {
            population.set(i, trialIndividual);
            allIndividuals.add(population.get(i));
          }

          // TODO: correct place?
          numClones = parameters.cloneHandler.handleClones(population);
        }

      }
      ++generation;
      allIndividuals = removeDuplicates(allIndividuals);
      allIndividuals = getNondominated(allIndividuals);
    }

    allIndividuals = removeDuplicates(allIndividuals);

    return allIndividuals;
  }

  /**
   * Builds individual of given individual, by converting
   * genotype to phenotype and using greedy individual builder
   * to place tasks on the timeline.
   *
   * @param individual individual with the individual to build
   * @return individual with built individual
   */
  private BaseIndividual<Double, Schedule> buildSchedule(BaseIndividual<Double, Schedule> individual) {
    Schedule schedule = individual.getProblem();
    parameters.converter.convertToInteger(schedule.getTasks(), individual.getGenes());
    parameters.constraintPreserver.repair(schedule);
    schedule.buildTaskResourceAssignments();
    individual.setProblem(parameters.scheduleBuilder.buildTimestamps(schedule));
    individual.setEvalValue(individual.evaluate());
    individual.setObjectives();
    individual.setNormalObjectives();
    individual.getProblem().setHashCode();
    return individual;
  }

  /**
   * Finds an individual with the lowest value of
   * evaluation function
   *
   * @param population population, in which we search
   * @return best individual
   */
  private BaseIndividual<Double, Schedule> findBestIndividual(List<BaseIndividual<Double, Schedule>> population) {
    BaseIndividual<Double, Schedule> best = population.get(0);
    double eval = best.getEvalValue();
    BaseIndividual<Double, Schedule> trial;
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
