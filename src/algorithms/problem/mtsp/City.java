package algorithms.problem.mtsp;

/**
 * Defines a City on a route. Basically it is a point
 * with two coordinates. On a valid route every city
 * has to be visited exactly once.
 */
public class City {

  private int id;

  private double x;
  private double y;

  public City(int cityId, double x, double y) {
    this.id = cityId;
    this.x = x;
    this.y = y;
  }

  public int getId() {
    return id;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  @Override
  public String toString() {
    return "City{" +
        "id=" + id +
        ", x=" + x +
        ", y=" + y +
        '}';
  }
}
