job('ELIS2-MicroBuildGate-GitPoll') {
  description('<strong>This job triggers the build for a any branch starting with CI-* or CI501-* that has been updated since the last run in the ELIS Image Service Microservices Pipeline.</strong>')
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(25)
    daysToKeep(25)
    numToKeep(25)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)

  }
  multiscm {
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
        url('${IMAGE_TRANSMISSION_GIT_REPO}')
        branch('*/master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('elis_image_transmission')
       }
     }
  }
  triggers {
    cron('0 0 1 */3 *')
   }
  steps {
      shell(readFileFromWorkspace('resources/microServices/microGitPoll.sh')) 
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
  }
}
