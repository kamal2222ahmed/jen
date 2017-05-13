job('ELIS2-Team-FT-Wake-Up-N-Sleep') {
  description(readFileFromWorkspace('resources/helperJobs/teamMasterftWake-and-Sleepdescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  parameters {
    stringParam("BRANCH")
    choiceParam("STATE", ['ENABLE', 'DISABLE'], 'To start your team instance this should be ENABLE. To stop your team instance this should be DISABLE.')
  }
   steps {
      shell(readFileFromWorkspace('resources/helperJobs/teamMasterftWake-and-Sleep.sh'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
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
    buildName('#${"BUILD_NUMBER"}-${"BRANCH"}')
  }
}
