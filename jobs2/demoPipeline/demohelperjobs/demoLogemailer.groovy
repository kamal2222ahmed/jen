job('DEMO-Log-Emailer') {
  description(readFileFromWorkspace('resources2/demoPipeline/logRetrieverDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(11)
    daysToKeep(45)
    numToKeep(500)
  }
  parameters {
    choiceParam('demo_server', ['el2-demomk-app1', 'el2-demolv-app1', 'el2-demomu-app1', 'el2-demolb-app1'], 'Select a demo server to harvest the property files from el2-demomk-app1 = DEMO MOCK, el2-demolv-app1 = DEMO LIVE, el2-demomu-app1 = DEMO MYUSCIS, el2-demolb-app1 = DEMO LOCKBOX')
    stringParam('email_addresses', '', 'Enter your USCIS email to have logs sent to')
  }
   steps {
      shell(readFileFromWorkspace('resources2/demoPipeline/logRetriver.txt'))
   }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
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
        archiveArtifacts {
            pattern('*.gz')
            onlyIfSuccessful()
        }
    }
}
