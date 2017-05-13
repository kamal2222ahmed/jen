
def elis_pipelines = ["ELIS2-BuildGateBuild", "ELIS2-IntegrationGateBuild", "ELIS2-HF-IntegrationGateBuild", "ELIS2-HOTFIX-BuildGateBuild", "ELIS2-StagingGateBuild"]
elis_pipelines.each {pipeline ->
job("${pipeline}") {
    if (pipeline=="ELIS2-StagingGateBuild") {
        description(readFileFromWorkspace('jobs/build/resources/stagingGateBuild/stagingGateBuildDescription.txt'))
    }
    if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
        description('Builds the ELIS2 App for the HOTFIX BUILD GATE')
    }
    if (pipeline=="ELIS2-BuildGateBuild") {
        description(readFileFromWorkspace('jobs/build/resources/buildGateBuild/buildGateBuildDescription.txt'))
    }
    if (pipeline=="ELIS2-IntegrationGateBuild") {
        description(readFileFromWorkspace('jobs/build/resources/integrationGateBuild/integrationGateBuildDescription.txt'))
    }
    if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
        description(readFileFromWorkspace('jobs/build/resources/hotfixIntegrationGateBuild/hotfixIntegrationGateBuildDescription.txt'))
    }
    configure { project ->
        def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
            teamDomain('uscis')
            authToken('CxGbmA6sSwX1wUSPX88I96Xp')
            buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
            if (pipeline=="ELIS2-BuildGateBuild") {
                room('#ci-buildgate')
            }
            if (pipeline=="ELIS2-IntegrationGateBuild") {
                room('#elis-pipeline')
            }
            if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
                room('#elis-pipeline')
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
                room('#ci-hotfixes')
            }
            if (pipeline=="ELIS2-StagingGateBuild") {
                room('#ci-pipeline')
            }
            startNotification(true)
            notifySuccess(true)
            notifyAborted(false)
            notifyNotBuilt(false)
            notifyUnstable(false)
            notifyFailure(true)
            notifyBackToNormal(false)
            notifyRepeatedFailure(false)
            includeTestSummary(false)
            commitInfoChoice('AUTHORS_AND_TITLES')
            if (pipeline=="ELIS2-IntegrationGateBuild") {
                includeCustomMessage(true)
                customMessage('IntegrationGateVersion: ${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
                includeCustomMessage(true)
                customMessage('${GIT_BRANCH_NAME}')
            }
            if (pipeline=="ELIS2-StagingGateBuild") {
                includeCustomMessage(false)
                customMessage()
            }
            if (pipeline=="ELIS2-BuildGateBuild") {
                includeCustomMessage(false)
                customMessage()
            }
            if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
                includeCustomMessage(false)
                customMessage()
            }
        }
    }
    parameters {
        if (pipeline=="ELIS2-BuildGateBuild") {
            stringParam('NEXUS_ID', 'BLD', 'The prefix of the nexus compiled and uploaded artiifact')
            stringParam('GIT_BRANCH_NAME', 'The branch that is to be built')
            stringParam('GIT_BRANCH_HASH', 'The hash of the branch that is to be built')
        }
        if (pipeline=="ELIS2-IntegrationGateBuild") {
            stringParam('MAJOR', '')
            stringParam('MINOR', '')
            stringParam('SPRINT','')
        }
        if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
            stringParam('MAJOR', '')
            stringParam('MINOR', '')
            stringParam('SPRINT', '')
        }
        if (pipeline=="ELIS2-StagingGateBuild") {
            stringParam('NEXUS_ID', 'STG', 'This is the nexus snapshot precursor')
        }
        if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
            stringParam('NEXUS_ID', '', 'This is the nexus snapshot precursor')
            stringParam('GIT_BRANCH_NAME', 'CI-Production_Hotfixes', 'This is the name of the branch to build from')
        }
    }
    logRotator (30, 10000, -1, 30)
    if (pipeline=="ELIS2-BuildGateBuild") {
        throttleConcurrentBuilds {
            maxPerNode(1)
            maxTotal(40)
        }
        blockOnDownstreamProjects()
        concurrentBuild(true)
    }
    jdk('java-1.8.0_u102')
    label 'npm-java8-builder'
    scm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                if (pipeline=="ELIS2-BuildGateBuild") {
                    branch('${GIT_BRANCH_HASH}')
                }
                if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
                    branch('${GIT_BRANCH_NAME}')
                }
                if (pipeline=="ELIS2-IntegrationGateBuild") {
                    branch('Integration')
                }
                if (pipeline=="ELIS2-StagingGateBuild") {
                    branch('Staging')
                }
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                    if (pipeline=="ELIS2-BuildGateBuild") {
                        mergeOptions {
                            branch('Staging')
                            getFastForwardMode()
                            remote('origin')
                            strategy('default')
                        }
                    }
                    if (pipeline=="ELIS2-IntegrationGateBuild") {
                        mergeOptions {
                            branch('master')
                            getFastForwardMode()
                            remote('origin')
                            strategy('default')
                        }
                    }
                    if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
                        mergeOptions {
                            branch('Release_Hotfixes')
                            getFastForwardMode()
                            remote('origin')
                            strategy('default')
                        }
                    }
                    if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
                        mergeOptions {
                            branch('Release_Hotfixes')
                            getFastForwardMode()
                            remote('origin')
                            strategy('default')
                       }
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
            pattern('Apps/InternalApp/InternalApp/build/karma/**/*, Apps/gradlebuild/reports/profile/**/*')
        }
        if (pipeline=="ELIS2-BuildGateBuild") {
            postBuildScripts {
                steps {
                    shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild/returnAFTToThePool.sh'))
                }
            }
        }
        if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
            postBuildScripts {
                steps {
                    shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild/returnAFTToThePool.sh'))
                }
            }
        }
        if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
            downstreamParameterized {
                trigger('AMI_Creation_Job_Parallel') {
                    condition('SUCCESS')
                    parameters {
                        predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}-${BUILD_NUMBER}')
                    }
                }
            }
        }
        if (pipeline=="ELIS2-IntegrationGateBuild") {
            downstreamParameterized {
                trigger('ELIS2-AcceptanceTesting,Database-changelog-diff') {
                    condition('SUCCESS')
                    parameters {
                        predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
                    }
                }
                trigger('ELIS2-IntegrationGateBuild-InterfaceCheck') {
                    condition('SUCCESS')
                    parameters {
                        predefinedProp('VERSION', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
                    }
                }
                trigger('AMI_Creation_Job_Parallel') {
                    condition('SUCCESS')
                    parameters {
                        predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}-${BUILD_NUMBER}')
                    }
                }
            }
        }
        if (pipeline=="ELIS2-IntegrationGateBuild") {
            git {
                tag('origin', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}') {
                     create(true)
                     update(true)
                }
                pushMerge(true)
                pushOnlyIfSuccess(true)
            }
        }
        if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
            git {
               tag('origin', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}') {
                   create(true)
                   update(true)
               }
               pushMerge(false)
               pushOnlyIfSuccess(true)
            }
        }
    }
    steps {
        shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild/preDatabaseDeploy.sh'))
        environmentVariables {
            propertiesFile('${WORKSPACE}/deploy.properties')
        }
        gradle {
            description('clean')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/')
            if (pipeline=="ELIS2-BuildGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild/1clean.txt'))
            }
            if (pipeline=="ELIS2-StagingGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/stagingGateBuild/1clean.txt'))
            }
            if (pipeline=="ELIS2-IntegrationGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild/1clean.txt'))
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateBuild/gradle-clean-switches.txt'))
            }
            tasks('clean')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild/npm-cache.sh'))
        gradle {
            description('build artifacts')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/')
            if (pipeline=="ELIS2-BuildGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild/2buildArtifacts.txt'))
            }
            if (pipeline=="ELIS2-StagingGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/stagingGateBuild/2buildArtifacts.txt'))
            }
            if (pipeline=="ELIS2-IntegrationGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/integrationGateBuild/gradle-build-artifacts-switches.txt'))
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateBuild/gradle-build-artifacts-switches.txt'))
            }
            tasks('build  runGulpFullNoInstall  :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        gradle {
            description('upload the artifacts')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('Apps/')
            if (pipeline=="ELIS2-StagingGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/stagingGateBuild/3uploadTheArtifacts.txt'))
            }
            if (pipeline=="ELIS2-BuildGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/buildGateBuild/3uploadTheArtifacts.txt'))
            }
            if (pipeline=="ELIS2-IntegrationGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/integrationGateBuild/3uploadTheArtifacts.txt'))
            }
            if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
                switches(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateBuild/gradle-upload-artifacts-switches.txt'))
            }
            tasks('uploadAll :Database:uploadArchives -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x installGulp -x runGulpFull  -x compileJava -x jar -x war')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        if (pipeline=="ELIS2-BuildGateBuild") {
            shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild/getAFTFromThePool.sh'))
            environmentVariables {
                propertiesFile('${WORKSPACE}/env.properties')
            }
        }
        if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
            shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild/getAFTFromThePool.sh'))
            environmentVariables {
                propertiesFile('${WORKSPACE}/env.properties')
            }
        }
        if (pipeline=="ELIS2-StagingGateBuild") {
            shell(readFileFromWorkspace('jobs/build/resources/stagingGateBuild/ftPrepare.sh'))
            environmentVariables {
                propertiesFile('${WORKSPACE}/env.properties')
            }
        }
        if (pipeline=="ELIS2-BuildGateBuild") {
            downstreamParameterized {
                trigger('ELIS2-BuildGate-Tests') {
                    block {
                        buildStepFailure('never')
                        failure('never')
                        unstable('UNSTABLE')
                    }
                    parameters {
                        currentBuild()
                        predefinedProp('SNAPSHOT_NUMBER', '${BUILD_NUMBER}')
                        predefinedProp('NEXUS_ID', '${NEXUS_ID}')
                        predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
                    }
               }
           }
        }
        if (pipeline=="ELIS2-StagingGateBuild") {
            downstreamParameterized {
                trigger('ELIS2-StagingGate-Tests') {
                    block {
                        buildStepFailure('never')
                        failure('never')
                        unstable('UNSTABLE')
                    }
                parameters {
                    currentBuild()
                    predefinedProp('SNAPSHOT_NUMBER', '${BUILD_NUMBER}')
                    predefinedProp('NEXUS_ID', '${NEXUS_ID}')
                    predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
                    predefinedProp('GIT_BRANCH_NAME', 'Staging')
                    predefinedProp('GIT_BRANCH_HASH', '${GIT_COMMIT}')
                }
             }
          }
        }
        if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
            downstreamParameterized {
                trigger('ELIS2-HOTFIX-BuildGate-Tests') {
                    block {
                        buildStepFailure('never')
                        failure('never')
                        unstable('UNSTABLE')
                    }
                parameters {
                    currentBuild()
                    predefinedProp('SNAPSHOT_NUMBER', '${BUILD_NUMBER}')
                    predefinedProp('NEXUS_ID', 'HTFX')
                    predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
                    predefinedProp('GIT_BRANCH_NAME', '${GIT_BRANCH_NAME}')
                }
             }
          }
       }
    }
    wrappers {
       buildUserVars()
           if (pipeline=="ELIS2-HF-IntegrationGateBuild") {
               buildName('#${ENV,var="MAJOR"}.${ENV,var="MINOR"}.${ENV,var="SPRINT"}.${BUILD_NUMBER}')
           }
           if (pipeline=="ELIS2-StagingGateBuild") {
               buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
           }
           if (pipeline=="ELIS2-BuildGateBuild") {
               buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
           }
           if (pipeline=="ELIS2-IntegrationGateBuild") {
               buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
           }
           if (pipeline=="ELIS2-HOTFIX-BuildGateBuild") {
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
