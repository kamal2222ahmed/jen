job('GitHub-Poll-Trigger') {
  description(readFileFromWorkspace('resources2/ciPipeline/pipelineEntrance/githubPollTrigger/gitHubPollTriggerDescription.txt'))
  jdk('java-1.8.0_u102')
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
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
    permissionAll('jgarciar')
    permissionAll('kejones')
    blocksInheritance()
  }
  properties {
  githubProjectUrl('https://git.uscis.dhs.gov/USCIS/elis-apps/')
  }
  triggers {
    githubPush()
    pollSCM {
      scmpoll_spec('')
    }
  }
  scm {
    git {
      remote {
        name('origin')
        url('git@git.uscis.dhs.gov:USCIS/elis-apps.git')
        branch('CI-*')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/ciPipeline/pipelineEntrance/githubPollTrigger/shellForGithubPollTriggerBuild_1.sh'))
      environmentVariables {
      propertiesFile('${WORKSPACE}/branch-${BUILD_NUMBER}.txt')
      }
      shell(readFileFromWorkspace('resources2/ciPipeline/pipelineEntrance/githubPollTrigger/shellForGithubPollTriggerBuild_2.sh'))
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
