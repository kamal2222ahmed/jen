job('ELIS2-HF-IntegrationGateBuild-testSonarInt') {
  description(readFileFromWorkspace('resources/hotfixIntegrationGateBuildTestSonarInt/hotfixIntegrationGateSonarIntDescription.txt'))
  jdk('java-1.8.0_u102')
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
  parameters {
    stringParam("MAJOR")
    textParam("MINOR")
    stringParam("SPRINT")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('Release_Hotfixes')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('Apps')
        }
      }
    }
  steps {
      shell(readFileFromWorkspace('resources/hotfixIntegrationGateBuildTestSonarInt/hf-integrationGate-testSonarInt-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
        description('clean')
        gradleName('gradle-2.7')
        passAsProperties(true)
        rootBuildScriptDir('Apps/')
        switches(readFileFromWorkspace('resources/hotfixIntegrationGateBuildTestSonarInt/2clean.txt'))
        tasks('clean')
        useWrapper(false)
        useWorkspaceAsHome(true)
      }
    gradle {
      description('Build Database.zip')
      gradleName('gradle-2.7')
      passAsProperties(true)
      rootBuildScriptDir('Apps/Database')
      switches(readFileFromWorkspace('resources/hotfixIntegrationGateBuildTestSonarInt/3buildDatabasezip.txt'))
      tasks('jar')
      useWrapper(false)
      useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/hotfixIntegrationGateBuildTestSonarInt/hf-IntegrationGate-testSonarInt-sed-database-4.txt'))
      gradle {
          description('build and compile the code and execute the unit and Integration tests and sonar analysis')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/hotfixIntegrationGateBuildTestSonarInt/5buildTestSonar.txt'))
          tasks('build runGulpFullNoInstall sonarqube -x buildRpm -x runGulpFull')
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
    buildName('#${ENV,var="MAJOR"}.${ENV,var="MINOR"}.${ENV,var="SPRINT"}.${BUILD_NUMBER}')
    timeout {
        absolute(220)
        failBuild()
        writeDescription('Build failed due to timeout after 220 minutes')
    }
  }
}
