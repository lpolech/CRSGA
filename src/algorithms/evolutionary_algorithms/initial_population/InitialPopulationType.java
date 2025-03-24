package algorithms.evolutionary_algorithms.initial_population;


public enum InitialPopulationType {

  RANDOM("RND"),
  NAIVE_SWAPS("NS"),
  DIVERSITY("DIV"),
  OPPOSITION("OPP"),
  OPPOSITION_INT("OPPI"),
  EVEN("EVN"),
  SHUFFLE("SHU"),
  RANDOM_TTP("RNDTTP"),
  READ_TTP("REDTTP"),
  READ_MSRCPSP("REDMS");

  private String abbreviation;
  public double numSwapsProportion;

  InitialPopulationType(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  InitialPopulationType(String abbreviation, double numSwapsProportion) {
    this.numSwapsProportion = numSwapsProportion;
    this.abbreviation = abbreviation + "(s:" + numSwapsProportion + ")";
  }

  public String toString() {
    return this.abbreviation;
  }

}
