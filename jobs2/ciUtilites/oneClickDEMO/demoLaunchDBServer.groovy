job('Demo_Environment_Creation_IDB1') {
  description('Creates a Demo database server (oracle)')
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
	permissionAll('rptighe')
    permissionAll('tabaig')
    permissionAll('wezewudi')
    permissionAll('wmfowlke')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(30)
  }
  parameters {
    stringParam('ENVIRONMENT', 'DEMOMOCK3', 'Environment Name')
    stringParam('Version', '7.2.21.37', 'Version of Code' )
  }
  multiscm {
    git {
      remote {
        name('origin')
        url('${CLOUDOPS_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('cloudops')
       }
     }
    git {
    remote {
        name('origin')
        url('${CHEF_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('chefrepo')
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
      shell(readFileFromWorkspace('resources2/ciUtilites/oneClickDEMO/demoDBspinUP.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
         gradle {
          description('Clean, Scorch, and, Update Database')
          gradleName('gradle-2.7')
          useWrapper(false)
          rootBuildScriptDir('Apps/')
          switches(readFileFromWorkspace('resources2/ciUtilites/oneClickDEMO/demoDBgradle.txt'))
          tasks(':Database:clean :Database:scorch :Database:update')
      }
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
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('# ${ENV,var="ENVIRONMENT"}  - ${BUILD_NUMBER}')
  }
}
