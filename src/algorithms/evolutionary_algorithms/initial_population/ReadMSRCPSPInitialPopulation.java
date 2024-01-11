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

public class ReadMSRCPSPInitialPopulation<PROBLEM extends BaseProblemRepresentation> extends BaseInitialPopulation<Double, PROBLEM> {

  private String inputFile;
  private static final Logger LOGGER = Logger.getLogger(ReadMSRCPSPInitialPopulation.class.getName());

  /**
   * Creates a a population by reading it from an input file
   *
   * @param problem problem, for which to generate a population
   * @param populationSize size of the population
   * @param evaluator list of evaluators
   * @param parameters set of parameters
   * @return population - list of individuals
   */
  @Override
  public List<BaseIndividual<Double, PROBLEM>> generate(PROBLEM problem, int populationSize,
                                                      BaseEvaluator<Double, PROBLEM> evaluator, ParameterSet<Double, PROBLEM> parameters) {

    List<BaseIndividual<Double, PROBLEM>> population = new ArrayList<>(populationSize);
    int numGenes = problem.getNumGenes();

    BufferedReader solutionReader = null;
    try {
      solutionReader = new BufferedReader(new FileReader(inputFile));
      String line;

      while ((line = solutionReader.readLine()) != null) {
        List<Double> genes = new ArrayList<>();
        String[] parts = line.split(" ");
        for (int i = 0; i < parts.length; ++i) {
          double gene = Double.parseDouble(parts[i]);
          genes.add(gene);
        }
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
    List<Double> genes = new ArrayList<>(Collections.nCopies(numGenes, null));
    while (population.size() < populationSize) {
      for (int j = 0; j < numGenes; ++j) {
        genes.set(j, parameters.random.next(parameters.upperBounds[j]));
      }
      population.add(new BaseIndividual<>(problem, genes, evaluator));
    }
    return population;
  }

  public void setInputFile(String file) {
    this.inputFile = file;
  }
}
