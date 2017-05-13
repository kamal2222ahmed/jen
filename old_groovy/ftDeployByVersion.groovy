
def jobName = 'ELIS2-FT-DeployByVersion'
def postScript = readFileFromWorkspace('jobs/build/resources/ftDeployByVersion/getFTLogs.sh')
job("${jobName}") {
    description('Deploy a specific version of the code to the FT server provided.')
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
        stringParam('ARTIFACT_VERSION')
        stringParam('ENVIRONMENT')
        stringParam('BRANCH')
        stringParam('TOGGLE')
    }
    logRotator (20, 500, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(1)
        maxTotal(40)
    }
    concurrentBuild(true)
    jdk('java-1.8.0_u102')
    label 'Build-Deploy-Node-Java8'
    multiscm {
        git {
            remote {
                name('origin')
                url('${JENKINSUTIL_REPO_SSH}')
                branch('master')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                wipeOutWorkspace()
                relativeTargetDirectory('jenkinsutil')
                cloneOptions {
                    timeout(25)
                }
            }
        }
        git {
            remote {
                url('${FT_REPO_SSH}')
                branch('master')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                wipeOutWorkspace()
                relativeTargetDirectory('ft')
                cloneOptions {
                    timeout(25)
                }
            }
        }
    }
    publishers {
        postBuildTask {
            task(' ',readFileFromWorkspace('jobs/build/resources/ftDeployByVersion/getFTLogs.sh'))
        }
        archiveArtifacts {
            pattern("logs/**, conf/**")
        }
    }
    steps {
        shell(readFileFromWorkspace('jobs/build/resources/ftDeployByVersion/preFTDeployshell.sh'))
        environmentVariables {
            propertiesFile('${WORKSPACE}/deploy.properties')
        }
        shell(readFileFromWorkspace('jobs/build/resources/ftDeployByVersion/preFTDeployshell2.sh'))
        shell(readFileFromWorkspace('jobs/build/resources/ftDeployByVersion/FTDeployKnifeSSHshell.sh'))
        shell(readFileFromWorkspace('jobs/build/resources/ftDeployByVersion/mongoscorch.sh'))
        gradle {
            description('Deploy the Production Database Version Baseline Update')
            gradleName('gradle-2.2.1')
            rootBuildScriptDir('Database-${PROD_VERSION}')
            passAsProperties(true)
            switches('-ParchiveBranch=develop -ParchiveBuildNumber=${BUILD_NUMBER} -PdatabaseServerPathToTablespace=/u01/oracle/oradata/ -PdatabaseServer=${dbip} -ParchiveRevision=${BUILD_NUMBER} -PbuildVersion=${ARTIFACT_VERSION} -PpbeKeyFile=${WORKSPACE}/ft/pbe.dat -Ptoggle=ALL_ON -Pnexus=${NEXUS} -PnexusUser=admin -PftWebHost=${tcip}')
            tasks('clean scorch update')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        gradle {
            description('Deploy the Test Database Version - Update')
            gradleName('gradle-2.2.1')
            rootBuildScriptDir('Database-${ARTIFACT_VERSION}')
            passAsProperties(true)
            switches('-ParchiveBranch=develop -ParchiveBuildNumber=${BUILD_NUMBER} -PdatabaseServerPathToTablespace=/u01/oracle/oradata/ -PdatabaseServer=${dbip} -ParchiveRevision=${BUILD_NUMBER} -PbuildVersion=${ARTIFACT_VERSION} -PpbeKeyFile=${WORKSPACE}/ft/pbe.dat -Ptoggle=ALL_ON -Pnexus=${NEXUS} -PnexusUser=admin -PftWebHost=${tcip}')
            tasks('update')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        gradle {
            description('Load the FT Data - loadFTData')
            gradleName('gradle-2.2.1')
            passAsProperties(true)
            rootBuildScriptDir('DataLoader-${ARTIFACT_VERSION}')
            switches('-PpbeKeyFile=${WORKSPACE}/ft/pbe.dat -PdatabaseServer=${dbip} -PftWebHost=${tcip}')
            tasks('loadBasicFTData loadBasicInternalUserFTData')
            useWrapper(false)
            useWorkspaceAsHome(true)
        }
        shell(readFileFromWorkspace('jobs/build/resources/ftDeployByVersion/FTDeploy.sh'))
    }
    wrappers {
       buildUserVars()
	   buildName('#${ENV,var="ENVIRONMENT"}-${ENV,var="ARTIFACT_VERSION"}')
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
