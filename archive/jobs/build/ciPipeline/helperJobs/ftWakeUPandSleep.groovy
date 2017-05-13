job('ELIS2-FT-Wake-Up-N-Sleep') {
  description(readFileFromWorkspace('resources/helperJobs/teamMasterftWake-and-Sleepdescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  concurrentBuild()
  parameters {
    stringParam("ENVIRONMENT")
    choiceParam("STATE", ['ON', 'OFF'], 'To start FT instance this should be ON. To stop FT instance this should be OFF.')
  }
  steps {
      shell(readFileFromWorkspace('resources/helperJobs/ftWake-and-Sleep.sh'))
  }
  publishers {
      postBuildScripts {
      steps {
        shell(readFileFromWorkspace("resources/helperJobs/ftWake-and-Sleep-postBuild.txt"))
        onlyIfBuildFails(false)
        onlyIfBuildSucceeds(false)
      }
    }
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
    credentialsBinding {
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="ENVIRONMENT"}-${ENV,var="STATE"}-${BUILD_NUMBER}')
  }
}
