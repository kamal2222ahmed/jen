buildFlowJob('Test-Team-ELIS2-BuildGateFlow') {
  description(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateFlow/teamBuildGateFlowdescription.txt'))
  jdk('java-1.8.0_u102')
  concurrentBuild()
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateFlow/groovyDSLForTeamBuildGateFlow.txt'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', "anonymous")
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    artifactDaysToKeep(3)
    daysToKeep(45)
    numToKeep(500)
  }
  parameters {
    stringParam("GIT_BRANCH_NAME", "SKYNET-mastertest")
    description(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateFlow/teamBuildGateFlowdescription.txt'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('##ci-masterteambuild')
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
      customMessage("${GIT_BRANCH_NAME}")
    }
  }
}
