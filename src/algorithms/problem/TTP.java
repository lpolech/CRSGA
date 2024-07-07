package algorithms.problem;

import algorithms.evolutionary_algorithms.ParameterSet;
import algorithms.problem.mkp.Knapsack;
import algorithms.problem.mtsp.City;
import algorithms.problem.mtsp.DistanceMatrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of a Travelling Thief Problem
 */
public class TTP extends BaseProblemRepresentation {

  private Knapsack knapsack;
  private int[] selection;
  private DistanceMatrix distanceMatrix;
  private int[] path;

  private double minSpeed;
  private double maxSpeed;
  private double rentingRatio;

  private double maxProfit;
  private double maxTravellingTime;
  private double minTravellingTime;

  // first index is the city, inner list contains id's of available items
  private List<List<Integer>> itemAvailabilities;

  public TTP(Knapsack k, City[] cities, double mnSpeed, double mxSpeed, double rr) {
    selection = new int[k.getItems().size()];
    distanceMatrix = new DistanceMatrix();
    distanceMatrix.setDistances(distanceMatrix.execute(cities));
    path = new int[distanceMatrix.getDistances().length];
    knapsack = k;
    minSpeed = mnSpeed;
    maxSpeed = mxSpeed;
    rentingRatio = rr;
    maxProfit = calculateMaxProfit();
    maxTravellingTime = calculateMaxTravellingTime();
    minTravellingTime = calculateMinTravellingTime();
  }

  public TTP(Knapsack k, DistanceMatrix matrix, double mnSpeed, double mxSpeed, double rr, double mxProfit, double maxTT, double minTT) {
    selection = new int[k.getItems().size()];
    distanceMatrix = matrix;
    path = new int[distanceMatrix.getDistances().length];
    knapsack = k;
    minSpeed = mnSpeed;
    maxSpeed = mxSpeed;
    rentingRatio = rr;
    maxProfit = mxProfit;
    maxTravellingTime = maxTT;
    minTravellingTime = minTT;
  }

  @Override
  public BaseProblemRepresentation buildSolution(List<? extends Number> genes,
                                                 ParameterSet<? extends Number, ? extends BaseProblemRepresentation> parameters) {
//    int[] sortedPathGenes = IntStream.range(0, path.length).boxed().sorted((i, j) ->
//        Double.compare(genes.get(i).doubleValue(), genes.get(j).doubleValue())).mapToInt( ele -> ele).toArray();
//    for (int i = 0; i < sortedPathGenes.length; ++i) {
//      path[i] = sortedPathGenes[i];
//    }
    List<Number> genesToModify = (List<Number>)genes;
    for (int i = 0; i < parameters.geneSplitPoint; ++i) {
      path[i] = (Integer)genes.get(i);
    }
    for (int i = 0; i < selection.length; ++i) {
      selection[i] = genes.get(path.length + i).intValue();
    }
    // TODO: create a constraint preserver
    List<Map.Entry<Integer, Double>> itemWithImpact = new ArrayList<>();
    List<Integer> availableItemsInCity;
    for (int i = 0; i < path.length - 1; ++i) {
      int city = path[i];
      availableItemsInCity = itemAvailabilities.get(city);
      for (int item : availableItemsInCity) {
        if (selection[item] > 0 && city == knapsack.getItem(item).getAvailability().get(selection[item] - 1)) {
          itemWithImpact.add(new AbstractMap.SimpleEntry<>(item, getItemPtofitToTravellingTimeWeight(item, city)));
        }
      }
    }
    itemWithImpact.sort(Map.Entry.comparingByValue());
    List<Integer> sortedKeys = itemWithImpact.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    int[] sortedIndices = sortedKeys.stream().mapToInt(e -> e).toArray();
//    IntStream.range(0, selection.length)
//        .boxed().sorted((i, j) -> Integer.compare(knapsack.getItem(i).getProfit() / knapsack.getItem(i).getWeight(),
//                                                  knapsack.getItem(j).getProfit() / knapsack.getItem(j).getWeight()) )
//        .mapToInt(ele -> ele).toArray();
    double currentWeight = getCurrentWeight();
    int index = 0;
    while (currentWeight > knapsack.getCapacity()) {
      if (selection[sortedIndices[index]] > 0) {
        selection[sortedIndices[index]] = 0;
        genesToModify.set(path.length + sortedIndices[index], 0);
        currentWeight -= knapsack.getItem(sortedIndices[index]).getWeight();
      }
      ++index;
    }
    this.setHashCode();
    return this;
  }

