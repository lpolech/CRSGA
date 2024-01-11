package algorithms.evolutionary_algorithms.genetic_algorithm;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.selection.BaseSelection;
import algorithms.evolutionary_algorithms.selection.DiversitySelection;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.problem.TTP;
import algorithms.problem.mkp.Item;
import algorithms.quality_measure.HVMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompetitionGA<PROBLEM extends BaseProblemRepresentation> extends GeneticAlgorithm<PROBLEM> {

  private NondominatedSorter<BaseIndividual<Integer, PROBLEM>> sorter;
  private boolean enhanceDiversity;
  private double diversityThreshold;


  public CompetitionGA(PROBLEM problem, int populationSize, int generationLimit,
                                  ParameterSet<Integer, PROBLEM> parameters, double mutationProbability,
                                  double crossoverProbability, double diversityTreshold, boolean enhanceDiversity) {
    super(problem, populationSize, generationLimit, parameters,
        mutationProbability, crossoverProbability);
    sorter = new NondominatedSorter<>();
    this.diversityThreshold = diversityTreshold;
    this.enhanceDiversity = enhanceDiversity;
  }

  public List<BaseIndividual<Integer, PROBLEM>> optimize() {
    int generation = 1;
    int generationDiversityThreshold = (int)(this.populationSize * diversityThreshold);
    BaseIndividual<Integer, PROBLEM> best;
    List<BaseIndividual<Integer, PROBLEM>> newPopulation;
    List<BaseIndividual<Integer, PROBLEM>> combinedPopulations = new ArrayList<>();

    BaseIndividual<Integer, PROBLEM> firstParent;
    BaseIndividual<Integer, PROBLEM> secondParent;
    BaseIndividual<Integer, PROBLEM> firstChild;
    BaseIndividual<Integer, PROBLEM> secondChild;
    List<List<Integer>> children;

    BaseSelection<Integer, PROBLEM> selection;
    selection = new DiversitySelection();
    ((DiversitySelection<Integer>) selection).setTournamentSize(6);

    population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);

    for (BaseIndividual<Integer, PROBLEM> individual : population) {
      individual.buildSolution(individual.getGenes(), parameters);
    }
    sorter.nondominatedSorting(population);
    sorter.crowdingDistance(population);
    best = findBestIndividual(population);
    combinedPopulations.addAll(population);
    combinedPopulations = removeDuplicates(combinedPopulations);
    combinedPopulations = getNondominated(combinedPopulations);

    while (generation < generationLimit) {
      newPopulation = new ArrayList<>();
      sorter.nondominatedSorting(population);
      sorter.crowdingDistance(population);

      while (newPopulation.size() < populationSize) {

        if (!enhanceDiversity || /*newPopulation.size() < generationDiversityThreshold*/ generation % 100 < 50) {
          firstParent = parameters.selection.select(population, combinedPopulations, newPopulation.size(), null, null, parameters);
          do {
            secondParent = parameters.selection.select(population, combinedPopulations, newPopulation.size(), firstParent, null, parameters);
          } while (firstParent == secondParent);
        } else {
          crowdingDistance(combinedPopulations);
          //          java.util.Collections.sort(combinedPopulations, new java.util.Comparator<BaseIndividual<Integer, PROBLEM>>() {
          //            public int compare(BaseIndividual<Integer, PROBLEM> o1, BaseIndividual<Integer, PROBLEM> o2) {
          //              return Double.compare(o2.getDistance(), o1.getDistance()); // descending
          //            }
          //          });
          firstParent = selection.select(population, combinedPopulations, newPopulation.size(), null, null, parameters);
//          secondParent = combinedPopulations.get(combinedPopulations.size() - 1);
          //          secondParent = selection.select(population, combinedPopulations, newPopulation.size(), firstParent, null, parameters);
          int index = combinedPopulations.indexOf(firstParent);
          int random = parameters.random.nextInt() % 2;
          if (random == 0) {
            index = index - 1;
          } else {
            index = index + 1;
          }
          if (index < 0 || index > combinedPopulations.size() - 1) {
            do {
              secondParent = selection.select(population, combinedPopulations, newPopulation.size(), null, null, parameters);
            } while (firstParent == secondParent);
          } else {
            secondParent = combinedPopulations.get(index);
          }
        }

        children = parameters.crossover.crossover(crossoverProbability, firstParent.getGenes(), secondParent.getGenes(), parameters);

        children.set(0, parameters.mutation.mutate(population, mutationProbability, children.get(0), 0, populationSize, parameters));
        children.set(1, parameters.mutation.mutate(population, mutationProbability, children.get(1), 0, populationSize, parameters));

        firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
        secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);

        // TODO: refactor after I get well :(
        firstChild.buildSolution(firstChild.getGenes(), parameters);
        final BaseIndividual<Integer, PROBLEM> fc = firstChild;
        for (int i = 0; newPopulation.stream().anyMatch(ind -> isDuplicate(ind, fc)) && i < 20; i++) {
          firstChild.setGenes(parameters.mutation.mutate(population, 1.0, firstChild.getGenes(), 0, populationSize, parameters));
          firstChild.buildSolution(firstChild.getGenes(), parameters);
        }
        if (!newPopulation.contains(firstChild)) {
          newPopulation.add(firstChild);
        }
        secondChild.buildSolution(secondChild.getGenes(), parameters);
        final BaseIndividual<Integer, PROBLEM> sc = secondChild;
        for (int i = 0; newPopulation.stream().anyMatch(ind -> isDuplicate(ind, sc)) && i < 20; i++) {
          secondChild.setGenes(parameters.mutation.mutate(population, 1.0, secondChild.getGenes(), 0, populationSize, parameters));
          firstChild.buildSolution(firstChild.getGenes(), parameters);
        }
        if (!newPopulation.contains(secondChild)) {
          newPopulation.add(secondChild);
        }

      }
      population = newPopulation;
      combinedPopulations.addAll(population);
      combinedPopulations = removeDuplicates(combinedPopulations);
      combinedPopulations = getNondominated(combinedPopulations);

//      if (generation % 100 == 0) {
//        combinedPopulations = selectMostEffective(combinedPopulations);
//        java.io.PrintStream xFile = null;
//        java.io.PrintStream fFile = null;
//        try {
//          xFile = new java.io.PrintStream(new java.io.File("inc_fnl4461_n44600_" + generation + ".x"));
//          for (BaseIndividual<Integer, PROBLEM> ind : combinedPopulations) {
//            BaseIndividual<Integer, algorithms.problem.TTP> indToSave = (BaseIndividual<Integer, algorithms.problem.TTP>)ind;
//            for (int i = 0; i < indToSave.getProblem().getPath().length; ++i) {
//              xFile.print(indToSave.getProblem().getPath()[i] + " ");
//            }
//            xFile.println();
//            for (int i = 0; i < indToSave.getProblem().getSelection().length; ++i) {
//              xFile.print(indToSave.getProblem().getSelection()[i] + " ");
//            }
//            xFile.println();
//            xFile.println();
//          }
//          fFile = new java.io.PrintStream(new java.io.File("inc_fnl4461_n44600_" + generation + ".f"));
//          for (BaseIndividual<Integer, PROBLEM> ind : combinedPopulations) {
//            fFile.println(ind.getObjectives()[0] + " " + -ind.getObjectives()[1]);
//          }
//        } catch (java.io.FileNotFoundException e) {
//          e.printStackTrace();
//        } finally {
//          if (xFile != null) {
//            xFile.close();
//          }
//          if (fFile != null) {
//            fFile.close();
//          }
//        }
//      }

      ++generation;
      System.out.println(generation);
    }

    combinedPopulations = removeDuplicates(combinedPopulations);
    List<BaseIndividual<Integer, PROBLEM>> pareto = getNondominated(combinedPopulations);
    pareto = selectMostEffective(pareto);

