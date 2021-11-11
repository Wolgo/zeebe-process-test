package io.camunda.testing.util;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployProcessCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import java.util.HashMap;
import java.util.Map;

// @TODO Use this also in other tests
public class Utilities {

  public static final class ProcessPackLoopingServiceTask {
    public static final String RESOURCE_NAME = "looping-servicetask.bpmn";
    public static final String PROCESS_ID = "looping-servicetask";

    public static final String ELEMENT_ID = "servicetask"; // id of the service task
    public static final String JOB_TYPE = "test"; // job type of service task
    public static final String TOTAL_LOOPS =
        "totalLoops"; // variable name to indicate number of loops
  }

  public static final class ProcessPackMultipleTasks {
    public static final String RESOURCE_NAME = "multiple-tasks.bpmn";
    public static final String PROCESS_ID = "multiple-tasks";
  }

  public static DeploymentEvent deployProcess(final ZeebeClient client, final String process) {
    return deployProcesses(client, process);
  }

  public static DeploymentEvent deployProcesses(
      final ZeebeClient client, final String... processes) {
    final DeployProcessCommandStep1 commandStep1 = client.newDeployCommand();

    DeployProcessCommandStep1.DeployProcessCommandBuilderStep2 commandStep2 = null;
    for (final String process : processes) {
      if (commandStep2 == null) {
        commandStep2 = commandStep1.addResourceFromClasspath(process);
      } else {
        commandStep2 = commandStep2.addResourceFromClasspath(process);
      }
    }

    return commandStep2.send().join();
  }

  public static ProcessInstanceEvent startProcessInstance(
      final ZeebeClient client, final String processId) throws InterruptedException {
    return startProcessInstance(client, processId, new HashMap<>());
  }

  public static ProcessInstanceEvent startProcessInstance(
      final ZeebeClient client, final String processId, final Map<String, Object> variables)
      throws InterruptedException {
    final ProcessInstanceEvent instanceEvent =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId(processId)
            .latestVersion()
            .variables(variables)
            .send()
            .join();
    Thread.sleep(100);
    return instanceEvent;
  }
}