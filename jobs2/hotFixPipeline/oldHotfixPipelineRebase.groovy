job('OLDREBASEJOB') {
  description(readFileFromWorkspace('resources2/hotFixPipeline/hotfixRebase/hotfixRebaseDescription.txt'))
  jdk('java-1.8.0_u112')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
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
  parameters {
    stringParam("GIT_TAG", "7.1.144.2109")
  }
  scm {
    git {
      remote {
        url('${APPS_REPO_SSH}')
        branch('CI-Production_Hotfixes')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
    }
  }
  steps {
    shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixRebase/shellForRebase.sh'))
  }
  publishers {
    git {
      pushOnlyIfSuccess(true)
      forcePush(true)
      branch("origin", "CI-Production_Hotfixes")
      branch("origin", "Release_Hotfixes")
    }
  }
  wrappers {
    timestamps()
    buildName('#${ENV,var="GIT_TAG"}')
  }
}
