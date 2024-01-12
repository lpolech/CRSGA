package algorithms.evolutionary_algorithms.genetic_algorithm;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.selection.BaseSelection;
import algorithms.evolutionary_algorithms.selection.ClusterBasedSelection;
import algorithms.evolutionary_algorithms.selection.DiversitySelection;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.quality_measure.HVMany;
import algorithms.visualization.BoxCluster;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ANTGA2 <PROBLEM extends BaseProblemRepresentation> extends GeneticAlgorithm<PROBLEM> {
    private NondominatedSorter<BaseIndividual<Integer, PROBLEM>> sorter;
    private ClusterBasedSelection clusterBasedSelection;
    private BoxCluster boxCluster;
    private String directory;
    private int maxAdditionalPopulationSize;
    private int minAdditionalPopulationSize;
    private int clusterSize;
    public ANTGA2(PROBLEM problem,
                  int populationSize,
                  int generationLimit,
                  ParameterSet<Integer, PROBLEM> parameters,
                  double mutationProbability,
                  double crossoverProbability,
                  String directory,
                  int clusterSize,
                  int maxAdditionalPopulationSize,
                  int minAdditionalPopulationSize) {
        super(problem, populationSize, generationLimit, parameters, mutationProbability, crossoverProbability);

        this.directory = directory;
        this.maxAdditionalPopulationSize = maxAdditionalPopulationSize;
        this.minAdditionalPopulationSize = minAdditionalPopulationSize;
        this.clusterSize = clusterSize;

        sorter = new NondominatedSorter<>();
        boxCluster = new BoxCluster();
        clusterBasedSelection = new ClusterBasedSelection();
    }

    public List<BaseIndividual<Integer, PROBLEM>> optimize() {
        int generation = 1;
        BaseIndividual<Integer, PROBLEM> best;
        List<BaseIndividual<Integer, PROBLEM>> newPopulation;
        List<BaseIndividual<Integer, PROBLEM>> combinedPopulations = new ArrayList<>();
        int currentAdditionalPopulationSize = minAdditionalPopulationSize;
        int lastGenerationWithImprovement = 0;
        int lastAddedIndividuals = 0;

        List<Pair<Integer, List<BaseIndividual<Integer, PROBLEM>>>> clusters;

        BaseIndividual<Integer, PROBLEM> firstParent;
        BaseIndividual<Integer, PROBLEM> secondParent;
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
        combinedPopulations.addAll(population);
        combinedPopulations = removeDuplicates(combinedPopulations);
        combinedPopulations = getNondominated(combinedPopulations);

        while (generation < generationLimit) {
            System.out.println(generation);
            newPopulation = new ArrayList<>();
            sorter.nondominatedSorting(population);

            while (newPopulation.size() < populationSize) {
                firstParent = parameters.selection.select(population, combinedPopulations, newPopulation.size(), null, null, parameters);
                secondParent = parameters.selection.select(population, combinedPopulations, newPopulation.size(), firstParent, null, parameters);

                children = parameters.crossover.crossover(crossoverProbability, firstParent.getGenes(), secondParent.getGenes(), parameters);
                children.set(0, parameters.mutation.mutate(population, mutationProbability, children.get(0), 0, populationSize, parameters));
                children.set(1, parameters.mutation.mutate(population, mutationProbability, children.get(1), 0, populationSize, parameters));

                firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
                secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);

                clonePreventionMethod(newPopulation, firstChild, secondChild);
                best = setBestIndividual(best, firstChild, secondChild);
            }

            population = newPopulation;
            removeDuplicatesAndDominated(population, combinedPopulations);

            if(combinedPopulations.size() > clusterSize) {
                Collections.sort(combinedPopulations);
                crowdingDistance(combinedPopulations);
                List<BaseIndividual<Integer, PROBLEM>> previousCombinedPopulation = new ArrayList<>(combinedPopulations);
                clusters = boxCluster.clustering(combinedPopulations, clusterSize);

                while (newPopulation.size() - populationSize < currentAdditionalPopulationSize) {
                    firstParent = clusterBasedSelection.select(clusters, parameters);
                    secondParent = clusterBasedSelection.select(clusters, parameters);

                    children = parameters.crossover.crossover(crossoverProbability, firstParent.getGenes(), secondParent.getGenes(), parameters);
                    children.set(0, parameters.mutation.mutate(newPopulation, mutationProbability, children.get(0), 0, newPopulation.size(), parameters));
                    children.set(1, parameters.mutation.mutate(newPopulation, mutationProbability, children.get(1), 0, newPopulation.size(), parameters));
                    firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
                    secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);

                    clonePreventionMethod(newPopulation, firstChild, secondChild);
                    best = setBestIndividual(best, firstChild, secondChild);
                }

                population = newPopulation;
                removeDuplicatesAndDominated(population, combinedPopulations);
                int individualsAddedToParetoFront = getIndividualsAddedToParetoFront(combinedPopulations, previousCombinedPopulation);

                if(individualsAddedToParetoFront != 0 && currentAdditionalPopulationSize < maxAdditionalPopulationSize){
                    var expectedAdditionalPopulationSize = currentAdditionalPopulationSize + individualsAddedToParetoFront;
                    currentAdditionalPopulationSize = expectedAdditionalPopulationSize <= maxAdditionalPopulationSize
                            ? expectedAdditionalPopulationSize
                            : maxAdditionalPopulationSize;
                    lastAddedIndividuals = individualsAddedToParetoFront;
                    lastGenerationWithImprovement = generation;
                } else if (generation - lastGenerationWithImprovement > 10 && currentAdditionalPopulationSize > minAdditionalPopulationSize){
                    var expectedAdditionalPopulationSize = currentAdditionalPopulationSize - lastAddedIndividuals;
                    currentAdditionalPopulationSize = expectedAdditionalPopulationSize >= minAdditionalPopulationSize
                            ? expectedAdditionalPopulationSize
                            : minAdditionalPopulationSize;
                }
            }
            ++generation;
        }

        combinedPopulations = removeDuplicates(combinedPopulations);
        List<BaseIndividual<Integer, PROBLEM>> pareto = getNondominated(combinedPopulations);
        return pareto;
    }

    private int getIndividualsAddedToParetoFront(List<BaseIndividual<Integer, PROBLEM>> combinedPopulations, List<BaseIndividual<Integer, PROBLEM>> previousCombinedPopulation) {
        previousCombinedPopulation.removeAll(combinedPopulations);
        var individualsAddedToParetoFront = previousCombinedPopulation.size();
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
            firstChild.setGenes(parameters.mutation.mutate(population, 1.0, firstChild.getGenes(), 0, populationSize, parameters));
        }
        if (!newPopulation.contains(firstChild)) {
            firstChild.buildSolution(firstChild.getGenes(), parameters);
            newPopulation.add(firstChild);
        }
        for (int i = 0; newPopulation.contains(secondChild) && i < 20; i++) {
            secondChild.setGenes(parameters.mutation.mutate(population, 1.0, secondChild.getGenes(), 0, populationSize, parameters));
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
