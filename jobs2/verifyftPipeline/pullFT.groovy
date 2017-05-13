job('PULL_FT') {
  description(readFileFromWorkspace('resources2/verifyFTpieline/pullFTdescription.txt'))
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
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    stringParam("REASON", null, "Enter a reason why the FT is being pulled from rotation.")
  }
  steps {
      shell(readFileFromWorkspace('resources2/verifyFTpieline/pullFT.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/env.properties')
      }
       downstreamParameterized {
        trigger('ELIS2-FT-Wake-Up-N-Sleep') {
          block {
            buildStepFailure('never')
            failure('never')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            predefinedProp('ENVIRONMENT', '$ENVIRONMENT')
            predefinedProp('STATE', 'ON')
           }
        }
     }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-verify-ft-pipeline')
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
    credentialsBinding {
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
    }
  }
}
