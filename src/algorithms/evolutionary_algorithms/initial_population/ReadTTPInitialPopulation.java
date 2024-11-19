package algorithms.evolutionary_algorithms.initial_population;

import algorithms.evaluation.BaseEvaluator;
import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadTTPInitialPopulation<PROBLEM extends BaseProblemRepresentation> extends BaseInitialPopulation<Integer, PROBLEM> {

  private static final String solutionFile = "competition/ntga_pla33810_n33809.x";
  private static final Logger LOGGER = Logger.getLogger(ReadTTPInitialPopulation.class.getName());

  /**
   * Creates a a population by randomizing
   * each individual
   *
   * @param problem problem, for which to generate a population
   * @param populationSize size of the population
   * @param evaluator list of evaluators
   * @param parameters set of parameters
   * @return population - list of individuals
   */
  @Override
  public List<BaseIndividual<Integer, PROBLEM>> generate(PROBLEM problem, int populationSize,
                                                      BaseEvaluator<Integer, PROBLEM> evaluator, ParameterSet<Integer, PROBLEM> parameters) {
    List<BaseIndividual<Integer, PROBLEM>> population = new ArrayList<>(populationSize);

    BufferedReader solutionReader = null;
    try {
      solutionReader = new BufferedReader(new FileReader(solutionFile));
      String line;

      while ((line = solutionReader.readLine()) != null) {
        List<Integer> genes = new ArrayList<>();
        String[] parts = line.split(" ");
        for (int i = 0; i < parts.length; ++i) {
          genes.add(Integer.parseInt(parts[i]));
        }

        line = solutionReader.readLine();
        parts = line.split(" ");
        for (int i = 0; i < parts.length; ++i) {
          genes.add(Integer.parseInt(parts[i]));
        }
        solutionReader.readLine();
        population.add(new BaseIndividual<>(problem, genes, evaluator));
      }
    } catch (IOException e) {
      LOGGER.log(Level.FINE, e.toString());
    } finally {
      try {
        if (null != solutionReader) {
          solutionReader.close();
        }
      } catch (IOException e) {
        LOGGER.log(Level.FINE, e.toString());
      }
    }

    int numGenes = problem.getNumGenes();
    int splitPoint = parameters.geneSplitPoint;

    List<Integer> genes = new ArrayList<>(Collections.nCopies(numGenes, null));
    while (population.size() < populationSize) {

      for (int j = 0; j < splitPoint; ++j) {
        genes.set(j, j);
      }
      Collections.shuffle(genes.subList(1, splitPoint), parameters.random.getRandom());

      for (int j = splitPoint; j < numGenes; ++j) {
        genes.set(j, parameters.random.next(parameters.upperBounds[j]));
      }

      population.add(new BaseIndividual<>(problem, genes, evaluator));
    }

    return population;
  }

}
