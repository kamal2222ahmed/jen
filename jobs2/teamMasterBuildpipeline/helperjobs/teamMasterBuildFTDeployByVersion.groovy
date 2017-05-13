job('ELIS2-Team-MasterBuildCheck-FT-DeployByVersion') {
  description(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/teamMasterftDeployByVersion-Description.txt'))
  jdk('java-1.8.0_u112')
  concurrentBuild()
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(50)
    daysToKeep(20)
    numToKeep(500)
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
        url('${CHEF_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/ftmUpdate.sh'))
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/shell1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/shell2.sh'))
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/shell3.sh'))
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/shell4-predeploy.sh'))
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/scorchMongo.sh'))
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/shell5.sh'))
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/shell6.sh'))
      gradle {
          description('Deploy Production Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${PROD_VERSION}')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/gradleProdDBSwitches.txt'))
          tasks('clean scorch update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Deploy Test Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/gradleArtifactDBSwitches.txt'))
          tasks('update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Load the FT Data')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('DataLoader-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/gradleLoadFtDataSwitches.txt'))
          tasks('loadBasicFTData loadBasicInternalUserFTData')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildftDeployByVersion/deployELIS.sh'))
  }
  publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources2/postBuildTaskGetLogsFromFT.sh"))
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
            predefinedProp("TITLE", '${ENVIRONMENT} Failed ELIS2-Team-MasterBuildCheck-FT-DeployByVersion-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} failed the ELIS2-Team-MasterBuildCheck-FT-DeployByVersion Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "danger")
          }
        }
        trigger ('test-slack') {
          condition('SUCCESS')
          parameters{
            predefinedProp("CHANNEL", "#ci-pipeline-fts")
            predefinedProp("TITLE", '${ENVIRONMENT} Passed ELIS2-Team-MasterBuildCheck-FT-DeployByVersion-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} Passed the ELIS2-Team-MasterBuildCheck-FT-DeployByVersion')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "good")
          }
        }
      }
  }
  wrappers {
    credentialsBinding {
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
