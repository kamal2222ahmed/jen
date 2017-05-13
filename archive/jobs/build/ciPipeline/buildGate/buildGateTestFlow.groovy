buildFlowJob('ELIS2-BuildGate-Tests') {
  description(readFileFromWorkspace('resources/buildGateTestFlow/buildGateTestFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/buildGateTestFlow/groovyDSLBuildGateTestFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
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
      onlyIfBuildSucceeds(false)
    }
  }
}
