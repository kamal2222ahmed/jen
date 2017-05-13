job('ELIS2-GitRepo-Branch-Report') {
  description('Creates an audit for Apps and Chef Repo branches')
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'cnwillia')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(30)
  }
  scm {
    git {
    remote {
        name('origin')
        url('https://git.uscis.dhs.gov/USCIS/${REPONAME}')
        branch('*/master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
     }
   }
  parameters {
  choiceParam ('REPONAME', ['elis2-apps.git', 'chefrepo.git'])
  steps {
      shell(readFileFromWorkspace('resources/ciUtilites/skynetHelperJobs/gitRepoBranchReport.sh'))
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
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
    wrappers {
    timestamps()
    buildName('${ENV,var="REPONAME"}-#${BUILD_NUMBER}')
    }
  }
}
