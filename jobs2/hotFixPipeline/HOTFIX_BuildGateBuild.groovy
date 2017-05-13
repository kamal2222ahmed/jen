job('ELIS2-HOTFIX-BuildGateBuild') {
  description(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateBuild/hotfixBuildGateBuildDescription.txt'))
  jdk('java-1.8.0_u112')
  label('npm-java8-builder')
  throttleConcurrentBuilds {
    maxTotal(3)
    maxPerNode(1)
  }
  concurrentBuild(true)
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
      shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateBuild/preDatabaseDeploy.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateBuild/1clean.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateBuild/npm-cache.sh'))
      gradle {
          description('build artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateBuild/2buildArtifacts.txt'))
          tasks('build  runGulpFullNoInstall  :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('upload the artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateBuild/3uploadArtifacts.txt'))
          tasks(':InternalApp:InternalApp:uploadArchives :Database:uploadArchives uploadAllS3 -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x installGulp -x runGulpFull  -x compileJava -x jar -x war')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/getAFTFromThePool.sh'))
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
          shell(readFileFromWorkspace("resources2/returnAFTToThePool.sh"))
        }
      }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-hotfixes')
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
