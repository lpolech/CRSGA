package runners.ttp;

import algorithms.brute_force.TTPExhaustiveSearch;
import algorithms.evaluation.EvaluatorType;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.factories.EvaluatorFactory;
import algorithms.io.TTPIO;
import algorithms.problem.BaseIndividual;
import algorithms.problem.TTP;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExhaustiveRunner {

  private static final Logger LOGGER = Logger.getLogger( ExhaustiveRunner.class.getName() );
  private static final String definitionFile = "assets/definitions/TTP/student/trivial_0.ttp";

  public static void main(String[] args) {
    run(args);
  }

  private static List<BaseIndividual<Integer, TTP>> run(String[] args) {
    TTPIO reader = new TTPIO();
    TTP ttp = reader.readDefinition(definitionFile);
    if (null == ttp) {
      LOGGER.log(Level.WARNING, "Could not read the Definition " + definitionFile);
      return null;
    }
    ParameterSet<Integer, TTP> parameters = new ParameterSet<>();
    parameters.upperBounds = ttp.getUpperBounds();
    parameters.evaluator = new EvaluatorFactory().createEvaluator(EvaluatorType.MULTI_OBJECTIVE_TTP_EVALUATOR, parameters.evalRate);

    TTPExhaustiveSearch exhaustiveSearch = new TTPExhaustiveSearch(ttp, parameters);
    List<BaseIndividual<Integer, TTP>> nonDominated = exhaustiveSearch.findNonDominated();
    return nonDominated;
  }

}
