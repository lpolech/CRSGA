package algorithms.factories;


import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.mutation.*;

public class MutationFactory {

  private ParameterSet parameters;

  public MutationFactory(ParameterSet parameters) {
    this.parameters = parameters;
  }

  /**
   * Creates mutation based on provided type.
   * Default: {@link RandomVectorMutation}
   *
   * @param type type to use
   * @return chosen mutation method
   */
  public BaseMutation createMutation(MutationType type) {
    switch (type) {
      case BEST:
        return new BestVectorMutation();
      case CURRENT:
        return new CurrentVectorMutation();
      case RANDOM_BIT:
        return new RandomBitMutation();
      case RANDOM_VECTOR:
        return new RandomVectorMutation();
      case NON_DOMINATED_RANDOM_VECTOR:
        return new NonDominatedRandomVectorMutation();
      case TTP_SWAP_BIT:
        return new TTPSwapBitMutation();
      case TTP_COMPETITION:
        return new TTPCompetitionMutation();
      case TTP:
        return new TTPMutation();
      default:
        return new RandomVectorMutation();
    }
  }

}
