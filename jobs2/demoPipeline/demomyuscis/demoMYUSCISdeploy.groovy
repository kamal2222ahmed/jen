job('ELIS2-DEMO-Mock-MyUSCIS-Deploy') {
  description(readFileFromWorkspace('resources2/demoPipeline/demomyuscis/demoMYUSCISDeployDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
    permission('hudson.model.Item.Cancel', 'drosmari')
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
  blockOn (['ELIS2-DEMO-Mock-MyUSCIS-ScorchUpdateLoad', 'System-.*']) {
      blockLevel('GLOBAL')
      }
  logRotator {
    daysToKeep(30)
    numToKeep(10000)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    stringParam("ELIS2ENV", "DEMOMYUSCIS")
    stringParam("ELIS2_VERSION", "", "<H1><strong>ENTER A BUILD VERSION (eg. 8.1.44.100) OR LEAVE<P><P> IT BLANK OF YOU DO WANT THE LATEST INTEGRATION GATE VERSION</strong></H1>")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${DEMOMYUSCIS_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
       }
     }
     git {
        remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('jenkinsutil')
        }
     }
     git {
        remote {
        name('origin')
        url('${ELIS_APPS_REPO}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
       }
     }
     git {
        remote {
        name('origin')
        url('${TOGGLES_REPO}')
        branch('master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('toggles')
        }
      }
    }
  triggers {
    cron('H(0-7) 8 * * *')
   }
  steps {
      shell(readFileFromWorkspace('resources2/demoPipeline/demomyuscis/preDeployDEMOMYUSCIS.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/properties_from_deploy.properties')
      }
      shell('${WORKSPACE}/deploy-prep.sh')
      gradle {
          description('Update Database')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources2/demoPipeline/gradleDatabaseUpdate.txt'))
          buildFile('build.gradle')
          tasks(':Database:update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/demoPipeline/sql_and_deploy.sh'))
      shell(readFileFromWorkspace('resources2/demoPipeline/gitdiff.sh'))
    }
    publishers {
      extendedEmail {
        triggers {
          failure {
            content(readFileFromWorkspace('resources2/demoPipeline/demomyuscis/demoMYUSCISemailFailureContent.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov', '#ELIS2-Skynet@uscis.dhs.gov', 'myUSCIS-Tech-Team@uscis.dhs.gov')
            sendTo {
              developers()
              culprits()
              recipientList()
            subject('Demo MYUSCIS Deployment Failure Notification')
            }
          }
          success {
            content(readFileFromWorkspace('resources2/demoPipeline/demomyuscis/demoMYUSCISemailSuccessContent.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov', '#OTCCDD@uscis.dhs.gov', 'myUSCIS-Tech-Team@uscis.dhs.gov', '#ELIS2Dev@uscis.dhs.gov', '#USCISEITT@uscis.dhs.gov', '#OTCEUT@uscis.dhs.gov')
            sendTo {
              developers()
              recipientList()
            subject('Deployment of Master Build Version ${ELIS2_VERSION} to the Demo MYUSCIS Server is complete')
             }
          }
        }
      }
    }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#elis-demodeploy')
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
      includeCustomMessage(true)
      customMessage('$ELIS2_VERSION')
    }
  }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
      string('masterpass', '5f30c7b1-6b22-4cff-9e0b-1351471609c0')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="ELIS2_VERSION"}')
    timeout {
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
