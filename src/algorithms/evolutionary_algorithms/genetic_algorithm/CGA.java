package algorithms.evolutionary_algorithms.genetic_algorithm;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.selection.ClusterDensityBasedSelection;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.visualization.EvolutionHistoryElement;
import algorithms.visualization.KmeansClusterisation;
import interfaces.QualityMeasure;
import javafx.util.Pair;

import java.util.*;

public class CGA<PROBLEM extends BaseProblemRepresentation> extends GeneticAlgorithm<PROBLEM> {
    private final double edgeClustersDispersionVal;
    private final QualityMeasure clusterWeightMeasure;
    private double KNAPmutationProbability;
    private double KNAPcrossoverProbability;
    private NondominatedSorter<BaseIndividual<Integer, PROBLEM>> sorter;
    private ClusterDensityBasedSelection clusterDensityBasedSelection;
    private KmeansClusterisation kmeansCluster;
    private String directory;
    private int maxAdditionalPopulationSize;
    private int minAdditionalPopulationSize;
    private int clusterSize;
    private int clusterIterLimit;

    public CGA(PROBLEM problem,
               QualityMeasure clusterWeightMeasure,
               int populationSize,
               int generationLimit,
               ParameterSet<Integer, PROBLEM> parameters,
               double TSPmutationProbability,
               double KNAPmutationProbability,
               double TSPcrossoverProbability,
               double KNAPcrossoverProbability,
               String directory,
               int clusterSize,
               int clusterIterLimit,
               double edgeClustersDispersionVal,
               int tournamentSize,
               int maxAdditionalPopulationSize,
               int minAdditionalPopulationSize,
               double diversityThreshold,
               boolean enhanceDiversity) {
        super(problem, populationSize, generationLimit, parameters, TSPmutationProbability, TSPcrossoverProbability);

        this.KNAPmutationProbability = KNAPmutationProbability;
        this.KNAPcrossoverProbability = KNAPcrossoverProbability;
        this.directory = directory;
        this.maxAdditionalPopulationSize = maxAdditionalPopulationSize;
        this.minAdditionalPopulationSize = minAdditionalPopulationSize;
        this.clusterSize = clusterSize;
        this.edgeClustersDispersionVal = edgeClustersDispersionVal;
        this.clusterIterLimit = clusterIterLimit;

        this.sorter = new NondominatedSorter<>();
        this.kmeansCluster = new KmeansClusterisation(false, false);
        this.clusterDensityBasedSelection = new ClusterDensityBasedSelection(tournamentSize);
        this.clusterWeightMeasure = clusterWeightMeasure;
    }

    public List<BaseIndividual<Integer, PROBLEM>> optimize() {
//        System.out.println("generation; additional population; cur arch size; curr arch measure; clust added ind; prev arch size; prev arch measure");
        int generation = 1;
        BaseIndividual<Integer, PROBLEM> best;
        List<BaseIndividual<Integer, PROBLEM>> newPopulation;
        List<BaseIndividual<Integer, PROBLEM>> archive = new ArrayList<>();

        ClusteringResult gaClusteringResults = null;

        List<EvolutionHistoryElement> evolutionHistory = new ArrayList<>();

//        BaseIndividual<Integer, PROBLEM> firstParent;
//        BaseIndividual<Integer, PROBLEM> secondParent;

        BaseIndividual<Integer, PROBLEM> firstChild;
        BaseIndividual<Integer, PROBLEM> secondChild;
        List<List<Integer>> children;

        int cost = populationSize;
        population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);

        for (BaseIndividual<Integer, PROBLEM> individual : population) {
            individual.buildSolution(individual.getGenes(), parameters);
        }

        archive.addAll(population);
        archive = removeDuplicates(archive);
        archive = getNondominated(archive);

        while (cost < generationLimit) {
            newPopulation = new ArrayList<>();
            gaClusteringResults = kmeansCluster.clustering(clusterWeightMeasure,
                    archive,
                    clusterSize,
                    clusterIterLimit,
                    edgeClustersDispersionVal,
                    generation, parameters);
//            gaClusteringResults.toFile();

//            while (newPopulation.size() < populationSize) {
                var pairs = clusterDensityBasedSelection.select(gaClusteringResults, parameters, clusterWeightMeasure, population);

                for(var e: population) {
                    evolutionHistory.add(new EvolutionHistoryElement(generation, e.getObjectives()[0], e.getObjectives()[1], 1,
                            e.getObjectives()[0], e.getObjectives()[1], e.getObjectives()[0], e.getObjectives()[1]));
                }

                for(var mama: pairs) {
                    var firstAndSecondParent = (Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>)mama;
                    BaseIndividual<Integer, PROBLEM> firstParent = firstAndSecondParent.getKey();
                    BaseIndividual<Integer, PROBLEM> secondParent = firstAndSecondParent.getValue();
                    children = parameters.crossover.crossover(crossoverProbability, KNAPcrossoverProbability,
                            firstParent.getGenes(), secondParent.getGenes(), parameters);
                    children.set(0, parameters.mutation.mutate(newPopulation, mutationProbability, KNAPmutationProbability,
                            children.get(0), 0, newPopulation.size(), parameters));
                    children.set(1, parameters.mutation.mutate(newPopulation, mutationProbability, KNAPmutationProbability,
                            children.get(1), 0, newPopulation.size(), parameters));
                    firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
                    firstChild.buildSolution(firstChild.getGenes(), parameters);
                    secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);
                    secondChild.buildSolution(secondChild.getGenes(), parameters);
                    evolutionHistory.add(new EvolutionHistoryElement(generation,
                                firstChild.getObjectives()[0], firstChild.getObjectives()[1], 2,
                                firstParent.getObjectives()[0], firstParent.getObjectives()[1],
                                secondParent.getObjectives()[0], secondParent.getObjectives()[1]));
                    evolutionHistory.add(new EvolutionHistoryElement(generation,
                                secondChild.getObjectives()[0], secondChild.getObjectives()[1], 2,
                                firstParent.getObjectives()[0], firstParent.getObjectives()[1],
                                secondParent.getObjectives()[0], secondParent.getObjectives()[1]));
                    cost = cost + 2;

//                    population.remove(firstParent);
//                    population.remove(secondParent);
                    population.add(firstChild);
                    population.add(secondChild);
                    population = population.subList(Math.max(0, population.size() - populationSize), population.size());
//                    System.out.println(population.size());
                }
//            }

