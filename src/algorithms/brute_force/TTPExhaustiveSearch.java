package algorithms.brute_force;


import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

import java.util.ArrayList;
import java.util.List;

public class TTPExhaustiveSearch {

  private TTP problem;
  protected ParameterSet<Integer, TTP> parameters;
  private List<BaseIndividual<Integer, TTP>> nonDominated;

  public TTPExhaustiveSearch(TTP problem, ParameterSet<Integer, TTP> parameters) {
    this.problem = problem;
    this.parameters = parameters;
    nonDominated = new ArrayList<>();
  }

  public List<BaseIndividual<Integer, TTP>> findNonDominated() {
    int[] path = new int[problem.getPath().length];
    for (int i = 0; i < path.length; ++i) {
      path[i] = i;
    }
    tryPaths(path, 0);
    for (int i = 0; i < nonDominated.size(); ++i) {
      double profit = 0;
      double travellingTime = nonDominated.get(i).getProblem().getTravellingTime();
      int[] selection = nonDominated.get(i).getProblem().getSelection();
      for (int j = 0; j < selection.length; ++j) {
        if (selection[j] > 0) {
          profit += nonDominated.get(i).getProblem().getKnapsack().getItem(j).getProfit();
        }
      }
//      System.out.println("Profit: " + profit + "\t Travelling Time: " + travellingTime);
      System.out.println(profit + ";" + travellingTime);
    }
    return nonDominated;
  }

  private void tryPaths(int[] path, int index){
    if (index >= path.length - 1) {
      int[] selection = new int[problem.getSelection().length];
      trySelections(path, selection, 0);
      return;
    }

    for (int i = index; i < path.length; i++) {
      int t = path[index];
      path[index] = path[i];
      path[i] = t;

      tryPaths(path, index+1);

      t = path[index];
      path[index] = path[i];
      path[i] = t;
    }
  }

  private void trySelections(int[] path, int[] selection, int index) {
    if(index >= selection.length){
      BaseIndividual<Integer, TTP> individual = new BaseIndividual<>(problem, parameters.evaluator);
      individual.getProblem().setPath(path.clone());
      individual.getProblem().setSelection(selection.clone());
      if (individual.getProblem().getCurrentWeight() > individual.getProblem().getKnapsack().getCapacity()) {
        return;
      }
      individual.setObjectives();
      nonDominated.add(individual);
      nonDominated = removeDuplicates(nonDominated);
      nonDominated = getNondominated(nonDominated);
      return;
    }

    int numValues = parameters.upperBounds[path.length + index];
    for (int i = 0; i < numValues; i++) {
      selection[index] = i;
      trySelections(path, selection, index + 1);
    }
  }

  // TODO: probably should be in a more generic place
  protected List<BaseIndividual<Integer, TTP>> removeDuplicates(
      List<BaseIndividual<Integer, TTP>> allIndividuals) {
    List<BaseIndividual<Integer, TTP>> allIndividualsNoDuplicates = new ArrayList<>();
    for (BaseIndividual<Integer, TTP> ind : allIndividuals) {
      if (allIndividualsNoDuplicates.stream().noneMatch(i ->
          i.getObjectives()[0] == ind.getObjectives()[0] &&
          i.getObjectives()[1] == ind.getObjectives()[1])
          ) {
        allIndividualsNoDuplicates.add(ind);
      }
    }
    return allIndividualsNoDuplicates;
  }

  // TODO: probably should be in a more generic place, it is also used by measures and now by mutation
  protected List<BaseIndividual<Integer, TTP>> getNondominated(
      List<BaseIndividual<Integer, TTP>> population) {

    List<BaseIndividual<Integer, TTP>> nondominatedSolutions = new ArrayList<>();

    for (BaseIndividual<Integer, TTP> individual : population) {
      if (individual.isNotDominatedBy(population)) {
        nondominatedSolutions.add(individual);
      }
    }

    return nondominatedSolutions;
  }

}
