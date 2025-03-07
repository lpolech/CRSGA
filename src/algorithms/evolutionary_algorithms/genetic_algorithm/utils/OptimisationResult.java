package algorithms.evolutionary_algorithms.genetic_algorithm.utils;

import algorithms.problem.BaseIndividual;
import algorithms.problem.BaseProblemRepresentation;

import java.util.List;

public class OptimisationResult<PROBLEM extends BaseProblemRepresentation> {
    private int afterCrossParentDominationCounter = 0;
    private int numberOfAfterCrossChecks = 0;
    private int afterCrossAndMutParentDominationCounter = 0;
    private int numberOfAfterCrossAndMutChecks = 0;
    private int afterCrossAfterCrossAndMutDominationCounter = 0;
    private int numberOfAfterCrossAfterCrossAndMutChecks = 0;
    private int afterCrossAndMutAfterCrossDominationCounter = 0;
    private int numberOfAfterCrossAndMutAfterCrossChecks = 0;

    public double getAfterCrossParentDominationProp() {
        return afterCrossParentDominationCounter/(double)numberOfAfterCrossChecks;
    }

    public double getAfterCrossAndMutParentDominationProp() {
        return afterCrossAndMutParentDominationCounter/(double)numberOfAfterCrossAndMutChecks;
    }

    public double getAfterCrossAfterCrossAndMutDominationProp() {
        return afterCrossAfterCrossAndMutDominationCounter/(double)numberOfAfterCrossAfterCrossAndMutChecks;
    }

    public double getAfterCrossAndMutAfterCrossDominationProp() {
        return afterCrossAndMutAfterCrossDominationCounter/(double)numberOfAfterCrossAndMutAfterCrossChecks;
    }

    public <PROBLEM extends BaseProblemRepresentation> void addDominanceStats(
            BaseIndividual<Integer, PROBLEM> firstParent,
            BaseIndividual<Integer, PROBLEM> secondParent,
            BaseIndividual<Integer, PROBLEM> childAfterCross,
            BaseIndividual<Integer, PROBLEM> childAfterCrossAndMut) {

        if(childAfterCross.dominates(firstParent)) {
            afterCrossParentDominationCounter++;
        }
        if(childAfterCross.dominates(secondParent)) {
            afterCrossParentDominationCounter++;
        }
        numberOfAfterCrossChecks += 2;

        if(childAfterCrossAndMut.dominates(firstParent)) {
            afterCrossAndMutParentDominationCounter++;
        } else {
            firstParent.recordUnsuccessfulUsage();
        }

        if(childAfterCrossAndMut.dominates(secondParent)) {
            afterCrossAndMutParentDominationCounter++;
        } else {
            secondParent.recordUnsuccessfulUsage();
        }

        numberOfAfterCrossAndMutChecks += 2;

        if(childAfterCross.dominates(childAfterCrossAndMut)) {
            afterCrossAfterCrossAndMutDominationCounter++;
        }
        if(childAfterCrossAndMut.dominates(childAfterCross)) {
            afterCrossAndMutAfterCrossDominationCounter++;
        }
        numberOfAfterCrossAndMutAfterCrossChecks += 2;
    }

    //FIXME: remove when TTP adjusted
    public <PROBLEM extends BaseProblemRepresentation> void addDominanceStats(
            BaseIndividual<Integer, PROBLEM> firstParent,
            BaseIndividual<Integer, PROBLEM> secondParent,
            BaseIndividual<Integer, PROBLEM> firstChildAfterCross,
            BaseIndividual<Integer, PROBLEM> secondChildAfterCross,
            BaseIndividual<Integer, PROBLEM> firstChildAfterCrossAndMut,
            BaseIndividual<Integer, PROBLEM> secondChildAfterCrossAndMut) {

        if(firstChildAfterCross.dominates(firstParent)) {
            afterCrossParentDominationCounter++;
        }
        if(firstChildAfterCross.dominates(secondParent)) {
            afterCrossParentDominationCounter++;
        }
        if(secondChildAfterCross.dominates(firstParent)) {
            afterCrossParentDominationCounter++;
        }
        if(secondChildAfterCross.dominates(secondParent)) {
            afterCrossParentDominationCounter++;
        }
        numberOfAfterCrossChecks += 4;

        if(firstChildAfterCrossAndMut.dominates(firstParent)) {
            afterCrossAndMutParentDominationCounter++;
        } else {
            firstParent.recordUnsuccessfulUsage();
        }

        if(firstChildAfterCrossAndMut.dominates(secondParent)) {
            afterCrossAndMutParentDominationCounter++;
        } else {
            secondParent.recordUnsuccessfulUsage();
        }

        if(secondChildAfterCrossAndMut.dominates(firstParent)) {
            afterCrossAndMutParentDominationCounter++;
        } else {
            firstParent.recordUnsuccessfulUsage();
        }

        if(secondChildAfterCrossAndMut.dominates(secondParent)) {
            afterCrossAndMutParentDominationCounter++;
        } else {
            secondParent.recordUnsuccessfulUsage();
        }

        numberOfAfterCrossAndMutChecks += 4;

        if(firstChildAfterCross.dominates(firstChildAfterCrossAndMut)) {
            afterCrossAfterCrossAndMutDominationCounter++;
        }
        if(secondChildAfterCross.dominates(secondChildAfterCrossAndMut)) {
            afterCrossAfterCrossAndMutDominationCounter++;
        }
        numberOfAfterCrossAfterCrossAndMutChecks += 2;

        if(firstChildAfterCrossAndMut.dominates(firstChildAfterCross)) {
            afterCrossAndMutAfterCrossDominationCounter++;
        }
        if(secondChildAfterCrossAndMut.dominates(secondChildAfterCross)) {
            afterCrossAndMutAfterCrossDominationCounter++;
        }
        numberOfAfterCrossAndMutAfterCrossChecks += 2;
    }

