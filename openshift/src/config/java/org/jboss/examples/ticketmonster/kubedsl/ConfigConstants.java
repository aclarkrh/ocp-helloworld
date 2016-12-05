package org.jboss.examples.ticketmonster.kubedsl;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ConfigConstants {
	
	public static final String APPLICATION_NAME = "${APPLICATION_NAME}";
	public static final String APPLICATION_VERSION = "${APPLICATION_VERSION}";
	public static final String APPLICATION_NAME_WITH_VERSION = APPLICATION_NAME + "-" + APPLICATION_VERSION;
    
	public static final String GIT_URI = "${GIT_URI}";
    public static final String SOURCE_REPOSITORY_REF = "${SOURCE_REPOSITORY_REF}";
    public static final String CONTEXT_DIR = "${CONTEXT_DIR}";
	public static final String SOURCE_SECRET = "${SOURCE_SECRET}";

    public static final String WAR_FILE_URL = "${WAR_FILE_URL}";
    
    public static final String XPAAS_IS_PULL_NAMESPACE = "${XPAAS_IS_PULL_NAMESPACE}";
    public static final String XPAAS_IS_NAME = "${XPAAS_IS_NAME}";
    public static final String XPAAS_IS_TAG = "${XPAAS_IS_TAG}";
    
    public static final String APP_IS_PULL_NAMESPACE = "${APP_IS_PULL_NAMESPACE}";

    public static final String HOSTNAME_HTTP = "${HOSTNAME_HTTP}";
    public static final String HOSTNAME_HTTPS = "${HOSTNAME_HTTPS}";
    
    public static final String READINESS_PROBE_PATH = "${READINESS_PROBE_PATH}";
    public static final int READINESS_PROBE_INITIAL_DELAY_SECS = 30;
    public static final int READINESS_PROBE_TIMEOUT_SECS = 10;

    public static final String LIVENESS_PROBE_PATH = "${LIVENESS_PROBE_PATH}";
    public static final int LIVENESS_PROBE_INITIAL_DELAY_SECS = 180;
    public static final int LIVENESS_PROBE_TIMEOUT_SECS = 10;
    public static final int LIVENESS_PROBE_FAILURE_THRESHOLD = 3;
    public static final int LIVENESS_PROBE_SUCCESS_THRESHOLD = 1;
    	    
    public static final int EAP_HTTP_PORT = 8080;
	public static final int EAP_JOLOKIA_PORT = 8778;

    public static final String PROTOCOL_TCP = "TCP";
    
    public static final String CPU_REQUEST = "${CPU_REQUEST}";
    public static final String MEM_REQUEST = "${MEM_REQUEST}";
    public static final String CPU_LIMIT = "${CPU_LIMIT}";
    public static final String MEM_LIMIT = "${MEM_LIMIT}";
    
    public static final String DB_CONFIGMAP_NAME = "${DB_CONFIGMAP_NAME}";
    public static final String DB_SECRET_NAME = "${DB_SECRET_NAME}";
    
    public static Map<String, String> getLabels() {
        return ImmutableMap.<String, String> builder()
                .put("app", ConfigConstants.APPLICATION_NAME_WITH_VERSION)
                .build();
    }
}
