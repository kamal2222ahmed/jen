buildFlowJob('ELIS2-StagingGate-Tests') {
  description(readFileFromWorkspace('resources2/ciPipeline/stagingGate/stagingGateTests/stagingGateTestFlowDescription.txt'))
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/ciPipeline/stagingGate/stagingGateTests/groovyDSLStagingGateTestFlow.groovy'))
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
    permissionAll('jgarciar')
    blocksInheritance()
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
      room('#ci-pipeline')
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
        shell(readFileFromWorkspace("resources2/ciPipeline/stagingGate/stagingGateTests/postBuildTaskGetLogsFromFT.sh"))
      }
      onlyIfBuildSucceeds(false)
    }
    aggregateBuildFlowTests()
    archiveArtifacts {
          pattern('logs/**')
          pattern('conf/**')
    }
    downstreamParameterized {
      trigger ('test-slack') {
        condition('FAILED')
        parameters{
          predefinedProp("CHANNEL", "#ci-pipeline")
          predefinedProp("TITLE", '${JOB_NAME} - ${BUILD_DISPLAY_NAME} Failed. Click the link below to debug:')
          predefinedProp("MESSAGE_TEXT", 'http://test-visualization.uscis.dhs.gov:8080/cuketimeline/${JOB_NAME}/${BUILD_NUMBER}/')
          predefinedProp("USERNAME", "jenkinsFTBot")
          predefinedProp("BUILDURL", '${BUILD_URL}')
          predefinedProp("COLOR", "danger")
        }
      }
    }
  }
}
