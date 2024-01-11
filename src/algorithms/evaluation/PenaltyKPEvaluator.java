package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.mkp.Item;
import algorithms.problem.mkp.Knapsack;
import algorithms.problem.mkp.KnapsackProblem;

import java.util.List;

/**
 * Knapsack Problem evaluator that introduces
 * penalty to overweight knapsacks. Overweight backpacks
 * have their objective values reduced to 0.
 */
// TODO: maybe get a better name
public class PenaltyKPEvaluator<GENE extends Number> extends BaseKPEvaluator<GENE> {

  double penalty;

  public PenaltyKPEvaluator() {  }

  @Override
  public BaseEvaluator<GENE, KnapsackProblem> getCopy(BaseIndividual<GENE, KnapsackProblem> individual) {
    return new PenaltyKPEvaluator<>(individual);
  }

  public PenaltyKPEvaluator(BaseIndividual<GENE, KnapsackProblem> individual) {
    super(individual);
    penalty = getPenalty();
  }

  /**
   * Calculates the highest profit / weight ratio.
   *
   * @return penalty
   */
  // TODO: use somewhere else, a different evaluator perhaps?
  private double getPenalty() {
    double penalty = 0.0;
    double ratio;
    List<Knapsack> knapsacks = individual.getProblem().getKnapsacks();
    for (Knapsack knapsack : knapsacks) {
      for (Item item : knapsack.getItems()) {
        ratio = (double)item.getProfit() / (double)item.getWeight();
        if ((double)item.getProfit() / (double)item.getWeight() > penalty) {
          penalty = ratio;
        }
      }
    }

    return penalty;
  }

  @Override
  public double[] getObjectives() {
    KnapsackProblem problem = individual.getProblem();
    double[] objectives = new double[problem.getKnapsacks().size()];
    objectives = getProfit(problem, objectives);
    Knapsack knapsack;
    int overweight;

    for (int i = 0; i < objectives.length; ++i) {
      knapsack = problem.getKnapsack(i);
      overweight = problem.getWeight(i) - knapsack.getCapacity();
      if (overweight > 0) {
        objectives[i] = 0.0;
      }

      objectives[i] = knapsack.getMaxProfit() - objectives[i];
    }
    return objectives;
  }


  @Override
  public EvaluatorType getType() {
    return EvaluatorType.PENALTY_KNAPSACK_EVALUATOR;
  }

}
