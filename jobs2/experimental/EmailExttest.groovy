job('EXP-EmailExt') {
  description(readFileFromWorkspace('resources2/helperjobs/chefEnvSync/chefEnvironmentSyncDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
    permissionAll('cnwillia')
    permissionAll('jbfrimpo')
    permissionAll('jnugorji')
    permissionAll('skahmed')
  }
  logRotator {
    numToKeep(10)
  }
  scm {
    git {
      remote {
        name('origin')
        url('${CHEF_REPO}')
        branch('master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      }
    }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/testSlack/testSlackShell.sh'))
  }
  publishers {
    extendedEmail {
      contentType('text/html')
      triggers {
        failure {
          recipientList('syed.k.ahmed@uscis.dhs.gov')
          replyToList('syed.k.ahmed@uscis.dhs.gov')
          sendTo {
            recipientList()
          subject('INFO: Testing Email-Ext rich email notifications, FAILURE')
          }
        }
        success {
          recipientList('syed.k.ahmed@uscis.dhs.gov')
          replyToList('syed.k.ahmed@uscis.dhs.gov')
          sendTo {
            recipientList()
          subject('INFO: Testing Email-Ext rich email notifications , SUCCESS')
          }
        }
      }
    }
  }
  wrappers {
    credentialsBinding {
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
    }
    buildUserVars()
    timestamps()
  }
}
