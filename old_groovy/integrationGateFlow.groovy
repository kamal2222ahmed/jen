
def jobName = 'ELIS2-IntegrationGateBuildFlow'

buildFlowJob("${jobName}") {
    description(readFileFromWorkspace('jobs/build/resources/integrationGateFlow/integrationGateFlowDescription.txt'))
    buildFlow(readFileFromWorkspace('jobs/build/resources/integrationGateFlow/groovyDSLIntegrationGateFlow.txt'))
    configure { project ->
        def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
            teamDomain('uscis')
            authToken('CxGbmA6sSwX1wUSPX88I96Xp')
            buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
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
    publishers {
        downstreamParameterized {
            trigger('ELIS2-Fortify-Linux-2') {
                condition('SUCCESS')
                parameters {
                    predefinedProp('BRANCH_TAG', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
                    predefinedProp('UPLOAD_FPR', 'false')
                }
            }
        }

    }
    parameters {
        stringParam('MAJOR', '8')
        stringParam('MINOR', '1')
        stringParam('SPRINT', '12')
    }
    logRotator (45, 500, -1, 30)
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
    }
}
