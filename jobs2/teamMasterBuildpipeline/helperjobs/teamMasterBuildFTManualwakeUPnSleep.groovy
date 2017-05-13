job('ELIS2-Manual-Wake-N-Sleep-Team-FT') {
  description(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildFtManualWakeUPnSleep/teamMasterftWake-and-SleepManualdescription.txt'))
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  parameters {
    choiceParam("STATE", ['ENABLE', 'DISABLE'], 'To stop and turn off your team instance this should be DISABLE. To start and turn on your team instance this should be ENABLE.')
    choiceParam("TEAM", ['skynet', 'aetas', 'alliance', 'aria51', 'atomics', 'carbon', 'cobra', 'huns', 'initech', 'ion', 'mib', 'modulus', 'nok', 'nuka', 'olmec', 'protean', 'roti', 'sparta', 'synergy'])
  }
  steps {
    shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildFtManualWakeUPnSleep/teamMasterftWake-and-Sleep-Manual.sh'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-pipeline-fts')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      includeTestSummary(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage()
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="BUILD_NUMBER"}-${ENV,var="STATE"}-${ENV,var="TEAM"}')
  }
}
