buildFlowJob('Serenity-StressTest-Flow-Chrome') {
  description('<h1 style="text-align:center;"><strong> <font size="5" color="red"> This job triggers the <b><font color="blue">ELIS2-FT-DeployByVersion</font></b> for your branch then kicks off the <b><font color="blue">Serenity-Clone-Chrome</font></b></strong> Test Suite.</font><strong></h1>')
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/serenityPipeline/stressTestFlow.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  logRotator {
    artifactDaysToKeep(3)
    daysToKeep(45)
    numToKeep(500)
  }
  parameters {
    stringParam("ENVIRONMENT", "FT108")
    stringParam("GIT_BRANCH_NAME", "AETAS-LockboxSmokeConversion")
    stringParam("NEXUS_ID", "BLD", "BLD (BuildGate)- works STG (Staging) - doesn't work 6.*.* (Integration) - doesn't work, not a snapshot. MSTRBLDTST (Master Build Test) - Unknown (no builds passed in over a day)")
    stringParam("SNAPSHOT_NUMBER", "18324", "You can use <a href='https://elis-jenkins.uscis.dhs.gov/view/All/job/ELIS2-BuildGate/lastStableBuild/api/xml?pretty=true&xpath=/*/id'>this id</a> for BLD.")
    stringParam("RELEASE")
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#charese-test-private')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(false)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage("${GIT_BRANCH_NAME}")
    }
  }
  publishers {
    postBuildScripts {
      steps {
        shell(readFileFromWorkspace("resources2/serenityPipeline/postBuildTaskGetLogsFromFT.sh"))
      }
      onlyIfBuildSucceeds(false)
    }
    aggregateBuildFlowTests()
    archiveArtifacts {
          pattern('logs/**')
          pattern('conf/**')
    }
  }
}
