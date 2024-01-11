package algorithms.io;

import algorithms.problem.mkp.Item;
import algorithms.problem.mkp.Knapsack;
import algorithms.problem.mkp.KnapsackProblem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles input / output of a Multi--objective
 * Knapsack Problem
 */
public class MKPIOSimple extends BaseIO {

  public KnapsackProblem readDefinition(String capacityFile, String weightFile, String profitFile) {
    int capacity = readCapacity(capacityFile);
    if (capacity == -1) {
      return null;
    }
    List<Integer> weights = readList(weightFile);
    List<Integer> profits = readList(profitFile);
    if (weights == null || profits == null || weights.size() != profits.size()) {
      return null;
    }

    int weight;
    int profit;
    List<Item> items = new ArrayList<>();
    for (int i = 0; i < weights.size(); ++i) {
      weight = weights.get(i);
      profit = profits.get(i);
      items.add(new Item(i, profit, weight));
    }
    Knapsack knapsack = new Knapsack(capacity, items);
    List<Knapsack> knapsacks = new ArrayList<>();
    knapsacks.add(knapsack);

    return new KnapsackProblem(knapsacks);
  }

  private int readCapacity(String capacityFile) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(capacityFile));
      String line = reader.readLine();

      return Integer.parseInt(line);
    } catch (IOException e) {
      LOGGER.log(Level.FINE, e.toString());
      return -1;
    } finally {
      closeReader(reader);
    }
  }

  private List<Integer> readList(String weightFile) {
    BufferedReader reader = null;
    List<Integer> weights = new ArrayList<Integer>();
    try {
      reader = new BufferedReader(new FileReader(weightFile));
      String line;

      while ((line = reader.readLine()) != null) {
        line = line.replaceAll("\\s", "");
        weights.add(Integer.parseInt(line));
      }

    } catch (IOException e) {
      LOGGER.log(Level.FINE, e.toString());
      return null;
    } finally {
      closeReader(reader);
    }
    return weights;
  }

}
