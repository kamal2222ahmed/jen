
def jobName = 'ELIS2-StagingGateFlow'

buildFlowJob("${jobName}") {
    disabled()
    description(readFileFromWorkspace('jobs/build/resources/stagingGateFlow/stagingGateBuildFlowDescription.txt'))
    buildFlow(readFileFromWorkspace('jobs/build/resources/stagingGateFlow/groovyDSLStagingGateFlow.txt'))
    publishers {
        git {
            branch('origin', 'Integration')
            branch('origin', 'master')
            pushMerge(true)
            pushOnlyIfSuccess(true)
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
    triggers {
        scm('*/5 * * * *')
    }
    scm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                branch('Staging')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                relativeTargetDirectory('Apps')
                cloneOptions{
                    timeout(25)
                }
            }
        }
    }
    parameters {
        stringParam('NEXUS_ID', 'STG', 'The prefix of the nexus compiled and uploaded artiifact')
    }
    logRotator (14, 500, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(1)
        maxTotal(40)
    }
    concurrentBuild(true)
    jdk('java-1.8.0_u102')
    label 'Build-Deploy-Node-Java8'
    wrappers {
       buildUserVars()
	   buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
       timestamps()
       timeout {
           absolute(380)
           failBuild()
           writeDescription('Build failed due to timeout after {380} minutes')
       }
    }
}
