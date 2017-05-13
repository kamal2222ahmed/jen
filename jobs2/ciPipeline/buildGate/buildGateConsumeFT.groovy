job('BuildGateConsumeFT') {
  jdk('java-1.8.0_u102')
  concurrentBuild()
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
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
    numToKeep(10000)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  parameters {
    stringParam("ENVIRONMENT")
  }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/consumeFT/startFT.sh'))
      shell(readFileFromWorkspace('resources2/helperjobs/consumeFT/statusCheckFT.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/ftstatus.properties')
      }
      conditionalSteps {
          condition {
	    stringsMatch("\${HEALTHCHECK}", "FAIL", false)
	  }
	  steps {
	    downstreamParameterized {
                trigger('RETURN_FT') {
                  block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('never')
                  }
                  parameters {
                    predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
                  }
                }
            }
	    shell(readFileFromWorkspace('resources2/helperjobs/consumeFT/sleepAfterFail.sh'))
	  }
      }
  }
  wrappers {
    credentialsBinding {
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="ENVIRONMENT"}-${BUILD_NUMBER}')
  }
}
