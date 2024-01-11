package algorithms.evolutionary_algorithms;

import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for evolutionary algorithms.
 */
public class EvolutionaryAlgorithm<GENE extends Number, PROBLEM extends BaseProblemRepresentation> {

  protected PROBLEM problem;
  protected ParameterSet<GENE, PROBLEM> parameters;

  protected int populationSize;
  // limits max number of generations
  protected int generationLimit;

  protected List<BaseIndividual<GENE, PROBLEM>> removeDuplicates(
      List<BaseIndividual<GENE, PROBLEM>> allIndividuals) {
    List<BaseIndividual<GENE, PROBLEM>> allIndividualsNoDuplicates = new ArrayList<>();
    for (BaseIndividual<GENE, PROBLEM> ind : allIndividuals) {
      if (allIndividualsNoDuplicates.stream().noneMatch(i -> i.compareTo(ind) == 0)) {
        allIndividualsNoDuplicates.add(ind);
      }
    }
    return allIndividualsNoDuplicates;
  }

  protected void removeDuplicatesAndDominated(
      List<BaseIndividual<GENE, PROBLEM>> population,
      List<BaseIndividual<GENE, PROBLEM>> allIndividuals) {
    for (BaseIndividual<GENE, PROBLEM> individual : population) {
      boolean dominated = false;
      boolean clone = false;
      BaseIndividual<GENE, PROBLEM> trial;
      for (int i = 0; i < allIndividuals.size() && !clone && !dominated; ++i) {
        trial = allIndividuals.get(i);
        if (trial.dominates(individual)) {
          dominated = true;
          continue;
        }
        if (trial.compareTo(individual) == 0) {
          clone = true;
          continue;
        }
        if (individual.dominates(trial)) {
          allIndividuals.remove(trial);
          --i;
        }
      }
      if (!dominated && !clone) {
        allIndividuals.add(individual);
      }
    }
  }

  // TODO: probably should be in a more generic place, it is also used by measures and now by mutation
  protected List<BaseIndividual<GENE, PROBLEM>> getNondominated(
      List<BaseIndividual<GENE, PROBLEM>> population) {

    List<BaseIndividual<GENE, PROBLEM>> nondominatedSolutions = new ArrayList<>();

    for (BaseIndividual<GENE, PROBLEM> individual : population) {
      if (individual.isNotDominatedBy(population)) {
        nondominatedSolutions.add(individual);
      }
    }

    return nondominatedSolutions;
  }

}
