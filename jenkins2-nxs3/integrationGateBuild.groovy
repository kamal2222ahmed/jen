job('ELIS2-IntegrationGateBuild') {
  description(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuild/integrationGateBuildDescription.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-builder')
  blockOn('ELIS2-IntegrationGateBuild') {
    blockLevel('GLOBAL')
    scanQueueFor('DISABLED')
  }
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
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
    permissionAll('jgarciar')
    blocksInheritance()
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
      shell(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuild/preDatabaseDeploy.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('archive/resources/integrationGateBuild/1clean.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuild/npm-cache.sh'))
      gradle {
          description('build artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('archive/resources/integrationGateBuild/2buildArtifacts.txt'))
          tasks('build  runGulpFullNoInstall  :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('upload the artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('archive/resources/integrationGateBuild/3uploadArtifacts.txt'))
          tasks('uploadAll :Database:uploadArchives uploadAllS3 -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest  -x runGulpFull  -x compileJava -x jar -x war')
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
            content(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuild/emailFailureContent.txt'))
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
            content(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuild/emailSuccessContent.txt'))
            recipientList('')
            replyToList('')
            sendTo {
              developers()
              recipientList()
            subject('MASTER Release Candidate Created: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
            }
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
      downstreamParameterized {
        trigger('ELIS2-AcceptanceTesting,Database-changelog-diff,makeTheIntFixBranch') {
          condition('SUCCESS')
          parameters {
            currentBuild()
            gitRevision(true)
            predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
          }
        }
	trigger('ELIS2-IntegrationGateDB-Disruption') {
          condition('SUCCESS')
          parameters {
            predefinedProp('BRANCH', 'Integration')
            predefinedProp('ELIS_VERSION', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
          }
        }
        trigger('ELIS2-IntegrationGateBuild-InterfaceCheck') {
          condition('SUCCESS')
          parameters {
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
