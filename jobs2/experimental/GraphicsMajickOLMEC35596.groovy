job('EXP-GraphicsMajickOLMEC35596') {
  description(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/buildGateBuild-testSonarIntDescription.txt'))
  jdk('java-1.8.0_u102')
  concurrentBuild()
  label('npm-java8-tester-kamal')
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
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
    permissionAll('rptighe')
    permissionAll('gsbath')
    blocksInheritance()
  }
  logRotator {
    artifactDaysToKeep(10)
    daysToKeep(10)
    numToKeep(20)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  parameters {
    stringParam("NEXUS_ID", 'BLD', "This is the nexus snapshot precursor")
    stringParam("GIT_BRANCH_NAME", 'OLMEC-35596', "This is the name of the branch to build from")
    //stringParam("GIT_BRANCH_HASH")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('OLMEC-35596')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        //mergeOptions {
        //  branch('Staging')
        //  getFastForwardMode()
        //  remote('origin')
        //  strategy('default')
        // }
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/buildGateBuild-testSonarInt-1.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
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
      shell(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/buildGateBuild-testSonarInt-sed-database.txt'))
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
          description('build and compile the code and execute the unit and Integration tests and sonar analysis')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/gradle-build-test-sonar.txt'))
          tasks('build runGulpFullNoInstall :Database:clean :Database:build :Workspace:javakeystore:createJKS -x sonarqube -x buildRpm -x runGulpFull')
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
      notifyFailure(false)
      notifyBackToNormal(false)
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
