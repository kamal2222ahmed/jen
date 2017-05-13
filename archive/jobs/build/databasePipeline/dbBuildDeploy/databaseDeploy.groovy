job('DB-Deploy') {
  description(readFileFromWorkspace('resources/databaseBuild/databaseDeployDesc.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-tester')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(10)
    daysToKeep(10)
    numToKeep(1000)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  concurrentBuild()
  parameters {
    stringParam("NEXUS_ID", null, "This is the nexus snapshot precursor")
    stringParam("GIT_BRANCH_NAME", null, "This is the name of the branch to build from")
    stringParam("GIT_BRANCH_HASH")
    stringParam("PROD_VERSION")
    stringParam("ARTIFACT_VERSION")
    stringParam("ENVIRONMENT")
    booleanParam("SMOKETEST", false, "whether or not to execute smoke test after the deploy")
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
	wipeOutWorkspace()
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources/databaseBuild/deploy-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources/databaseBuild/deploy-2.sh'))
      gradle {
          description('Deploy Production Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${PROD_VERSION}')
          switches(readFileFromWorkspace('resources/databaseBuild/databaseDeploy.txt'))
          tasks('clean scorch update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Deploy Test Database')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources/databaseBuild/databaseDeploy.txt'))
          tasks('update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('build and compile the code and execute the unit and Integration tests')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('DataLoader-${PROD_VERSION}')
          switches(readFileFromWorkspace('resources/databaseBuild/databaseLoadData.txt'))
          tasks('loadBasicFTData loadBasicInternalUserFTData')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/databaseBuild/deploy-3.sh'))
      shell(readFileFromWorkspace('resources/databaseBuild/deploy-4.sh'))
      conditionalSteps {
          condition {
              booleanCondition('${SMOKETEST}')
          }
          steps {
              downstreamParameterized {
                trigger('DB-SmokeTest') {
                  block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                  }
                  parameters {
                    predefinedProp('GIT_BRANCH_HASH', 'CI-IntFix-${PROD_VERSION}')
                    predefinedProp('GIT_BRANCH_NAME', '${GIT_BRANCH_NAME}')
                    predefinedProp('NEXUS_ID', '${NEXUS_ID}')
                    predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
                  }
                }
              }
          }
      }
  }
  publishers {
      postBuildTask {
         task(' ', readFileFromWorkspace('resources/databaseBuild/deploy-postbuild.sh'))
      }
      archiveArtifacts {
          pattern('conf/**, logs/**')
       }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
      room('#ci-buildgate')
      startNotification(false)
      notifySuccess(false)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      includeTestSummary(false)
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
  wrappers {
    credentialsBinding {
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
    timeout {
        absolute(180)
        failBuild()
        writeDescription('Build failed due to timeout after 180 minutes')
    }
  }
  publishers {
    downstreamParameterized {
      trigger('ELIS2-FT-Wake-Up-N-Sleep') {
        condition('ALWAYS')
        parameters {
          predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
          predefinedProp('STATE', 'OFF')
        }
      }
    }
  }
}
