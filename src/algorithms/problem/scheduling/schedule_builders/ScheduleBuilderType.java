package algorithms.problem.scheduling.schedule_builders;


public enum ScheduleBuilderType {

  SCHEDULE_BULDER("SB"),
  FORWARD_SCHEDULE_BUILDER("FSB"),
  BACKWARD_SCHEDULE_BUILDER("BSB");

  private String abbreviation;

  ScheduleBuilderType(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String toString() {
    return this.abbreviation;
  }
}
