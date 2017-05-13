job('workOnTheWeekend') {
    description('<H1> Simply <font color="red">click</font> the <font color="red">Build with Parameters </font> on the <font color="red">left</font> and <font color="red">ENTER YOUR EMAIL Address (eg: hello.u.people@uscis.dhs.gov)</font></H1><p><p><H3>If this errors out for you please call Charese Williams @ 301-237-4714 or 202-304-5967 or @reese on Slack</H3>')
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
    stringParam('IPaddress', '', '<H3><font color="red">If you want a specific machine shut down, started, or rebooted, please enter a IP address in this field. <p>If this field is left blank, the normal function of shutting down the machine that is associated with your email will take place.</font></H3>')
   }
  steps {
  shell(readFileFromWorkspace('resources2/ciUtilites/VMmanagement/weekenderWorkIP.sh'))
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
    buildName('#${BUILD_USER}')
   }
}
