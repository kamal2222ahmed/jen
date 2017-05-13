job('DB-Deploy') {
  description(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/databaseDeployDesc.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-tester')
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
    booleanParam("SMOKETEST", false, "whether or not to execute smoke test after the deploy")
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
	wipeOutWorkspace()
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/getAFTFromThePool.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/env.properties')
      }
      downstreamParameterized {
        trigger('ConsumeFTFlow') {
          block {
            buildStepFailure('never')
            failure('never')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
          }
        }
      }
      shell(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/deploy-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/deploy-2.sh'))
      gradle {
          description('Deploy Production Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${PROD_VERSION}')
          switches(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/databaseDeploy.txt'))
          tasks('clean scorch update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Deploy Test Database')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/databaseDeploy.txt'))
          tasks('update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('build and compile the code and execute the unit and Integration tests')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('DataLoader-${PROD_VERSION}')
          switches(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/databaseLoadData.txt'))
          tasks('loadBasicFTData loadBasicInternalUserFTData')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/deploy-3.sh'))
      shell(readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/deploy-4.sh'))
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
         task(' ', readFileFromWorkspace('resources2/dbPipeline/databaseDeploy/deploy-postbuild.sh'))
      }
      archiveArtifacts {
          pattern('conf/**, logs/**')
       }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
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
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
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
