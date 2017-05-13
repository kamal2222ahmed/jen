job('Serenity-Pre-Flow-Chrome') {
  description('<h1 style="text-align:center;"><strong> <font size="5" color="red">This job pulls your branch then kicks off the <b><font color="blue">Serenity-StressTest-Flow-Chrome</font></b></strong>. This job runs every hour</font><strong></h1>')
  jdk('java-1.8.0_u102')
  label('npm-java8-builder')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  logRotator {
    artifactDaysToKeep(3)
    daysToKeep(45)
    numToKeep(500)
  }
  parameters {
    stringParam("ENVIRONMENT", "FT109")
    stringParam("GIT_BRANCH_NAME", "master")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
        }
      }
    }
  steps {
    systemGroovyCommand(readFileFromWorkspace('resources2/serenityPipeline/stressPreFlow.groovy')) {
      }
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
  }
  triggers {
     cron('0 0-23/1 * * *')
  }
  publishers {
      downstreamParameterized {
        trigger('Serenity-StressTest-Flow-Chrome') {
      parameters {
          predefinedProp('deploy.properties', '${WORKSPACE}/deploy.properties')
          }
       }
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
      notifyBackToNormal(false)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(false)
      customMessage("${GIT_BRANCH_NAME}")
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
  }
}
