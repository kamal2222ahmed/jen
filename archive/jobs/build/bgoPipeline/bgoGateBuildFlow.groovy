buildFlowJob('ELIS2-Test-BGO-BuildGateFlow') {
  description(readFileFromWorkspace('resources/bgoBuildGateFlow/bgoBuildGateBuildFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/bgoBuildGateFlow/groovyDSLBGObuildGateFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  throttleConcurrentBuilds {
    maxTotal(20)
    maxPerNode(4)
  }
  logRotator {
    artifactDaysToKeep(3)
    daysToKeep(45)
    numToKeep(500)
  }
  parameters {
    stringParam("GIT_BRANCH_NAME1", "master")
    stringParam("NEXUS_ID", "BGO")
  }
  triggers {
     cron('H/59 0-23/4 * * *')
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('http://10.103.130.42:8080/jenkins')
      room('#ci-jenkins2_0dsl')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(false)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage("${GIT_BRANCH_NAME1}")
    }
  }
}
