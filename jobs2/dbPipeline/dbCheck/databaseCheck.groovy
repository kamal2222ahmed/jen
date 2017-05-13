job('DB-Database-Check') {
  jdk('java-1.8.0_u112')
  label('Build-Deploy-Node-Java8')
  concurrentBuild()
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
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(10000)
  }
  throttleConcurrentBuilds {
    maxTotal(20)
    maxPerNode(2)
  }
  concurrentBuild()
  parameters {
    stringParam("TARGET_BRANCH")
    stringParam("GIT_BRANCH_NAME", null, "This is the name of the branch to build from")
    stringParam("NEXUS_ID", null, "This is the nexus snapshot precursor")
    booleanParam("SMOKETEST", false, "whether or not to execute smoke test after the deploy")
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
          branch('${TARGET_BRANCH}')
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
	    url('${JENKINSUTIL_REPO_SSH}')
            branch('master')
            credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
        }
        extensions {
            wipeOutWorkspace()
            relativeTargetDirectory('jenkinsutil')
            cloneOptions {
               timeout(25)
            }
        }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/dbPipeline/databaseCheck/changeInDatabase.sh'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
      conditionalSteps {
          condition {
	      not {
		  booleanCondition('${DATABASECHECK}')
	      }
	  }
	  steps {
	      downstreamParameterized {
	   	trigger('DB-PreMerge') {
		  block {
		    buildStepFailure('FAILURE')
		    failure('FAILURE')
		    unstable('UNSTABLE')
		  }
		  parameters {
		    predefinedProp('GIT_BRANCH_NAME', '${GIT_BRANCH_NAME}')
		    predefinedProp('PROD_VERSION', '${PROD_VERSION}')
		    predefinedProp('NEXUS_ID', '${NEXUS_ID}')
		    predefinedProp('SMOKETEST', '${SMOKETEST}')
		  }
		}
	      }
	  }
      }
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-buildgate')
      startNotification(false)
      notifySuccess(false)
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
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
    timeout {
        absolute(380)
        failBuild()
        writeDescription('Build failed due to timeout after 380 minutes')
    }
  }
}
