
def jobName = 'ELIS2-BuildGateFlow'

buildFlowJob("${jobName}") {
    description(readFileFromWorkspace('jobs/build/resources/buildGateFlow/buildGateBuildFlowDescription.txt'))
    buildFlow(readFileFromWorkspace('jobs/build/resources/buildGateFlow/groovyDSLBuildGateFlow.txt'))
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
        stringParam('NEXUS_ID', 'BLD', 'The prefix of the nexus compiled and uploaded artiifact')
        stringParam('GIT_BRANCH_NAME', 'The branch that is to be built')
        stringParam('GIT_BRANCH_HASH', 'The hash of the branch that is to be built')
    }
    logRotator (75, 500, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(1)
        maxTotal(40)
    }
    concurrentBuild(true)
    jdk('java-1.8.0_u102')
    label 'Build-Deploy-Node-Java8'
    wrappers {
       buildUserVars()
       timestamps()
       timeout {
           absolute(380)
           failBuild()
           writeDescription('Build failed due to timeout after {380} minutes')
       }
    }
}
buildFlowJob('ELIS2-HOTFIX-BuildGateFlow') {
    description(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateFlow/hotfixBuildFlowDescription.txt'))
    buildFlow(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateFlow/groovyDSLForHotfixBuildFlow.txt'))
    publishers {
        git {
            branch('origin', 'Release_Hotfixes')
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
    parameters {
        stringParam('NEXUS_ID', 'HTFX', 'The prefix of the nexus compiled and uploaded artiifact')
        stringParam('GIT_BRANCH_NAME', 'CI-Production_Hotfixes', 'The branch that is to be built')
    }
    logRotator (75, 500, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(1)
        maxTotal(5)
    }
    concurrentBuild(true)
    jdk('java-1.8.0_u102')
    label 'Build-Deploy-Node-Java8'
    scm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                branch('$[GIT_BRANCH_NAME}')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                mergeOptions{
                    branch('Release_Hotfixes')
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

    wrappers {
       buildUserVars()
       timestamps()
       timeout {
           absolute(380)
           failBuild()
           writeDescription('Build failed due to timeout after {380} minutes')
       }
    }
}
