import org.apache.commons.lang.StringUtils

def mvnCmd = "mvn -s configuration/settings.xml"

def bldNamespace = "${namespaceBase}-bld"
def devNamespace = "${namespaceBase}-dev"
def tstNamespace = "${namespaceBase}-tst"

def ocCmd = "oc --server=${ocpUrl} --insecure-skip-tls-verify --token=${ocpToken}"

node() {

  env.JAVA_HOME = tool name: 'JDK8', type: 'hudson.model.JDK'
  env.M2_HOME = tool name: 'Maven 3.3.9', type: 'hudson.tasks.Maven$MavenInstallation'
  env.PATH = "${env.JAVA_HOME}/bin:${env.M2_HOME}/bin:${env.PATH}"

  def artifactoryServer
  def artifactoryMaven

	// Artifactory Config
	artifactoryServer = Artifactory.server("${artifactoryServerId}")
	artifactoryMaven = Artifactory.newMavenBuild()
	artifactoryMaven.tool = 'Maven 3.3.9' // Tool name from Jenkins configuration
	artifactoryMaven.deployer releaseRepo:'${releaseRepo}', snapshotRepo:'${snapshotRepo}', server: artifactoryServer

  stage ('WAR Build') {
    git branch: "${gitBranch}", credentialsId: "${gitCredentialsId}", url: "${gitUrl}"
    artifactoryMaven.run pom: 'pom.xml', goals: 'clean install -DskipITs=true -s configuration/settings.xml'
  }

  stage ('OCP Config Build') {
    artifactoryMaven.deployer.artifactDeploymentPatterns.addInclude("**/*kubernetes-*.json")
	  artifactoryMaven.opts = "-Dfabric8.parameter.SOURCE_REPOSITORY_REF.value=${gitBranch}"
    artifactoryMaven.run pom: 'pom.xml', goals: 'clean install -P kube-dsl -s configuration/settings.xml'
  }

  stage ('Image Build') {
    def artifact = getArtifact()
    def version = getVersion()
    version = version.replaceAll("\\.", "-")
	  version = version.toLowerCase();
	  version = version.replaceAll("-snapshot", "-s")
	  version = version.replaceAll("-final", "-f")

    // We need to delete the bc oc apply on an existing bc resets status:lastVersion to 0.
    // We then get an error when we start the build - build no. already exists
    //sh "${ocCmd} delete bc ${artifact}-${version} --ignore-not-found=true -n ${bldNamespace}"
    sh "${ocCmd} process -f target/classes/kubernetes-build.json -n ${bldNamespace} | ${ocCmd} apply -n ${bldNamespace} -f -"
    sh "${ocCmd} start-build ${artifact}-${version} -n ${bldNamespace} --wait=true"
  }

  stage ('BLD Deploy') {
    sh "${ocCmd} process -f target/classes/kubernetes-run.json -n ${devNamespace} | ${ocCmd} apply -n ${bldNamespace} -f -"
  }
  
//  stage ('DEV Deploy') {
//    sh "${ocCmd} process -f target/classes/kubernetes-run.json -n ${devNamespace} | ${ocCmd} apply -n ${devNamespace} -f -"
//  }

//  stage ('TST Deploy') {
//    input "Deploy to TST?"
//    sh "${ocCmd} process -f target/classes/kubernetes-run.json -n ${tstNamespace} | ${ocCmd} apply -n ${tstNamespace} -f -"
//  }
}

def getArtifact() {
    def matcher = readFile('pom.xml') =~ '<artifactId>(.+)</artifactId>'
    matcher ? matcher[0][1] : null
}

def getVersion() {
    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
    matcher ? matcher[0][1] : null
}
