package algorithms.evolutionary_algorithms.genetic_algorithm;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.selection.BaseSelection;
import algorithms.evolutionary_algorithms.selection.DiversitySelection;
import algorithms.evolutionary_algorithms.selection.NondominatedSortingNoCrowdingTournament;
import algorithms.evolutionary_algorithms.selection.NondominatedSortingTournament;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.ArrayList;
import java.util.List;

public class NTGAIII<PROBLEM extends BaseProblemRepresentation> extends GeneticAlgorithm<PROBLEM> {

  private NondominatedSorter<BaseIndividual<Integer, PROBLEM>> sorter;
  private BaseSelection<Integer, PROBLEM> rankSelection;
  private BaseSelection<Integer, PROBLEM> cdSelection;
  private BaseSelection<Integer, PROBLEM> gapSelection;

  public NTGAIII(PROBLEM problem, int populationSize, int generationLimit, ParameterSet<Integer, PROBLEM> parameters, double mutationProbability, double crossoverProbability) {
    super(problem, populationSize, generationLimit, parameters, mutationProbability, crossoverProbability);
    sorter = new NondominatedSorter<>();
    rankSelection = new NondominatedSortingNoCrowdingTournament();
    ((NondominatedSortingNoCrowdingTournament)rankSelection).setTournamentSize(parameters.tournamentSize);
    cdSelection = new NondominatedSortingTournament();
    ((NondominatedSortingTournament)cdSelection).setTournamentSize(parameters.tournamentSize);
    gapSelection = new DiversitySelection();
    ((DiversitySelection) gapSelection).setTournamentSize(parameters.tournamentSize);
  }

  public List<BaseIndividual<Integer, PROBLEM>> optimize() {
    int generation = 1;
    List<BaseIndividual<Integer, PROBLEM>> combinedPopulations = new ArrayList<>();

    List<BaseIndividual<Integer, PROBLEM>> populationRank = createPopulation(combinedPopulations);
    List<BaseIndividual<Integer, PROBLEM>> populationCD = createPopulation(combinedPopulations);
    List<BaseIndividual<Integer, PROBLEM>> populationGap = createPopulation(combinedPopulations);

    while (generation < generationLimit) {

      optimizePopulation(rankSelection, false, false, populationRank, combinedPopulations);
//      optimizePopulation(cdSelection, true, false, populationCD, combinedPopulations);
      optimizePopulation(gapSelection, false, true, populationGap, combinedPopulations);

      ++generation;
    }

    combinedPopulations = removeDuplicates(combinedPopulations);
    List<BaseIndividual<Integer, PROBLEM>> pareto = getNondominated(combinedPopulations);

    return pareto;
  }

  public void optimizePopulation(BaseSelection<Integer, PROBLEM> selection, boolean hasCD, boolean hasGap,
                                 List<BaseIndividual<Integer, PROBLEM>> population,
                                 List<BaseIndividual<Integer, PROBLEM>> combinedPopulations) {

    BaseIndividual<Integer, PROBLEM> firstParent;
    BaseIndividual<Integer, PROBLEM> secondParent;
    BaseIndividual<Integer, PROBLEM> firstChild;
    BaseIndividual<Integer, PROBLEM> secondChild;
    List<List<Integer>> children;

    List<BaseIndividual<Integer, PROBLEM>> newPopulation = new ArrayList<>();
    sorter.nondominatedSorting(population);
    if (hasCD) {
      crowdingDistance(population);
    }

    while (newPopulation.size() < populationSize) {

      if (hasGap) {
        combinedPopulations = gapDistanceRevised(combinedPopulations);
      }
      firstParent = selection.select(population, combinedPopulations, -1, null, null, parameters);
      secondParent = selection.select(population, combinedPopulations, combinedPopulations.indexOf(firstParent), firstParent, null, parameters);
      children = parameters.crossover.crossover(crossoverProbability, firstParent.getGenes(), secondParent.getGenes(), parameters);
      children.set(0, parameters.mutation.mutate(population, mutationProbability, children.get(0), 0, populationSize, parameters));
      children.set(1, parameters.mutation.mutate(population, mutationProbability, children.get(1), 0, populationSize, parameters));

      firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
      secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);

      // TODO: refactor after I get well :(
      for (int i = 0; newPopulation.contains(firstChild) && i < 20; i++) {
        firstChild.setGenes(parameters.mutation.mutate(population, 1.0, firstChild.getGenes(), 0, populationSize, parameters));
      }
      if (!newPopulation.contains(firstChild)) {
        firstChild.buildSolution(firstChild.getGenes(), parameters);
        newPopulation.add(firstChild);
      }
      for (int i = 0; newPopulation.contains(secondChild) && i < 20; i++) {
        secondChild.setGenes(parameters.mutation.mutate(population, 1.0, secondChild.getGenes(), 0, populationSize, parameters));
      }
      if (!newPopulation.contains(secondChild)) {
        secondChild.buildSolution(secondChild.getGenes(), parameters);
        newPopulation.add(secondChild);
      }

    }
    population = newPopulation;

    removeDuplicatesAndDominated(population, combinedPopulations);
  }

  public List<BaseIndividual<Integer, PROBLEM>> createPopulation(List<BaseIndividual<Integer, PROBLEM>> combinedPopulations) {
    List<BaseIndividual<Integer, PROBLEM>> population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);
    for (BaseIndividual<Integer, PROBLEM> individual : population) {
      individual.buildSolution(individual.getGenes(), parameters);
    }
    sorter.nondominatedSorting(population);
    sorter.crowdingDistance(population);

    combinedPopulations.addAll(population);
    combinedPopulations = removeDuplicates(combinedPopulations);
    combinedPopulations = getNondominated(combinedPopulations);

    return population;
  }

}
