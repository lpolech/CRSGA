package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;
import algorithms.problem.mkp.Knapsack;

/**
 * Base evaluator for Travelling Salesman Problem.
 * Has functions calculating times of travel and profits of selected items.
 */
public abstract class BaseTTPEvaluator<GENE extends Number> extends BaseEvaluator<GENE, TTP> {

  public BaseTTPEvaluator() { }

  public BaseTTPEvaluator(BaseIndividual<GENE, TTP> individual) {
    super(individual);
  }

  protected double getProfit() {
    double profit = 0d;
    TTP problem = individual.getProblem();
    Knapsack knapsack = problem.getKnapsack();
    int[] selection = problem.getSelection();
    for (int i = 0; i < selection.length; ++i) {
      if (selection[i] > 0) {
        profit += knapsack.getItem(i).getProfit();
      }
    }
    return profit;
  }

}
