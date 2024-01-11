package test.validation;

import algorithms.io.MSRCPSPIO;
import algorithms.problem.scheduling.Schedule;
import algorithms.problem.scheduling.Task;
import algorithms.validation.AssignmentValidator;
import algorithms.validation.BaseValidator;
import algorithms.validation.ValidationResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class AssignmentValidatorTest {

  @Test
  public void testValidate() {
    MSRCPSPIO reader = new MSRCPSPIO();
    Schedule schedule = reader.readDefinition("assets/test/10_7_10_7.def");
    assertNotNull("Schedule was not readDefinition correctly", schedule);

    BaseValidator validator = new AssignmentValidator();
    assertEquals("Assignment constraint should be violated",
        ValidationResult.FAILURE, validator.validate(schedule));

    for (Task task : schedule.getTasks()) {
      schedule.assign(task, schedule.getResource(1));
    }

    assertEquals("Assignment constraint should not be violated",
        ValidationResult.SUCCESS, validator.validate(schedule));

  }

}