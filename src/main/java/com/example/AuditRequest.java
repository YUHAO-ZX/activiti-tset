package com.example;

import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.text.ParseException;
import java.util.*;

/**
 * Created by niceday on 17/5/9.
 */
public class AuditRequest {
    static TaskService taskService;
    static FormService formService;
    static HistoryService historyService;

    public static void main(String[] args) throws ParseException {
//        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
//                .setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
//                .setJdbcUsername("sa")
//                .setJdbcPassword("")
//                .setJdbcDriver("org.h2.Driver")
//                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://172.20.17.51:3306/activiti2")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        String pName = processEngine.getName();
        String ver = ProcessEngine.VERSION;
        System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");

        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        System.out.println(
                "Found process definition ["
                        + processDefinition.getName() + "] with id ["
                        + processDefinition.getId() + "]");

        RuntimeService runtimeService = processEngine.getRuntimeService();
        //执行入口
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("anyway");
        System.out.println("Onboarding process started with process instance id ["
                + processInstance.getProcessInstanceId()
                + "] key [" + processInstance.getProcessDefinitionKey() + "]");

        taskService = processEngine.getTaskService();
        formService = processEngine.getFormService();
        historyService = processEngine.getHistoryService();

        while (processInstance != null && !processInstance.isEnded()) {
            List<Task> tasks = taskService.createTaskQuery()
                    .taskCandidateGroup("managers").list();

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if(task.getName().equals("一级审批")){
                    level1Audit(task);
                }else if(task.getName().equals("二级审批")){
                    level2Audit(task);
                }
            }
            processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstance.getId()).singleResult();
        }
    }

    private static void level1Audit(Task task){
        Map<String, Object> variables = new HashMap<String, Object>();
        FormData formData = formService.getTaskFormData(task.getId());
        for (FormProperty formProperty : formData.getFormProperties()) {
            String message = formProperty.getId();
            if(message.equals("message")){
                variables.put(formProperty.getId(), "imp1");
            }
        }
        taskService.complete(task.getId(),variables);
        System.out.println("################level1Audit finish###################");
    }

    private static void level2Audit(Task task){
        Map<String, Object> variables = new HashMap<String, Object>();
        FormData formData = formService.getTaskFormData(task.getId());
        for (FormProperty formProperty : formData.getFormProperties()) {
            String message = formProperty.getId();
            if(message.equals("idea")){
                variables.put(formProperty.getId(), "注意权限安全");
            }
        }
        taskService.complete(task.getId(),variables);
        System.out.println("################level2Audit finish###################");
    }
}
