package algorithms.evolutionary_algorithms.crossover;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseProblemRepresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of binomial crossover.
 * Creates trial vector by choosing each gene from donor
 * with probability cr or by choosing gene from target.
 */
public class BinomialCrossover<GENE extends Number> extends BaseCrossover<GENE, BaseProblemRepresentation> {

  /**
   * Performs a binomial crossover.
   *
   * @param cr cross over rate
   * @param target target genes, probability to be chosen
   *               is 1 - cr
   * @param donor donor genes, probability to be chosen is cr
   * @param parameters set of parameters
   * @return resulting array of genes
   */
  @Override
  public List<List<GENE>> crossover(double cr, List<GENE> target, List<GENE> donor, ParameterSet<GENE, BaseProblemRepresentation> parameters) {
    List<GENE> trial = new ArrayList<>(Collections.nCopies(target.size(), null));
    for (int i = 0; i < target.size(); ++i) {
      if (parameters.random.nextDouble() < cr) {
        trial.set(i, donor.get(i));
      } else {
        trial.set(i, target.get(i));
      }
    }
    // Ensure that at least one gene will be taken from mutant
    int single = parameters.random.nextInt(target.size());
    trial.set(single, donor.get(single));

    List<List<GENE>> result = new ArrayList<>();
    result.add(trial);

    return result;
  }

}
