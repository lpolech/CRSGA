package algorithms.problem.mkp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Knapsack used in a Knapsack Problem.
 * Has a capacity and a list of items that can be placed inside.
 */
public class Knapsack {

  private int capacity;
  private List<Item> items;

  public Knapsack(int c, List<Item> i) {
    capacity = c;
    items = i;
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public int getMaxWeight() {
    return items.stream().mapToInt(Item::getWeight).sum();
  }

  public int getMaxProfit() {
    List<Item> itemCopies = new ArrayList<>();
    for (Item item : items) {
      itemCopies.add(new Item(item.getId(), item.getProfit(), item.getWeight()));
    }
    itemCopies = itemCopies.stream().sorted((i1, i2) -> Double.compare((double)i2.getProfit() / (double)i2.getWeight(), (double)i1.getProfit() / (double)i1.getWeight()))
        .collect(Collectors.toList());
    int i = 1;
    int cap = itemCopies.get(0).getWeight();
    int maxProfit = itemCopies.get(0).getProfit();
    while (cap < capacity) {
      maxProfit += itemCopies.get(i).getProfit();
      cap += itemCopies.get(i).getWeight();
      ++i;
    }
    return maxProfit;
//    return items.stream().mapToInt(Item::getProfit).sum();
  }

  public Item getItem(int n) {
    return items.get(n);
  }

  public int getCapacity() {
    return capacity;
  }
}
