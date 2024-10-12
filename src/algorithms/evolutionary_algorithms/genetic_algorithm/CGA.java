package algorithms.evolutionary_algorithms.genetic_algorithm;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.evolutionary_algorithms.genetic_algorithm.utils.OptimisationResult;
import algorithms.evolutionary_algorithms.selection.ClusterDensityBasedSelection;
import algorithms.evolutionary_algorithms.selection.IndividualsPairingMethod;
import algorithms.evolutionary_algorithms.util.ClusteringResult;
import algorithms.evolutionary_algorithms.util.IndividualCluster;
import algorithms.evolutionary_algorithms.util.IndividualWithDstToItsCentre;
import algorithms.evolutionary_algorithms.util.NondominatedSorter;
import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;
import algorithms.quality_measure.GenerationalDistance;
import algorithms.quality_measure.HVMany;
import algorithms.quality_measure.InvertedGenerationalDistance;
import algorithms.visualization.EvolutionHistoryElement;
import algorithms.visualization.KmeansClusterisation;
import interfaces.QualityMeasure;
import javafx.util.Pair;
import util.FILE_OUTPUT_LEVEL;
import util.ParameterFunctions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CGA<PROBLEM extends BaseProblemRepresentation> extends GeneticAlgorithm<PROBLEM> {
    private final double edgeClustersDispersionVal;
    private final QualityMeasure clusterWeightMeasure;
    private final HVMany hvCalculator;
    private final String outputFilename;
    private final int iterationNumber;
    private final int indExclusionUsageLimit;
    private final int indExclusionGenDuration;
    private final double turDecayParam;
    private final int minTournamentSize;
    private final ParameterFunctions parameterFunction;
    private final List<BaseIndividual<Integer, PROBLEM>> optimalParetoFront;
    private final FILE_OUTPUT_LEVEL saveResultFiles;
    private final int clusteringRunFrequencyInCost;
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
    private OptimisationResult optimisationResult;
    private int populationTurProp;
    private int mutationVersion;
    private IndividualsPairingMethod pairingMethod;

    public OptimisationResult getOptimisationResult() {
        return optimisationResult;
    }

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
               int populationTurProp,
               double diversityThreshold,
               boolean enhanceDiversity,
               HVMany hv,
               List<BaseIndividual<Integer, PROBLEM>> optimalParetoFront,
               String outputFilename,
               FILE_OUTPUT_LEVEL saveResultFiles,
               int iterationNumber,
               int indExclusionUsageLimit,
               int indExclusionGenDuration,
               double turDecayParam,
               int minTournamentSize,
               IndividualsPairingMethod indPairingMethod,
               int clusteringRunFrequencyInCost) {
        super(problem, populationSize, generationLimit, parameters, TSPmutationProbability, TSPcrossoverProbability);

        this.KNAPmutationProbability = KNAPmutationProbability;
        this.KNAPcrossoverProbability = KNAPcrossoverProbability;
        this.directory = directory;
        this.maxAdditionalPopulationSize = maxAdditionalPopulationSize;
        this.minAdditionalPopulationSize = minAdditionalPopulationSize;
        this.populationTurProp = populationTurProp;
        this.clusterSize = clusterSize;
        this.edgeClustersDispersionVal = edgeClustersDispersionVal;
        this.clusterIterLimit = clusterIterLimit;

        this.sorter = new NondominatedSorter<>();
        this.kmeansCluster = new KmeansClusterisation(false, false);
        this.clusterDensityBasedSelection = new ClusterDensityBasedSelection(tournamentSize);
        this.clusterWeightMeasure = clusterWeightMeasure;
        this.mutationVersion = mutationVersion;
        this.hvCalculator = hv;
        this.optimalParetoFront = optimalParetoFront;
        this.outputFilename = outputFilename;
        this.saveResultFiles = saveResultFiles;
        this.iterationNumber = iterationNumber;
        this.indExclusionUsageLimit = indExclusionUsageLimit;
        this.indExclusionGenDuration = indExclusionGenDuration;
        this.turDecayParam = turDecayParam;
        this.minTournamentSize = minTournamentSize;

        if(minTournamentSize > 0) {
            this.parameterFunction = new ParameterFunctions(generationLimit,
                    ParameterFunctions.FUNCTION_TYPE.EXPONENTIAL,
                    minTournamentSize,
                    tournamentSize,
                    turDecayParam);
        } else {
            this.parameterFunction = null;
        }
        this.pairingMethod = indPairingMethod;
        this.clusteringRunFrequencyInCost = clusteringRunFrequencyInCost;
    }

    public List<BaseIndividual<Integer, PROBLEM>> optimize() {
        // create empty file
        String hvHistoryFilePath = outputFilename + File.separator + "hv_hisotry" + this.iterationNumber + ".csv";
        if(saveResultFiles.getLevel() > 1) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(hvHistoryFilePath));
                writer.write("gen;cost;hv;igd;gd;child dominance cnt;archive changes cnt\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("generation; additional population; cur arch size; curr arch measure; clust added ind; prev arch size; prev arch measure");
        int generation = 1;
        List<BaseIndividual<Integer, PROBLEM>> archive = new ArrayList<>();
        List<BaseIndividual<Integer, PROBLEM>> excludedArchive = new ArrayList<>();

        ClusteringResult gaClusteringResults = null;

        List<EvolutionHistoryElement> evolutionHistory = new ArrayList<>();

//        BaseIndividual<Integer, PROBLEM> firstParent;
//        BaseIndividual<Integer, PROBLEM> secondParent;

        BaseIndividual<Integer, PROBLEM> firstChild;
        BaseIndividual<Integer, PROBLEM> secondChild;
        List<List<Integer>> children;

        this.optimisationResult = new OptimisationResult();

        int cost = populationSize;
        int costSinceLastClustering = 0;
        population = parameters.initialPopulation.generate(problem, populationSize, parameters.evaluator, parameters);

        for (BaseIndividual<Integer, PROBLEM> individual : population) {
            individual.buildSolution(individual.getGenes(), parameters);
        }

        archive.addAll(population);
        archive = removeDuplicates(archive);
        archive = getNondominated(archive);
        population = new ArrayList<>();

        boolean isClusterinRun = true;
        while (cost < generationLimit) {
            int archiveChanges = 0;
            if(costSinceLastClustering >= clusteringRunFrequencyInCost) {
                archiveChanges = removeDuplicatesAndDominated(population, archive);
                population = new ArrayList<>();
                isClusterinRun = true;
                costSinceLastClustering = 0;
                recordGenerationAndUpdateArchiveAndExcludedIndividuals(indExclusionUsageLimit, indExclusionGenDuration, archive, excludedArchive); // TODO: archive exclusion should be adjusted since we have dynamic clustering
            }
            gaClusteringResults = kmeansCluster.clustering(gaClusteringResults, clusterWeightMeasure,
                    archive,
                    clusterSize,
                    clusterIterLimit,
                    edgeClustersDispersionVal,
                    generation,
                    parameters,
                    indExclusionUsageLimit,
                    indExclusionGenDuration,
                    excludedArchive,
                    saveResultFiles,
                    population,
                    isClusterinRun);

            if(isClusterinRun) {
                isClusterinRun = false;
            }
//            archiveChanges = removeDuplicatesAndDominated(population, archive);

//            while (newPopulation.size() < populationSize) {
                var pairs = clusterDensityBasedSelection.select(gaClusteringResults,
                        parameters, clusterWeightMeasure, parameterFunction, cost, pairingMethod);

//                for(var e: population) {
//                    EvolutionHistoryElement.addIfNotFull(evolutionHistory,
//                            generation, e.getObjectives()[0], e.getObjectives()[1], 1,
//                            e.getObjectives()[0], e.getObjectives()[1], e.getObjectives()[0], e.getObjectives()[1]);
//                }

                int noOfChildDominatingParents = 0;
                if(clusteringRunFrequencyInCost > 0) {
                    Collections.shuffle(pairs);
                    pairs = pairs.subList(0, Math.min((int)Math.ceil((clusteringRunFrequencyInCost - costSinceLastClustering)/2.0), pairs.size())); // each pair costs 2 cost
                }
                for(var mama: pairs) {
                    var firstAndSecondParent = (Pair<BaseIndividual<Integer, PROBLEM>, BaseIndividual<Integer, PROBLEM>>)mama;
                    BaseIndividual<Integer, PROBLEM> firstParent = firstAndSecondParent.getKey();
                    BaseIndividual<Integer, PROBLEM> secondParent = firstAndSecondParent.getValue();
                    children = parameters.crossover.crossover(crossoverProbability, KNAPcrossoverProbability,
                            firstParent.getGenes(), secondParent.getGenes(), parameters);

                    var firstChildAfterCross = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
                    firstChildAfterCross.buildSolution(firstChildAfterCross.getGenes(), parameters);
                    var secondChildAfterCross = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);
                    secondChildAfterCross.buildSolution(secondChildAfterCross.getGenes(), parameters);

                    children.set(0, parameters.mutation.mutate(null, mutationProbability, KNAPmutationProbability,
                            children.get(0), 0, -666, parameters));
                    children.set(1, parameters.mutation.mutate(null, mutationProbability, KNAPmutationProbability,
                            children.get(1), 0, -666, parameters));

                    var firstChildAfterCrossAndMut = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
                    firstChildAfterCrossAndMut.buildSolution(firstChildAfterCrossAndMut.getGenes(), parameters);
                    var secondChildAfterCrossAndMut = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);
                    secondChildAfterCrossAndMut.buildSolution(secondChildAfterCrossAndMut.getGenes(), parameters);
                    this.optimisationResult.addDominanceStats(firstParent, secondParent, firstChildAfterCross,
                            secondChildAfterCross, firstChildAfterCrossAndMut, secondChildAfterCrossAndMut);

                    firstChild = new BaseIndividual<>(problem, children.get(0), parameters.evaluator);
                    firstChild.buildSolution(firstChild.getGenes(), parameters);
                    secondChild = new BaseIndividual<>(problem, children.get(1), parameters.evaluator);
                    secondChild.buildSolution(secondChild.getGenes(), parameters);
                    EvolutionHistoryElement.addIfNotFull(evolutionHistory, generation,
                                firstChild.getObjectives()[0], firstChild.getObjectives()[1], -2,
                                firstParent.getObjectives()[0], firstParent.getObjectives()[1],
                                secondParent.getObjectives()[0], secondParent.getObjectives()[1]);
                    EvolutionHistoryElement.addIfNotFull(evolutionHistory, generation,
                                secondChild.getObjectives()[0], secondChild.getObjectives()[1], -2,
                                firstParent.getObjectives()[0], firstParent.getObjectives()[1],
                                secondParent.getObjectives()[0], secondParent.getObjectives()[1]);
                    cost = cost + 2;
                    costSinceLastClustering = costSinceLastClustering + 2;

                    if(firstChild.dominates(firstParent) || firstChild.dominates(secondParent)) {
                        noOfChildDominatingParents++;
                    }
                    if(secondChild.dominates(firstParent) || secondChild.dominates(secondParent)) {
                        noOfChildDominatingParents++;
                    }

//                    population.remove(firstParent);
//                    population.remove(secondParent);
                    population.add(firstChild);
                    population.add(secondChild);
//                    population = population.subList(Math.max(0, population.size() - populationSize), population.size());
//                    System.out.println(population.size());
                }
