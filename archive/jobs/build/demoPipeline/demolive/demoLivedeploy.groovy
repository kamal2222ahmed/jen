job('ELIS2-DEMO-Live-Deploy') {
  description(readFileFromWorkspace('resources/demoPipeline/demolive/demoLiveDeployDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  blockOn (['ELIS2-DEMO-Live-ScorchUpdateLoad', 'System-.*']) {
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
    stringParam("ELIS2ENV", "DEMOLIVE")
    stringParam("nexus", "el2-nexus")
    stringParam("ELIS2_VERSION", "", "<strong>ENTER A BUILD VERSION IF YOU DONT WANT THE LATEST INTEGRATION GATE BUILD<P>LEAVE IT BLANK OF YOU DO WANT THE LATEST INTEGRATION GATE VERSION</strong>")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${DEMOLIVE_REPO_SSH}')
        branch('*/master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
     }
     git { 
        remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('*/master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
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
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
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
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }   
      extensions {
        relativeTargetDirectory('toggles')
        }
      } 
    }
  triggers {
    cron('0 0 1 */3 *')
   }
  steps {
      shell(readFileFromWorkspace('resources/demoPipeline/demolive/preDeployDEMOLIVE.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/properties_from_deploy.properties')
      }
      shell('${WORKSPACE}/deploy-prep.sh')      
      gradle {
          description('Update Database')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources/demoPipeline/gradleDatabaseUpdate.txt'))
          buildFile('build.gradle')
          tasks(':Database:update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/demoPipeline/sql_and_deploy.sh'))
      shell(readFileFromWorkspace('resources/demoPipeline/gitdiff.sh'))
    }
    publishers {
      extendedEmail {
        triggers {
          failure {
            content(readFileFromWorkspace('resources/demoPipeline/demolive/demoLIVEemailFailureContent.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov', '#ELIS2-Skynet@uscis.dhs.gov', 'myUSCIS-Tech-Team@uscis.dhs.gov')
            sendTo {
              developers()
              culprits()
              recipientList()
            subject('Demo Live Deployment Failure Notification')
            }
          }
          success {
            content(readFileFromWorkspace('resources/demoPipeline/demolive/demoLIVEemailSuccessContent.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov', '#OTCCDD@uscis.dhs.gov', 'myUSCIS-Tech-Team@uscis.dhs.gov', '#ELIS2Dev@uscis.dhs.gov', '#USCISEITT@uscis.dhs.gov', '#OTCEUT@uscis.dhs.gov')
            sendTo {
              developers()
              recipientList()
            subject('Deployment of Master Build Version ${BTAR_VERSION} to the Demo Live Server is complete')
             }
          }
        } 
      } 
   }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
      room('#elis-demo-deployments')
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
    credentialsBinding {
      string('masterpass', '5f30c7b1-6b22-4cff-9e0b-1351471609c0')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var=BTAR_VERSION}')
    timeout {
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
