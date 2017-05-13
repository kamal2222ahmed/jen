job('ELIS2-IntegrationGateDB-Disruption') {
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  blockOn('ELIS2-IntegrationGateBuild') {
    blockLevel('GLOBAL')
    scanQueueFor('DISABLED')
  }
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(20)
    daysToKeep(100)
    numToKeep(100)
  }
  parameters {
    stringParam("ELIS_VERSION")
    stringParam("BRANCH")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('jenkinsutil')
        cloneOptions {
          timeout(25)
        }
      }
    }
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${BRANCH}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources/integrationGateDBDisuption/getProdVersionshell.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources/integrationGateDBDisuption/databaseDisruptionChecker.sh'))
  }
  publishers {
    extendedEmail {
      attachBuildLog(true)
      defaultContent('$DEFAULT_CONTENT')
      defaultSubject('DB-Disruption Check job is failing for ${ELIS_VERSION}')
      recipientList('#ELIS2-SKynet@uscis.dhs.gov')
      replyToList('#ELIS2-SKynet@uscis.dhs.gov')
      triggers {
        failure {
          sendTo {
            recipientList()
          }
        }
      }
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('${ENV,var="ELIS_VERSION"} - #${BUILD_NUMBER}')
    timeout {
        absolute(30)
        failBuild()
        writeDescription('Build failed due to timeout after 30 minutes')
    }
  }
}
