job('DB-PreMerge') {
  description(readFileFromWorkspace('resources/databaseBuild/databasePreMergeDesc.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-builder')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(10000)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  concurrentBuild()
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
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_NAME}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
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
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('CI-IntFix-${PROD_VERSION}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('Prod')
        cloneOptions {
          timeout(25)
        }
      }
    }
    git {
        remote {
            url('${JENKINSUTIL_REPO_SSH}')
            branch('master')
            credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
        }
        extensions {
            wipeOutWorkspace()
            relativeTargetDirectory('jenkinsutil')
            cloneOptions {
               timeout(25)
            }
        }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources/databaseBuild/buildDatabase.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources/databaseBuild/npm-cache.sh'))
      gradle {
          description('Database build upladArtifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/buildGateBuild/2buildArtifacts.txt'))
          tasks('clean  :Database:build :Database:uploadArchives')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Build CI-IntFix Branch')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Prod/')
          switches(readFileFromWorkspace('resources/buildGateBuild/2buildArtifacts.txt'))
          tasks('build  runGulpFullNoInstall  -x :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('upload the artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Prod/')
          switches(readFileFromWorkspace('resources/buildGateBuild/3uploadTheArtifacts.txt'))
          tasks('uploadAll -x :Database:uploadArchives -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x installGulp -x runGulpFull  -x compileJava -x jar -x war')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/getAFTFromThePool.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/env.properties')
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
            predefinedProp('PROD_VERSION', '${PROD_VERSION}')
            predefinedProp('ARTIFACT_VERSION', '${NEXUS_ID}.${BUILD_NUMBER}-SNAPSHOT')
            predefinedProp('NEXUS_ID', '${NEXUS_ID}')
            predefinedProp('SMOKETEST', '${SMOKETEST}')
            predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
          }
        }
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
      commitInfoChoice('AUTHORS_AND_TITLES')
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
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
