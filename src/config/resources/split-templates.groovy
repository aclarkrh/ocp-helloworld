import groovy.json.*

def jsonText = new File("${project.build.directory}/classes/kubernetes.json").text
def kubeMap = new JsonSlurper().parseText(jsonText)

def buildObjects = []
def deployObjects = []

def buildTemplate = kubeMap.getClass().newInstance(kubeMap)
def deployTemplate = kubeMap.getClass().newInstance(kubeMap)

kubeMap.objects.each() {

    if(it.kind == 'BuildConfig' || it.kind == 'ImageStream'){
        buildObjects.add(it)

    }else{
        deployObjects.add(it)
    }
}

def buildParams = ['APPLICATION_NAME', 'APPLICATION_VERSION', 'SOURCE_REPOSITORY_REF', 'GIT_URI', 'CONTEXT_DIR', 'SOURCE_SECRET', 'APP_IS_TAG', 'WAR_FILE_URL', 'XPAAS_IS_NAME', 'XPAAS_IS_TAG', 'XPAAS_IS_PULL_NAMESPACE']
def deployParams = ['APPLICATION_NAME', 'APPLICATION_VERSION', 'APP_IS_PULL_NAMESPACE', 'HOSTNAME_HTTP', 'HOSTNAME_HTTPS', 'READINESS_PROBE_PATH', 'LIVENESS_PROBE_PATH', 'PERSISTENT_VOLUME_CLAIM_SIZE', 'PERSISTENT_VOLUME_MOUNT_PATH', 'CPU_REQUEST', 'MEM_REQUEST', 'CPU_LIMIT', 'MEM_LIMIT', 'DB_CONFIGMAP_NAME', 'DB_SECRET_NAME']

buildTemplate.objects = buildObjects
deployTemplate.objects = deployObjects

def buildParamList = kubeMap.parameters.findAll{ buildParams.contains(it.name) == true }
def deployParamList = kubeMap.parameters.findAll{ deployParams.contains(it.name) == true }

buildTemplate.parameters = buildParamList
deployTemplate.parameters = deployParamList

def buildTemplateFile = new File("${project.build.directory}/classes/kubernetes-build.json")
buildTemplateFile << JsonOutput.prettyPrint(JsonOutput.toJson(buildTemplate))
def runTemplateFile = new File("${project.build.directory}/classes/kubernetes-run.json")
runTemplateFile << JsonOutput.prettyPrint(JsonOutput.toJson(deployTemplate))