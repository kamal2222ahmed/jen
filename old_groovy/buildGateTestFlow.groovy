
def elis_pipelines = ["ELIS2-BuildGate-Tests"]
elis_pipelines.each {pipeline ->
        buildFlowJob("${pipeline}") {
        buildNeedsWorkspace(true)
        description(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/buildGateTestFlowDescription.txt'))
        buildFlow(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/groovyDSLBuildGateTestFlow.txt'))
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
            stringParam('NEXUS_ID', 'BLD', 'The prefix of the nexus compiled and uploaded artiifact')
            stringParam('ENVIRONMENT')
            stringParam('SNAPSHOT_NUMBER')
            stringParam('MYSQL_PASSWORD')
            stringParam('MYSQL_USER', 'jenkins')
            stringParam('GIT_BRANCH_NAME', '',  'The branch that is to be built')
            stringParam('GIT_BRANCH_HASH', '', 'The hash of the branch that is to be built')
        }
        logRotator (45, 500, -1, 30)
        concurrentBuild(true)
        jdk('java-1.8.0_u102')
        label 'Build-Deploy-Node-Java8'
        publishers {
             aggregateBuildFlowTests()
        }
        wrappers {
           buildUserVars()
           timestamps()
        }
    }
}
