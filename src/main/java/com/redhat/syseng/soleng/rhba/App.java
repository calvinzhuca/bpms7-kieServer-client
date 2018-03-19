package com.redhat.syseng.soleng.rhba;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;


public class App {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8180/kie-execution-server/services/rest/server";
        //below is to be used for kie server in OCP. 
        //String serverUrl = "http://myapp-kieserver-test-rhba.rhdp.ocp.cloud.lab.eng.bos.redhat.com/services/rest/server";

        String username = "executionUser";
        String password = "password";
        KieServicesClient client = KieServicesFactory.newKieServicesRestClient(serverUrl, username, password);
        System.out.println(client.getClass().getName());
        for (KieContainerResource kieContainerResource : client.listContainers().getResult().getContainers()) {
            System.out.println("Got kie container " + kieContainerResource.getContainerId());
        }
        ProcessServicesClient processClient = client.getServicesClient(ProcessServicesClient.class);
        String containerId = "testProject1_1.0";
        String processDefinitionId = "testProject1.HW1";
        Map<String, Object> variables = new HashMap<String, Object>();
        /*
        variables.put("name", "John Doe");
        variables.put("age", 21);
*/
        long processInstanceId = processClient.startProcess(containerId, processDefinitionId, variables);
        System.out.println("Started process instance #" + processInstanceId);
        List<ProcessInstance> processInstances = processClient.findProcessInstances(containerId, 0, 100);
        System.out.println( "Found processes IDs: " + processInstances.stream().map(ProcessInstance::getId).collect(Collectors.toList()) );
    }
}
