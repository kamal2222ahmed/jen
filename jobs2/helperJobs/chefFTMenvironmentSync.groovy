job('FTM-CI-Chef-EnvironmentSync') {
  description('Syncs the FTM environments in the ChefRepo with the Chef Server')
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
        configure { node ->
          def pollingRestriction = node / 'extensions' / 'hudson.plugins.git.extensions.impl.PathRestriction' {
            includedRegions('environments/FTM_TEMPLATE.json')
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
     shell(readFileFromWorkspace('resources2/helperjobs/chefEnvSync/chefFTMenvironmentSync.sh'))
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
          subject('Chef FTM Environments Updated')
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