//            }

            for(IndividualCluster cluster: gaClusteringResults.getClustersWithIndDstToCentre()) { //archive) {
                int clusterId = cluster.getClusterId();
                for(var clsInd: cluster.getCluster()) {
                    var e = ((IndividualWithDstToItsCentre)clsInd).getIndividual();
                    EvolutionHistoryElement.addIfNotFull(evolutionHistory, generation, e.getObjectives()[0], e.getObjectives()[1], clusterId,
                            e.getObjectives()[0], e.getObjectives()[1], e.getObjectives()[0], e.getObjectives()[1]);
                }
            }

            writeReportingFiles(excludedArchive, gaClusteringResults);
//            population = new ArrayList<>();

//            var optimalParetoFrontWithArchive = this.getNondominatedFromTwoLists(archive, this.optimalParetoFront);
            List<BaseIndividual<Integer, PROBLEM>> optimalParetoFrontWithArchive = new ArrayList<>(this.optimalParetoFront);
            List<BaseIndividual<Integer, PROBLEM>> archCopy = new ArrayList<>(archive);
            archiveChanges = removeDuplicatesAndDominated(population, archCopy);
            removeDuplicatesAndDominated(archCopy, optimalParetoFrontWithArchive);
            double archiveHv = this.hvCalculator.getMeasure(archCopy);
            double archiveIgd = new InvertedGenerationalDistance(optimalParetoFrontWithArchive).getMeasure(archCopy);
            double archiveGd = new GenerationalDistance(optimalParetoFrontWithArchive).getMeasure(archCopy);
            if(saveResultFiles.getLevel() > 1) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(hvHistoryFilePath, true));
                    writer.write(generation + ";" + cost + ";" + archiveHv + ";" + archiveIgd + ";" + archiveGd
                            + ";" + noOfChildDominatingParents + ";" + archiveChanges + "\n");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            population = getIndividualClosesToArchive(population, archive, populationSize, populationTurProp);

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

        removeDuplicatesAndDominated(population, archive);
        if(saveResultFiles.getLevel() > 1) {
            EvolutionHistoryElement.toFile(evolutionHistory, gaClusteringResults.getClusteringResultFilePath());
        }
        archive = removeDuplicates(archive);
        List<BaseIndividual<Integer, PROBLEM>> pareto = getNondominated(archive);
        return pareto;
    }

    private void writeReportingFiles(List<BaseIndividual<Integer, PROBLEM>> excludedArchive, ClusteringResult gaClusteringResults) {
        if(excludedArchive.size() > 0 && saveResultFiles.getLevel() > 1) {
            toFileExcludedIndividuals(excludedArchive, gaClusteringResults.getClusteringResultFilePath(), gaClusteringResults.getClusteringResultFileName());
        }
        gaClusteringResults.toFile();
    }

    private void toFileExcludedIndividuals(List<BaseIndividual<Integer,PROBLEM>> excludedArchive, String clusteringResultFilePath, String clusteringResultFileName) {
        try {
            String fullPath = clusteringResultFilePath + File.separator + "excludedInd_" + clusteringResultFileName;
            Files.createDirectories(Paths.get(clusteringResultFilePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
            StringBuilder output = new StringBuilder("Usage Cnt;Adj Usage Cnt;Unsuc Usage Cnt;Adj Unsuc Usage Cnt;Number of Times It Was Excluded;Curr Exclusion Cnt;Obj 0; Obj 1;Norm Obj 0;Norm Obj 1\n");

            for(var ind: excludedArchive) {
                output.append(ind.getUsageCounter() + ";");
                output.append(ind.getAdjustedUsageCounter() + ";");
                output.append(ind.getUnsuccessfulUsageCounter() + ";");
                output.append(ind.getAdjusterUnsuccessfulUsageCounter() + ";");
                output.append(ind.getNumberOfTimesItHasBeenExcluded() + ";");
                output.append(ind.getExclusionGenerationCounter() + ";");
                for(double obj: ind.getObjectives()) {
                    output.append(obj + ";");
                }

                for(double normObj: ind.getNormalObjectives()) {
                    output.append(normObj + ";");
                }
                output.append("\n");
            }
            writer.write(output.toString());
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void recordGenerationAndUpdateArchiveAndExcludedIndividuals(int indExclusionUsageLimit,
                                                                        int indExclusionGenDuration,
                                                                        List<BaseIndividual<Integer, PROBLEM>> archive,
                                                                        List<BaseIndividual<Integer, PROBLEM>> excludedArchive) {
        recordGenerationForExcludedIndividuals(archive, excludedArchive);
//        updateArchiveAndExcludedIndividuals(indExclusionUsageLimit, indExclusionGenDuration, archive, excludedArchive);
    }

    private void recordGenerationForExcludedIndividuals(List<BaseIndividual<Integer, PROBLEM>> archive,
                                                        List<BaseIndividual<Integer, PROBLEM>> excludedArchive) {
        // Use an iterator to safely remove elements from excludedArchive while iterating
        Iterator<BaseIndividual<Integer, PROBLEM>> iterator = excludedArchive.iterator();

        while (iterator.hasNext()) {
            BaseIndividual<Integer, PROBLEM> individual = iterator.next();
            // Decrement the exclusion generation counter
            individual.reduceExclusionGenerationCounter();

            // If the counter reaches zero, move the individual back to the main archive
            if (individual.getExclusionGenerationCounter() == 0) {
                List<BaseIndividual<Integer, PROBLEM>> artificialList = new ArrayList<>(1);
                artificialList.add(individual);
                removeDuplicatesAndDominated(artificialList, archive);
                iterator.remove(); // Remove from excludedArchive
            }
        }
    }

    private void updateArchiveAndExcludedIndividuals(int indExclusionUsageLimit, int indExclusionGenDuration,
                                                     List<BaseIndividual<Integer, PROBLEM>> archive,
                                                     List<BaseIndividual<Integer, PROBLEM>> excludedArchive) {
        // Step 1: Filter individuals whose usageCounter exceeds the indExclusionUsageLimit
        List<BaseIndividual<Integer, PROBLEM>> filteredIndividuals = archive.stream()
                .filter(individual -> individual.getAdjusterUnsuccessfulUsageCounter() > indExclusionUsageLimit)
                .collect(Collectors.toList());

        // Step 2: Sort the filtered individuals based on usageCounter and evalValue in descending order
        filteredIndividuals.sort(
                Comparator.comparingInt(BaseIndividual<Integer, PROBLEM>::getAdjusterUnsuccessfulUsageCounter).reversed()
                        .thenComparing(Comparator.comparingDouble(BaseIndividual<Integer, PROBLEM>::getEvalValue).reversed())
        );

        // Step 3: Make sure at least one archive element is present
        filteredIndividuals = filteredIndividuals.subList(0, Math.min(filteredIndividuals.size(), archive.size() - 1));

        // Step 4: Update exclusion counters and move individuals to the excluded list
        for (BaseIndividual<Integer, PROBLEM> individual : filteredIndividuals) {
            individual.excludeFromArchive(indExclusionGenDuration);
            archive.remove(individual);
            excludedArchive.add(individual);
        }
    }

    private List<BaseIndividual<Integer,PROBLEM>> getIndividualClosesToArchive(
            List<BaseIndividual<Integer,PROBLEM>> population,
            List<BaseIndividual<Integer,PROBLEM>> archive,
            int  populationSize,
            int turProp) {
        List<Pair<BaseIndividual<Integer, PROBLEM>, Double>> individualWithMinDst = new ArrayList<>(population.size());
        List<Pair<BaseIndividual<Integer, PROBLEM>, Double>> individualWithMinDstLimit = new ArrayList<>(population.size());
        List<Pair<BaseIndividual<Integer, PROBLEM>, Double>> individualsBasedOnMinArchiveDst = new ArrayList<>(archive.size());
        List<Pair<BaseIndividual<Integer, PROBLEM>, Double>> individualsBasedOnMinArchiveDstLimit = new ArrayList<>(archive.size());

        for(var ar: archive) {
            double minDistance = Double.MAX_VALUE;
            BaseIndividual<Integer,PROBLEM> minDstInd = null;
            for(var ind: population) {
                double distance = Math.sqrt(Math.pow(ind.getObjectives()[0] - ar.getObjectives()[0], 2) + Math.pow(ind.getObjectives()[1] - ar.getObjectives()[1], 2));
                if(!isZero(distance) && distance < minDistance) {
                    minDistance = distance;
                    minDstInd = ind;
                }
            }
            if (!isZero(minDistance) && minDstInd != null) { // remove archive points
                individualsBasedOnMinArchiveDst.add(new Pair<>(minDstInd, minDistance));
                population.remove(minDstInd);
            }
        }

        List<Pair<BaseIndividual<Integer, PROBLEM>, Double>> individualsChosenByDynamicTur = new ArrayList<>(individualsBasedOnMinArchiveDst.size());

        if(individualsBasedOnMinArchiveDst.size() < populationSize) {
            individualsChosenByDynamicTur.addAll(individualsBasedOnMinArchiveDst);
        } else {
            int numberOfPointsToPick = Math.min(individualsBasedOnMinArchiveDst.size(), populationSize);
            for (int i = 0; i < numberOfPointsToPick; i++) {
                int chosenIndividualIndex = (int) (parameters.random.nextDouble() * individualsBasedOnMinArchiveDst.size());
                int dynamicTurSize = Math.max(1, (int) ((turProp * individualsBasedOnMinArchiveDst.size()) / 100.0)); // tur size depents on the number of clusters as at the beginning there is not many clusters
                for (int t = 0; t < dynamicTurSize - 1; ++t) {
                    int otherIndividualIndex = (int) (parameters.random.nextDouble() * individualsBasedOnMinArchiveDst.size());
                    double chosenIndDst = individualsBasedOnMinArchiveDst.get(chosenIndividualIndex).getValue();
                    double otherIndDst = individualsBasedOnMinArchiveDst.get(otherIndividualIndex).getValue();

                    if (otherIndDst < chosenIndDst) {
                        chosenIndividualIndex = otherIndividualIndex;
                    }
                }
                individualsChosenByDynamicTur.add(individualsBasedOnMinArchiveDst.get(chosenIndividualIndex));
                individualsBasedOnMinArchiveDst.remove(chosenIndividualIndex); // TODO: mozna odkomentowac zeby bylo bez zwracania
            }
        }

//        individualsBasedOnMinArchiveDst.sort(Comparator.comparingDouble(Pair::getValue));
//        individualsBasedOnMinArchiveDstLimit = individualsBasedOnMinArchiveDst.subList(0, Math.min(individualsBasedOnMinArchiveDst.size(), populationSize));
        int slotsLeft = populationSize - individualsChosenByDynamicTur.size(); //individualsBasedOnMinArchiveDstLimit.size();

        if(slotsLeft >= 0) {
            for (var ind : population) {
                double minDistance = Double.MAX_VALUE;
                for (var ar : archive) {
                    double distance = Math.sqrt(Math.pow(ind.getObjectives()[0] - ar.getObjectives()[0], 2) + Math.pow(ind.getObjectives()[1] - ar.getObjectives()[1], 2));
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
                if (!isZero(minDistance)) { // remove archive points
                    individualWithMinDst.add(new Pair<>(ind, minDistance));
                }
            }

            for(int i = 0; i < slotsLeft && !individualWithMinDst.isEmpty(); i++) {
                int chosenIndividualIndex = (int) (parameters.random.nextDouble() * individualWithMinDst.size());
                int dynamicTurSize = Math.max(1, (int) ((turProp * individualWithMinDst.size()) /100.0)); // tur size depents on the number of clusters as at the beginning there is not many clusters
                for (int t = 0; t < dynamicTurSize - 1; ++t) {
                    int otherIndividualIndex = (int) (parameters.random.nextDouble() * individualWithMinDst.size());
                    double chosenIndDst = individualWithMinDst.get(chosenIndividualIndex).getValue();
                    double otherIndDst = individualWithMinDst.get(otherIndividualIndex).getValue();

                    if(otherIndDst < chosenIndDst) {
                        chosenIndividualIndex = otherIndividualIndex;
                    }
                }
                individualsChosenByDynamicTur.add(individualWithMinDst.get(chosenIndividualIndex));
                individualWithMinDst.remove(chosenIndividualIndex); // TODO; odkomentuj dla braku zwracania
            }

//            individualWithMinDst.sort(Comparator.comparingDouble(Pair::getValue));
//            individualWithMinDstLimit = individualWithMinDst.subList(0, Math.min(individualWithMinDst.size(), slotsLeft));
        }

//        List<BaseIndividual<Integer,PROBLEM>> selectedArchMinDstInd = individualsBasedOnMinArchiveDstLimit.stream()
//                .map(Pair::getKey).collect(Collectors.toCollection(LinkedList::new));
//        List<BaseIndividual<Integer,PROBLEM>> selectedMinDstInd = individualWithMinDstLimit.stream()
//                .map(Pair::getKey).collect(Collectors.toCollection(LinkedList::new));
        List<BaseIndividual<Integer,PROBLEM>> returnInd = new ArrayList<>();
//        returnInd.addAll(selectedArchMinDstInd);
//        returnInd.addAll(selectedMinDstInd);
        List<BaseIndividual<Integer,PROBLEM>> selectedDynamicTur = individualsChosenByDynamicTur.stream()
                .map(Pair::getKey).collect(Collectors.toCollection(LinkedList::new));
        returnInd.addAll(selectedDynamicTur);

        return returnInd;
    }

    public boolean isZero(double val) {
        return Math.abs(val) < 2 * Double.MIN_VALUE;
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

    public int getNumberOfNotDominated(List<BaseIndividual<Integer, PROBLEM>> front, List<BaseIndividual<Integer, PROBLEM>> apf) {
        int mnd = 0;
        for (BaseIndividual<Integer, PROBLEM> individual : front) {
            if (individual.isNotDominatedBy(apf)) {
                mnd++;
            }
        }
        return mnd;
    }
}
