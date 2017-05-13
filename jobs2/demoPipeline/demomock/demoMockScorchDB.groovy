job('ELIS2-DEMO-Mock-ScorchUpdateLoad') {
  description(readFileFromWorkspace('resources2/demoPipeline/demomock/demoMockDBscorchDescription.txt'))
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
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
  blockOn (['ELIS2-DEMO-Mock-Deploy', 'System-.*']) {
      blockLevel('GLOBAL')
      }
  logRotator {
    daysToKeep(30)
    numToKeep(10000)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    stringParam("Version", "5.1.58.1001")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${DEMOMOCK_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
       }
     }
     git {
        remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('refs/tags/${Version}')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
       }
     }
     git {
        remote {
        name('origin')
        url('${TOGGLES_REPO}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('toggles')
        }
      }
    }
  steps {
      shell(readFileFromWorkspace('resources2/demoPipeline/demomock/dbDEMOMOCKGradlePropeties_stopTomcat.sh'))
      gradle {
          description('Database Clean + Scorch + Update')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources2/demoPipeline/demomock/demoMockGradleDBscorchUpdate.txt'))
          tasks(':Database:clean :Database:scorch :Database:update')
          rootBuildScriptDir('Apps')
          useWrapper(false)
          useWorkspaceAsHome(true)
       }
      gradle {
          description('Database Data Loader')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources2/demoPipeline/demomock/demoMockDataLoader.txt'))
          tasks(':Shared:DataLoader:clean :Shared:DataLoader:build :Shared:DataLoader:loadFTData')
          rootBuildScriptDir('Apps')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/demoPipeline/demomock/demoMockdbConnect_tomcatStart.sh'))
    }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#elis-demodeploy')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      includeTestSummary(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage()
    }
  }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
      string('masterpass', '5f30c7b1-6b22-4cff-9e0b-1351471609c0')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var=Version}')
    timeout {
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
