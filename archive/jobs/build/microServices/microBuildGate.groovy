job('ELIS2-MicroBuildGate') {
  description('Builds the ELIS2 Application for MicroServices Testing')
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(25)
    daysToKeep(25)
    numToKeep(400)
  }
  throttleConcurrentBuilds {
    maxTotal(15)
    maxPerNode(1)
  }
  concurrentBuild()
  parameters {
    stringParam("GIT_BRANCH_NAME", null, "This is the name of the branch to build from")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${IMAGE_TRANSMISSION_GIT_REPO}')
        branch('${GIT_BRANCH_HASH}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
        mergeOptions {
          branch('Integration')
          getFastForwardMode()
          remote('origin')
          strategy('default')
        }
        mergeOptions {
          branch('master')
          getFastForwardMode()
          remote('origin')
          strategy('default')
        }
      }
    }
  }
  steps {
     shell(readFileFromWorkspace('resources/microServices/preDatabaseDeploy.sh'))
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources/microServices/clean1.txt'))
          tasks('clean build baseline update intTest apiTest')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
     gradle {
          description('build artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources/microServices/build2.txt'))
          tasks('buildRpm uploadArchives -x compileJava -x jar')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
     shell(readFileFromWorkspace('resources/microServices/rpmBuild.sh'))
     conditionalSteps {
       condition {
         status('SUCCESS', 'SUCCESS')
       }
       steps {
         downstreamParameterized {
           trigger('ELIS2-MicroBuildGate-Merge') {
             block {
               buildStepFailure('never')
               failure('never')
               unstable('UNSTABLE')
             }
             parameters {
               predefinedProp('GIT_BRANCH_HASH=${GIT_COMMIT}', '')
               predefinedProp('SNAPSHOT_NUMBER=${BUILD_NUMBER}', '')
             }
           }
         }
       }
     }
   }
   publishers {
     downstreamParameterized {
       trigger('ELIS2-MicroIntegrationGate') {
         condition('SUCCESS')
         parameters {
           currentBuild()
          }
        }
     }
   }
  configure { project ->
      def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
          teamDomain('uscis')
          authToken('CxGbmA6sSwX1wUSPX88I96Xp')
          buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
          room('#ci-jenkins2_0dsl')
          startNotification(false)
          notifySuccess(true)
          notifyAborted(false)
          notifyNotBuilt(false)
          notifyUnstable(false)
          notifyFailure(true)
          notifyBackToNormal(false)
          notifyRepeatedFailure(false)
          includeTestSummary(false)
          commitInfoChoice('NONE')
          includeCustomMessage(true)
          customMessage("${GIT_BRANCH_NAME}")
      }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${SNAPSHOT_NUMBER}')
  }
}
