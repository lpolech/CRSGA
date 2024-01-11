package algorithms.heuristics;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;

import java.util.ArrayList;
import java.util.List;

public class TabuSearch {

  private Schedule schedule;
  private ParameterSet<Integer, Schedule> parameters;
  private int neighbourhoodSize;
  private int iterationLimit;

  public TabuSearch(Schedule schedule, ParameterSet<Integer, Schedule> parameters, int iterationLimit, int neighbourhoodSize) {
    this.schedule = schedule;
    this.parameters = parameters;
    this.iterationLimit = iterationLimit;
    this.neighbourhoodSize = neighbourhoodSize;
  }

  public BaseIndividual<Integer, Schedule> optimize() {
    List<BaseIndividual<Integer, Schedule>> population = parameters.initialPopulation.generate(schedule, 1, parameters.evaluator, parameters);
    List<BaseIndividual<Integer, Schedule>> tabuList = new ArrayList<>(iterationLimit + 1);
    BaseIndividual<Integer, Schedule> individual = population.get(0);
    individual.buildSolution(individual.getGenes(), parameters);
    tabuList.add(individual);

    List<Integer> nextGenes;
    BaseIndividual<Integer, Schedule> nextIndividual;
    for (int i = 0; i < iterationLimit; ++i) {
      population.clear();

      for (int n = 0; n < neighbourhoodSize; ++n) {
        boolean isTabu;
        do {
          isTabu = false;
          nextGenes = parameters.mutation.mutate(null, 1.0, individual.getGenes(), 0, 0, parameters);
          nextIndividual = new BaseIndividual<>(schedule, nextGenes, parameters.evaluator);
          nextIndividual.buildSolution(nextGenes, parameters);
          for (int t = 0; t < tabuList.size() && !isTabu; ++t) {
            isTabu = tabuList.get(t).compareTo(nextIndividual) == 0;
          }
          if (isTabu) {
            System.out.println(2);
          }
        } while (isTabu);
        tabuList.add(nextIndividual);
        population.add(nextIndividual);
      }

      for (int b = 0; b < population.size(); ++b) {
        if (population.get(b).getEvalValue() < individual.getEvalValue()) {
          individual = population.get(b);
        }
      }

    }

    return individual;
  }

}
