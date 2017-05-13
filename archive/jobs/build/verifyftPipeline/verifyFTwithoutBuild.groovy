buildFlowJob('ELIS2-VERIFY-FT-WITHOUTBUILD') {
  description(readFileFromWorkspace('resources/verifyFTpieline/verifyFTwithoutBuildDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/verifyFTpieline/groovyDSLverifyFTwithoutBuild.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    artifactDaysToKeep(15)
    daysToKeep(15)
    numToKeep(15)
  }
  parameters {
    stringParam('ENVIRONMENT')
    stringParam('BTAR_VERSION', '6.1.122.1604')
    stringParam('NEXUS_ID', '6.1.122')
    stringParam('BUILD_VERSION', '1604', '(e.g for Release Candidate 5.1.104.124 this would be "124")')
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
