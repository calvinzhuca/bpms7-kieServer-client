package com.redhat.syseng.soleng.rhba;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.jms.Queue;
import java.util.stream.Collectors;
import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.api.marshalling.MarshallingFormat;



public class AppJms {

    public static void main(String[] args) throws NamingException {
        String username = "executionUser";
        String password = "password";
        String REMOTING_URL = new String("http-remoting://myapp-kieserver-test-rhba.rhdp.ocp.cloud.lab.eng.bos.redhat.com:80");
        
        String INITIAL_CONTEXT_FACTORY = new String("org.jboss.naming.remote.client.InitialContextFactory");
        //String INITIAL_CONTEXT_FACTORY = new String("org.wildfly.naming.client.WildFlyInitialContextFactory");
        
        String CONNECTION_FACTORY = new String("jms/RemoteConnectionFactory");
        String REQUEST_QUEUE_JNDI = new String("jms/queue/KIE.SERVER.REQUEST");
        String RESPONSE_QUEUE_JNDI = new String("jms/queue/KIE.SERVER.RESPONSE");

        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, REMOTING_URL);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        InitialContext context = new InitialContext(env);

        Queue requestQueue = (Queue) context.lookup(REQUEST_QUEUE_JNDI);
        Queue responseQueue = (Queue) context.lookup(RESPONSE_QUEUE_JNDI);
        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY);
        //KieServicesConfiguration conf = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue, username, password);
        KieServicesConfiguration conf = KieServicesFactory.newJMSConfiguration(context, username, password);

        //conf.setMarshallingFormat(MarshallingFormat.JSON);
        
        KieServicesClient client = KieServicesFactory.newKieServicesClient(conf);
        ProcessServicesClient processClient = client.getServicesClient(ProcessServicesClient.class);
        
        String containerId = "testProject1_1.0";
        String processDefinitionId = "testProject1.HW1";

        long processInstanceId = processClient.startProcess(containerId, processDefinitionId);
        System.out.println("Started process instance #" + processInstanceId);
        List<ProcessInstance> processInstances = processClient.findProcessInstances(containerId, 0, 100);
        System.out.println("Found processes IDs: " + processInstances.stream().map(ProcessInstance::getId).collect(Collectors.toList()));
    }
}
