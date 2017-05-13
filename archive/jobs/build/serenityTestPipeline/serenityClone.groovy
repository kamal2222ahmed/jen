job('Serenity-Clone-Chrome') {
  description('Runs all Serenity tests on the SERENITY-STRESS branch. <br/> Description of icons: Notepad - view logs Folder - view serenity reports floppy - save serenity reports Database - View video recording (best viewed if saved locally)')
  jdk('java-1.8.0_u102')
  label('Linux-Test-Chrome')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  logRotator {
    artifactDaysToKeep(15)
    daysToKeep(15)
    numToKeep(100)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    stringParam("ENVIRONMENT", "FT1000")
    stringParam("GIT_BRANCH_NAME", "master", "The name of the branch in Git to Build the Test off of")
    stringParam("NEXUS_ID", null, "This is the nexus snapshot precursor")
    stringParam("SNAPSHOT_NUMBER")
    stringParam("MAX_PARALLEL_FORKS", "1", "How many threads to run in parallel.")
    booleanParam("IS_SNAPSHOT")
    textParam("ADDITIONAL_GRADLE_PARAMS", "", "Additional parameters to pass to gradle when running tests.")
    stringParam("BENEFIT_TYPES_TO_RUN", "I90APISmokeTest", "")
  }
  multiscm {
      git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_HASH}')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        mergeOptions {
          branch('AETAS-I90APIReimplementation')
          getFastForwardMode()
          remote('origin')
          strategy('default')
        }
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
     git {
      remote {
        name('origin')
        url('${CHEF_REPO_SSH}')
        branch('master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('chefrepo')
      }
    }    
  } 
  steps {
      shell(readFileFromWorkspace('resources/serenityPipeline/ffmpegStep1.sh'))
      gradle {
          description('Clean Serenity Test Output')
          gradleName('gradle-2.7')
          rootBuildScriptDir('Apps/Tests/Serenity/')
          switches(readFileFromWorkspace('resources/serenityPipeline/cleanStep2.txt'))
          tasks('cleanSerenityTestOutput test activeMQPrinter aggregate')
          useWrapper(false)
          useWorkspaceAsHome(true)
       }
    }
  publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources/serenityPipeline/ffmpegStatus3.txt"))
          shell(readFileFromWorkspace("resources/serenityPipeline/getLogs4.txt"))
         archiveArtifacts {
         pattern('logs/**, conf/**, Apps/Tests/Serenity/target/site/serenity/**')
      }
      groovyCommand(readFileFromWorkspace('resources/serenityPipeline/postbuild5.groovy'))
         }
      }
    archiveJunit('Apps/Tests/Serenity/build/test-results/TEST*.xml') { retainLongStdout() } 
   }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
      room('#ci-jenkins2_0dsl')
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
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${ENV,var="ENVIRONMENT"}-${BUILD_NUMBER}')
    timeout {
        absolute(180)
        failBuild()
        writeDescription('Test Stuck')
    }
  }
}
