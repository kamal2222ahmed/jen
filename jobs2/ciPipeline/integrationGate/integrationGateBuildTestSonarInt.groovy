job('ELIS2-IntegrationGateBuild-testSonarInt') {
  description(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/integrationGateBuildTestSonarIntDescription.txt'))
  jdk('java-1.8.0_u102')
  label('npm-java8-tester')
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
      shell(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/buildGateBuild-testSonarInt-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/gradle-clean-switches.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/buildGateBuild-testSonarInt-sed-database.txt'))
      shell(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/copyGradleProperties.txt'))
      gradle {
          description('execute Sonar Int Tests')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/gradle-execute-tests-switches.txt'))
          tasks('build runGulpFullNoInstall   :Database:clean :Database:build :Workspace:javakeystore:createJKS -x buildRpm -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('execute Sonar')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/gradle-execute-sonar-switches.txt'))
          tasks('sonarqube -x buildRpm -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
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
             content(readFileFromWorkspace('resources2/ciPipeline/integrationGate/integrationGateBuildTestSonarInt/emailFailureContent.txt'))
             recipientList('')
             replyToList('')
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
      room('#elis-pipeline')
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
  configure { project ->
    def hygieia = project / 'publishers' / 'jenkins.plugins.hygieia.HygieiaPublisher' {
      hygieiaAPIUrl("http://10.103.135.123:8080/api/")
      hygieiaJenkinsName("CI-PIPELINE")
    }
    hygieia << 'hygieiaSonar' {
      publishBuildStart("true")
      ceQueryIntervalInSeconds("10")
      ceQueryMaxAttempts("30")
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
        absolute(180)
        failBuild()
        writeDescription('Build failed due to timeout after 180 minutes')
    }
  }
}
