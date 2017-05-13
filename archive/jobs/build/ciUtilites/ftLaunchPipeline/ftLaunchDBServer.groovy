job('launchFT-idb1') {
  description('Creates a FT database server (oracle)')
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(30)
  }
  parameters {
    stringParam('ENVIRONMENT', 'FT9000', 'Environment Name')
    stringParam('Version', '7.2.21.37', 'Version of Code' )
    choiceParam('DELETE', ['FALSE', 'TRUE'], 'TRUE - Delete chef node and client if it already exists.  FALSE - Do not delete node and client')
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${CLOUDOPS_REPO_SSH}')
        branch('*/master')
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
    git {
    remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('*/master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('Apps')
       }
     }
   }
  steps {
      shell(readFileFromWorkspace('resources/ciUtilites/makeFTpipeline/DBspinUP.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
         gradle {
          description('Clean, Scorch, and, Update Database')
          gradleName('gradle-2.7')
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/ciUtilites/makeFTpipeline/DBgradle.txt'))
          tasks(':Database:clean :Database:scorch :Database:update')
      }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('http://10.103.130.42:8080/jenkins')
      room('#ci-jenkins2_0dsl')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('# ${ENV,var="ENVIRONMENT"}  - ${BUILD_NUMBER}')
  }
}
