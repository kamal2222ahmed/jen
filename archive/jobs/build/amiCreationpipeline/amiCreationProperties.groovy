job('PROPERTIES_CREATION_to_S3') {
  description(readFileFromWorkspace('resources2/amiCreation/amiCreationPropertiesDescription.txt'))
  jdk('java-1.8.0_u102')
  label('ami_maker_properties_machine')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  throttleConcurrentBuilds {
    maxTotal(10)
    maxPerNode(10)
  }
  parameters {
    stringParam('RELEASE_CANDIDATE', '8.1.1-45', 'LATEST RELEASE CANDIDATE')
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
      shell(readFileFromWorkspace('resources2/amiCreation/amiCreationProperties.sh'))
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
    buildName('#${BUILD_NUMBER} ${RELEASE_CANDIDATE}')
  }
}
