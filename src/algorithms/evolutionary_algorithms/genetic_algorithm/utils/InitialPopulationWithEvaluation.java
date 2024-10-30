package algorithms.evolutionary_algorithms.genetic_algorithm.utils;

import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

public class InitialPopulationWithEvaluation <PROBLEM extends BaseProblemRepresentation> {
    private BaseIndividual<Integer, PROBLEM> initialPopulation;
    private double profit;
    private double distance;

    public InitialPopulationWithEvaluation(BaseIndividual<Integer, PROBLEM> initialPopulation, double distance, double profit) {
        this.initialPopulation = initialPopulation;
        this.distance = distance;
        this.profit = profit;
    }

    public BaseIndividual<Integer, PROBLEM> getInitialPopulation() {
        return initialPopulation;
    }

    public void setInitialPopulation(BaseIndividual<Integer, PROBLEM> initialPopulation) {
        this.initialPopulation = initialPopulation;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
