job('Shutdown-N-Restart-Linux') {
  description('Job is used to shutdown or restart your linux VM')
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
    choiceParam('STATE', ['REBOOT', 'START', 'STOP'], '<p> <b>REBOOT</b> - Reboot your linux machine <p> <b>START</b> - Start your linux vm up<p> <b>STOP</b> - Shut down your linux vm')
  }
  steps {
  shell(readFileFromWorkspace('resources/ciUtilites/VMmanagement/shutdownNrestartLINUX.sh'))  
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
    timestamps()
    buildName('#${BUILD_USER}-${STATE}')
   } 
}
