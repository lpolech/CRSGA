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

  InitialPopulationType(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String toString() {
    return this.abbreviation;
  }

}
