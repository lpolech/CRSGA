package algorithms.evaluation;

import algorithms.problem.BaseIndividual;
import algorithms.problem.scheduling.Resource;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.Skill;
import algorithms.problem.scheduling.Task;

import java.util.Arrays;

/**
 * Base evaluator for schedules.
 * Has functions related to cost and duration.
 */
abstract public class BaseScheduleEvaluator<GENE extends Number> extends BaseEvaluator<GENE, Schedule> {

  protected double minCost;
  protected double minDuration;
  protected double maxCost;
  protected double maxDuration;

  public BaseScheduleEvaluator() { }

  public BaseScheduleEvaluator(BaseIndividual<GENE, Schedule> individual) {
    super(individual);
    setMaxValues();
  }

  public void setMaxValues() {
    minDuration = getMinDuration();
    minCost = getMinCost();
    maxDuration = getMaxDuration();
    maxCost = getMaxCost();
  }

  @Override
  public double[] getObjectives() {
    double[] objectives = new double[2];
    objectives[0] = getDuration();
    objectives[1] = getCost();
    return objectives;
  }

  @Override
  public double[] getNormalObjectives() {
    // TODO: - get min cost / duration ?
    double[] objectives = new double[2];
    objectives[0] = getDuration() / maxDuration;
    objectives[1] = getCost() / maxCost;
    return objectives;
  }

  /**
   * Gets total duration of the project, which is the latest finish
   * date of all resources.
   *
   * @return total duration of the project
   */
  public double getDuration() {
    Schedule schedule = individual.getProblem();
    int result = 0;
    Resource[] resources = schedule.getResources();
    for (Resource r : resources) {
      if (r.getFinish() > result) {
        result = r.getFinish();
      }
    }
    return result;
  }

  /**
   * Gets total cost of the project, which is the sum of all resources' salary
   * times the duration of the tasks they work on.
   *
   * @return total cost of the project
   */
  public double getCost() {
    Schedule schedule = individual.getProblem();
    double cost = 0;
    Task[] tasks = schedule.getTasks();
    for (Task t : tasks) {
      if (t.getResourceId() != -1) {
        cost += schedule.getResource(t.getResourceId()).getSalary()
            * t.getDuration();

      }

    }
    return  cost;
  }

  /**
   * Duration of the shortest task times number tasks divided by number of resources
   *
   * @return minimum possible duration of the individual
   */
  public double getMinDuration() {
    Schedule schedule = individual.getProblem();
    Task[] tasks = schedule.getTasks();
    Task shortest = tasks[0];
    for (Task t : tasks) {
      if (t.getDuration() < shortest.getDuration()) {
        shortest = t;
      }
    }

    return (shortest.getDuration() * tasks.length) / (double)schedule.getResources().length;
  }

  /**
   * Sums duration of all task of the individual.
   *
   * @return maximum possible duration of the individual
   */
  public double getMaxDuration() {
    Schedule schedule = individual.getProblem();
    int duration = 0;
    for (Task t : schedule.getTasks()) {
      duration += t.getDuration();
    }
    return duration;
  }

  /**
   * Sums cost of all tasks of the individual as if
   * the least expensive resource would work on them.
   *
   * @return minimum possible cost of the individual
   */
  public double getMinCost() {
    Schedule schedule = individual.getProblem();
    Resource[] resources = schedule.getResources();
    Resource expRes = resources[0];
    for (Resource r : resources) {
      if (r.getSalary() < expRes.getSalary()) {
        expRes = r;
      }
    }
    int maxCost = 0;
    for (Task t : schedule.getTasks()) {
      maxCost += (t.getDuration() * expRes.getSalary());
    }
    return maxCost;
  }

  /**
   * Sums cost of all tasks of the individual as if
   * the most expensive resource would work on them.
   *
   * @return maximum possible cost of the individual
   */
  public double getMaxCost() {
    Schedule schedule = individual.getProblem();
    Resource[] resources = schedule.getResources();
    Resource expRes = resources[0];
    for (Resource r : resources) {
      if (r.getSalary() > expRes.getSalary()) {
        expRes = r;
      }
    }
    int maxCost = 0;
    for (Task t : schedule.getTasks()) {
      maxCost += (t.getDuration() * expRes.getSalary());
    }
    return maxCost;
  }

  protected double getAverageUseOfResourceTime() {
    Schedule schedule = individual.getProblem();
    Resource[] resources = schedule.getResources();
    double expectedUsedTime = maxDuration / (double) resources.length;
    double sum = 0.0d;
    for (Resource r : resources) {
      sum += Math.abs(r.getWorkingTime() - expectedUsedTime);
    }
    return sum;
  }

  public double getMaxUseOfResourceTime() {
    Schedule schedule = individual.getProblem();
    Resource[] resources = schedule.getResources();
    double expectedUsedTime = maxDuration / (double) resources.length;
    return expectedUsedTime * (resources.length - 1);
  }

