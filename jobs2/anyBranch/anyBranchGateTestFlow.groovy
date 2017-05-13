buildFlowJob('ELIS2-anyBranchGate-Tests') {
  description(readFileFromWorkspace('resources2/anyBranch/anyBranchGateTestFlow/anyBranchGateTestFlowDescription.txt'))
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  concurrentBuild()
  buildFlow(readFileFromWorkspace('resources2/anyBranch/anyBranchGateTestFlow/groovyDSLBuildGateTestFlow.groovy'))
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
    artifactDaysToKeep(11)
    daysToKeep(45)
    numToKeep(500)
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
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
  publishers {
   downstreamParameterized {
      trigger('ELIS2-FT-Wake-Up-N-Sleep') {
        condition('ALWAYS')
        parameters {
          predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
          predefinedProp('STATE', 'OFF')
        }
      }
    }
    postBuildScripts {
      steps {
        shell(readFileFromWorkspace("resources2/postBuildTaskGetLogsFromFT.sh"))
      }
      onlyIfBuildSucceeds(false)
    }
    archiveArtifacts {
        pattern('logs/**, conf/**')
    }
    downstreamParameterized {
      trigger ('test-slack') {
        condition('FAILED')
        parameters{
          predefinedProp("CHANNEL", "#ci-buildgate")
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
