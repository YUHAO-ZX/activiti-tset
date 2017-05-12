package com.example;

import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by niceday on 17/5/9.
 */
public class AskForLeaveRequest {
    public static void main(String[] args) {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        String pName = processEngine.getName();
        String ver = ProcessEngine.VERSION;
        System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");

        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("AskForLeave.bpmn").deploy();
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
//                .deploymentId(deployment.getId()).singleResult();
//        System.out.println(
//                "Found process definition ["
//                        + processDefinition.getName() + "] with id ["
//                        + processDefinition.getId() + "]");

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("myProcess_1");
//        System.out.println("Onboarding process started with process instance id ["
//                + processInstance.getProcessInstanceId()
//                + "] key [" + processInstance.getProcessDefinitionKey() + "]");

        TaskService taskService = processEngine.getTaskService();

        Scanner scanner = new Scanner(System.in);

        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup("managers").list();

        for (int i = 0; i < tasks.size(); i++) {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("message","important");
            Task task = tasks.get(i);
            taskService.complete(task.getId(), variables);
        }

        runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId()).singleResult();

        tasks = taskService.createTaskQuery()
                .taskCandidateGroup("managers").list();


        for (int i = 0; i < tasks.size(); i++) {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("message","important");
            Task task = tasks.get(i);
            taskService.complete(task.getId(), variables);
        }


        scanner.close();
    }
}
