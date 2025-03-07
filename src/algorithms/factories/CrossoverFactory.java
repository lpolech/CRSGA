package algorithms.factories;


import algorithms.evolutionary_algorithms.crossover.*;

public class CrossoverFactory {

  /**
   * Creates crossover based on provided type.
   * Default: {@link BinomialCrossover}
   *
   * @param type type to use
   * @return chosen crossover method
   */
  public BaseCrossover createCrossover(CrossoverType type) {
    switch (type) {
      case BINOMIAL:
        return new BinomialCrossover();
      case EXPONENTIAL:
        return new ExponentialCrossover();
      case SINGLE_POINT:
        return new SinglePointCrossover();
      case TTP_ORDERED:
        return new TTPOrderedCrossover();
      case UNIFORM:
        return new UniformCrossover();
      case TTP_COMPETITION:
        return new TTP_CompetitionCrossover();
      case TTP:
        return new TTPCrossover();
      default:
        return new BinomialCrossover();
    }
  }

}
