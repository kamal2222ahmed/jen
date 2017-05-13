buildFlowJob('ELIS2-IntegrationGateBuildFlow') {
  description(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateFlow/integrationGateFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateFlow/groovyDSLIntegrationGateFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
    permissionAll('akkausha')
    permissionAll('ambutt')
    permissionAll('caaponte')
    permissionAll('cnwillia')
    permissionAll('fconwumb')
    permissionAll('jbfrimpo')
    permissionAll('jnugorji')
    permissionAll('jsjohnso')
    permissionAll('kchen1')
    permissionAll('nppatel')
    permissionAll('rthandavan')
    permissionAll('rvangar')
    permissionAll('skahmed')
    permissionAll('skkrishn')
    permissionAll('srkhan')
    permissionAll('srkombam')
    permissionAll('tabaig')
    permissionAll('wezewudi')
    permissionAll('wmfowlke')
    permissionAll('jgarciar')
    blocksInheritance()
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
    stringParam("MAJOR", "10")
    stringParam("MINOR", "1")
    stringParam("SPRINT", "70")
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#elis-pipeline')
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
      customMessage('IntegrationGateVersion: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
    }
  }
}
