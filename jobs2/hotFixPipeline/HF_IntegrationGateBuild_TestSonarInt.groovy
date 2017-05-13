job('OLD-ELIS2-HF-IntegrationGateBuild-testSonarInt') {
  description(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuildTestSonarInt/hotfixIntegrationGateSonarIntDescription.txt'))
  jdk('java-1.8.0_u112')
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
        }
      }
    }
  steps {
      shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuildTestSonarInt/hf-integrationGate-testSonarInt-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
        description('clean')
        gradleName('gradle-2.7')
        passAsProperties(true)
        rootBuildScriptDir('Apps/')
        switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuildTestSonarInt/2clean.txt'))
        tasks('clean')
        useWrapper(false)
        useWorkspaceAsHome(true)
      }
    gradle {
      description('Build Database.zip')
      gradleName('gradle-2.7')
      passAsProperties(true)
      rootBuildScriptDir('Apps/Database')
      switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuildTestSonarInt/3buildDatabasezip.txt'))
      tasks('jar')
      useWrapper(false)
      useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuildTestSonarInt/hf-IntegrationGate-testSonarInt-sed-database-4.txt'))
      gradle {
          description('build and compile the code and execute the unit and Integration tests and sonar analysis')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuildTestSonarInt/5buildTestSonar.txt'))
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
      room('#elis-pipeline')
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
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
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
