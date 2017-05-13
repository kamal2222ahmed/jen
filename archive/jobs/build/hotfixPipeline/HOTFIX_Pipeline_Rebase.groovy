job('ELIS2-HOTFIX-Pipeline_Rebase') {
  description(readFileFromWorkspace('resources/hotfixRebase/hotfixRebaseDescription.txt'))
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  parameters {
    stringParam("GIT_TAG", "7.1.144.2109")
  }
  scm {
    git {
      remote {
        url('${APPS_REPO_SSH}')
        branch('CI-Production_Hotfixes')
        credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
      }
    }
  }
  steps {
    shell(readFileFromWorkspace('resources/hotfixRebase/shellForRebase.sh'))
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
