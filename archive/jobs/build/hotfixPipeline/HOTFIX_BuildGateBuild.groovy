job('ELIS2-HOTFIX-BuildGateBuild') {
  description(readFileFromWorkspace('resources/hotfixBuildGateBuild/hotfixBuildGateBuildDescription.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-builder')
  throttleConcurrentBuilds {
    maxTotal(3)
    maxPerNode(1)
  }
  concurrentBuild(true)
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    daysToKeep(75)
    numToKeep(500)
  }
  throttleConcurrentBuilds {
    maxTotal(15)
    maxPerNode(3)
  }
  parameters {
    stringParam("GIT_BRANCH_NAME", "CI-Production_Hotfixes")
    stringParam("NEXUS_ID", "HTFX")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_NAME}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        mergeOptions {
          branch('Release_Hotfixes')
          getFastForwardMode()
          remote('origin')
          strategy('default')
        }
        relativeTargetDirectory('Apps')
        }
      }
    }
  steps {
      shell(readFileFromWorkspace('resources/hotfixBuildGateBuild/preDatabaseDeploy.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/hotfixBuildGateBuild/1clean.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/hotfixBuildGateBuild/npm-cache.sh'))
      gradle {
          description('build artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/hotfixBuildGateBuild/2buildArtifacts.txt'))
          tasks('build  runGulpFullNoInstall  :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('upload the artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/hotfixBuildGateBuild/3uploadArtifacts.txt'))
          tasks('uploadAll :Database:uploadArchives -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x installGulp -x runGulpFull  -x compileJava -x jar -x war')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/getAFTFromThePool.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/env.properties')
      }
      downstreamParameterized {
        trigger('ELIS2-HOTFIX-BuildGate-Tests') {
          block {
            buildStepFailure('never')
            failure('never')
            unstable('UNSTABLE')
          }
          parameters {
            currentBuild()
            predefinedProp('SNAPSHOT_NUMBER', '${BUILD_NUMBER}')
            predefinedProp('NEXUS_ID', 'HTFX')
            predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
          }
        }
     }
  }
  publishers {
      archiveArtifacts {
          pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*, Apps/Backend/Elis2Services/build/**/*')
      }
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources/returnAFTToThePool.sh"))
        }
      }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
      room('#ci-jenkins2_0dsl')
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
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
      string('SONAR_CRED', 'a9ab8a8e-d6fc-4241-893c-e500ebeca7df')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
    timeout {
        absolute(220)
        failBuild()
        writeDescription('Build failed due to timeout after 220 minutes')
    }
  }
}
