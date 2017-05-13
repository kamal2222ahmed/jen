job('AMI_CREATION_JOB_PARALLEL') {
  description(readFileFromWorkspace('resources/amiCreation/amiCreationParallelDescription.txt'))
  jdk('java-1.8.0_u102')
  label('ami-maker')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  parameters {
    stringParam('ENVIRONMENT', 'SKY', 'Environment Name for AMI')
    stringParam('RELEASE_CANIDATE', '8.1.2-419', 'Release Canidate Version')
  }
  steps {
      shell(readFileFromWorkspace('resources/amiCreation/amiCreationParallel.groovy'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
      room('#ci-amipipeline')
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
    buildName('${BUILD_NUMBER}${RELEASE_CANIDATE}')
  }
}