  protected double getAverageCashFlowDeviation(double duration) {
    Schedule schedule = individual.getProblem();
    double[] cashFlows = new double[(int)duration];
    Task[] tasks = schedule.getTasks();
    double cashFlow;
    for (Task t : tasks) {
      cashFlow = schedule.getResource(t.getResourceId()).getSalary();
      if (t.getStart() > -1) {
        int finish = t.getStart() + t.getDuration();
        for (int i = t.getStart(); i < finish; ++i) {
          cashFlows[i] += cashFlow;
        }
      }
    }

    double average = Arrays.stream(cashFlows).average().orElse(Double.NaN);
    double deviation = 0.0d;
    for (double flow : cashFlows) {
      deviation += Math.abs(flow - average);
    }
    return deviation;
  }

  protected double getSkillOveruse() {
    Schedule schedule = individual.getProblem();
    double overuse = 0.0d;
    Task[] tasks = schedule.getTasks();
    for (Task t : tasks) {
      Resource r = schedule.getResource(t.getResourceId());
      for (Skill tSkill : t.getRequiredSkills()) {
        Skill rSkill = Arrays.stream(r.getSkills()).filter(s -> s.getType().equals(tSkill.getType())).findFirst().orElse(null);
        if (rSkill == null) {
          throw new IllegalStateException("Incorrect task - resource assignment");
        }
        overuse += rSkill.getLevel() - tSkill.getLevel();
      }

    }
    return  overuse;
  }

  public double getMaxSkillOveruse() {
    Schedule schedule = individual.getProblem();
    Task[] tasks = schedule.getTasks();
    double maxOveruse = 0.0d;
    for (Task t : tasks) {
      for (Skill tSkill : t.getRequiredSkills()) {

        int maxLevel = 0;
        for (Resource r : schedule.getResources()) {
          Skill rSkill = Arrays.stream(r.getSkills()).filter(s -> s.getType().equals(tSkill.getType())).findFirst().orElse(null);
          if (rSkill != null && rSkill.getLevel() > maxLevel) {
            maxLevel = rSkill.getLevel();
          }
        }
        maxOveruse += maxLevel - tSkill.getLevel();

      }
    }
    return maxOveruse;
  }

  public double getMaxAverageCashFlowDeviation() {
    return maxCost;
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
    BaseIndividual<GENE, Schedule> nadirPoint = new BaseIndividual<>(schedule, this);

    double[] objectives = new double[2];
    objectives[0] = getMaxDuration();
    objectives[1] = getMaxCost();
    nadirPoint.setObjectives(objectives);

    objectives = new double[2];
    objectives[0] = 1.0;
    objectives[1] = 1.0;
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

    double[] objectives = new double[2];
    objectives[0] = getMinDuration();
    objectives[1] = getMinCost();
    perfectPoint.setObjectives(objectives);

    double[] normalObjectives = new double[2];
    normalObjectives[0] = objectives[0] / getMaxDuration();
    normalObjectives[1] = objectives[1] / getMaxCost();
    perfectPoint.setNormalObjectives(normalObjectives);

    return perfectPoint;
  }

  /**
   * Creates an extreme point. It contains the best possible value
   * of duration and worst possible value of cost.
   *
   * @return perfect point
   */
  public BaseIndividual<GENE, Schedule> getExtremeDurationPoint() {
    Schedule schedule = individual.getProblem();
    BaseIndividual<GENE, Schedule> perfectPoint = new BaseIndividual<GENE, Schedule>(schedule, this);

    double[] objectives = new double[2];
    objectives[0] = minDuration;
    objectives[1] = maxCost;
    perfectPoint.setObjectives(objectives);

    double[] normalObjectives = new double[2];
    normalObjectives[0] = objectives[0] / maxDuration;
    normalObjectives[1] = 1.0;
    perfectPoint.setNormalObjectives(normalObjectives);

    return perfectPoint;

  }

  /**
   * Creates an extreme point. It contains the best possible value
   * of cost and worst possible value of duratoin.
   *
   * @return perfect point
   */
  public BaseIndividual<GENE, Schedule> getExtremeCostPoint() {
    Schedule schedule = individual.getProblem();
    BaseIndividual<GENE, Schedule> perfectPoint = new BaseIndividual<GENE, Schedule>(schedule, this);

    double[] objectives = new double[2];
    objectives[0] = maxDuration;
    objectives[1] = minCost;
    perfectPoint.setObjectives(objectives);

    double[] normalObjectives = new double[2];
    normalObjectives[0] = 1.0;
    normalObjectives[1] = objectives[1] / maxCost;
    perfectPoint.setNormalObjectives(normalObjectives);

    return perfectPoint;

  }

}
