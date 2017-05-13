buildFlowJob('ELIS2-IntegrationGateBuildFlow') {
  description(readFileFromWorkspace('resources/integrationGateFlow/integrationGateFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/integrationGateFlow/groovyDSLIntegrationGateFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  blockOn('ELIS2-IntegrationGateBuildFlow') {
    blockLevel('GLOBAL')
    scanQueueFor('DISABLED')
  }
  logRotator {
    artifactDaysToKeep(45)
    daysToKeep(45)
    numToKeep(500)
  }
  parameters {
    stringParam("MAJOR", "8")
    stringParam("MINOR", "1")
    stringParam("SPRINT", "18")
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('http://10.103.130.42:8080/jenkins')
      room('#ci-jenkins2_0dsl')
      startNotification(true)
      notifyAborted(false)
      notifyFailure(true)
      notifyNotBuilt(false)
      notifySuccess(true)
      notifyUnstable(false)
      notifyBackToNormal(true)
      notifyRepeatedFailure(true)
      includeTestSummary(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(true)
      customMessage("IntegrationGateVersion: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}")
    }
  }
}
