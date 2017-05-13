buildFlowJob('ELIS2-HOTFIX-BuildGate-Tests') {
  description(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateTestFlow/hotfixBuildGateTestFlowDescription.txt'))
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateTestFlow/groovyDSLHotfixBuildGateTestFlow.groovy'))
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
      room('#ci-hotfixes')
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
    }
  }
}
