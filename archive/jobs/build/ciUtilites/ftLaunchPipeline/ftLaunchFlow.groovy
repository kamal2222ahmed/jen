buildFlowJob('FT-launch-Flow') {
  description('1 Click Deployment of a FT Environment (APP + Database Server)')
  jdk('java-1.8.0_u102')
  buildNeedsWorkspace(false)
  buildFlow(readFileFromWorkspace('resources/ciUtilites/makeFTpipeline/ftLaunchFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(30)
  }
  parameters {
    stringParam('ENVIRONMENT', 'FT9000', 'Environment Name')
    stringParam('Version', '7.2.21.37', 'Version of Code' )
    choiceParam('DELETE', ['FALSE', 'TRUE'], 'TRUE - Delete chef node and client if it already exists.  FALSE - Do not delete node and client')
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
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
}
