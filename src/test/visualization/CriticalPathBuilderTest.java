package test.visualization;

import algorithms.problem.scheduling.Resource;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.Skill;
import algorithms.problem.scheduling.Task;
import algorithms.validation.CompleteValidator;
import algorithms.validation.ValidationResult;
import algorithms.visualization.CriticalPathBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CriticalPathBuilderTest {
  private Schedule schedule;
  private CriticalPathBuilder builder = new CriticalPathBuilder();

  @Before
  public void setUp() {
    int[] predecessors = {1, 2};
    Task[] tasks = {
        new Task(1, new Skill[]{new Skill("1", 2)}, 4, new int[0]),
        new Task(2, new Skill[]{new Skill("2", 1)}, 7, new int[0]),
        new Task(3, new Skill[]{new Skill("1", 1)}, 5, predecessors),
    };
    Skill[] firstSkills = {new Skill("1", 2)};
    Skill[] secondSkills = {new Skill("2", 1)};
    Resource[] resources = {
        new Resource(1, 13, firstSkills),
        new Resource(2, 9, secondSkills),
    };
    schedule = new Schedule(tasks, resources);
    schedule.getTask(1).setStart(1);
    schedule.getTask(2).setStart(1);
    schedule.getTask(3).setStart(8);
    schedule.getTask(1).setResourceId(1);
    schedule.getTask(2).setResourceId(2);
    schedule.getTask(3).setResourceId(1);
    schedule.getResource(1).setFinish(4);
    schedule.getResource(1).setFinish(7);
    schedule.getResource(1).setFinish(12);
  }

  @Test
  public void checkScheduleValidity() {
    CompleteValidator validator = new CompleteValidator();
    assertEquals(ValidationResult.SUCCESS, validator.validate(schedule));
  }

  @Test
  public void checkCriticalPath() {
    Set<Integer> critical = builder.BuildCriticalPath(schedule);
    assertEquals(false, critical.contains(1));
    assertEquals(true, critical.contains(2));
    assertEquals(true, critical.contains(3));
  }
}
