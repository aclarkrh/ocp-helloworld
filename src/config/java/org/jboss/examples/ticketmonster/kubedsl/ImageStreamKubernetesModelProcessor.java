package org.jboss.examples.ticketmonster.kubedsl;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.fabric8.openshift.api.model.TemplateBuilder;

public class ImageStreamKubernetesModelProcessor {

    public void on(TemplateBuilder builder) {
        builder.addNewImageStreamObject()
                .withNewMetadata()
	                .withName(ConfigConstants.APPLICATION_NAME)
	                .withLabels(getLabels())
                .endMetadata()
                .withNewSpec()
                .endSpec()
                .endImageStreamObject()
                .build();
    }

    public static Map<String, String> getLabels() {
        return ImmutableMap.<String, String> builder()
                .put("app", ConfigConstants.APPLICATION_NAME)
                .build();
    }
}
