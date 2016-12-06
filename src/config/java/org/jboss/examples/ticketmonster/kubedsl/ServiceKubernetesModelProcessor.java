package org.jboss.examples.ticketmonster.kubedsl;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.fabric8.kubernetes.api.model.IntOrStringBuilder;
import io.fabric8.openshift.api.model.TemplateBuilder;

public class ServiceKubernetesModelProcessor {

    public void on(TemplateBuilder builder) {

        builder.addNewServiceObject()
                .withNewSpec()
                    .withPorts()
                        .addNewPort()
                            .withPort(ConfigConstants.EAP_HTTP_PORT)
                            .withTargetPort(new IntOrStringBuilder().withIntVal(ConfigConstants.EAP_HTTP_PORT).build())
                        .endPort()
                    .withSelector(getSelector())
                .endSpec()
                .withNewMetadata()
                    .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                    .withLabels(ConfigConstants.getLabels())
                    .withAnnotations(ImmutableMap.<String, String>of(
                            "description",
                            "The app server's http port."))
                .endMetadata()
                .endServiceObject()

                .build();

    }

    public static Map<String, String> getSelector() {
        return ImmutableMap.<String, String> builder()
                .put("deploymentconfig", ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                .build();
    }
}
