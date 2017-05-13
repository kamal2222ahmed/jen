job('Database-changelog-diff') {
  jdk('java-1.8.0_u102')
  label('master')
  description('Alert the SMART DBIZ Team whenever code changes are pushed to the Database subtree')
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
    stringParam("RELEASE_CANDIDATE", "8.1.13.54", "This is the Release candidate number")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('jenkinsutil')
        cloneOptions {
          timeout(25)
        }
      }
    }
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/DatabaseChangeLogDiff/shellforDatabaseChangeLogDiff'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/properties_to_inject.txt')
      }
  }
  publishers {
    extendedEmail {
      attachmentPatterns('Apps/changelog.txt')
      triggers {
        failure {
          content(readFileFromWorkspace('resources2/helperjobs/DatabaseChangeLogDiff/failurebody'))
          recipientList('charese.n.williams@uscis.dhs.gov', 'daniel.m.belyea@uscis.dhs.gov', 'juniad.m.khanani@uscis.dhs.gov')
          sendTo {
            developers()
            culprits()
            recipientList()
          subject('$PROJECT_DEFAULT_SUBJECT')
          }
        }
        success {
          content(readFileFromWorkspace('resources2/helperjobs/DatabaseChangeLogDiff/successbody'))
          recipientList(readFileFromWorkspace('resources2/helperjobs/DatabaseChangeLogDiff/successRecipientList'))
          sendTo {
            developers()
            recipientList()
          subject('ELIS2 Database subtree has changed in Master Version: $CURRENT_MASTER_TAG')
           }
        }
      }
    }
 }
  wrappers {
    buildUserVars()
    timestamps()
  }
}
