buildFlowJob('ELIS2-HOTFIX-BuildGateFlow') {
  description(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateFlow/hotfixBuildFlowDescription.txt'))
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/hotFixPipeline/hotfixBuildGateFlow/groovyDSLForHotfixBuildFlow.txt'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
      room('#ci-hotfixes')
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
      git {
        branch("origin", "Release_Hotfixes")
        pushMerge(true)
        pushOnlyIfSuccess(true)
      }
      downstream("ELIS2-HF-IntegrationGateFlow", 'SUCCESS')
    }
 }
