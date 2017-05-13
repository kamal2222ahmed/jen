
def elis_pipelines = ["ELIS2-HOTFIX-BuildGate-Tests"]
elis_pipelines.each {pipeline ->
        buildFlowJob("${pipeline}") {
        buildNeedsWorkspace(true)
        description(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateTestFlow/hotfixBuildGateTestFlowDescription.txt'))
        buildFlow(readFileFromWorkspace('jobs/build/resources/hotfixBuildGateTestFlow/groovyDSLHotfixBuildGateTestFlow.txt'))
        configure { project ->
            def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
            teamDomain('uscis')
            authToken('CxGbmA6sSwX1wUSPX88I96Xp')
            buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
            room('#ci-buildgate')
            startNotification(true)
            notifySuccess(true)
            notifyAborted(false)
            notifyNotBuilt(false)
            notifyUnstable(false)
            notifyFailure(true)
            notifyBackToNormal(false)
            notifyRepeatedFailure(false)
            commitInfoChoice('NOTHING')
            includeCustomMessage(false)
            customMessage()
            }
        }
        parameters {
            stringParam('ENVIRONMENT')
            stringParam('GIT_BRANCH_NAME', '',  'The branch that is to be built')
            stringParam('NEXUS_ID', 'HTFX', 'The prefix of the nexus compiled and uploaded artiifact')
            stringParam('SNAPSHOT_NUMBER')
        }
        logRotator {
            daysToKeep(15)
        }
        concurrentBuild(true)
        jdk('java-1.8.0_u102')
        label 'Build-Deploy-Node-Java8'
        publishers {
             postBuildScripts {
                   steps {
                         shell(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/postBuildTaskGetLogsFromFT.sh'))
                   }
             }
             aggregateBuildFlowTests()
        }
        wrappers {
           buildUserVars()
           timestamps()
        }
    }
}
