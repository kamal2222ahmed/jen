job('FT-PROD-DB-EXPORT-CREATE') {
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
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
    artifactDaysToKeep(50)
    daysToKeep(20)
    numToKeep(500)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  parameters {
    stringParam("EXTRACT_VERSION")
    stringParam("ENVIRONMENT")
    stringParam("BRANCH")
    stringParam("TOGGLE")
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
        url('${FT_REPO_SSH}')
        branch('master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('ft')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/ftProdDbExport/ftProdDBExport-shell1.txt'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      shell(readFileFromWorkspace('resources2/helperjobs/ftProdDbExport/ftProdDBExport-shell2.txt'))
      gradle {
          description('Deploy Test Database Version')
          gradleName('gradle-2.7')
          passAsProperties(true)
          rootBuildScriptDir('Database-${EXTRACT_VERSION}')
          switches(readFileFromWorkspace('resources2/helperjobs/ftProdDbExport/ftProdDBExport-DeployDBSwitches.txt'))
          tasks('update')
          useWrapper(false)
          useWorkspaceAsHome(true)
      }
      shell(readFileFromWorkspace('resources2/helperjobs/ftProdDbExport/ftProdDBExport-executeExport.txt'))
  }
  wrappers {
    credentialsBinding {
      usernamePassword('AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', '29f1bb47-b63d-4985-b8a0-16d98d1a149c')
    }
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="ENVIRONMENT"}-${ENV,var="EXTRACT_VERSION"}')
    timeout {
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
