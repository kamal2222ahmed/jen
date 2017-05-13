job('AMI_CREATION_JOB_PE') {
  description(readFileFromWorkspace('resources2/amiCreation/amiCreationDescription_PE.txt'))
  jdk('java-1.8.0_u102')
  label('ami-maker')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  parameters {
    stringParam('ENVIRONMENT', 'ZDD', 'KEEP THIS VARIABLE AS ZDD, SINCE THIS AMI IS NOT SPECIFIC TO ANY ENVIRONMENT')
    stringParam('RELEASE_CANDIDATE', '8.1.1-45', 'LATEST RELEASE CANDIDATE')
    booleanParam('FORCE')
  }
  scm {
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
   }
  steps {
      shell(readFileFromWorkspace('resources2/amiCreation/amiCreation_PE.sh'))
  environmentVariables {
      propertiesFile('${WORKSPACE}/amiIdFile.txt')
    }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
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
