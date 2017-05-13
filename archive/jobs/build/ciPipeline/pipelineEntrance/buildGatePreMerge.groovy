job('ELIS2-BuildGate-PreMerge') {
  description(readFileFromWorkspace('resources/buildGatePreMerge/buildGatePreMergeDescription.txt'))
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
    stringParam("NEXUS_ID", "BLD", "This is the nexus snapshot precursor")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('Release_Hotfixes')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        mergeOptions {
          branch('${GIT_BRANCH_NAME}')
          getFastForwardMode()
          remote('origin')
          strategy('default')
        }
        relativeTargetDirectory('Release Hotfixes')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      setBuildResult('SUCCESS')
      shell(readFileFromWorkspace('resources/buildGatePreMerge/shellForPreMerge.sh'))
  }
  publishers {
    git {
      branch('origin', '${GIT_BRANCH_NAME}')
      pushMerge(true)
      pushOnlyIfSuccess(true)
    }
    downstreamParameterized {
      trigger('ELIS2-BuildGateFlow') {
        condition('SUCCESS')
        parameters {
          currentBuild()
          predefinedProp('GIT_BRANCH_HASH', '${GIT_COMMIT}')
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
    buildName('#${ENV,var="GIT_BRANCH_NAME"}')
  }
}
