job('Test-ELIS2-FT-DeployByVersion-NOLIQUIBASE') {
  description(readFileFromWorkspace('resources/verifyFTpieline/ftDeployByVersionNoliquidbase-Description.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(30)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  parameters {
    stringParam("ARTIFACT_VERSION", "7.2.10.222")
    stringParam("ENVIRONMENT", "null", "The fuctional test enivornment to test this against")
    stringParam("BRANCH", "master", "The branch that is being built")
    stringParam("TOGGLE", "ALL_ON", "This is the toggle configuration")
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
      shell(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-preDeploy.sh'))
      shell(readFileFromWorkspace('resources/helperJobs/ftDeployByVersion-cleanMongo.txt'))
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
            predefinedProp("TITLE", '${ENVIRONMENT} Failed Test-ELIS2-FT-NoLiquidBase-DeplymentByVersion')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} failed the Test-ELIS2-FT-NoLiquidBase-DeployByVersion Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "danger")
          }
        }
        trigger ('test-slack') {
          condition('SUCCESS')
          parameters{
            predefinedProp("CHANNEL", "#ci-pipeline-fts")
            predefinedProp("TITLE", '${ENVIRONMENT} Passed Test-ELIS2-FT-NoLiquidBase-DeplymentByVersion')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} Passed the Test-ELIS2-FT-NoLiquidBase-DeployByVersion Task')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "good")
           }
         } 
      }
   }
}