//    BaseIndividual<Integer, PROBLEM> selected = pareto.get(20);
//
//    for (int i = 1; i < parameters.geneSplitPoint; ++i) {
//      int city = selected.getGenes().get(i);
//      for (int j = 0; j < 5; ++j) {
//        int gene = city + 279 * j - 1;
//        double ratio = ((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getProfit()
//            / (double)((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getWeight();
//        System.out.print(((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getProfit() +
//            " " + ((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getWeight() +
//            " " + selected.getGenes().get(parameters.geneSplitPoint + gene) + "              ");
//      }
//      System.out.println();
//    }

    if (generationLimit == 0) {
      int itemNum = 1;
      int cityNum = 33809;
      int counter = 0;
      for (BaseIndividual<Integer, PROBLEM> selected : pareto) {
        TTP ttp = (TTP)selected.getProblem();
        double[][] distances = ttp.getDistanceMatrix().getDistances();

//      for (int i = 1; i < parameters.geneSplitPoint; ++i) {
//        for (int j = 2; j < parameters.geneSplitPoint - i + 1; ++j) {
//
//          int headStart = i - 1;
//          int headEnd = i;
//          int tailStart = i - 1 + j;
//          int tailEnd = i + j;
//          if (i + j == parameters.geneSplitPoint) {
//            tailEnd = 0;
//          }
//
//          double currentDistance = distances[selected.getGenes().get(headStart)][selected.getGenes().get(headEnd)]
//              + distances[selected.getGenes().get(tailStart)][selected.getGenes().get(tailEnd)];
//          double potentialDistance = distances[selected.getGenes().get(headStart)][selected.getGenes().get(tailStart)]
//              + distances[selected.getGenes().get(headEnd)][selected.getGenes().get(tailEnd)];
//          boolean hasItems = false;
//          for (int k = headEnd; k < tailStart; ++k) {
//            int city = selected.getGenes().get(k);
//            for (int l = 0; l < itemNum; ++l) {
//              int gene = city + cityNum * l - 1;
//              if (selected.getGenes().get(parameters.geneSplitPoint + gene) == 1) {
//                hasItems = true;
//              }
//            }
//          }
//          if (potentialDistance < currentDistance && !hasItems) {
//
//            while (headEnd < tailStart) {
//
//              List<Integer> genes = selected.getGenes();
//              int temp = genes.get(headEnd);
//              genes.set(headEnd, genes.get(tailStart));
//              genes.set(tailStart, temp);
//
//              ++headEnd;
//              --tailStart;
//            }
//
//          }
//        }
//      }


      for (int i = 1; i < parameters.geneSplitPoint; ++i) {
        int city = selected.getGenes().get(i);
        for (int j = 0; j < itemNum; ++j) {
          int gene = city + cityNum * j - 1;
          if (selected.getGenes().get(parameters.geneSplitPoint + gene) == 1) {
            Item item = ((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene);
            double profit = item.getProfit();
            double weight = item.getWeight();

            boolean found = false;
            List<NewItem> newItems = new ArrayList<>(5);
            for (int k = parameters.geneSplitPoint - 1; k > i && !found; --k) {

              int newCity = selected.getGenes().get(k);
              for (int l = 0; l < itemNum && !found; ++l) {
                int newGene = newCity + cityNum * l - 1;
                Item newItem = ((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(newGene);
                if (selected.getGenes().get(parameters.geneSplitPoint + newGene) == 1) {
                  continue;
                }
                double newProfit = newItem.getProfit();
                double newWeight = newItem.getWeight();

                NewItem n = new NewItem();
                n.gene = newGene;
                n.profit = newProfit;
                if (newItems.size() < 2) {
                  newItems.add(n);
                } else {
                  NewItem replace = newItems.get(0);
                  for (int m = 1; m < newItems.size(); ++m) {
                    if (newItems.get(m).profit < replace.profit) {
                      replace = newItems.get(m);
                    }
                  }
                  newItems.remove(replace);
                  newItems.add(n);
                }

                double sum = 0.0d;
                for (NewItem ni : newItems) {
                  sum += ni.profit;
                }
                if (sum > profit) {
                  BaseIndividual<Integer, PROBLEM> copy = new BaseIndividual<>(selected.getProblem(), selected.getGenes(), selected.getEvaluator());
                  copy.getGenes().set(parameters.geneSplitPoint + gene, 0);
                  for (NewItem ni : newItems) {
                    copy.getGenes().set(parameters.geneSplitPoint + ni.gene, 1);
                  }
                  copy.buildSolution(copy.getGenes(), parameters);
                  if (copy.dominates(selected)) {
                    pareto.set(pareto.indexOf(selected), copy);
                    selected = copy;
                    System.out.println("Replaced!");
                  }
                  found = true;
                }

//                if (newWeight <= weight && newProfit >= profit && selected.getGenes().get(parameters.geneSplitPoint + newGene) == 0) {
//                  selected.getGenes().set(parameters.geneSplitPoint + gene, 0);
//                  selected.getGenes().set(parameters.geneSplitPoint + newGene, 1);
//                  gene = newGene;
//                  profit = newProfit;
//                  weight = newWeight;
//                  found = true;
//                }
              }

            }

          }
        }
      }
      selected.buildSolution(selected.getGenes(), parameters);

      counter++;
      System.out.println(counter);

//        for (int i = 1; i < parameters.geneSplitPoint; ++i) {
//          int city = selected.getGenes().get(i);
//          for (int j = 0; j < 5; ++j) {
//            int gene = city + 279 * j - 1;
//            double ratio = ((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getProfit()
//                / (double)((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getWeight();
//            System.out.print(((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getProfit() +
//                " " + ((algorithms.problem.TTP)selected.getProblem()).getKnapsack().getItem(gene).getWeight() +
//                " " + selected.getGenes().get(parameters.geneSplitPoint + gene) + "              ");
//          }
//          System.out.println();
//        }
      }
    }



    this.problem = best.getProblem();

//    ConvergenceMeasure ed = new ConvergenceMeasure(
//        parameters.evaluator.getPerfectPoint());
//    System.out.print(ed.getMeasure(pareto));
//    System.out.print(" ");
//
//    HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
//    System.out.print(hv.getMeasure(pareto));
//    System.out.print(" ");
//
//    ONVG pfs = new ONVG();
//    System.out.print(pfs.getMeasure(pareto));
//    System.out.print(" ");
//
//    Spacing spacing = new Spacing();
//    System.out.print(spacing.getMeasure(pareto));

    return pareto;
  }

  private boolean isDuplicate(final BaseIndividual<Integer, PROBLEM> ind1, final BaseIndividual<Integer, PROBLEM> ind2) {
    return ind1.getObjectives()[0] == ind2.getObjectives()[0] && ind1.getObjectives()[1] == ind2.getObjectives()[1];
  }

  private List<BaseIndividual<Integer, PROBLEM>> selectMostEffective(List<BaseIndividual<Integer, PROBLEM>> population) {
    int size = 20;

    BaseIndividual<Integer, PROBLEM> worst = population.get(0);
    while (population.size() > size) {
      crowdingDistance(population);
      double worstDistance = Double.POSITIVE_INFINITY;
      for (BaseIndividual<Integer, PROBLEM> ind : population) {
        if (ind.getDistance() < worstDistance) {
          worstDistance = ind.getDistance();
          worst = ind;
        }
      }
      population.remove(worst);

    }
    return population;
  }

  protected List<BaseIndividual<Integer, PROBLEM>> removeDuplicates(
      List<BaseIndividual<Integer, PROBLEM>> allIndividuals) {
    List<BaseIndividual<Integer, PROBLEM>> allIndividualsNoDuplicates = new ArrayList<>();
    for (BaseIndividual<Integer, PROBLEM> ind : allIndividuals) {
      if (allIndividualsNoDuplicates.stream().noneMatch(i ->
          i.getObjectives()[0] == ind.getObjectives()[0] &&
          i.getObjectives()[1] == ind.getObjectives()[1])
      ) {
        allIndividualsNoDuplicates.add(ind);
      }
    }
    return allIndividualsNoDuplicates;
  }

  protected BaseIndividual<Integer, PROBLEM> findBestIndividual(
      List<BaseIndividual<Integer, PROBLEM>> population) {
    BaseIndividual<Integer, PROBLEM> best = population.get(0);
    double eval = best.getEvalValue();
    BaseIndividual<Integer, PROBLEM> trial;
    for (int i = 1; i < population.size(); ++i) {
      trial = population.get(i);
      if (trial.getEvalValue() < eval) {
        best = trial;
        eval = trial.getEvalValue();
      }
    }

    return best;
  }

  private class NewItem {

    public int gene;
    public double profit;

  }

}
