job('ELIS2-IntegrationGateBuild') {
  description(readFileFromWorkspace('resources/integrationGateBuild/integrationGateBuildDescription.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-builder')
  blockOn('ELIS2-IntegrationGateBuild') {
    blockLevel('GLOBAL')
    scanQueueFor('DISABLED')
  }
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(20)
    daysToKeep(100)
    numToKeep(1000)
  }
  parameters {
    stringParam("MAJOR")
    stringParam("MINOR")
    stringParam("SPRINT")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('Integration')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        mergeOptions {
          branch('master')
          getFastForwardMode()
          remote('origin')
          strategy('default')
        }
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources/buildGateBuild/preDatabaseDeploy.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/integrationGateBuild/1clean.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/buildGateBuild/npm-cache.sh'))
      gradle {
          description('build artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/integrationGateBuild/2buildArtifacts.txt'))
          tasks('build  runGulpFullNoInstall  :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('upload the artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/integrationGateBuild/3uploadArtifacts.txt'))
          tasks('uploadAll :Database:uploadArchives uploadAllS3 -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x installGulp -x runGulpFull  -x compileJava -x jar -x war')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
  }
  publishers {
      archiveArtifacts {
          pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*, Apps/Backend/Elis2Services/build/**/*')
      }
      extendedEmail {
        triggers {
          failure {
            content(readFileFromWorkspace('resources/integrationGateBuild/emailFailureContent.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov, #ELIS2TechLeads@uscis.dhs.gov')
            replyToList('#ELIS2TechLeads@uscis.dhs.gov')
            sendTo {
              developers()
              culprits()
              recipientList()
            subject('IMMEDIATE ACTION REQUIRED: INTEGRATION GATE FAILURE')
            }
          }
          success {
            content(readFileFromWorkspace('resources/integrationGateBuild/emailSuccessContent.txt'))
            recipientList('#ELIS2-CI@uscis.dhs.gov, #ELIS2TechLeads@uscis.dhs.gov')
            replyToList('#ELIS2TechLeads@uscis.dhs.gov')
            sendTo {
              developers()
              recipientList()
            subject('MASTER Release Candidate Created: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
            }
          }
        }
      }
      downstreamParameterized {
        trigger('ELIS2-AcceptanceTesting,Database-changelog-diff,makeTheIntFixBranch') {
          condition('SUCCESS')
          parameters {
            currentBuild()
            gitRevision(true)
            predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
          }
        }
        trigger('ELIS2-IntegrationGateBuild-InterfaceCheck') {
          condition('SUCCESS')
          parameters {
            currentBuild()
            gitRevision(true)
            predefinedProp('VERSION', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
          }
        }
        trigger('AMI_Creation_Job_Parallel') {
          condition('SUCCESS')
          parameters {
            currentBuild()
            gitRevision(true)
            predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}-${BUILD_NUMBER}')
          }
        }
     }
     git {
       pushMerge(false)
       pushOnlyIfSuccess(true)
       forcePush(false)
       tag("origin", '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}'){
         create(true)
         update(true)
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
      notifyBackToNormal(false)
      notifyRepeatedFailure(false)
      includeTestSummary(true)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(true)
      customMessage('IntegrationGateVersion: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
    }
  }
  publishers {
    downstreamParameterized {
        trigger('FT-PROD-DB-EXPORT-CREATE') {
          parameters {
            predefinedProp('EXTRACT_VERSION', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
            predefinedProp('ENVIRONMENT', 'FT999')
            predefinedProp('BRANCH', 'master')
            predefinedProp('TOGGLE', 'ALL_ON')
          }
        }
     }
  }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
    }
    buildUserVars()
    timestamps()
    buildName('${ENV,var="MAJOR"}.${ENV,var="MINOR"}.${ENV,var="SPRINT"}.${BUILD_NUMBER}')
    timeout {
        absolute(30)
        failBuild()
        writeDescription('Build failed due to timeout after 30 minutes')
    }
  }
}
