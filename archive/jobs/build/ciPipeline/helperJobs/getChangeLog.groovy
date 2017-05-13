job('ELIS2-GetChangeLog') {
  description(readFileFromWorkspace('resources/helperJobs/getChangeLogs-Description.txt'))
  disabled()
  jdk('java-1.8.0_u102')
  label('master')
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
        url('${APPS_REPO_SSH}')
        branch('*/master')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
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
      shell(readFileFromWorkspace('resources/helperJobs/getChangeLogs-shell.sh'))
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
