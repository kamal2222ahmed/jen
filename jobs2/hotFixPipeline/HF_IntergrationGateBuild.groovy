job('ELIS2-HF-IntegrationGateBuild') {
  description(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuild/hotfixIntegrationGateBuildDescription.txt'))
  jdk('java-1.8.0_u112')
  label('npm-java8-builder')
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
  logRotator {
    daysToKeep(3)
    numToKeep(5)
  }
  parameters {
    stringParam("MAJOR")
    textParam("MINOR")
    stringParam("SPRINT")
    stringParam("GIT_BRANCH_NAME", "Release_Hotfixes")
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
        relativeTargetDirectory('Apps')
      }
    }
  }
  steps {
    shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuild/preDatabaseDeploy.sh'))
    environmentVariables {
      propertiesFile('${WORKSPACE}/deploy.properties')
    }
    gradle {
      description('clean')
      gradleName('gradle-2.7')
      passAsProperties(true)
      rootBuildScriptDir('Apps/')
      switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuild/1clean.txt'))
      tasks('clean')
      useWrapper(false)
      useWorkspaceAsHome(true)
    }
    shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuild/npm-cache.sh'))
    gradle {
      description('build artifacts')
      gradleName('gradle-2.7')
      passAsProperties(true)
      rootBuildScriptDir('Apps/')
      switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuild/2buildArtifacts.txt'))
      tasks('build  runGulpFullNoInstall  :Database:build -x :Database:baseline -x :Database:update -x :Database:scorch :Workspace:javakeystore:createJKS -x karma -x :Database:baselineResources -x test -x intTest -x runGulpFull')
      useWrapper(false)
      useWorkspaceAsHome(true)
    }
    gradle {
      description('upload the artifacts')
      gradleName('gradle-2.7')
      passAsProperties(true)
      rootBuildScriptDir('Apps/')
      switches(readFileFromWorkspace('resources2/hotFixPipeline/hotfixIntegrationGateBuild/3uploadArtifacts.txt'))
      tasks(':InternalApp:InternalApp:uploadArchives :Database:uploadArchives uploadAllS3 -x karma -x :Database:baselineResources -x test -x :Database:baseline -x intTest -x installGulp -x runGulpFull  -x compileJava -x jar -x war')
      useWrapper(false)
      useWorkspaceAsHome(true)
    }
  }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
    }
  }
  publishers {
    archiveArtifacts {
      pattern('Apps/InternalApp/InternalApp/build/karma/**/*,Apps/gradlebuild/reports/profile/**/*, Apps/Backend/Elis2Services/build/**/*')
    }
    downstreamParameterized {
      trigger('AMI_Creation_Job_Parallel') {
        condition('SUCCESS')
        parameters {
          currentBuild()
          gitRevision(true)
          predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}-${BUILD_NUMBER}')
        }
     }
     trigger('FT-PROD-DB-EXPORT-CREATE') {
       condition('SUCCESS')
       parameters {
         predefinedProp('EXTRACT_VERSION', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
         predefinedProp('ENVIRONMENT', 'FT999')
         predefinedProp('BRANCH', 'Release_Hotfixes')
         predefinedProp('TOGGLE', 'ALL_ON')
       }
       trigger('ELIS2-IntegrationGateDB-Disruption') {
         condition('SUCCESS')
         parameters {
           predefinedProp('ELIS_VERSION', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
           predefinedProp('BRANCH', 'Release_Hotfixes')
         }
       }
       trigger('makeTheIntFixBranch') {
         condition('SUCCESS')
         parameters {
           predefinedProp('RELEASE_CANDIDATE', '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
           predefinedProp('ENVIRONMENT', 'FT999')
           predefinedProp('BRANCH', 'Release_Hotfixes')
           predefinedProp('TOGGLE', 'ALL_ON')
         }
     }
     git {
       pushOnlyIfSuccess()
       tag("origin", '${MAJOR}.${MINOR}.${SPRINT}.${BUILD_NUMBER}')
       {
         create(true)
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
      includeCustomMessage(false)
      customMessage()
      }
    }
  }
}
}
