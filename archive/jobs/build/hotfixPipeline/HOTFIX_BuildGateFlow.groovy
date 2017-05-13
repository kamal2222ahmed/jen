buildFlowJob('ELIS2-HOTFIX-BuildGateFlow') {
  description(readFileFromWorkspace('resources/hotfixBuildGateFlow/hotfixBuildFlowDescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources/hotfixBuildGateFlow/groovyDSLForHotfixBuildFlow.txt'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  throttleConcurrentBuilds {
    maxTotal(3)
    maxPerNode(2)
  }
  concurrentBuild(true)
  logRotator {
    artifactDaysToKeep(3)
    daysToKeep(75)
    numToKeep(500)
  }
  parameters {
    stringParam("GIT_BRANCH_NAME", "CI-Production_Hotfixes")
    stringParam("NEXUS_ID", "HTFX")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_NAME}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        mergeOptions {
         branch('Release_Hotfixes')
         getFastForwardMode()
         remote('origin')
         strategy('default')
        }
         wipeOutWorkspace()
          relativeTargetDirectory('Apps')
        }
      }
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
      notifyBackToNormal(false)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage("${GIT_BRANCH_NAME}")
      }
    }
 }
