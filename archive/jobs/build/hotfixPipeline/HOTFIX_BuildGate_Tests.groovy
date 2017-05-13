buildFlowJob('ELIS2-HOTFIX-BuildGate-Tests') {
  description(readFileFromWorkspace('resources/hotfixBuildGateTestFlow/hotfixBuildGateTestFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/hotfixBuildGateTestFlow/groovyDSLHotfixBuildGateTestFlow.txt'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    daysToKeep(15)
  }
  throttleConcurrentBuilds {
    maxTotal(3)
    maxPerNode(1)
  }
  concurrentBuild(true)
  parameters {
    stringParam("ENVIRONMENT")
    stringParam("GIT_BRANCH_NAME")
    stringParam("NEXUS_ID", "HTFX")
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
        shell(readFileFromWorkspace("resources/postBuildTaskGetLogsFromFT.sh"))
      }
    }
  }
}
