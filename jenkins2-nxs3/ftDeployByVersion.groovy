job('ELIS2-FT-DeployByVersion') {
  description(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-Description.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
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
  }
  logRotator {
    artifactDaysToKeep(50)
    daysToKeep(20)
    numToKeep(500)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  parameters {
    stringParam("ARTIFACT_VERSION")
    stringParam("ENVIRONMENT")
    stringParam("BRANCH")
    stringParam("TOGGLE")
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
        url('${FT_REPO_SSH}')
        branch('master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('ft')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-shell1.txt'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('archive/resources/helperJobs/ftDeployByVersionDownloadDB-shell2.txt'))
      shell(readFileFromWorkspace('archive/resources/helperJobs/ftDeployByVersion-preDeploy.sh'))
      shell(readFileFromWorkspace('archive/resources/helperJobs/ftDeployByVersion-cleanMongo.txt'))
      gradle {
          description('Deploy Test Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-DeployTestDBSwitches.txt'))
          tasks('update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Load the FT Data')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('DataLoader-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-DeployFTDataSwitches.txt'))
          tasks('loadBasicFTData loadBasicInternalUserFTData')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('archive/resources/helperJobs/ftDeployByVersion-deployELIS.sh'))
  }
  publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources2/helperjobs/ftDeployByVersion/postBuildTaskGetLogsFromFT.sh"))
        }
        onlyIfBuildFails(false)
        onlyIfBuildSucceeds(false)
      }
      archiveArtifacts {
          pattern('conf/**')
          pattern('logs/**')
      }
  }
  wrappers {
    credentialsBinding {
      file('keyToUse', '152b1ba5-edc4-4559-bc2d-6d0df679befe')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="ENVIRONMENT"}-${ENV,var="ARTIFACT_VERSION"}')
    timeout {
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
