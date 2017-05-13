job('Test-Team-ELIS2-BuildGate') {
  description(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/teamBuildGateBuildDescription.txt'))
  jdk('java-1.8.0_u112')
  concurrentBuild()
  label('npm-java8-builder')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    daysToKeep(30)
    numToKeep(100)
  }
  throttleConcurrentBuilds {
    maxTotal(12)
    maxPerNode(1)
  }
  parameters {
    stringParam("GIT_BRANCH_NAME")
    stringParam("NEXUS_ID", "MSTRBLDTST")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_NAME}')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
         }
        }
      }
  steps {
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/teambranchCheck.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/1clean.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/npm-cache.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('build artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/2buildArtifacts.txt'))
          tasks('build  runGulpFullNoInstall  :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('upload the artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/3uploadArtifacts.txt'))
          tasks('uploadAll :Database:uploadArchives uploadAllS3 -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x runGulpFull -x compileJava -x jar -x war')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/teamFTbranchCheck.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/env.properties')
      }
      downstreamParameterized {
        trigger('ELIS2-Team-MasterBuildTestCheck-Tests') {
          block {
            buildStepFailure('never')
            failure('never')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            predefinedProp('SNAPSHOT_NUMBER', '${BUILD_NUMBER}')
            predefinedProp('NEXUS_ID', '${NEXUS_ID}')
            predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
            predefinedProp('TEAM', '${TEAM}')
            }
          }
        }
        downstreamParameterized {
        trigger('ELIS2-Team-FT-Wake-Up-N-Sleep') {
          parameters {
            predefinedProp('STATE', 'DISABLE')
            predefinedProp('BRANCH', '${GIT_BRANCH_NAME}')
           }
         }
      }
  }
  publishers {
      archiveArtifacts {
          pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*')
      }
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/updateteamFTpool.sh"))
          onlyIfBuildFails(false)
          onlyIfBuildSucceeds(false)
        }
     }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-masterteambuild')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      includeTestSummary(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage()
    }
  }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
      string('SONAR_CRED', 'a9ab8a8e-d6fc-4241-893c-e500ebeca7df')
      file('keyToUse', '6aaa142d-b2d3-4d6c-af8d-0a438ef89fe7')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
    timeout {
        absolute(300)
        failBuild()
        writeDescription('Build failed due to timeout after 300 minutes')
      }
   }
}
