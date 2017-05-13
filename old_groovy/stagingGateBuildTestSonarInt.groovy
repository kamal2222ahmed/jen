
def jobName = 'ELIS2-StagingGateBuild-testSonarInt'

job("${jobName}") {
    description(readFileFromWorkspace('jobs/build/resources/stagingGateBuildTestSonarInt/stagingGateBuild-testSonarIntDescription.txt'))
    configure { project ->
           def testPub = project / publishers / 'hudson.tasks.junit.JUnitResultArchiver' {
                    testResults(readFileFromWorkspace('jobs/build/resources/stagingGateBuildTestSonarInt/junit-archive.txt'))
                    keepLongStdio(true)
                    healthScaleFactor('1.0')
           }
    }
    configure { project ->
        def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
            teamDomain('uscis')
            authToken('CxGbmA6sSwX1wUSPX88I96Xp')
            buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
            room('#charese-test-private')
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
    parameters {
        stringParam('NEXUS_ID', 'STG', 'The prefix of the nexus compiled and uploaded artiifact')
    }
    logRotator (75, 100, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(1)
        maxTotal(1)
    }
    jdk('java-1.8.0_u102')
    label 'npm-java8-tester'
    multiscm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                branch('Staging')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                mergeOptions{
                    branch('master')
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
        shell(readFileFromWorkspace('jobs/build/resources/stagingGateBuildTestSonarInt/stagingGateBuild-testSonarInt-1.sh'))
        environmentVariables {
            propertiesFile('deploy.properties')
        }
        gradle {
            description('clean')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/')
            switches(readFileFromWorkspace('jobs/build/resources/stagingGateBuildTestSonarInt/1clean.txt'))
            tasks('clean')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        gradle {
            description('Build Database.zip')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/Database')
            switches(readFileFromWorkspace('jobs/build/resources/stagingGateBuildTestSonarInt/2cleanJarDatabase.txt'))
            tasks('clean jar')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        shell(readFileFromWorkspace('jobs/build/resources/stagingGateBuildTestSonarInt/sed-database.txt'))
        gradle {
            description('build and compile the code and execute the unit and Integration tests and sonar analysis')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/')
            switches(readFileFromWorkspace('jobs/build/resources/stagingGateBuildTestSonarInt/3compileExecuteTests.txt'))
            tasks('build runGulpFullNoInstall   :Database:clean :Database:build :Workspace:javakeystore:createJKS -x sonarRunner -x buildRpm -x runGulpFull')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
    }
    wrappers {
       buildUserVars()
	   buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
	   credentialsBinding {
		   string('MYSQL_CRED', 'a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
		   string('SONAR_CRED', 'a9ab8a8e-d6fc-4241-893c-e500ebeca7df')
	   }
       timestamps()
       timeout {
           absolute(220)
           failBuild()
           writeDescription('Build failed due to timeout after 220 minutes')
       }
    }
}
