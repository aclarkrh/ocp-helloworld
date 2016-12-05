package org.jboss.examples.ticketmonster.kubedsl;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.openshift.api.model.BuildTriggerPolicy;
import io.fabric8.openshift.api.model.BuildTriggerPolicyBuilder;
import io.fabric8.openshift.api.model.TemplateBuilder;

    public class BuildConfigKubernetesModelProcessor {

    public void on(TemplateBuilder builder) {
        builder.addNewBuildConfigObject()
                .withNewMetadata()
                    .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                    .withLabels(ConfigConstants.getLabels())
                .endMetadata()
                .withNewSpec()
                    .withTriggers(getTriggers())
                    .withNewSource()
	                    .withNewGit()
	                        .withUri(ConfigConstants.GIT_URI)
	                        .withRef(ConfigConstants.SOURCE_REPOSITORY_REF)
	                    .endGit()
	                    .withType("Git")
	                    .withContextDir(ConfigConstants.CONTEXT_DIR)
	                    .withNewSourceSecret()
	                        .withName(ConfigConstants.SOURCE_SECRET)
	                    .endSourceSecret()
                    .endSource()
                    .withNewStrategy()
                        .withNewSourceStrategy()
                            .addNewEnv()
                                .withName("WAR_FILE_URL")
                                .withValue(ConfigConstants.WAR_FILE_URL)
                            .endEnv()
                            .withNewFrom()
                                .withKind("ImageStreamTag")
                                .withName(ConfigConstants.XPAAS_IS_NAME + ":" + ConfigConstants.XPAAS_IS_TAG)
                                .withNamespace(ConfigConstants.XPAAS_IS_PULL_NAMESPACE)
                            .endFrom()
                        .endSourceStrategy()
                        .withType("Source")
                    .endStrategy()
                    .withNewOutput()
                        .withNewTo()
                            .withKind("ImageStreamTag")
                            .withName(ConfigConstants.APPLICATION_NAME + ":" + ConfigConstants.APPLICATION_VERSION)
                        .endTo()
                    .endOutput()
                .endSpec()
            .endBuildConfigObject()
            .build();
    }

    private List<BuildTriggerPolicy> getTriggers() {

        List<BuildTriggerPolicy> p = new ArrayList<BuildTriggerPolicy>();

        p.add(new BuildTriggerPolicyBuilder()
                .withType("ImageChange")
                .withNewImageChange()
                .endImageChange()
                .build());

//        p.add(new BuildTriggerPolicyBuilder()
//                .withType("ConfigChange")
//                .build());


        return p;
    }

}
