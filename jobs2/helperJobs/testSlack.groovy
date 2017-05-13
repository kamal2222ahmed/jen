job('test-slack') {
  jdk('java-1.8.0_u102')
  label('slack-notifier')
  concurrentBuild()
  description('testing my own slack integration')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
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
  logRotator {
    numToKeep(30)
  }
  parameters {
    stringParam("CHANNEL", "#reeses-corner", null)
    stringParam("TITLE", "Jenkins Build X", null)
    stringParam("MESSAGE_TEXT", "The Build X was successful", null)
    stringParam("USERNAME", "reese_bot", null)
    stringParam("COLOR", null, null)
    stringParam("BUILDURL", '${BUILD_URL}', null)
  }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/testSlack/testSlackShell.sh'))
  }
  publishers {
 }
  wrappers {
    buildUserVars()
    timestamps()
    credentialsBinding {
      string('SLACK_INT_TOKEN', '66eb217b-08dc-4782-83e1-a5ff26fc6dad')
    }
  }
}
