package org.jboss.examples.ticketmonster.kubedsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.HTTPGetAction;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.openshift.api.model.DeploymentTriggerImageChangeParams;
import io.fabric8.openshift.api.model.DeploymentTriggerPolicy;
import io.fabric8.openshift.api.model.TemplateBuilder;
import io.fabric8.utils.Lists;

public class DeploymentConfigKubernetesModelProcessor {

    public void on(TemplateBuilder builder) {
        builder.addNewDeploymentConfigObject()
                .withNewMetadata()
                    .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                    .withLabels(ConfigConstants.getLabels())
                .endMetadata()
                .withNewSpec()
                    .withNewStrategy()
                        .withType("Rolling")
                    .endStrategy()
                .withTriggers(getTriggers())
                .withReplicas(1)
                    .withSelector(getSelectors())
                    .withNewTemplate()
                        .withNewMetadata()
                            .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                            .withLabels(getDcLabels())
                        .endMetadata()
                        .withNewSpec()
                            .withTerminationGracePeriodSeconds(60L)
                            .withContainers(getContainers())
                            .withRestartPolicy("Always")
                        .endSpec()
                    .endTemplate()

                .endSpec()
                .endDeploymentConfigObject()

                .build();
    }


    private Map<String, String> getDcLabels() {
    	return ImmutableMap.<String, String> builder()
                .put("app", ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                .put("deploymentconfig", ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                .build();
	}


	private List<DeploymentTriggerPolicy> getTriggers() {
        DeploymentTriggerPolicy configChange = new DeploymentTriggerPolicy();
        configChange.setType("ConfigChange");

        ObjectReference from = new ObjectReference();
        from.setName(ConfigConstants.APPLICATION_NAME + ":" + ConfigConstants.APPLICATION_VERSION);
        from.setKind("ImageStreamTag");
        from.setNamespace(ConfigConstants.APP_IS_PULL_NAMESPACE);

        DeploymentTriggerImageChangeParams imageChangeParms = new DeploymentTriggerImageChangeParams();
        imageChangeParms.setFrom(from);
        imageChangeParms.setAutomatic(false);

        DeploymentTriggerPolicy imageChange = new DeploymentTriggerPolicy();
        imageChange.setType("ImageChange");
        imageChange.setImageChangeParams(imageChangeParms);
        imageChangeParms.setContainerNames(Lists.newArrayList(ConfigConstants.APPLICATION_NAME_WITH_VERSION));

        List<DeploymentTriggerPolicy> triggers = new ArrayList<DeploymentTriggerPolicy>();
        triggers.add(configChange);
        triggers.add(imageChange);

        return triggers;
    }

    private List<ContainerPort> getPorts() {
        List<ContainerPort> ports = new ArrayList<ContainerPort>();

        ContainerPort http = new ContainerPort();
        http.setContainerPort(ConfigConstants.EAP_HTTP_PORT);
        http.setProtocol(ConfigConstants.PROTOCOL_TCP);
        http.setName("http");

        ports.add(http);

        ContainerPort jolokia = new ContainerPort();
        jolokia.setContainerPort(ConfigConstants.EAP_JOLOKIA_PORT);
        jolokia.setProtocol(ConfigConstants.PROTOCOL_TCP);
        jolokia.setName("jolokia");

        ports.add(jolokia);

        return ports;
    }

    private Container getContainers() {
        Container container = new Container();

        container.setName(ConfigConstants.APPLICATION_NAME_WITH_VERSION);
//        container.setImage(ConfigConstants.APP_IS_PULL_NAMESPACE + "/" + ConfigConstants.APPLICATION_NAME + ":" + ConfigConstants.APPLICATION_VERSION);
        container.setImagePullPolicy("Always");
        container.setReadinessProbe(getReadinessProbe());
        container.setLivenessProbe(getLivenessProbe());
        container.setPorts(getPorts());
        //container.setEnv(getEnv());
        container.setResources(getResourceRequirements());
        return container;
    }

    private Map<String, String> getSelectors() {
        Map<String, String> selectors = new HashMap<>();
        selectors.put("app", ConfigConstants.APPLICATION_NAME_WITH_VERSION);
        selectors.put("deploymentconfig", ConfigConstants.APPLICATION_NAME_WITH_VERSION);

        return selectors;
    }

    private Probe getLivenessProbe() {
        Probe liveProbe = new Probe();
        
        HTTPGetAction httpGet = new HTTPGetAction();
        httpGet.setPath(ConfigConstants.LIVENESS_PROBE_PATH);
        httpGet.setPort(new IntOrString(ConfigConstants.EAP_HTTP_PORT));
        httpGet.setScheme("HTTP"); 	
        
        liveProbe.setHttpGet(httpGet);
        // Can't parameterise these as we're forced to use an Integer so s placeholder string doesn't compile.
        liveProbe.setInitialDelaySeconds(ConfigConstants.LIVENESS_PROBE_INITIAL_DELAY_SECS);
        liveProbe.setTimeoutSeconds(ConfigConstants.LIVENESS_PROBE_TIMEOUT_SECS);
        liveProbe.setFailureThreshold(ConfigConstants.LIVENESS_PROBE_FAILURE_THRESHOLD);
        liveProbe.setSuccessThreshold(ConfigConstants.LIVENESS_PROBE_SUCCESS_THRESHOLD);

        return liveProbe;
    }


    private Probe getReadinessProbe() {
        Probe readyProbe = new Probe();
        
        HTTPGetAction httpGet = new HTTPGetAction();
        httpGet.setPath(ConfigConstants.READINESS_PROBE_PATH);
        httpGet.setPort(new IntOrString(ConfigConstants.EAP_HTTP_PORT));
        httpGet.setScheme("HTTP"); 	
        
        readyProbe.setHttpGet(httpGet);
        // Can't parameterise this as we're forced to use an Integer so s placeholder string doesn't compile.
        readyProbe.setInitialDelaySeconds(ConfigConstants.READINESS_PROBE_INITIAL_DELAY_SECS);
        readyProbe.setTimeoutSeconds(ConfigConstants.READINESS_PROBE_TIMEOUT_SECS);
        
        return readyProbe;

    }

    private ResourceRequirements getResourceRequirements() {
        ResourceRequirements resourceRequirements = new ResourceRequirements();
        resourceRequirements.setRequests(getRequests());
        resourceRequirements.setLimits(getLimits());

        return resourceRequirements;
    }

    private Map<String, Quantity> getRequests() {
        Map<String, Quantity> limits = new HashMap<String, Quantity>();
        limits.put("cpu", new Quantity(ConfigConstants.CPU_REQUEST));
        limits.put("memory", new Quantity(ConfigConstants.MEM_REQUEST));

        return limits;
    }

    private Map<String, Quantity> getLimits() {
        Map<String, Quantity> limits = new HashMap<String, Quantity>();
        limits.put("cpu", new Quantity(ConfigConstants.CPU_LIMIT));
        limits.put("memory", new Quantity(ConfigConstants.MEM_LIMIT));

        return limits;
    }

}
