package algorithms.factories;


import algorithms.evaluation.*;

public class EvaluatorFactory {

  /**
   * Creates evaluator based on provided type.
   * Default: Throws error if no correct type is provided,
   * default value would make no sense, as there are different
   * evaluators for different problems
   *
   * @param type type to use
   * @return chosen crossover method
   */
  public BaseEvaluator createEvaluator(EvaluatorType type, double evalRate) {
    switch (type) {
      case DURATION_EVALUATOR:
        return new DurationEvaluator();
      case COST_EVALUATOR:
        return new CostEvaluator();
      case AVERAGE_CASH_FLOW_DEVIATION_EVALUATOR:
        return new AverageCashFlowDeviationEvaluator();
      case SKILL_OVERUSE_EVALUATOR:
        return new SkillOveruseEvaluator();
      case AVERAGE_USE_OF_RESOURCE_TIME_EVALUATOR:
        return new AverageUseOfResourceTimeEvaluator();
      case WEIGHTED_EVALUATOR:
        return new WeightedEvaluator(evalRate);
      case THREE_SCHEDULE_EVALUATOR:
        return new ThreeScheduleEvaluator();
      case FOUR_SCHEDULE_EVALUATOR:
        return new FourScheduleEvaluator();
      case FIVE_SCHEDULE_EVALUATOR:
        return new FiveScheduleEvaluator();
      case WEIGHTED_FIVE_SCHEDULE_EVALUATOR:
        return new WeightedFiveScheduelEvaluator();
      case BASE_TSP_EVALUATOR:
        return new BaseTSPEvaluator();
      case MAOP1_EVALUATOR:
        return new MaOP1Evaluator();
      case BASE_KNAPSACK_EVALUATOR:
        return new BaseKPEvaluator();
      case PENALTY_KNAPSACK_EVALUATOR:
        return new PenaltyKPEvaluator();
      case SINGLE_OBJECTIVE_TTP_EVALUATOR:
        return new SOTTPEvaluator();
      case MULTI_OBJECTIVE_TTP_EVALUATOR:
        return new MOTTPEvaluator();
      case COMPETITION_EVALUATOR:
        return new CompetitionEvaluator();
      case EXPERIMENTAL_SCHEDULE:
        return new ExperimentalScheduleEvaluator();
      case BASE_SCHEDULE_EVALUATOR:
        return new BiScheduleEvaluator();
      default:
        throw new IllegalArgumentException("Please provide the correct enum value from EvaluatorType enum");
    }
  }

}
