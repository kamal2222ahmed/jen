buildFlowJob('ELIS2-StagingGate-Tests') {
  description(readFileFromWorkspace('resources/stagingGateTestFlow/stagingGateTestFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/stagingGateTestFlow/groovyDSLStagingGateTestFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(500)
  }
  parameters {
    stringParam("ENVIRONMENT")
    stringParam("GIT_BRANCH_NAME")
    stringParam("GIT_BRANCH_HASH")
    stringParam("NEXUS_ID", "STG")
    stringParam("SNAPSHOT_NUMBER")
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
      notifyBackToNormal(false)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage()
    }
  }
  publishers {
    postBuildScripts {
      steps {
        shell(readFileFromWorkspace("resources/postBuildTaskGetLogsFromFT.sh"))
      }
      onlyIfBuildSucceeds(false)
    }
    aggregateBuildFlowTests()
    archiveArtifacts {
          pattern('logs/**')
          pattern('conf/**')
    }
  }
}
