job('ELIS2-MicroIntegrationGate') {
  description('')
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(25)
    daysToKeep(25)
    numToKeep(400)
  }
  throttleConcurrentBuilds() {
    maxTotal(15)
    maxPerNode(1)
  }
  concurrentBuild()
  parameters {
    stringParam("MAJOR", "6")
    stringParam('MINOR', "4")
    stringParam("SPRINT", "69")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${IMAGE_TRANSMISSION_GIT_REPO}')
        branch('Integration')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        mergeOptions {
          branch('master')
          getFastForwardMode()
          remote('origin')
          strategy('default')
           }
        }
     }
  }
  steps {
      gradle {
          description('compile the artifacts and run the low level tests')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources/microServices/cleanIntegration1.txt'))
          tasks('clean build')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
     shell(readFileFromWorkspace('resources/microServices/rpmBuild.sh'))
     gradle {
          description('create the build deployable artifact and upload it to nexus')
          gradleName('gradle-2.7')
          passAsProperties(true)
          switches(readFileFromWorkspace('resources/microServices/buildIntegration2.txt'))
          tasks('buildRpm uploadArchives -x compileJava -x jar')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
   }
   publishers {
     git {
         pushMerge(false)
         pushOnlyIfSuccess(true)
         forcePush(false)
         tag("origin", '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}'){
         create(true)
          update(true)           
           }       
        } 
     }    
  configure { project ->
      def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
          teamDomain('uscis')
          authToken('CxGbmA6sSwX1wUSPX88I96Xp')
          buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
          room('#ci-jenkins2_0dsl')
          startNotification(false)
          notifySuccess(true)
          notifyAborted(false)
          notifyNotBuilt(false)
          notifyUnstable(false)
          notifyFailure(true)
          notifyBackToNormal(false)
          notifyRepeatedFailure(false)
          includeTestSummary(false)
          commitInfoChoice('NONE')
          includeCustomMessage(true)
      }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('${ENV,var="MAJOR"}.${ENV,var="MINOR"}.${ENV,var="SPRINT"}.${BUILD_NUMBER}')
  }
}
