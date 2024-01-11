package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Schedule;

public class FourScheduleEvaluator<GENE extends Number> extends BaseScheduleEvaluator<GENE> {

  protected double maxAverageCashFlowDeviation;
  protected double maxSkillOveruse;

  public FourScheduleEvaluator() {
    super();
  }

  public FourScheduleEvaluator(BaseIndividual<GENE, Schedule> individual) {
    super(individual);
    setMaxValues();
  }

  @Override
  public void setMaxValues() {
    super.setMaxValues();
    maxAverageCashFlowDeviation = getMaxAverageCashFlowDeviation();
    maxSkillOveruse = getMaxSkillOveruse();
  }

  @Override
  public double evaluate() {
    return 0.0d;
  }

  @Override
  public double[] getObjectives() {
    double[] objectives = new double[4];
    double duration = getDuration();
    objectives[0] = duration;
    objectives[1] = getCost();
    objectives[2] = getAverageCashFlowDeviation(duration);
    objectives[3] = getSkillOveruse();
    return objectives;
  }

  @Override
  public double[] getNormalObjectives() {
    // TODO: - get min cost / duration ?
    double[] objectives = new double[4];
    double duration = getDuration();
    objectives[0] = duration / maxDuration;
    objectives[1] = getCost() / maxCost;
    objectives[2] = getAverageCashFlowDeviation(duration) / maxAverageCashFlowDeviation;
    objectives[3] = getSkillOveruse() / maxSkillOveruse;
    return objectives;
  }

  /**
   * Creates a Nadir point. It contains the worst possible values
   * of all criteria.
   *
   * @return Nadir point
   */
  @Override
  public BaseIndividual<GENE, Schedule> getNadirPoint() {
    Schedule schedule = individual.getProblem();
    BaseIndividual<GENE, Schedule> nadirPoint = new BaseIndividual<GENE, Schedule>(schedule, this);

    double[] objectives = new double[4];
    objectives[0] = getMaxDuration();
    objectives[1] = getMaxCost();
    objectives[2] = getMaxAverageCashFlowDeviation();
    objectives[3] = getMaxSkillOveruse();
    nadirPoint.setObjectives(objectives);

    objectives = new double[4];
    objectives[0] = 1.0d;
    objectives[1] = 1.0d;
    objectives[2] = 1.0d;
    objectives[3] = 1.0d;
    nadirPoint.setNormalObjectives(objectives);

    return nadirPoint;
  }

  /**
   * Creates a perfect point. It contains the best possible values
   * of all criteria.
   *
   * @return perfect point
   */
  @Override
  public BaseIndividual<GENE, Schedule> getPerfectPoint() {
    Schedule schedule = individual.getProblem();
    BaseIndividual<GENE, Schedule> perfectPoint = new BaseIndividual<GENE, Schedule>(schedule, this);

    double[] objectives = new double[4];
    objectives[0] = this.getMinDuration();
    objectives[1] = this.getMinCost();
    objectives[2] = 0.0d;
    objectives[3] = 0.0d;
    perfectPoint.setObjectives(objectives);

    double[] normalObjectives = new double[4];
    normalObjectives[0] = objectives[0] / this.getMaxDuration();
    normalObjectives[1] = objectives[1] / this.getMaxCost();
    normalObjectives[2] = 0.0d;
    normalObjectives[3] = 0.0d;
    perfectPoint.setNormalObjectives(normalObjectives);

    return perfectPoint;
  }

  @Override
  public FourScheduleEvaluator getCopy(BaseIndividual<GENE, Schedule> individual) {
    return new FourScheduleEvaluator<>(individual);
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.FOUR_SCHEDULE_EVALUATOR;
  }

  @Override
  public int getNumObjectives() {
    return 4;
  }

}
