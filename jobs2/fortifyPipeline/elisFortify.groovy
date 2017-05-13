job('ELIS-Fortify') {
  description('Runs the Fortify Scan on the tag of the bTar when a new code base is pushed to it')
  jdk('java-1.8.0_u102')
  concurrentBuild()
  label('fortify-hvm')
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
  configure { project ->
      def clearOutWorkspace = project / 'buildWrappers' / 'hudson.plugins.ws__cleanup.PreBuildCleanup' {
          deleteDirs(false)
          cleanupParameter()
          externalDelete()
      }
  }
  logRotator {
    daysToKeep(30)
  }
  throttleConcurrentBuilds {
    maxTotal(16)
    maxPerNode(2)
  }
  parameters {
    stringParam('GRADLE_PROJECT', '', '<p>This job allows for the scanning of the entire Apps repo or an individual project.</p> <p>To run a scan on the entire Apps repo, leave the field empty.</p> <p>For example, to run a scan on the Elis2Services enter the full project path: <pre>:Backend:Elis2Services</pre></p>')
    stringParam('VERSION', 'Dev')
    booleanParam('UPLOAD_FPR')
  }
 multiscm {
    git {
      remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('jenkinsutil')
       }
     }
   git {
    remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
       }
     }
   }
  steps {
      shell(readFileFromWorkspace('resources2/fortifyScan/getLatestRC.sh'))
        environmentVariables {
          propertiesFile('${WORKSPACE}/rc.properties')
   }
  conditionalSteps {
     condition {
       not {
           booleanCondition('${UPLOAD_FPR}')
      }
      steps {
      gradle {
        description('Fortify Clean')
        gradleName('gradle-2.7')
        passAsProperties(true)
        rootBuildScriptDir('Apps/')
        switches(readFileFromWorkspace('resources2/fortifyScan/fortifyClean.txt'))
        tasks('clean ${GRADLE_PROJECT}:fortifyScanProject')
        useWrapper(false)
        useWorkspaceAsHome(true)
              }
           }
        }
     }
   conditionalSteps {
     condition {
           booleanCondition('${UPLOAD_FPR}')
      }
      steps {
      gradle {
          description('Fortify Upload')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/fortifyScan/fortifyUpload.txt'))
          tasks('clean ${GRADLE_PROJECT}:fortifyScanProject ${GRADLE_PROJECT}:fortifyUpload')
          useWrapper(false)
          useWorkspaceAsHome(true)
           }
        }
     }
  }
  publishers {
      archiveArtifacts {
          pattern('**/*ScanReport*.pdf,**/*.fpr,**/*DeveloperWorkbook*.pdf')
     }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-fortifyscan')
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
      string('fortifyUploadToken', 'd3b2687f-b0f2-4033-acf2-0057fcd9d7da')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="BRANCH_TAG"} - ${ENV,var="PROJECT"}')
  }
}
