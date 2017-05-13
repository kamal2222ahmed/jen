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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
      shell(readFileFromWorkspace('resources2/integrationGateDBDisruption/getProdVersionshell.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources2/integrationGateDBDisruption/databaseDisruptionChecker.sh'))
  }
  publishers {
    extendedEmail {
      attachBuildLog(true)
      defaultContent('$DEFAULT_CONTENT')
      defaultSubject('DB-Disruption Check - ${ELIS_VERSION} CONTAINS DISRUPTIVE DB Changes')
      recipientList('#ELIS2-SKynet@uscis.dhs.gov,#ELISReleaseCaptain@uscis.dhs.gov')
      replyToList('#ELIS2-SKynet@uscis.dhs.gov, #ELISReleaseCaptain@uscis.dhs.gov')
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
