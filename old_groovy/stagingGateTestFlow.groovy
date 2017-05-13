
buildFlowJob("ELIS2-StagingGate-Tests") {
     buildNeedsWorkspace(true)
     description(readFileFromWorkspace('jobs/build/resources/stagingGateTestFlow/stagingGateTestFlowDescription.txt'))
     buildFlow(readFileFromWorkspace('jobs/build/resources/stagingGateTestFlow/groovyDSLStagingGateTestFlow.txt'))
     configure { project ->
            def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
                    teamDomain('uscis')
                        authToken('CxGbmA6sSwX1wUSPX88I96Xp')
                        buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
             	          room('#ci-pipeline')
             	          startNotification(true)
                        notifySuccess(true)
                        notifyAborted(true)
                        notifyNotBuilt(false)
                        notifyUnstable(false)
                        notifyFailure(true)
                        notifyBackToNormal(true)
                        notifyRepeatedFailure(false)
             	          commitInfoChoice('AUTHORS_AND_COMMITS')
                        includeCustomMessage(false)
                        customMessage()
	              }
         }
    parameters {
          stringParam('NEXUS_ID', 'STG', 'The prefix of the nexus compiled and uploaded artiifact')
          stringParam('ENVIRONMENT')
          stringParam('SNAPSHOT_NUMBER')
          credentialsParam('MYSQL_CRED') {
                  type('org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl')
                  required()
                  defaultValue('a21fcdcb-9221-4078-a70e-f0fa1c62a28b')
                  description('credential for the mysql database to retrieve a ft')
          }
          stringParam('GIT_BRANCH_NAME', '', 'The branch that is to be built')
          stringParam('GIT_BRANCH_HASH', '', 'The hash of the branch that is to be built')
   }
   logRotator {
      daysToKeep(30)
   }
   publishers {
        postBuildScripts {
              steps {
                    shell(readFileFromWorkspace('jobs/build/resources/buildGateTestFlow/postBuildTaskGetLogsFromFT.sh'))
              }
        }
        aggregateBuildFlowTests()
   }
   jdk('java-1.8.0_u102')
   label 'Build-Deploy-Node-Java8'
   wrappers {
        buildUserVars()
        timestamps()
   }
}
