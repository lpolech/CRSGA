package runners.mtsp;


import algorithms.io.MTSPIO;
import algorithms.problem.mtsp.City;
import algorithms.problem.mtsp.DistanceMatrix;

public class MTSPRunner {

  private static String definitionFileA = "assets/definitions/MTSP/kroA200.tsp";
  private static String definitionFileB = "assets/definitions/MTSP/kroB200.tsp";

  public static void main(String[] args) {
    run();
  }

  private static void run() {
    MTSPIO reader = new MTSPIO();
    City[] citiesA = reader.getCities(definitionFileA);
    City[] citiesB = reader.getCities(definitionFileB);
    DistanceMatrix[] distances = new DistanceMatrix[2];
    distances[0] = new DistanceMatrix();
    distances[0].setDistances(distances[0].execute(citiesA));
    distances[1] = new DistanceMatrix();
    distances[1].setDistances(distances[1].execute(citiesB));

    System.out.println();
  }

}
