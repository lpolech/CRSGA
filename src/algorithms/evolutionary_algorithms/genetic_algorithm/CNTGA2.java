package algorithms.evolutionary_algorithms.genetic_algorithm;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.selection.BaseSelection;
import algorithms.evolutionary_algorithms.selection.ClusterDensityBasedSelection;
import algorithms.evolutionary_algorithms.selection.DiversitySelection;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.quality_measure.HVMany;
import algorithms.visualization.KmeansClusterisation;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CNTGA2<PROBLEM extends BaseProblemRepresentation> extends GeneticAlgorithm<PROBLEM> {
    private final double edgeClustersDispersionVal;
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

    public CNTGA2(PROBLEM problem,
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
                  int maxAdditionalPopulationSize,
                  int minAdditionalPopulationSize) {
        super(problem, populationSize, generationLimit, parameters, TSPmutationProbability, TSPcrossoverProbability);

        this.KNAPmutationProbability = KNAPmutationProbability;
        this.KNAPcrossoverProbability = KNAPcrossoverProbability;
        this.directory = directory;
        this.maxAdditionalPopulationSize = maxAdditionalPopulationSize;
        this.minAdditionalPopulationSize = minAdditionalPopulationSize;
        this.clusterSize = clusterSize;
        this.edgeClustersDispersionVal = edgeClustersDispersionVal;
        this.clusterIterLimit = clusterIterLimit;

        sorter = new NondominatedSorter<>();
        kmeansCluster = new KmeansClusterisation(false, false);
        clusterDensityBasedSelection = new ClusterDensityBasedSelection();
    }

    public List<BaseIndividual<Integer, PROBLEM>> optimize() {
//        System.out.println("generation; additional population; cur arch size; curr arch measure; clust added ind; prev arch size; prev arch measure");
        HVMany hv = new HVMany(parameters.evaluator.getNadirPoint());
        int generation = 1;
        BaseIndividual<Integer, PROBLEM> best;
        List<BaseIndividual<Integer, PROBLEM>> newPopulation;
        List<BaseIndividual<Integer, PROBLEM>> archive = new ArrayList<>();
        int currentAdditionalPopulationSize = minAdditionalPopulationSize;
        int lastGenerationWithImprovement = 0;
        int lastAddedIndividuals = 0;

        ClusteringResult gaClusteringResults;

//        BaseIndividual<Integer, PROBLEM> firstParent;
//        BaseIndividual<Integer, PROBLEM> secondParent;
        Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>> firstAndSecondParent;
        BaseIndividual<Integer, PROBLEM> firstChild;
        BaseIndividual<Integer, PROBLEM> secondChild;
        List<List<Integer>> children;

        BaseSelection<Integer, PROBLEM> selection = new DiversitySelection();
        ((DiversitySelection<Integer>) selection).setTournamentSize(parameters.tournamentSize);

        population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);

        for (BaseIndividual<Integer, PROBLEM> individual : population) {
            individual.buildSolution(individual.getGenes(), parameters);
        }

        sorter.nondominatedSorting(population);
        sorter.crowdingDistance(population);
        best = findBestIndividual(population);
        archive.addAll(population);
        archive = removeDuplicates(archive);
        archive = getNondominated(archive);

        while (generation < generationLimit) {
            newPopulation = new ArrayList<>();
            sorter.nondominatedSorting(population);

//            while (newPopulation.size() < populationSize) {
//                firstAndSecondParent = parameters.selection.select(population, archive, newPopulation.size(), null, null, parameters);
//                secondParent = parameters.selection.select(population, archive, newPopulation.size(), firstAndSecondParent, null, parameters);
//
//                children = parameters.crossover.crossover(crossoverProbability, KNAPcrossoverProbability,
//                                                            firstAndSecondParent.getGenes(), secondParent.getGenes(), parameters);
//                children.set(0, parameters.mutation.mutate(population, mutationProbability, KNAPmutationProbability,
//                                                            children.get(0), 0, populationSize, parameters));
//                children.set(1, parameters.mutation.mutate(population, mutationProbability, KNAPmutationProbability,
//                                                            children.get(1), 0, populationSize, parameters));
//
//                firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
//                secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);
//
//                clonePreventionMethod(newPopulation, firstChild, secondChild);
//                best = setBestIndividual(best, firstChild, secondChild);
//            }

//            population = newPopulation;
//            removeDuplicatesAndDominated(population, archive);

//            if(archive.size() > clusterSize) {
                Collections.sort(archive);
                crowdingDistance(archive);
                List<BaseIndividual<Integer, PROBLEM>> previousArchive = new ArrayList<>(archive);
                gaClusteringResults = kmeansCluster.clustering(archive,
                        clusterSize,
                        clusterIterLimit,
                        edgeClustersDispersionVal,
                        generation);

//                while (newPopulation.size() - populationSize < currentAdditionalPopulationSize) {
                while (newPopulation.size() < populationSize) {
                    firstAndSecondParent = clusterDensityBasedSelection.select(gaClusteringResults, parameters);
//                    firstParent = parameters.selection.select(population, archive, newPopulation.size(), null, null, parameters);
//                    secondParent = parameters.selection.select(population, archive, newPopulation.size(), firstParent, null, parameters);

                    children = parameters.crossover.crossover(crossoverProbability, KNAPcrossoverProbability,
                                                    firstAndSecondParent.getKey().getGenes(), firstAndSecondParent.getValue().getGenes(), parameters);
                    children.set(0, parameters.mutation.mutate(newPopulation, mutationProbability, KNAPmutationProbability,
                                                    children.get(0), 0, newPopulation.size(), parameters));
                    children.set(1, parameters.mutation.mutate(newPopulation, mutationProbability, KNAPmutationProbability,
                                                    children.get(1), 0, newPopulation.size(), parameters));
                    firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
                    secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);

                    clonePreventionMethod(newPopulation, firstChild, secondChild);
                    best = setBestIndividual(best, firstChild, secondChild);
                }

                population = newPopulation;
                removeDuplicatesAndDominated(population, archive);
                gaClusteringResults.toFile();

//                int individualsAddedToParetoFront = getIndividualsAddedToParetoFront(archive, previousArchive);

//                if(individualsAddedToParetoFront != 0 && currentAdditionalPopulationSize < maxAdditionalPopulationSize){
//                    var expectedAdditionalPopulationSize = currentAdditionalPopulationSize + individualsAddedToParetoFront;
//                    currentAdditionalPopulationSize = expectedAdditionalPopulationSize <= maxAdditionalPopulationSize
//                            ? expectedAdditionalPopulationSize
//                            : maxAdditionalPopulationSize;
//                    lastAddedIndividuals = individualsAddedToParetoFront;
//                    lastGenerationWithImprovement = generation;
//                } else if (generation - lastGenerationWithImprovement > 10 && currentAdditionalPopulationSize > minAdditionalPopulationSize){
//                    var expectedAdditionalPopulationSize = currentAdditionalPopulationSize - lastAddedIndividuals;
//                    currentAdditionalPopulationSize = expectedAdditionalPopulationSize >= minAdditionalPopulationSize
//                            ? expectedAdditionalPopulationSize
//                            : minAdditionalPopulationSize;
//                }
                // generation; cur arch size; curr arch measure; clust added ind; prev arch size; prev arch measure
//                System.out.println(generation + ";\t" + currentAdditionalPopulationSize + ";\t" + archive.size()
//                        + ";\t" + hv.getMeasure(archive) + ";\t" //+ individualsAddedToParetoFront
//                        + ";\t" + previousArchive.size() + ";\t" + hv.getMeasure(previousArchive));
//            }
            ++generation;
        }

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
