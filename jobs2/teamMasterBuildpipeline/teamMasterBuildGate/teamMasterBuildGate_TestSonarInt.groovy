job('Test-Team-ELIS2-BuildGate-testSonarInt') {
  description(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildTestSonarInt/teamMasterBuild-testSonarIntDescription.txt'))
  jdk('java-1.8.0_u112')
  concurrentBuild()
  label('npm-java8-tester')
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
    maxTotal(20)
    maxPerNode(1)
  }
  parameters {
    stringParam("NEXUS_ID", "MSTRBLDTST")
    stringParam("GIT_BRANCH_NAME")
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
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildTestSonarInt/teamBranchCheck.sh'))
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildTestSonarInt/teamMasterBuild-testSonarInt-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('Build Database.zip')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/Database')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildTestSonarInt/teamMasterBuild-gradle-database-clean-jar-2.txt'))
          tasks('clean jar')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildTestSonarInt/teamMasterBuild-testSonarInt-sed-database-3.txt'))
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildTestSonarInt/teamMasterBuild-gradle-clean-4.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildGateBuild/npm-cache.sh'))
      gradle {
          description('build and compile the code and execute the unit and Integration tests and sonar analysis')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/teamMasterBuildTestCheck/teamMasterBuildTestSonarInt/teamMasterBuild-gradle-build-test-sonar-5.txt'))
          tasks('build runGulpFullNoInstall :Database:clean :Database:build :Workspace:javakeystore:createJKS -x sonarqube -x buildRpm -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
  }
  publishers {
      archiveArtifacts {
          pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*')
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
      room('##ci-masterteambuild')
      startNotification(true)
      notifySuccess(true)
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
      string('SONAR_CRED', 'a9ab8a8e-d6fc-4241-893c-e500ebeca7df')
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
