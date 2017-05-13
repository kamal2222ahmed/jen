job('DB-intTest') {
  description(readFileFromWorkspace('resources2/dbPipeline/databaseIntTest/databaseIntTestDesc.txt'))
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
  configure { project ->
        def cleanDatWorkspace = project / buildWrappers / 'hudson.plugins.ws__cleanup.PreBuildCleanup' {
            deleteDirs(false)
            cleanupParameter()
            externalDelete()
        }
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
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('CI-IntFix-${PROD_VERSION}')
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
      shell(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/buildGateBuild-testSonarInt-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources2/dbPipeline/databaseIntTest/dbIntTest1.sh'))
      gradle {
          description('Build Database.zip')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/Database')
          switches(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/gradle-database-clean-jar.txt'))
          tasks('clean jar')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/dbPipeline/databaseIntTest/databaseBuild-IntTest-sed-database.sh'))
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/gradle-clean.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Deploy Production Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${PROD_VERSION}')
          switches(readFileFromWorkspace('resources2/dbPipeline/databaseIntTest/databaseUpdate.txt'))
          tasks('clean scorch update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('Deploy Test Database')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${ARTIFACT_VERSION}')
          switches(readFileFromWorkspace('resources2/dbPipeline/databaseIntTest/databaseUpdate.txt'))
          tasks('update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('build and compile the code and execute the unit and Integration tests')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/gradle-build-test-sonar.txt'))
          tasks('build runGulpFullNoInstall -x sonarRunner -x sonarqube -x buildRpm -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
  }
  publishers {
      archiveArtifacts {
          pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*, Apps/Backend/Elis2Services/build/**/*')
       }
  }
  configure { project ->
         def testPub = project / publishers / 'hudson.tasks.junit.JUnitResultArchiver' {
                  testResults('**\\TEST-*.xml')
                  keepLongStdio(true)
                  healthScaleFactor('1.0')
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
}
