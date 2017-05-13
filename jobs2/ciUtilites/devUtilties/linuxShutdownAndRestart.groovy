job('Shutdown-N-Restart-Linux') {
  description('Job is used to shutdown or restart your linux VM')
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(30)
  }
  parameters {
    stringParam('emailAddress', '', "Please enter your email address")
    choiceParam('STATE', ['REBOOT', 'START', 'STOP'], '<p> <b>REBOOT</b> - Reboot your linux machine <p> <b>START</b> - Start your linux vm up<p> <b>STOP</b> - Shut down your linux vm')
  }
  steps {
  shell(readFileFromWorkspace('resources2/ciUtilites/VMmanagement/shutdownNrestartLINUX.sh'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-helperjobs')
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
