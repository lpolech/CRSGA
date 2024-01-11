package algorithms.problem.mtsp;


/**
 * Distance matrix of a TSP. Contains all distances between cities.
 * Usually should contain zeroes on a diagonal and have equal
 * lower and upper triangles.
 */
public class DistanceMatrix {

  private double[][] distances;

  public double[][] execute(City[] cities) {
    int length = cities.length;
    double[][] distances = new double[length][];

    for (int i = 0; i < length; i++) {
      distances[i] = new double[length];
      for (int j = 0; j < length; j++) {
        if (j > i) {
          distances[i][j] = getDistanceBetween(cities[i], cities[j]);
        } else {
          distances[i][j] = distances[j][i];
        }
      }
    }

    return distances;
  }

  private double getDistanceBetween(City start, City end) {
    return Math.ceil(Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2)));
  }

  @Override
  public DistanceMatrix clone() {
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    int xlength = this.distances.length;
    int ylength = this.distances[0].length;
    double[][] distances = new double[xlength][];
    for (int i = 0; i < xlength; i++) {
      distances[i] = new double[ylength];
      for (int j = 0; j < ylength; j++) {
        distances[i][j] = this.distances[i][j];
      }
    }

    distanceMatrix.setDistances(distances);
    return distanceMatrix;
  }

  public double[][] getDistances() {
    return distances;
  }

  public void setDistances(double[][] distances) {
    this.distances = distances;
  }

}
