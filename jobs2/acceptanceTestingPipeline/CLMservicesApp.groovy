job('ELIS2-CLM-ServicesApp') {
  description(readFileFromWorkspace('resources2/acceptanceTesting/clmsServicesAppDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
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
  logRotator {
    daysToKeep(60)
    numToKeep(25)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    stringParam('RELEASE_CANDIDATE', '', 'Release Candidate Version')
  }
  steps {
    shell(readFileFromWorkspace('resources2/acceptanceTesting/wgetserivcesApprc.sh'))
  }
  configure { project ->
    def slack = project / 'builders' / 'com.sonatype.insight.ci.hudson.PreBuildScan' {
      billOfMaterialsToken('CSS')
      pathConfig {
        scanTargets('ServicesApp**')
      }
      failOnSecurityAlerts(false)
      failOnClmServerFailures(false)
      stageId('release')
      username('')
      password('fHP+GG4gUJ5R5678EE0SYw==')
    }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
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
    buildName('#${ENV,var="RELEASE_CANDIDATE"}')
  }
}
