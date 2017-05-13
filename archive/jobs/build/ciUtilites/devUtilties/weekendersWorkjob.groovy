job('workOnTheWeekend') {
  description('<H1> Simply <font color="red">login to Jenkins first</font>, and <font color="red">click</font> the <font color="red">Build with Parameters </font> on the <font color="red">left</font></H1><H3>If this errors out for you please call Charese Williams @ 301-237-4714 or 202-304-5967 or @reese on Slack</H3>')
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
    stringParam('IPaddress', '', '<H3><font color="red">If you want a specific machine shut down, started, or rebooted, please enter a IP address in this field. <p>If this field is left blank, the normal function of shutting down the machine that is associated with your email will take place.</font></H3>')
   }
  steps {
  shell(readFileFromWorkspace('resources/ciUtilites/VMmanagement/weekenderWorkIP.sh'))  
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
    buildName('#${BUILD_USER}')
   }
}
