package algorithms.evaluation;


import algorithms.problem.BaseIndividual;
import algorithms.problem.maop.MaOP1;

public class MaOP1Evaluator<GENE extends Double> extends BaseEvaluator<GENE, MaOP1> {

  @Override
  public double evaluate() {
    return 0;
  }

  public MaOP1Evaluator() { }

  public MaOP1Evaluator(BaseIndividual<GENE, MaOP1> individual) {
    super(individual);
  }

  @Override
  public BaseEvaluator<GENE, MaOP1> getCopy(BaseIndividual<GENE, MaOP1> individual) {
    return new MaOP1Evaluator<>(individual);
  }

  @Override
  public double[] getObjectives() {
    MaOP1 problem = individual.getProblem();
    double[] scalingFactors = problem.getScalingFactors();
    int numObjectives = problem.getNumObjectives();
    double[] objectives = new double[numObjectives];
    for (int i = 0; i < numObjectives; ++i) {
      objectives[i] = scalingFactors[i] * problem.getShape(i) * (1 + problem.getDistance(i));
    }
    return objectives;
  }

  @Override
  public double[] getNormalObjectives() {
    double[] objectives = getObjectives();
    // For now I assume it's the same
    return objectives;
  }

  @Override
  public EvaluatorType getType() {
    return EvaluatorType.MAOP1_EVALUATOR;
  }

  @Override
  public BaseIndividual getNadirPoint() {
    MaOP1 problem = individual.getProblem();
    BaseIndividual<GENE, MaOP1> perfectPoint = new BaseIndividual<>(problem, this);

    double[] objectives = new double[2];
    objectives[0] = 1.0;
    objectives[1] = 1.0;
    perfectPoint.setObjectives(objectives);
    perfectPoint.setNormalObjectives(objectives);

    return perfectPoint;
  }

  @Override
  public BaseIndividual getPerfectPoint() {
    MaOP1 problem = individual.getProblem();
    BaseIndividual<GENE, MaOP1> perfectPoint = new BaseIndividual<>(problem, this);

    double[] objectives = new double[2];
    objectives[0] = 0.0;
    objectives[1] = 0.0;
    perfectPoint.setObjectives(objectives);
    perfectPoint.setNormalObjectives(objectives);

    return perfectPoint;
  }

  @Override
  public int getNumObjectives() {
    return 2;
  }

}
