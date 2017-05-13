buildFlowJob('ELIS2-StagingGateFlow') {
  description(readFileFromWorkspace('resources2/ciPipeline/stagingGate/stagingGateFlow/stagingGateBuildFlowDescription.txt'))
  triggers {
    scm('*/5 * * * *')
  }
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
    permission('com.cloudbees.plugins.credentials.CredentialsProvider.View', 'anonymous')
    permission('hudson.model.Item.Workspace', 'anonymous')
    permissionAll('akkausha')
    permissionAll('ambutt')
    permissionAll('caaponte')
    permissionAll('cnwillia')
    permissionAll('fconwumb')
    permissionAll('jbfrimpo')
    permissionAll('jnugorji')
    permissionAll('jsjohnso')
    permissionAll('kchen1')
    permissionAll('nppatel')
    permissionAll('rthandavan')
    permissionAll('rvangar')
    permissionAll('skahmed')
    permissionAll('skkrishn')
    permissionAll('srkhan')
    permissionAll('srkombam')
    permissionAll('tabaig')
    permissionAll('wezewudi')
    permissionAll('wmfowlke')
    permissionAll('jgarciar')
    blocksInheritance()
    setBlocksInheritance(true)
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/ciPipeline/stagingGate/stagingGateFlow/groovyDSLStagingGateFlow.groovy'))
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
      room('#ci-pipeline')
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
