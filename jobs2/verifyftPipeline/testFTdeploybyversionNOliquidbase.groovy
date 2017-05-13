job('Test-ELIS2-FTDeployByVersion-NOLIQUIBASE') {
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
    stringParam("BRANCH", "master")
    stringParam("TOGGLE", "ALL_ON")
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
      shell(readFileFromWorkspace('resources2/verifyFTpieline/ftDeployByVersion-NOLIQUIDBASE-shell1.txt'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources2/verifyFTpieline/ftDeployByVersion-preDeploy-NOLIQUIDBASE.sh'))
      shell(readFileFromWorkspace('resources2/verifyFTpieline/internalApp-curl.txt'))
  }
  publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources2/helperjobs/ftDeployByVersion/postBuildTaskGetLogsFromFT.sh"))
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
            predefinedProp("TITLE", '${ENVIRONMENT} Failed FT-DeplymentByVersionNOliquidbase-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} failed the FT-DeployByVersion-NOliquidbase Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "danger")
          }
        }
        trigger ('test-slack') {
          condition('SUCCESS')
          parameters{
            predefinedProp("CHANNEL", "#ci-verify-ft-pipeline")
            predefinedProp("TITLE", '${ENVIRONMENT} Passed FT-DeplymentByVersionNOliquidbase-${BRANCH}')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} Passed the FT-DeployByVersionNOliquidbase Task')
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
