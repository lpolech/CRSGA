package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.mkp.Knapsack;
import algorithms.problem.mkp.KnapsackProblem;

/**
 * Evaluator for a Knapsack Problem
 */
public class BaseKPEvaluator<GENE extends Number> extends BaseEvaluator<GENE, KnapsackProblem> {

  public BaseKPEvaluator() { }

  public BaseKPEvaluator(BaseIndividual<GENE, KnapsackProblem> individual) {
    super(individual);
  }

  @Override
  public double evaluate() {
    return getNormalObjectives()[0];
  }

  @Override
  public BaseEvaluator<GENE, KnapsackProblem> getCopy(BaseIndividual<GENE, KnapsackProblem> individual) {
    return new BaseKPEvaluator<>(individual);
  }

  @Override
  public double[] getObjectives() {
    KnapsackProblem problem = individual.getProblem();
    double[] objectives = new double[problem.getKnapsacks().size()];
    objectives = getProfit(problem, objectives);
    for (int i = 0; i < objectives.length; ++i) {
      objectives[i] = problem.getKnapsack(i).getMaxProfit() - objectives[i];
    }
    return objectives;
  }

  protected double[] getProfit(KnapsackProblem problem, double[] objectives) {
    int[] selection = problem.getSelection();
    Knapsack knapsack;
    for (int i = 0; i < selection.length; ++i) {
      if (selection[i] == 0) {
        // item isn't selected for any backpack
        continue;
      }
      knapsack = problem.getKnapsack(selection[i] - 1);
      objectives[selection[i] - 1] += knapsack.getItem(i).getProfit();
    }
    return objectives;
  }

  @Override
  public double[] getNormalObjectives() {
    double[] objectives = getObjectives();
    for (int i = 0; i < objectives.length; ++i) {
      objectives[i] /= individual.getProblem().getKnapsack(i).getMaxProfit();
    }
    return objectives;
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.BASE_KNAPSACK_EVALUATOR;
  }

  @Override
  public BaseIndividual getNadirPoint() {
    return null;
  }

  @Override
  public BaseIndividual getPerfectPoint() {
    return null;
  }

  @Override
  public int getNumObjectives() {
    return individual.getProblem().getKnapsacks().size();
  }

}
