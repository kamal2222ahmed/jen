
def elis_pipelines = ["test-flow-dsl"]
elis_pipelines.each { pipeline ->
          buildFlowJob("${pipeline}") {
                buildNeedsWorkspace(true)
                description(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/buildGateTestFlowDescription.txt'))
                buildFlow(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/groovyDSLBuildGateTestFlow.txt'))
                configure {
                it / publishers << 'jenkins.plugins.slack.SlackNotifier' {
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
       }
}