  public double getItemPtofitToTravellingTimeWeight(int itemNum, int pathIndex) {
    double[][] distances = distanceMatrix.getDistances();

    double distance = 0d;
    double itemWeight = knapsack.getItem(itemNum).getWeight();;
    double itemVelocity = maxSpeed - (itemWeight * ( (maxSpeed - minSpeed)  / knapsack.getCapacity() ));
    itemVelocity = Math.max(itemVelocity, minSpeed);

    for (int i = pathIndex; i < path.length - 1; ++i) {
      distance += distances[path[i]][path[i+1]];
    }

    //Traveling distance between last and first city
    distance += distances[path[path.length - 1]][path[0]];
    double time = distance / itemVelocity;
    double itemProfit = knapsack.getItem(itemNum).getProfit();
    return itemProfit / time;
  }

  @Override
  public void fixGenes(List<Number> genes) {
    int[] sortedIndices = IntStream.range(0, selection.length)
        .boxed().sorted((i, j) -> Integer.compare(knapsack.getItem(i).getProfit() / knapsack.getItem(i).getWeight(),
            knapsack.getItem(j).getProfit() / knapsack.getItem(j).getWeight()) )
        .mapToInt(ele -> ele).toArray();

    double currentWeight = 0d;
    for (int i = 0; i < selection.length; ++i) {
      if ((int)genes.get(i + path.length) > 0) {
        currentWeight += knapsack.getItem(i).getWeight();
      }
    }

    int index = 0;
    while (currentWeight > knapsack.getCapacity()) {
      if ((int)genes.get(path.length + sortedIndices[index]) > 0) {
        genes.set(path.length + sortedIndices[index], 0);
        currentWeight -= knapsack.getItem(sortedIndices[index]).getWeight();
      }
      ++index;
    }
  }

  public double getTravellingTime() {
    double[][] distances = distanceMatrix.getDistances();

    double time = 0d;
    double velocity;
    double currentWeight = 0d;
    List<Integer> availableItemsInCity;

    for (int i = 0; i < path.length - 1; ++i) {
      availableItemsInCity = itemAvailabilities.get(path[i]);
      for (int item : availableItemsInCity) {
        if (selection[item] > 0 && path[i] == knapsack.getItem(item).getAvailability().get(selection[item] - 1)) {
          currentWeight += knapsack.getItem(item).getWeight();
        }
      }
      velocity = maxSpeed - (currentWeight * ( (maxSpeed - minSpeed)  / knapsack.getCapacity() ));
      velocity = Math.max(velocity, minSpeed);
      time += distances[path[i]][path[i+1]] / velocity;
    }

    //Traveling time between last and first city
    availableItemsInCity = itemAvailabilities.get(path[path.length - 1]);
    for (int item : availableItemsInCity) {
      if (selection[item] > 0 && path[path.length - 1] == knapsack.getItem(item).getAvailability().get(selection[item] - 1)) {
        currentWeight += knapsack.getItem(item).getWeight();
      }
    }
    velocity = maxSpeed - (currentWeight * ( (maxSpeed - minSpeed)  / knapsack.getCapacity() ));
    velocity = Math.max(velocity, minSpeed);
    time += distances[path[path.length - 1]][path[0]] / velocity;
    return time;
  }

  public double getCurrentWeight() {
    double currentWeight = 0d;
    for (int i = 0; i < selection.length; ++i) {
      if (selection[i] > 0) {
        currentWeight += knapsack.getItem(i).getWeight();
      }
    }
    return currentWeight;
  }

  public double calculateMaxTravellingTime() {
    double maxDistance = getMaxDistance();
    return maxDistance / minSpeed;
  }

  public double calculateMinTravellingTime() {
    double minDistance = getMinDistance();
    return minDistance / maxSpeed;
  }

