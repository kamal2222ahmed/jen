job('Serenity-Pre-Flow-Chrome') {
  description('<strong>This job pulls your branch then kicks off the <b><font color="blue">Serenity-StressTest-Flow-Chrome</font></b></strong>. This job runs every hour')
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
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
    stringParam("ENVIRONMENT", "FT2000")
    stringParam("GIT_BRANCH_NAME", "master")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${JENKINSUTIL_REPO_SSH}')
        branch('*/master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
      extensions {
        relativeTargetDirectory('jenkinsutil')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
    groovyCommand(readFileFromWorkspace('resources/serenityPipeline/stressPreFlow.groovy'))
      environmentVariables {
          propertiesFile('${WORKSPACE}/deploy.properties')
      }
  }
  triggers {
     cron('0 0 1 */3 *')
  }
  publishers {
      downstreamParameterized {
        trigger('Serenity-StressTest-Flow-Chrome') {
      parameters {
        propertiesFile('deploy.properties')
          }
       }  
    } 
 }
 configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      buildServerUrl('http://10.103.130.42:8080/jenkins')
      room('#ci-jenkins2_0dsl')
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
