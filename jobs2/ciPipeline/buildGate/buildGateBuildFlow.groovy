buildFlowJob('ELIS2-BuildGateFlow') {
  description(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateFlow/buildGateBuildFlowDescription.txt'))
  jdk('java-1.8.0_u112')
  concurrentBuild()
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateFlow/groovyDSLBuildGateFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
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
    blocksInheritance()
  }
  throttleConcurrentBuilds {
    maxTotal(20)
    maxPerNode(4)
  }
  logRotator {
    artifactDaysToKeep(3)
    daysToKeep(10)
    numToKeep(300)
  }
  parameters {
    stringParam("ENVIRONMENT")
    stringParam("GIT_BRANCH_NAME")
    stringParam("GIT_BRANCH_HASH")
    stringParam("NEXUS_ID", "BLD")
    stringParam("SNAPSHOT_NUMBER")
    stringParam("MYSQL_PASSWORD")
    stringParam("MYSQL_USER",null, "jenkins")
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-buildgate')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(false)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage("${GIT_BRANCH_NAME}")
    }
  }
}
