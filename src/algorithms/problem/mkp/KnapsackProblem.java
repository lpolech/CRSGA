package algorithms.problem.mkp;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseProblemRepresentation;

import java.util.Arrays;
import java.util.List;

/**
 * Representation of a Knapsack Problem. Contains a list of Knapsacks
 * and a solution to the problem. In case of multiple knapsacks
 * one item can be contained in only one knapsack. Assumes
 * that every item can be selected for every knapsack.
 */
public class KnapsackProblem extends BaseProblemRepresentation {

  private List<Knapsack> knapsacks;

  // n'th value represents the number of a knapsack
  // that the n'th item is in
  private int[] selection;

  public KnapsackProblem(List<Knapsack> k) {
    knapsacks = k;
    selection = new int[k.get(0).getItems().size()];
  }

  public boolean isWithinCapacity() {
    for (int i = 0; i < knapsacks.size(); ++i) {
      if (getWeight(0) > knapsacks.get(i).getCapacity()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns weight of all selected items for a given Knapsack.
   *
   * @param n number of a Knapsack
   * @return weight of n'th Knapsack
   */
  public int getWeight(int n) {
    int weight = 0;
    Knapsack knapsack = getKnapsack(n);
    for (int i = 0; i < selection.length; ++i) {
      if (selection[i] == n + 1) {
        weight += knapsack.getItem(i).getWeight();
      }
    }
    return weight;
  }

  /**
   * Return the upper bound of each item selection.
   * Every item can be selected for every knapsack,
   * so it's simply a number of knapsacks + 1 (no knapsack selected).
   */
  public int getUpperBounds() {
    return knapsacks.size() + 1;
  }

  @Override
  public BaseProblemRepresentation cloneDeep() {
    KnapsackProblem problem = new KnapsackProblem(knapsacks);
    problem.setSelection(selection.clone());
    return problem;
  }

  @Override
  public int getNumGenes() {
    return selection.length;
  }

  @Override
  public BaseProblemRepresentation buildSolution(List<? extends Number> genes, ParameterSet<? extends Number, ? extends BaseProblemRepresentation> parameters) {
    for (int i = 0; i < selection.length; ++i) {
      selection[i] = genes.get(i).intValue();
    }
    this.setHashCode();
    return this;
  }

  @Override
  public void setHashCode() {
    this.hashCode = Arrays.hashCode(selection);
  }

  public int[] getSelection() {
    return selection;
  }

  public void setSelection(int[] selection) {
    this.selection = selection;
  }

  public List<Knapsack> getKnapsacks() {
    return knapsacks;
  }

  public void setKnapsacks(List<Knapsack> knapsacks) {
    this.knapsacks = knapsacks;
  }

  public Knapsack getKnapsack(int n) {
    return knapsacks.get(n);
  }
}
