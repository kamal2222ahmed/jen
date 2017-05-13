job('ELIS2-BuildGate-PreMerge') {
  description(readFileFromWorkspace('resources2/ciPipeline/pipelineEntrance/buildGatePreMerge/buildGatePreMergeDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
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
    permissionAll('jgarciar')
    blocksInheritance()
  }
  logRotator {
    artifactDaysToKeep(25)
    daysToKeep(25)
    numToKeep(400)
  }
  throttleConcurrentBuilds {
    maxTotal(9)
    maxPerNode(3)
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
          timeout(50)
        }
      }
    }
  }
  steps {
      setBuildResult('SUCCESS')
      shell(readFileFromWorkspace('resources2/ciPipeline/pipelineEntrance/buildGatePreMerge/shellForPreMerge.sh'))
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
          room('#ci-buildgate')
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
    preScmSteps {
      steps {
        shell('ssh-keygen -R git.uscis.dhs.gov')
      }
      failOnError()
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}')
  }
}
