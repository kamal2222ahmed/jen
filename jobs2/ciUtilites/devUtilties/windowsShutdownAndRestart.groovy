job('Shutdown-N-Restart-Windows') {
  description('<H2> 1. <font color="red">Click</font> the <font color="red">Build with Parameters </font> on the <font color="red">left</font> and <font color="red"> ENTER EMAIL (eg: me@uscis.dhs.gov) & IP Address (eg: 10.103.444.444) </font></H2><H2>2. Click Build</H2><p><p><p><p><H4>If this errors out for you please send an email to #ELIS2-SKynet@uscis.dhs.gov or slack @skynetsupport | #skynet on Slack</H4>')
  jdk('java-1.8.0_u112')
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
    choiceParam('STATE', ['REBOOT', 'START', 'STOP'], 'REBOOT- Reboot your linux machine.  START - Start your linux vm up. STOP - Shut down your linux vm.')
    stringParam('IPaddress', '', '<H3><font color="red">If you want a specific machine shut down, started, or rebooted, please enter a IP address in this field. <p>If this field is left blank, the normal function of shutting down the machine that is associated with your email will take place.</font></H3>')
   }
  steps {
  shell(readFileFromWorkspace('resources2/ciUtilites/VMmanagement/shutdownNrestartWINDOWS.sh'))
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
