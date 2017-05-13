job('ELIS2-FT-DeployByVersion') {
  description(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-Description.txt'))
  jdk('java-1.8.0_u102')
  concurrentBuild()
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
        url('${FT_REPO_SSH}')
        branch('master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
      shell(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersionDownloadDB-shell2.txt'))
      shell(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-preDeploy.sh'))
      shell(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-cleanMongo.txt'))
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
      shell(readFileFromWorkspace('resources2/helperjobs/ftDeployByVersion/ftDeployByVersion-deployELIS.sh'))
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
      downstreamParameterized {
        trigger ('test-slack') {
          condition('FAILED')
          parameters{
            predefinedProp("CHANNEL", "#ci-pipelinefailure")
            predefinedProp("TITLE", '${ENVIRONMENT} Failed FT-DeplymentByVersion-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} failed the FT-DeployByVersion Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "danger")
          }
        }
        trigger ('test-slack') {
          condition('SUCCESS')
          parameters{
            predefinedProp("CHANNEL", "#ci-pipeline-fts")
            predefinedProp("TITLE", '${ENVIRONMENT} Passed FT-DeplymentByVersion-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} Passed the FT-DeployByVersion Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "good")
          }
        }
      }
  }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
      file('keyToUse', '6aaa142d-b2d3-4d6c-af8d-0a438ef89fe7')
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
