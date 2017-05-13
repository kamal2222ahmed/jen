job('ELIS2-Lockbox-Performance-Testing') {
  description('<h1 style="text-align:center;"><strong><font size="5" color="red">Test job for proof of concept on PT to the left (Lockbox)</font><strong></h1>')
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
  triggers {
     cron('H 9-16/6 * * *')
  }
  scm {
    git {
      remote {
        name('origin')
        url('${PERF_TEST_REPO_SSH}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/acceptanceTesting/lockboxPerformanceTest.sh'))
  }
  publishers {
      postBuildScripts {
        steps {
          shell(readFileFromWorkspace("resources2/acceptanceTesting/lockboxPerformanceTestPostBuild.sh"))
          shell(readFileFromWorkspace("resources2/acceptanceTesting/lockboxPerformanceTestPostBuild2.sh"))
          onlyIfBuildFails(false)
          onlyIfBuildSucceeds(false)
        }
     }
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
      customMessage('You can find the results at http://10.103.130.223:8080/ELIS-dashboard/latest and https://spider.uscis.dhs.gov/en-US/app/CIS_ELIS2/oit_performance_testing__trend_analysis_and_visualization_clone')
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
  }
}
