job('AMI_CREATION_JOB_OLD') {
  description(readFileFromWorkspace('resources2/amiCreation/amiCreationDescription.txt'))
  jdk('java-1.8.0_u102')
  label('ami-maker')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
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
  parameters {
    stringParam('ENVIRONMENT', 'ZDD', 'KEEP THIS VARIABLE AS ZDD, SINCE THIS AMI IS NOT SPECIFIC TO ANY ENVIRONMENT')
    stringParam('RELEASE_CANDIDATE', '8.1.1-45', 'LATEST RELEASE CANDIDATE')
    booleanParam('FORCE')
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${CLOUDOPS_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('cloudops')
       }
     }
    git {
    remote {
        name('origin')
        url('${CHEF_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('chefrepo')
       }
     }
   }
  steps {
      shell(readFileFromWorkspace('resources2/amiCreation/amiCreation.sh'))
  environmentVariables {
      propertiesFile('${WORKSPACE}/amiIdFile.txt')
    }
  }
  publishers {
     extendedEmail {
        triggers {
          failure {
            content(readFileFromWorkspace('resources2/amiCreation/failure.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov', '#ELIS2-Skynet@uscis.dhs.gov')
            sendTo {
              developers()
              culprits()
              recipientList()
            subject('CI AMI Creation Job Release Candidate ${RELEASE_CANDIDATE} Failure Notification')
            }
          }
          success {
            content(readFileFromWorkspace('resources2/amiCreation/success.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov', '#ELIS2-Skynet@uscis.dhs.gov', '#ELISReleaseCaptain@uscis.dhs.gov')
            sendTo {
              developers()
              recipientList()
            subject('AMI Creation of Release Candidate ${RELEASE_CANDIDATE} is complete')
             }
          }
        }
      }
   }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-amipipeline')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      includeTestSummary(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage()
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('#${BUILD_NUMBER} ${RELEASE_CANDIDATE}')
  }
}
