job('makeTheIntFixBranch') {
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
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
    numToKeep(100)
  }
  parameters {
    stringParam("RELEASE_CANDIDATE", "8.1.13.54", "This is the Release candidate number")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${RELEASE_CANDIDATE}')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/makeTheIntFixBranch/shellforMakeTheIntFixBranch.sh'))
  }
  wrappers {
    buildUserVars()
    timestamps()
  }
}
