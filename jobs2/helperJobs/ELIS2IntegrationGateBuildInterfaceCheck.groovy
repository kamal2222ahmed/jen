job('ELIS2-IntegrationGateBuild-InterfaceCheck') {
  jdk('java-1.8.0_u102')
  label('npm-java8-builder')
  concurrentBuild()
  description('Alert the SMART DBIZ Team whenever code changes are pushed to the Database subtree')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
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
    permissionAll('jbkuper')
    permissionAll('drosmari')
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
    numToKeep(30)
  }
  parameters {
    stringParam("VERSION", null, "This is the Release candidate number")
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('jenkinsutil')
        cloneOptions {
          timeout(25)
        }
      }
    }
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${VERSION}')
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
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/IntegrationGateInterfaceCheck/npmcache'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      gradle {
          description('clean')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/helperjobs/IntegrationGateInterfaceCheck/1clean.txt'))
          tasks('clean')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('build artifacts')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/helperjobs/IntegrationGateInterfaceCheck/2buildArtifacts'))
          tasks('build runGulpFullNoInstall :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      gradle {
          description('do the interfaces check')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Apps/Backend/Elis2Services')
          switches(readFileFromWorkspace('resources2/helperjobs/IntegrationGateInterfaceCheck/3interfaceCheck'))
          tasks('scanInterfaceChanges')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/helperjobs/IntegrationGateInterfaceCheck/4InterfaceDecision'))
  }
  publishers {
    archiveArtifacts {
        pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*, Apps/Backend/Elis2Services/build/**/*')
    }
    extendedEmail {
      attachmentPatterns('Apps/*-output.txt, Apps/*-input.txt')
      triggers {
        failure {
          content(readFileFromWorkspace('resources2/helperjobs/DatabaseChangeLogDiff/failurebody'))
          recipientList('charese.n.williams@uscis.dhs.gov', 'daniel.m.belyea@uscis.dhs.gov', 'juniad.m.khanani@uscis.dhs.gov')
          sendTo {
            developers()
            culprits()
            recipientList()
          subject('$PROJECT_DEFAULT_SUBJECT')
          }
        }
        success {
          content(readFileFromWorkspace('resources2/helperjobs/DatabaseChangeLogDiff/successbody'))
          recipientList(readFileFromWorkspace('resources2/helperjobs/DatabaseChangeLogDiff/successRecipientList'))
          sendTo {
            developers()
            recipientList()
          subject('ELIS2 Database subtree has changed in Master Version: $CURRENT_MASTER_TAG')
           }
        }
      }
    }
    downstreamParameterized {
        trigger('updateTagForInterfaceCheck') {
          condition('SUCCESS')
          parameters {
            predefinedProp('VERSION', '${Version}')
          }
        }
     }
 }
 configure { project ->
   def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
     teamDomain('uscis')
     authToken('CxGbmA6sSwX1wUSPX88I96Xp')
     room('#elis-pipeline')
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
     includeCustomMessage(true)
     customMessage('IntegrationGateVersion: ${VERSION}')
   }
 }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="VERSION"}-${BUILD_NUMBER}')
  }
}
