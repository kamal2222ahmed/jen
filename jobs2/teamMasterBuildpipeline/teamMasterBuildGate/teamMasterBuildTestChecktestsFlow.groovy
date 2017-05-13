buildFlowJob('ELIS2-Team-MasterBuildTestCheck-Tests') {
  description(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateTestFlow/teamMasterBuildTestCheckTestsDescription.txt'))
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  concurrentBuild(true)
  buildFlow(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateTestFlow/groovyDSLteamMasterBuildGateTestFlow.txt'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    daysToKeep(15)
  }
  parameters {
    stringParam("ENVIRONMENT")
    stringParam("GIT_BRANCH_NAME")
    stringParam("NEXUS_ID", "STG")
    stringParam("SNAPSHOT_NUMBER", "2766")
    stringParam("TEAM")
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
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
  publishers {
    postBuildScripts {
      steps {
        shell(readFileFromWorkspace("resources2/teamMasterBuildTestCheck/teamMasterBuildGateTestFlow/postBuildTaskGetLogsFromTeamFT.sh"))
      }
    }
  }
}