  private double getMaxDistance() {
    double[][] distances = distanceMatrix.getDistances();
    double distance = 0.0d;
    for (int i = 0; i < distances.length; ++i) {
      double maxDistance = 0;
      for (int j = 0; j < distances[i].length; ++j) { // do not assume symmetry
        if (distances[i][j] > maxDistance && i != j) {
          maxDistance = distances[i][j];
        }
      }
      distance += maxDistance;
    }
    return distance;
  }

  private double getMinDistance() {
    double[][] distances = distanceMatrix.getDistances();
    double distance = 0.0d;
    for (int i = 0; i < distances.length; ++i) {
      double minDistance = Double.MAX_VALUE;
      for (int j = 0; j < distances[i].length; ++j) { // do not assume symmetry
        if (distances[i][j] < minDistance && i != j) {
          minDistance = distances[i][j];
        }
      }
      distance += minDistance;
    }
    return distance;
  }

  private double calculateMaxProfit() {
    return knapsack.getMaxProfit();
  }

  /**
   * Upper bound of a tsp-gene is the number of cities.
   * Upper bound of a kp-gene is number of cities where given item is available
   * plus one (not taking the item).
   *
   * @return upper bounds of genes
   */
  public int[] getUpperBounds() {
    int numTSPGenes = path.length;
    int numKPGenes = selection.length;
    int[] upperBounds = new int[numTSPGenes + numKPGenes];
    for (int i = 0; i < numTSPGenes; ++i) {
      upperBounds[i] = path.length;
    }
    for (int i = 0; i < numKPGenes; ++i) {
      upperBounds[numTSPGenes + i] = knapsack.getItem(i).getAvailability().size() + 1;
    }
    return upperBounds;
  }

  /**
   * Point where genotype is divided into TSP and KP.
   *
   * @return split point of the genotype
   */
  public int getSplitPoint() {
    return path.length;
  }

  @Override
  public BaseProblemRepresentation cloneDeep() {
    TTP ttp = new TTP(knapsack, this.distanceMatrix, this.minSpeed, this.maxSpeed, this.rentingRatio, this.maxProfit, this.maxTravellingTime, this.minTravellingTime);
    ttp.setSelection(selection.clone());
    ttp.setPath(path.clone());
    ttp.setItemAvailabilities(itemAvailabilities);
    return ttp;
  }

  @Override
  public int getNumGenes() {
    return path.length + selection.length;
  }

  @Override
  public void setHashCode() {
    long hash = 0xCBF29CE484222325L;
    long prime = 0x100000001B3L;
    int[] sortedIndices = IntStream.range(0, path.length)
        .boxed().sorted((i, j) -> Integer.compare(path[i], path[j]) )
        .mapToInt(ele -> ele).toArray();
    for(int p : sortedIndices)
    {
      hash ^= Integer.hashCode(p);
      hash *= prime;
    }
    for(int s : selection)
    {
      hash ^= Integer.hashCode(s);
      hash *= prime;
    }
    this.hashCode = hash;
  }

  public int[] getSelection() {
    return selection;
  }

  public void setSelection(int[] selection) {
    this.selection = selection;
  }

  public int[] getPath() {
    return path;
  }

  public void setPath(int[] path) {
    this.path = path;
  }

  public DistanceMatrix getDistanceMatrix() {
    return distanceMatrix;
  }

  public List<List<Integer>> getItemAvailabilities() {
    return itemAvailabilities;
  }

  public void setItemAvailabilities(List<List<Integer>> itemAvailabilities) {
    this.itemAvailabilities = itemAvailabilities;
  }

  public Knapsack getKnapsack() {
    return knapsack;
  }

  public void setKnapsack(Knapsack knapsack) {
    this.knapsack = knapsack;
  }

  public double getMinSpeed() {
    return minSpeed;
  }

  public double getMaxSpeed() {
    return maxSpeed;
  }

  public double getRentingRatio() {
    return rentingRatio;
  }

  public double getMaxProfit() {
    return maxProfit;
  }

  public double getMaxTravellingTime() {
    return maxTravellingTime;
  }

  public double getMinTravellingTime() {
    return minTravellingTime;
  }
}
