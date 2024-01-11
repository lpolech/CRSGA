package algorithms.io;


import algorithms.problem.mtsp.City;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles input / output of a Multi--objective
 * Traveling Salesman Problem
 */
public class MTSPIO extends BaseIO {

  public City[] getCities(String fileName) {
    List<City> cities = new LinkedList<>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(fileName));
      String line = reader.readLine();

      //header
      line = skipTo(reader, line, "DIMENSION");
      Integer dimension = Integer.valueOf(line.split(" ")[1]);
      skipTo(reader, line, "NODE_COORD");

      //data
      while ((line = reader.readLine()) != null
          && cities.size() < dimension) {
        cities.add(createCity(line));
      }
    } catch (IOException e) {
      LOGGER.log(Level.FINE, e.toString());
      return null;
    } finally {
      closeReader(reader);
    }

    return cities.toArray(new City[cities.size()]);
  }

  private City createCity(String line) {
    String[] split = line.split("\\s+");
    return new City(Integer.valueOf(split[0]) - 1, Integer.valueOf(split[1]), Integer.valueOf(split[2]));
  }

}
