job('ELIS2-Performance-Testing') {
  description('<h1 style="text-align:center;"><strong><font size="5" color="red">Team Masterbuild Performance Test Jenkins Gate Job </font><strong></h1>')
  jdk('java-1.8.0_u102')
  label('el2-ci-jmeter')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
    permissionAll('anonymous')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(100)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    stringParam('ENVIRONMENT')
    stringParam('GIT_BRANCH_NAME')
    stringParam('TEAM')
    stringParam('NEXUS_ID')
    stringParam('SNAPSHOT_NUMBER')
  }
  scm {
    git {
      remote {
        name('origin')
        url('git@git.uscis.dhs.gov:USCIS/ITE-Performance.git')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/acceptanceTesting/performanceTesting/lockboxPerformanceTestPostBuild.sh'))
      shell(readFileFromWorkspace('resources2/acceptanceTesting/performanceTesting/lockboxPerformanceTestPostBuild2.sh'))
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#jmeter-results')
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
      customMessage('You can find the results at https://spider.uscis.dhs.gov/en-US/app/CIS_ELIS2/oit_performance_testing__trend_analysis_and_visualization_clone')
    }
  }
  wrappers {
    buildUserVars()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${ENV,var="ENVIRONMENT"}-${BUILD_NUMBER}')
    timestamps()
  }
}
