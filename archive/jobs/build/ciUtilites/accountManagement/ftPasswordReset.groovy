job('FT System Account Password Reset') {
  description('Resets password for ec2-user, socscan, chef, and tomcat7 in the Functional Test environment')
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(30)
  }
  parameters {
    stringParam('ENVIRONMENT', 'FT')
  steps {
  shell(readFileFromWorkspace('resources/ciUtilites/accountManagment/ftPasswordReset.sh'))  
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
   wrappers {
    credentialsBinding {
      string('jenkins_id', '')
         } 
      } 
   }
}
