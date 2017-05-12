package com.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Date;

/**
 * Created by niceday on 17/5/9.
 */
public class AutomatedDataDelegate implements JavaDelegate {
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Date now = new Date();
        delegateExecution.setVariable("autoWelcomeTime", now);
        System.out.println("Faux call to backend for ["
                + delegateExecution.getVariable("fullName") + "]");
    }
}
