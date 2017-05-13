job('Chef-EnvironmentSync') {
  description(readFileFromWorkspace('resources/helperJobs/chefEnvironmentSyncDescription.txt'))
  disabled()
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  triggers {
    scm('* * * * *')
  }
  logRotator {
    numToKeep(100)
  }
  scm {
    git {
      remote {
        name('origin')
        url('${CHEF_REPO}')
        branch('master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
        configure { node ->
          def pollingRestriction = node / 'extensions' / 'hudson.plugins.git.extensions.impl.PathRestriction' {
            includedRegions('environments/FT_TEMPLATE.json')
            excludedRegions('cookbooks/*')
          }
        }
        configure { node ->
          def cleanBeforeCheckout = node / 'extensions' / 'hudson.plugins.git.extensions.impl.cleanBeforeCheckout' {

          }
        }
      }
    }
  steps {
      shell(readFileFromWorkspace('resources/helperJobs/chefEnvironmentSync.sh'))
  }
  publishers {
    extendedEmail {
      triggers {
        failure {
          recipientList('#ELIS2-CI@uscis.dhs.gov')
          replyToList('#ELIS2-CI@uscis.dhs.gov')
          sendTo {
            developers()
            culprits()
            recipientList()
          subject('FAILURE: Chef Repo Out of Sync')
          }
        }
        success {
          recipientList('#ELIS2-CI@uscis.dhs.gov')
          replyToList('#ELIS2-CI@uscis.dhs.gov')
          sendTo {
            recipientList()
          subject('Chef Environments Updated')
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
