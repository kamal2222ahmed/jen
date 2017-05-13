job('RETURN_FT') {
  description(readFileFromWorkspace('resources2/verifyFTpieline/returnFTdescription.txt'))
  jdk('java-1.8.0_u102')
  concurrentBuild()
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
    maxTotal(10)
    maxPerNode(1)
  }
  parameters {
    stringParam("ENVIRONMENT", null, "Enter the name of the environment to return back to the pool. Ex. FT101")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${CHEF_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
       }
     }
   }
  steps {
      shell(readFileFromWorkspace('resources2/verifyFTpieline/returnFT.sh'))
      shell(readFileFromWorkspace('resources2/verifyFTpieline/knifeUpdate.sh'))
      }
    publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources2/verifyFTpieline/stopInstance.sh"))
          onlyIfBuildFails(false)
          onlyIfBuildSucceeds(false)
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
    buildName('#${ENV,var="ENVIRONMENT"}-${BUILD_NUMBER}')
   }
}
