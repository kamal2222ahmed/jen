buildFlowJob('AMI_CREATION_JOB_PARALLEL') {
  description(readFileFromWorkspace('resources2/amiCreation/amiCreationParallelDescription.txt'))
  jdk('java-1.8.0_u102')
  label('ami-maker')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/amiCreation/amiCreationParallel.groovy'))
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
  }
  parameters {
    stringParam('ENVIRONMENT', 'ZDD', 'Environment Name for AMI')
    stringParam('RELEASE_CANDIDATE', '8.1.2-419', 'Release Candidate Version')
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
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
    buildName('#${BUILD_NUMBER} ${RELEASE_CANDIDATE}')
  }
}
