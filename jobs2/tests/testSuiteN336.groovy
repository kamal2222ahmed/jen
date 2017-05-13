
def jobName = 'N336'
def switchesForGradle = readFileFromWorkspace('resources2/serenityTests/gradleForN336.txt')
def afterGroovyScript = readFileFromWorkspace('resources2/serenityTests/serenityAfterTestReport.txt')
def elis_pipelines = ["ELIS2-VerifyTest", "ELIS2-HF-TS", "ELIS2-TestSuite", "DB-TestSuite", "ELIS2-Team-TestSuite", "ELIS2-BGO-TestSuite"]
elis_pipelines.each {pipeline ->
job("${pipeline}-${jobName}") {
    description(readFileFromWorkspace('resources2/serenityTests/N336Description.txt'))
    authorization {
      permission('hudson.model.Item.Discover', "anonymous")
      permission('hudson.model.Item.Read', 'anonymous')
      permissionAll('akkausha')
      permissionAll('ambutt')
      permissionAll('brprin')
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
      permissionAll('mrahman1')
      permissionAll('qhrashid')
    }
    configure { project ->
           def testPub = project / publishers / 'hudson.tasks.junit.JUnitResultArchiver' {
                    testResults('Apps/Tests/Serenity/build/test-results/TEST*.xml')
                    keepLongStdio(true)
                    testDataPublishers{
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
            if (pipeline=="ELIS2-TestSuite" || pipeline=="DB-TestSuite") {
              room('#ci-smoketest')
            }
            if (pipeline=="ELIS2-VerifyTest") {
              room('#ci-smoketest')
            }
            if (pipeline=="ELIS2-HF-TS") {
              room('#ci-hotfixes')
            }
            if (pipeline=="ELIS2-Team-TestSuite") {
              room('#elis-teamftmonitor')
            }
             if (pipeline=="ELIS2-BGO-TestSuite") {
              room('#ci-bgo-pipeline')
            }
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
             timeout(30)
             displayNameOffset(1)
             additionalOptions('-nolisten tcp -noreset')
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
        stringParam('GIT_BRANCH_NAME')
        stringParam('ENVIRONMENT')
        if (pipeline != "ELIS2-HF-TS" && pipeline != "ELIS2-VerifyTest" && pipeline != "ELIS2-BGO-TestSuite") {
          stringParam('GIT_BRANCH_HASH')
        }
        if (pipeline == "DB-TestSuite") {
          booleanParam("IS_SNAPSHOT", false, null)
        }
        if (pipeline == "ELIS2-Team-TestSuite") {
          stringParam("TEAM", "test", "the team this test is being executed for")
        }
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
    jdk('java-1.8.0_u112')
    label 'Linux-Test-Java8'
    multiscm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                if (pipeline != "ELIS2-HF-TS" && pipeline != "ELIS2-VerifyTest" && pipeline != "ELIS2-BGO-TestSuite" && pipeline != "ELIS2-Team-TestSuite") {
                    branch('${GIT_BRANCH_HASH}')
                } else {
                    branch('${GIT_BRANCH_NAME}')
                }
                credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
                credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
      if (pipeline == "ELIS2-Team-TestSuite") {
          shell(readFileFromWorkspace('resources2/serenityTests/databaseSedForTeamTests.sh'))
      } else {
          shell(readFileFromWorkspace('resources2/serenityTests/getFTSerenityGradleProperties.sh'))
      }
        gradle {
            description('Perform the Test')
            gradleName('gradle-2.7')
            passAsProperties(true)
            rootBuildScriptDir('Apps/Tests/Serenity')
            if (pipeline == "ELIS2-Team-TestSuite") {
                switches(readFileFromWorkspace('resources2/serenityTests/teamgradleForN336TestSuite.txt'))
            } else {
                switches(switchesForGradle)
            }
            tasks('clean test activeMQPrinter aggregate')
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
