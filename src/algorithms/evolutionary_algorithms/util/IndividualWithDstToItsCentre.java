package algorithms.evolutionary_algorithms.util;

import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

public class IndividualWithDstToItsCentre<PROBLEM extends BaseProblemRepresentation> {
    private double distance;
    private BaseIndividual<Integer, PROBLEM> individual;

    public IndividualWithDstToItsCentre(double distance, BaseIndividual<Integer, PROBLEM> individual) {
        this.distance = distance;
        this.individual = individual;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public BaseIndividual<Integer, PROBLEM> getIndividual() {
        return individual;
    }

    public void setIndividual(BaseIndividual<Integer, PROBLEM> individual) {
        this.individual = individual;
    }
}
