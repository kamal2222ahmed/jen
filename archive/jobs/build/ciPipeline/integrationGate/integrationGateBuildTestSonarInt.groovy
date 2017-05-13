job('ELIS2-IntegrationGateBuild-testSonarInt') {
  description(readFileFromWorkspace('resources/integrationGateBuildTestSonarInt/integrationGateBuildTestSonarIntDescription.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-tester')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(25)
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
      shell(readFileFromWorkspace('resources/buildGateBuild-testSonarInt/buildGateBuild-testSonarInt-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/integrationGateBuildTestSonarInt/gradle-clean-switches.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources/buildGateBuild-testSonarInt/buildGateBuild-testSonarInt-sed-database.txt'))
      shell(readFileFromWorkspace('resources/integrationGateBuildTestSonarInt/copyGradleProperties.txt'))
      gradle {
          description('execute Sonar Int Tests')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources/integrationGateBuildTestSonarInt/gradle-execute-tests-switches.txt'))
          tasks('build runGulpFullNoInstall   :Database:clean :Database:build :Workspace:javakeystore:createJKS sonarqube -x buildRpm -x runGulpFull')
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
             content(readFileFromWorkspace('resources/integrationGateBuildTestSonarInt/emailFailureContent.txt'))
             recipientList('#ELIS2TechLeads@uscis.dhs.gov')
             replyToList('#ELIS2TechLeads@uscis.dhs.gov')
             sendTo {
               developers()
             subject('IMMEDIATE ACTION REQUIRED: Integration Gate Failure')
             }
           }
         }
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
      buildServerUrl('http://10.103.130.42:8080/jenkins')
      room('#ci-jenkins2_0dsl')
      startNotification(false)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      includeTestSummary(false)
      commitInfoChoice('NOTHING')
      includeCustomMessage(true)
      customMessage("IntegrationGateVersion: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}")
    }
  }
  wrappers {
    credentialsBinding {
      string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
      string('SONAR_CRED', 'a9ab8a8e-d6fc-4241-893c-e500ebeca7df')
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="MAJOR"}.${ENV,var="MINOR"}.${ENV,var="SPRINT"}.${BUILD_NUMBER}')
    timeout {
        absolute(180)
        failBuild()
        writeDescription('Build failed due to timeout after 180 minutes')
    }
  }
}
