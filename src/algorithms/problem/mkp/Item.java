package algorithms.problem.mkp;

import java.util.ArrayList;
import java.util.List;

/**
 * Item used in a Knapsack Problem. Contains weight and price (profit).
 * In a multi-objective case, if an item has different properties for different
 * knapsacks, multiple instances of an Item are created.
 */
public class Item {

  private int id;

  private int profit;
  private int weight;

  // availability for TTP problem
  private List<Integer> availability;

  public Item(int i, int p, int w) {
    id = i;
    profit = p;
    weight = w;
    availability = new ArrayList<>();
  }

  public int getWeight() {
    return weight;
  }

  public int getProfit() {
    return profit;
  }

  public int getId() {
    return id;
  }

  public void addAvailabitliy(int a) {
    availability.add(a);
  }

  public List<Integer> getAvailability() {
    return availability;
  }

  public void setAvailability(List<Integer> availability) {
    this.availability = availability;
  }
}
