
def elis_pipelines = ["ELIS2-BuildGateBuild-testSonarInt", "ELIS2-IntegrationGateBuild-testSonarInt", "ELIS2-HOTFIX-BuildGate-testSonarInt"]
elis_pipelines.each {pipeline ->
job("${pipeline}") {
    if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
        description(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/buildGateBuild-testSonarIntDescription.txt'))
    }
    if (pipeline=="ELIS2-ELIS2-IntegrationGateBuild-testSonarInt") {
        description(readFileFromWorkspace('jobs/build/resources/integrationGateBuildTestSonarInt/integrationGateBuildTestSonarIntDescription.txt'))
    }
    if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
        description(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateTestSonarInt/hotfixBuildGateBuild-testSonarIntDescription.txt'))
    }
    configure { project ->
           def testPub = project / publishers / 'hudson.tasks.junit.JUnitResultArchiver' {
                    testResults(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/junit-archive.txt'))
                    keepLongStdio(true)
                    healthScaleFactor('1.0')
           }
    }
    configure { project ->
        def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
            teamDomain('uscis')
            authToken('CxGbmA6sSwX1wUSPX88I96Xp')
            buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
            if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
                room('#ci-buildgate')
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
            if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
                room('#elis-pipeline')
                startNotification(true)
                notifySuccess(true)
                notifyAborted(false)
                notifyNotBuilt(false)
                notifyUnstable(false)
                notifyFailure(true)
                notifyBackToNormal(false)
                notifyRepeatedFailure(false)
                commitInfoChoice('AUTHOR_AND_TITLESS')
                includeCustomMessage(true)
                customMessage('IntegrationGateVersion: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
               room('#elis-pipeline')
               startNotification(true)
               notifySuccess(true)
               notifyAborted(false)
               notifyNotBuilt(false)
               notifyUnstable(false)
               notifyFailure(true)
               notifyBackToNormal(true)
               notifyRepeatedFailure(false)
               commitInfoChoice('AUTHORS')
               includeCustomMessage(false)
               customMessage()
            }
        }
    }
    if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
        parameters {
            stringParam('NEXUS_ID', 'BLD', 'The prefix of the nexus compiled and uploaded artiifact')
            stringParam('GIT_BRANCH_NAME', 'The branch that is to be built')
            stringParam('GIT_BRANCH_HASH', 'The hash of the branch that is to be built')
        }
        logRotator (30, 10000, -1, 30)
        throttleConcurrentBuilds {
            maxPerNode(1)
            maxTotal(40)
        }
        concurrentBuild(true)
    }
    if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
        logRotator (75, 500, -1, 30)
        parameters {
            stringParam('MAJOR')
            stringParam('MINOR')
            stringParam('SPRINT')
        }
    }
    if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
        logRotator (75, 500, -1, 30)
        parameters {
            stringParam('NEXUS_ID', '', 'This is the nexus snapshot precursor')
            stringParam('GIT_BRANCH_NAME', '', 'This is the name of the branch to build from')
        }
        throttleConcurrentBuilds {
            maxPerNode(1)
            maxTotal(40)
        }
        concurrentBuild(true)
    }
    jdk('java-1.8.0_u102')
    label 'npm-java8-tester'
    scm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
                    branch('${GIT_BRANCH_HASH}')
                }
                if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
                    branch('Integration')
                }
                if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
                    branch('${GIT_BRANCH_NAME}')
                }
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                mergeOptions {
                    if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
                        branch('Staging')
                    }
                    if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
                        branch('master')
                    }
                    if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
                        branch('Release_Hotfixes')
                    }
                    getFastForwardMode()
                    remote('origin')
                    strategy('default')
                }
                relativeTargetDirectory('Apps')
                cloneOptions{
                    timeout(25)
                }
            }
        }
    }
    publishers {
        archiveArtifacts {
            pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*, Apps/Backend/Elis2Services/build/**/*')
        }
    }
    steps {
        shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/buildGateBuild-testSonarInt-1.sh'))
        environmentVariables {
            propertiesFile('${WORKSPACE}/deploy.properties')
        }
        gradle {
            description('Build Database.zip')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/Database')
            if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/gradle-database-clean-jar.txt'))
            }
            if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/integrationGateBuildTestSonarInt/gradle-build-database-switches.txt'))
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/gradle-database-clean-jar.txt'))
            }
            tasks('clean jar')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/buildGateBuild-testSonarInt-sed-database.txt'))
        gradle {
            description('clean')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/')
            if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/gradle-clean.txt'))
            }
            if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/integrationGateBuildTestSonarInt/gradle-clean-switches.txt'))
            }
            tasks('clean')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        gradle {
            description('build and compile the code and execute the unit and Integration tests and sonar analysis')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/')
            if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/gradle-build-test-sonar.txt'))
            }
            if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/integrationGateBuildTestSonarInt/gradle-execute-tests-switches.txt'))
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild-testSonarInt/gradle-build-test-sonar.txt'))
            }
            tasks('build runGulpFullNoInstall   :Database:clean :Database:build :Workspace:javakeystore:createJKS -x sonarRunner -x buildRpm -x runGulpFull')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
    }
    wrappers {
       buildUserVars()
           if (pipeline=="ELIS2-BuildGateBuild-testSonarInt") {
               buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
           }
           if (pipeline=="ELIS2-IntegrationGateBuild-testSonarInt") {
               buildName('#${ENV,var="MAJOR"}.${ENV,var="MINOR"}.${ENV,var="SPRINT"}.${BUILD_NUMBER}')
           }
           if (pipeline=="ELIS2-HOTFIX-BuildGate-testSonarInt") {
               buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
           }
	   credentialsBinding {
		   string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
		   string('SONAR_CRED', 'a9ab8a8e-d6fc-4241-893c-e500ebeca7df')
	   }
       timestamps()
       timeout {
           absolute(380)
           failBuild()
           writeDescription('Build failed due to timeout after {380} minutes')
       }
    }
  }
}