    public static double getAvgAfterCrossParentDominationCounter(List<OptimisationResult> listOfResults) {
        int sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossParentDominationCounter();
        }
        return sum/(double)listOfResults.size();
    }

    public static double getAvgAfterCrossAndMutParentDominationCounter(List<OptimisationResult> listOfResults) {
        int sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossAndMutParentDominationCounter();
        }
        return sum/(double)listOfResults.size();
    }

    public static double getAvgAfterCrossAfterCrossAndMutDominationCounter(List<OptimisationResult> listOfResults) {
        int sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossAfterCrossAndMutDominationCounter();
        }
        return sum/(double)listOfResults.size();
    }

    public static double getAvgAfterCrossAndMutAfterCrossDominationCounter(List<OptimisationResult> listOfResults) {
        int sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossAndMutAfterCrossDominationCounter();
        }
        return sum/(double)listOfResults.size();
    }

    public static double getAvgAfterCrossParentDominationProp(List<OptimisationResult> listOfResults) {
        double sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossParentDominationProp();
        }
        return sum/(double)listOfResults.size();
    }

    public static double getAvgAfterCrossAndMutParentDominationProp(List<OptimisationResult> listOfResults) {
        double sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossAndMutParentDominationProp();
        }
        return sum/(double)listOfResults.size();
    }

    public static double getAvgAfterCrossAfterCrossAndMutDominationProp(List<OptimisationResult> listOfResults) {
        double sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossAfterCrossAndMutDominationProp();
        }
        return sum/(double)listOfResults.size();
    }

    public static double getAvgAfterCrossAndMutAfterCrossDominationProp(List<OptimisationResult> listOfResults) {
        double sum = 0;
        for(var e: listOfResults) {
            sum += e.getAfterCrossAndMutAfterCrossDominationProp();
        }
        return sum/(double)listOfResults.size();
    }

    public int getAfterCrossParentDominationCounter() {
        return afterCrossParentDominationCounter;
    }

    public void setAfterCrossParentDominationCounter(int afterCrossParentDominationCounter) {
        this.afterCrossParentDominationCounter = afterCrossParentDominationCounter;
    }

    public int getNumberOfAfterCrossChecks() {
        return numberOfAfterCrossChecks;
    }

    public void setNumberOfAfterCrossChecks(int numberOfAfterCrossChecks) {
        this.numberOfAfterCrossChecks = numberOfAfterCrossChecks;
    }

    public int getAfterCrossAndMutParentDominationCounter() {
        return afterCrossAndMutParentDominationCounter;
    }

    public void setAfterCrossAndMutParentDominationCounter(int afterCrossAndMutParentDominationCounter) {
        this.afterCrossAndMutParentDominationCounter = afterCrossAndMutParentDominationCounter;
    }

    public int getNumberOfAfterCrossAndMutChecks() {
        return numberOfAfterCrossAndMutChecks;
    }

    public void setNumberOfAfterCrossAndMutChecks(int numberOfAfterCrossAndMutChecks) {
        this.numberOfAfterCrossAndMutChecks = numberOfAfterCrossAndMutChecks;
    }

    public int getAfterCrossAfterCrossAndMutDominationCounter() {
        return afterCrossAfterCrossAndMutDominationCounter;
    }

    public void setAfterCrossAfterCrossAndMutDominationCounter(int afterCrossAfterCrossAndMutDominationCounter) {
        this.afterCrossAfterCrossAndMutDominationCounter = afterCrossAfterCrossAndMutDominationCounter;
    }

    public int getNumberOfAfterCrossAfterCrossAndMutChecks() {
        return numberOfAfterCrossAfterCrossAndMutChecks;
    }

    public void setNumberOfAfterCrossAfterCrossAndMutChecks(int numberOfAfterCrossAfterCrossAndMutChecks) {
        this.numberOfAfterCrossAfterCrossAndMutChecks = numberOfAfterCrossAfterCrossAndMutChecks;
    }

    public int getAfterCrossAndMutAfterCrossDominationCounter() {
        return afterCrossAndMutAfterCrossDominationCounter;
    }

    public void setAfterCrossAndMutAfterCrossDominationCounter(int afterCrossAndMutAfterCrossDominationCounter) {
        this.afterCrossAndMutAfterCrossDominationCounter = afterCrossAndMutAfterCrossDominationCounter;
    }

    public int getNumberOfAfterCrossAndMutAfterCrossChecks() {
        return numberOfAfterCrossAndMutAfterCrossChecks;
    }

    public void setNumberOfAfterCrossAndMutAfterCrossChecks(int numberOfAfterCrossAndMutAfterCrossChecks) {
        this.numberOfAfterCrossAndMutAfterCrossChecks = numberOfAfterCrossAndMutAfterCrossChecks;
    }
}
