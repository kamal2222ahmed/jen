
def jobName = 'ELIS2-BuildGateAL-Tests'

buildFlowJob("${jobName}") {
    description(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/buildGateTestFlowDescription.txt'))
    buildFlow(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/groovyDSLALCBuildGateTestFlow.txt'))
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
        stringParam('ENVIRONMENT', 'FT129')
        stringParam('SNAPSHOT_NUMBER', '8.1.8.525')
        stringParam('MYSQL_PASSWORD', 'jenkins')
        stringParam('MYSQL_USER', 'jenkins')
        stringParam('GIT_BRANCH_NAME', '', 'The branch that is to be built')
        stringParam('GIT_BRANCH_HASH', '', 'The hash of the branch that is to be built')
    }
    logRotator (30, 10000, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(1)
        maxTotal(40)
    }
    concurrentBuild(true)
    jdk('java-1.8.0_u102')
    label 'Build-Deploy-Node-Java8'
    publishers {
        archiveArtifacts {
            pattern("logs/**, conf/**")
        }
        postBuildScripts {
            steps {
                shell(readFileFromWorkspace('jobs/build/resources/buildGateBuild/returnAFTToThePool.sh'))
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
