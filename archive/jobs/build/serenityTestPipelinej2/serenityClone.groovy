job('Serenity-Clone-Chrome') {
  description('<h1 style="text-align:center;"><strong> <font size="5" color="red">Runs all Serenity tests on the SERENITY-STRESS branch. <br/> Description of icons: 1. Notepad - view logs  2. Folder - view serenity reports 3. Floppy - save serenity reports  4. Database - view video recording (best viewed if saved locally)</font><strong></h1>')
  jdk('java-1.8.0_u112')
  label('Linux-Test-Java8-serenity')
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
    stringParam("ENVIRONMENT", "FT108")
    stringParam("GIT_BRANCH_NAME", "master", "The name of the branch in Git to Build the Test off of")
    stringParam("NEXUS_ID", '', "This is the nexus snapshot precursor")
    stringParam("SNAPSHOT_NUMBER")
    stringParam("MAX_PARALLEL_FORKS", "1", "How many threads to run in parallel.")
    booleanParam('IS_SNAPSHOT')
    textParam("ADDITIONAL_GRADLE_PARAMS", "", "Additional parameters to pass to gradle when running tests.")
    stringParam("BENEFIT_TYPES_TO_RUN", "I90APISmokeTest")
  }
  multiscm {
      git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_NAME}')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
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
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('chefrepo')
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/serenityPipeline/ffmpegStep1.sh'))
      gradle {
          description('Clean Serenity Test Output')
          gradleName('gradle-2.2.1')
          rootBuildScriptDir('Apps/Tests/Serenity/')
          switches(readFileFromWorkspace('resources2/serenityPipeline/cleanStep2.txt'))
          tasks('cleanSerenityTestOutput test activeMQPrinter aggregate')
          useWrapper(false)
          useWorkspaceAsHome(true)
       }
    }
  publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources2/serenityPipeline/ffmpegStatus3.txt"))
          shell(readFileFromWorkspace("resources2/serenityPipeline/getLogs4.txt"))
         archiveArtifacts {
         pattern('logs/**, conf/**, Apps/Tests/Serenity/target/site/serenity/**')
      }
      systemGroovyCommand(readFileFromWorkspace('resources2/serenityPipeline/postbuild5.groovy'))
         }
      }
    archiveJunit('Apps/Tests/Serenity/build/test-results/TEST*.xml') { retainLongStdout() }
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
