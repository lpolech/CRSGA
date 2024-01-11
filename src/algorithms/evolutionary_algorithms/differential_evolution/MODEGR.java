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
 * Implementation of the multi objective differential evolution approach.
 */
public class MODEGR<PROBLEM extends BaseProblemRepresentation> extends EvolutionaryAlgorithm<Double, PROBLEM> {

  private List<BaseIndividual<Double, PROBLEM>> population;

  // mutation parameter
  private double f;
  // crossover parameter
  private double cr;
  // limits max number of clones in population
  private int maxClones;

  private List<BaseMeasure> measures;

  private NondominatedSorter<BaseIndividual<Double, PROBLEM>> sorter;

  public MODEGR(PROBLEM problem, int populationSize, double f, double cr,
                int generationLimit, int maxClones, ParameterSet<Double, PROBLEM> parameters,
                List<BaseMeasure> measures) {
    this.problem = problem;
    this.parameters = parameters;
    this.populationSize = populationSize;
    this.f = f;
    this.cr = cr;
    this.generationLimit = generationLimit;
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
    int numClones = 0;
    List<Double> donor;
    BaseIndividual<Double, PROBLEM> current;
    List<Double> trialGenes;
    BaseIndividual<Double, PROBLEM> trialIndividual;
    List<BaseIndividual<Double, PROBLEM>> allIndividuals = new ArrayList<>();

    population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);
    for (BaseIndividual<Double, PROBLEM> individual : population) {
      individual.buildSolution(individual.getGenes(), parameters);
    }
    allIndividuals.addAll(population);

    while ((generation < generationLimit) && (numClones < maxClones)) {

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
        trialIndividual = parameters.selection.select(population, allIndividuals, i, current, trialIndividual, parameters);
        if (population.get(i) != trialIndividual) {
          population.set(i, trialIndividual);
          allIndividuals.add(population.get(i));
        }
        // TODO: correct place?
        //numClones = parameters.cloneHandler.handleClones(population);
      }
      ++generation;
      allIndividuals = removeDuplicates(allIndividuals);
      allIndividuals = getNondominated(allIndividuals);
    }

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
    // TODO: figure out whose responsibility is it !!!
    Schedule schedule = individual.getProblem();
    parameters.converter.convertToInteger(schedule.getTasks(), individual.getGenes());
    parameters.constraintPreserver.repair(schedule);
    schedule.buildTaskResourceAssignments();
    individual.setProblem(parameters.scheduleBuilder.buildTimestamps(schedule));
    individual.setEvalValue(individual.evaluate());
    individual.setObjectives();
    individual.setNormalObjectives();
    individual.setHashCode();
    individual.getProblem().setHashCode();
    return individual;
  }

}