            for(var e: archive) {
                evolutionHistory.add(new EvolutionHistoryElement(generation, e.getObjectives()[0], e.getObjectives()[1], 0,
                        e.getObjectives()[0], e.getObjectives()[1], e.getObjectives()[0], e.getObjectives()[1]));
            }

            gaClusteringResults.toFile();
            removeDuplicatesAndDominated(population, archive);

//            newPopulation.addAll(population);
//            newPopulation.sort(Comparator.comparingDouble(BaseIndividual::getEvalValue));
//
//            // remove last duplicate elem
//            for(int i = newPopulation.size() - 1; i >= 1; i--) {
//                var currElemGenes = newPopulation.get(i).getGenes();
//                var prevElemGenes = newPopulation.get(i-1).getGenes();
//                boolean isDuplicate = true;
//                for(int j = 0; j < currElemGenes.size() && isDuplicate; j++) {
//                    if(currElemGenes.get(j) != prevElemGenes.get(j)) {
//                        isDuplicate = false;
//                        break;
//                    }
//                }
//                if(isDuplicate) {
//                    newPopulation.remove(i);
//                }
//            }
//
//            population = newPopulation.subList(0, populationSize);

            ++generation;
        }


        EvolutionHistoryElement.toFile(evolutionHistory, gaClusteringResults.getClusteringResultFilePath());
        archive = removeDuplicates(archive);
        List<BaseIndividual<Integer, PROBLEM>> pareto = getNondominated(archive);
        return pareto;
    }

    public List<BaseIndividual<Integer, PROBLEM>> getNondominatedFromTwoLists(
            List<BaseIndividual<Integer, PROBLEM>> population1,
            List<BaseIndividual<Integer, PROBLEM>> population2) {

        List<BaseIndividual<Integer, PROBLEM>> combinedLists = new ArrayList<>();
        combinedLists.addAll(population1);
        combinedLists.addAll(population2);

        return getNondominated(combinedLists);
    }

    private int getIndividualsAddedToParetoFront(List<BaseIndividual<Integer, PROBLEM>> combinedPopulations, List<BaseIndividual<Integer, PROBLEM>> previousCombinedPopulation) {
        List<BaseIndividual<Integer, PROBLEM>> combinedPopulationsCopy = new ArrayList<>();
        combinedPopulationsCopy.addAll(combinedPopulations);
        combinedPopulationsCopy.removeAll(previousCombinedPopulation);
        var individualsAddedToParetoFront = combinedPopulationsCopy.size();
        return individualsAddedToParetoFront;
    }

    private BaseIndividual<Integer, PROBLEM> setBestIndividual(BaseIndividual<Integer, PROBLEM> best, BaseIndividual<Integer, PROBLEM> firstChild, BaseIndividual<Integer, PROBLEM> secondChild) {
        if (firstChild.getEvalValue() < best.getEvalValue()) {
            best = firstChild;
        }
        if (secondChild.getEvalValue() < best.getEvalValue()) {
            best = secondChild;
        }
        return best;
    }

    private void clonePreventionMethod(List<BaseIndividual<Integer, PROBLEM>> newPopulation, BaseIndividual<Integer, PROBLEM> firstChild, BaseIndividual<Integer, PROBLEM> secondChild) {
        for (int i = 0; newPopulation.contains(firstChild) && i < 20; i++) {
            firstChild.setGenes(parameters.mutation.mutate(population, mutationProbability, KNAPmutationProbability, firstChild.getGenes(), 0, populationSize, parameters));
        }
        if (!newPopulation.contains(firstChild)) {
            firstChild.buildSolution(firstChild.getGenes(), parameters);
            newPopulation.add(firstChild);
        }
        for (int i = 0; newPopulation.contains(secondChild) && i < 20; i++) {
            secondChild.setGenes(parameters.mutation.mutate(population, mutationProbability, KNAPmutationProbability, secondChild.getGenes(), 0, populationSize, parameters));
        }
        if (!newPopulation.contains(secondChild)) {
            secondChild.buildSolution(secondChild.getGenes(), parameters);
            newPopulation.add(secondChild);
        }
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
}
