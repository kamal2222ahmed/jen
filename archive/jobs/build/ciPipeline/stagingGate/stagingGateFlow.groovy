buildFlowJob('ELIS2-StagingGateFlow') {
  description(readFileFromWorkspace('resources/stagingGateFlow/stagingGateBuildFlowDescription.txt'))
  triggers {
    scm('*/5 * * * *')
  }
  blockOn('ELIS2-StagingGateFlow') {
    blockLevel('GLOBAL')
    scanQueueFor('DISABLED')
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
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/stagingGateFlow/groovyDSLStagingGateFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(20)
    daysToKeep(14)
    numToKeep(500)
  }
  parameters {
    stringParam("NEXUS_ID", "STG")
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('http://10.103.130.42:8080/jenkins')
      room('#ci-jenkins2_0dsl')
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
    git {
      branch("origin", "Integration")
      branch("origin", "master")
      pushMerge(true)
      pushOnlyIfSuccess(true)
    }
    downstream("ELIS2-IntegrationGateBuildFlow", 'SUCCESS')
  }
}
