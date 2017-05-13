job('ELIS2-CLM-InternalApp') {
  description(readFileFromWorkspace('resources/acceptanceTesting/clmInternaAppDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    daysToKeep(60)
    numToKeep(25)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    stringParam('RELEASE_CANIDATE', '', 'Release Candidate Version')
  }
  steps {
    shell(readFileFromWorkspace('resources/acceptanceTesting/wgetinternalApprc.sh'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
      room('#ci-acceptancetest')
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
    buildName('#${ENV,var="BTAR_VERSION"}')
  }
}
