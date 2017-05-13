buildFlowJob('ELIS2-BGO-Test-Flow') {
  description(readFileFromWorkspace('resources2/bgoPipeline/bgoTestFlow/bgoGateTestFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/bgoPipeline/bgoTestFlow/groovyDSLbgoBuildGateTestFlow.groovy'))
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
    artifactDaysToKeep(11)
    daysToKeep(45)
    numToKeep(500)
  }
  parameters {
    stringParam("ENVIRONMENT")
    stringParam("GIT_BRANCH_NAME", "master")
    stringParam("NEXUS_ID", "BLD")
    stringParam("SNAPSHOT_NUMBER")
    stringParam("MYSQL_PASSWORD")
    stringParam("MYSQL_USER",null, "jenkins")
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-bgo-pipeline')
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
    aggregateBuildFlowTests()
    archiveArtifacts {
          pattern('logs/**')
          pattern('conf/**')
    }
  }
}
