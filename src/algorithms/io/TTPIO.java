package algorithms.io;

import algorithms.problem.TTP;
import algorithms.problem.mkp.Item;
import algorithms.problem.mkp.Knapsack;
import algorithms.problem.mtsp.City;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles input / output of a Multi--objective
 * Traveling Thief Problem
 */
public class TTPIO extends BaseIO {

  public TTP readDefinition(String definitionFile) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(definitionFile));

      String line;
      int numCities;
      int numItems;
      int capacity;
      double minSpeed;
      double maxSpeed;
      double rentingRatio;
      String[] parts;

      // header
      reader.readLine(); // problem name
      reader.readLine(); // knapsack data type
      // number of cities
      line = reader.readLine();
      parts = line.split(":\\s+");
      numCities = Integer.parseInt(parts[1]);
      // number of items
      line = reader.readLine();
      parts = line.split(":\\s+");
      numItems = Integer.parseInt(parts[1]);
      // capacity
      line = reader.readLine();
      parts = line.split(":\\s+");
      capacity = Integer.parseInt(parts[1]);
      // min speed
      line = reader.readLine();
      parts = line.split(":\\s+");
      minSpeed = Double.parseDouble(parts[1]);
      // max speed
      line = reader.readLine();
      parts = line.split(":\\s+");
      maxSpeed = Double.parseDouble(parts[1]);
      // renting ratio
      line = reader.readLine();
      parts = line.split(":\\s+");
      rentingRatio = Double.parseDouble(parts[1]);
      reader.readLine(); // edge weigh type

      reader.readLine(); // tsp header

      City[] cities = readCities(reader, line, parts, numCities);

      reader.readLine(); // kp header

      Knapsack knapsack = readKnapsack(reader, line, parts, numItems, capacity);

      TTP ttp = new TTP(knapsack, cities, minSpeed, maxSpeed, rentingRatio);
      List<List<Integer>> itemAvailabilities = new ArrayList<>(numCities);
      for (int i = 0; i < numCities; ++i) {
        itemAvailabilities.add(new ArrayList<>());
      }
      for (Item item : knapsack.getItems()) {
        for (Integer availability : item.getAvailability()) {
          itemAvailabilities.get(availability).add(item.getId() - 1);
        }
      }
      ttp.setItemAvailabilities(itemAvailabilities);

      return ttp;
    } catch (IOException e) {
      LOGGER.log(Level.FINE, e.toString());
      return null;
    } finally {
      closeReader(reader);
    }
  }

  private City[] readCities(BufferedReader reader, String line, String[] parts, int numCities) throws IOException {
    City[] cities = new City[numCities];
    City city;
    for (int i = 0; i < numCities; ++i) {
      line = reader.readLine();
      parts = line.split("\\s");
      city = new City(Integer.parseInt(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
      cities[i] = city;
    }
    return cities;
  }

  private Knapsack readKnapsack(BufferedReader reader, String line, String[] parts, int numItems, int capacity) throws IOException {
    List<Item> items = new ArrayList<>(numItems);
    Item item;
    for (int i = 0; i < numItems; ++i) {
      line = reader.readLine();
      parts = line.split("\\s");
      item = new Item(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
      item.addAvailabitliy(Integer.parseInt(parts[3]) - 1);
      items.add(item);
    }
    return new Knapsack(capacity, items);
  }

}
