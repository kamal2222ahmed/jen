job('ELIS2-GetChangeLog') {
  description(readFileFromWorkspace('resources2/helperjobs/gitChangeLog/getChangeLogs-Description.txt'))
  disabled()
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
  }
  logRotator {
    numToKeep(100)
  }
  parameters {
    stringParam("FirstTag", "5.1.13.83")
    stringParam("LastTag", "HEAD")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${ELIS_APPS_REPO}')
        branch('*/master')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
        configure { node ->
          def pollingRestriction = node / 'extensions' / 'hudson.plugins.git.extensions.impl.PathRestriction' {
            includedRegions('environments/FT_TEMPLATE.json')
            excludedRegions('cookbooks/*')
          }
        }
        configure { node ->
          def cleanBeforeCheckout = node / 'extensions' / 'hudson.plugins.git.extensions.impl.cleanBeforeCheckout' {

          }
        }
      }
    }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/gitChangeLog/getChangeLogs-shell.sh'))
  }
  publishers {
    archiveArtifacts {
        pattern('changelog.txt')
    }
  }
  wrappers {
    buildUserVars()
    timestamps()
  }
}
