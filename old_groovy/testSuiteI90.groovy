
def jobName = 'I90'
def switchesForGradle = readFileFromWorkspace('jobs/build/resources/serenityTests/gradleForI90TestSuite.txt')
def afterGroovyScript = readFileFromWorkspace('jobs/build/resources/serenityTests/serenityAfterTestReport.txt')
def elis_pipelines = ["ELIS2-VerifyTest", "ELIS2-HF-TS", "ELIS2-TestSuite"]
elis_pipelines.each {pipeline ->
job("${pipeline}-${jobName}") {
    description(readFileFromWorkspace('jobs/build/resources/serenityTests/i90Description.txt'))
    configure { project ->
           def testPub = project / publishers / 'hudson.tasks.junit.JUnitResultArchiver' {
                    testResults('Apps/Tests/Serenity/build/test-results/TEST*.xml')
                    keepLongStdio(true)
                    testDataPublishers {
                        "hudson.plugins.claim.ClaimTestDataPublisher"{
                        }
                    }
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
    configure { project ->
         def xvfbBlock = project / buildWrappers / 'org.jenkinsci.plugins.xvfb.Xvfb' {
             installationName('Default')
             screen('1024x768x24')
             debug(false)
             timeout(0)
             displayNameOffset(1)
             additionalOptions('-nolisten tcp')
             shutdownWIthBuild(false)
             autoDisplayName(true)
             assignedLabels()
             parallelBuild(true)
         }
    }
    configure { project ->
        def cleanDatWorkspace = project / buildWrappers / 'hudson.plugins.ws__cleanup.PreBuildCleanup' {
            deleteDirs(false)
            cleanupParameter()
            externalDelete()
        }
    }
    configure { project->
        def testReportPublish = project / publishers / 'hudson.tasks.junit.JUnitResultArchiver' {
            testResults('Apps/Tests/Serenity/build/test-results/TEST*.xml')
            keepLongStdio(true)
            healthScaleFactor(1.0)
            allowEmptyResults(false)
        }
    }
    parameters {
        stringParam('ARTIFACT_VERSION')
        stringParam('ENVIRONMENT')
        stringParam('GIT_BRANCH_NAME')
        stringParam('GIT_BRANCH_HASH')
        stringParam('NEXUS_ID')
        stringParam('SNAPSHOT_NUMBER')
        stringParam('MAX_PARALLEL_FORKS', '1')
        stringParam('ADDITIONAL_GRADLE_PARAMS')
    }
    logRotator (14, 100, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(1)
        maxTotal(40)
    }
    concurrentBuild(true)
    jdk('java-1.8.0_u102')
    label 'Linux-Test-Java8'
    multiscm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                branch('${GIT_BRANCH_HASH}')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            if (pipeline=="ELIS2-TestSuite") {
                extensions {
                    mergeOptions {
                        branch('Staging')
                        getFastForwardMode()
                        remote('origin')
                        strategy('default')
                    }
                    wipeOutWorkspace()
                    relativeTargetDirectory('Apps')
                    cloneOptions {
                        timeout(25)
                    }
                }
             } else {
                extensions {
                    relativeTargetDirectory('Apps')
                    cloneOptions {
                       timeout(25)
                    }
                }
            }
        }
        git {
            remote {
                url('${CHEF_REPO_SSH}')
                branch('master')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                wipeOutWorkspace()
                relativeTargetDirectory('ChefRepo')
                cloneOptions {
                    timeout(25)
                }
            }
        }
    }
    publishers {
        archiveArtifacts {
            pattern("Apps/Tests/Serenity/target/site/serenity/**")
        }
        groovyPostBuild {
            script(afterGroovyScript)
            sandbox(true)
        }
    }
    steps {
        shell(readFileFromWorkspace('jobs/build/resources/serenityTests/getFTSerenityGradleProperties.sh'))
        gradle {
            description('Perform the Test')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/Tests/Serenity')
            switches(switchesForGradle)
            tasks('clean cucumberMonkey test activeMQPrinter aggregate ensureNoTestFailures')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
    }
    wrappers {
       buildUserVars()
	   buildName('#${ENV,var="GIT_BRANCH_NAME"}  -  ${ENV,var="ENVIRONMENT"}  -  ${BUILD_NUMBER}')
       timestamps()
       timeout {
           absolute(60)
           failBuild()
           writeDescription('I was terminanted because I was slow')
       }
    }
 }
}
