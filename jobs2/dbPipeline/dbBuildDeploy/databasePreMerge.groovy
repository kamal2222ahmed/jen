job('DB-PreMerge') {
  description(readFileFromWorkspace('resources2/dbPipeline/databasePreMerge/databasePreMergeDesc.txt'))
  jdk('java-1.8.0_u112')
  concurrentBuild()
  label('npm-java8-builder')
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
    artifactDaysToKeep(25)
    daysToKeep(25)
    numToKeep(400)
  }
  throttleConcurrentBuilds {
    maxTotal(20)
    maxPerNode(1)
  }
  parameters {
    stringParam("NEXUS_ID")
    stringParam("GIT_BRANCH_NAME", null, "This is the name of the branch to build from")
    stringParam("PROD_VERSION", null, "This is the nexus snapshot precursor")
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
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_NAME}')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
    git {
        remote {
            url('${APPS_REPO_SSH}')
            branch('CI-IntFix-${PROD_VERSION}')
            credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
        }
        extensions {
            wipeOutWorkspace()
            relativeTargetDirectory('Prod')
            cloneOptions {
               timeout(25)
            }
        }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/dbPipeline/databasePreMerge/buildDatabase.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources2/dbPipeline/databasePreMerge/npm-cache.sh'))
      gradle {
          description('Database build uploadArtifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/dbPipeline/databasePreMerge/1-gradleSwitches.txt'))
          tasks('clean :Database:build :Database:uploadArchives')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Clean  CI-IntFix')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Prod/')
          switches(readFileFromWorkspace('resources2/dbPipeline/databasePreMerge/2-gradleSwitch.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Build CI-IntFix Branch')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Prod/')
          switches(readFileFromWorkspace('resources2/dbPipeline/databasePreMerge/3-gradleSwitches.txt'))
          tasks('build  runGulpFullNoInstall  -x :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('upload the artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Prod/')
          switches(readFileFromWorkspace('resources2/dbPipeline/databasePreMerge/4-gradleSwitches.txt'))
          tasks('uploadAll -x :Database:uploadArchives uploadAllS3 -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x installGulp -x runGulpFull  -x compileJava -x jar -x war')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      downstreamParameterized {
        trigger('DB-Test-Flow') {
          block {
            buildStepFailure('FAILURE')
            failure('FAILURE')
            unstable('UNSTABLE')
          }
          parameters {
            predefinedProp('GIT_BRANCH_HASH', '${GIT_COMMIT}')
            predefinedProp('GIT_BRANCH_NAME', '${GIT_BRANCH_NAME}')
            predefinedProp('NEXUS_ID', '${NEXUS_ID}')
            predefinedProp('PROD_VERSION', '${PROD_VERSION}')
            predefinedProp('ARTIFACT_VERSION', '${NEXUS_ID}.${BUILD_NUMBER}-SNAPSHOT')
            predefinedProp('SMOKETEST', '${SMOKETEST}')
          }
        }
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
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
