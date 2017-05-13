job('ELIS2-BGO-DeployByVersion') {
  description(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-Description.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
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
    stringParam("ENVIRONMENT", 'FT115')
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
      shell(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-shell1.txt'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources/helperJobs/ftDeployByVersionDownloadDB-shell2.txt'))
      shell(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-preDeploy.sh'))
      shell(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-cleanMongo.txt'))
      gradle {
          description('Deploy Test Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-DeployTestDBSwitches.txt'))
          tasks('update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Load the FT Data')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('DataLoader-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-DeployTestDBSwitches.txt'))
          tasks('loadBasicFTData loadBasicInternalUserFTData')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-deployELIS.sh'))
  }
  publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources/postBuildTaskGetLogsFromFT.sh"))
        }
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
            predefinedProp("TITLE", '${ENVIRONMENT} Failed BGO-DeplymentByVersion-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} failed the BGO-DeployByVersion Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "danger")
          }
        }
        trigger ('test-slack') {
          condition('SUCCESS')
          parameters{
            predefinedProp("CHANNEL", "#ci-pipeline-fts")
            predefinedProp("TITLE", '${ENVIRONMENT} Passed BGO-DeplymentByVersion-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} Passed the BGO-DeployByVersion Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "good")
          }
        }
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
