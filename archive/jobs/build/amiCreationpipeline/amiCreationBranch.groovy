job('AMI_CREATION_JOB_OWN_BRANCH') {
  description(readFileFromWorkspace('resources/amiCreation/amiCreationBranchDescription.txt'))
  jdk('java-1.8.0_u102')
  label('ami-maker')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  parameters {
    stringParam('ENVIRONMENT', 'ZDD', 'KEEP THIS VARIABLE AS ZDD, SINCE THIS AMI IS NOT SPECIFIC TO ANY ENVIRONMENT')
    stringParam('RELEASE_CANIDATE', '8.1.1-45', 'LATEST RELEASE CANIDATE')
    stringParam('BRANCH_NAME', 'aqeel', 'Within your branch: Update the tomcat_base-ami.yml with your own GOLD AMI ID from AMI_CREATION_GOLD_OWN_BRANCH job. For changes to Userdata - Update /cloudops/templates/chef-client-packer-tomcat.yml')
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${CLOUDOPS_REPO_SSH}')
        branch('${BRANCH_NAME}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
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
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('chefrepo')
       }
     }
   }
  steps {
      shell(readFileFromWorkspace('resources/amiCreation/amiCreationBranch.sh'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
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
    buildName('${BUILD_NUMBER}${RELEASE_CANIDATE}')
  }
}
