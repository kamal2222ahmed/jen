job('ELIS2-BuildGate-GitPoll') {
  description(readFileFromWorkspace('resources/pipelineEntrance/gitHubPollTriggerDescription.txt'))
  jdk('java-1.8.0_u102')
  properties {
  githubProjectUrl('https://git.uscis.dhs.gov/USCIS/elis-apps-testing')
  }
  triggers {
    gitHubPushTrigger()
  }
  scm {
    git {
      remote {
        name('origin')
        url('git@git.uscis.dhs.gov:uscis/elis-apps-testing.git')
        branch('CI-*')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources/pipelineEntrance/shellForGithubPollTriggerBuild_1.sh'))
      environmentVariables {
      propertiesFile('${WORKSPACE}/branch-${BUILD_NUMBER}.txt')
      }
      shell(readFileFromWorkspace('resources/pipelineEntrance/shellForGithubPollTriggerBuild_2.sh'))
  }
  publishers {
    downstreamParameterized {
      trigger('ELIS2-BuildGate-PreMerge') {
        condition('SUCCESS')
        parameters {
          currentBuild()
          predefinedProp('GIT_BRANCH_NAME', '$branchToBuild')
          predefinedProp('NEXUS_ID', 'BLD')
        }
      }
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
  }
}
