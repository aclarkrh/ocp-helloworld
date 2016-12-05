package org.jboss.examples.ticketmonster.kubedsl;

import com.google.common.collect.ImmutableMap;
import io.fabric8.openshift.api.model.TemplateBuilder;


public class RouteKubernetesModelProcessor {

    public void on(TemplateBuilder builder) {

        builder.addNewRouteObject()
                .withNewMetadata()
                    .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                    .withLabels(ConfigConstants.getLabels())
                    .withAnnotations(ImmutableMap.<String, String>of(
                            "description",
                            "Route for application's http service."))
                .endMetadata()
                .withNewSpec()
                	// Including host with an empty hostname causes route creation to fail as the json contains "host": ""
                	// which means that OCP creates the route with "" rather than generating if no host attribute is supplied
                    //.withHost(ConfigConstants.HOSTNAME_HTTP)
                    .withNewTo()
                        .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                    .endTo()
                .endSpec()
                .endRouteObject()

/* Synergy Appstore doesn't require an HTTPS route.
                .addNewRouteObject()
                .withNewMetadata()
                    .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION + "-https")
                    .withLabels(ConfigConstants.getLabels())
                    .withAnnotations(ImmutableMap.<String, String>of(
                            "description",
                            "Route for application's https service."))
                .endMetadata()
                .withNewSpec()
	             	// Including host with an empty hostname causes route creation to fail as the json contains "host": ""
                	// which means that OCP creates the route with "" rather than generating if no host attribute is supplied
	                //.withHost(ConfigConstants.HOSTNAME_HTTPS)
                    .withNewTo()
                        .withName(ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                    .endTo()
                    .withNewTls()
                        .withTermination("edge")
                    .endTls()
                .endSpec()
                .endRouteObject()
*/
                
                .build();
    }

}
