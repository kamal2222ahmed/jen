
def jobName = 'ELIS2-BuildGate-GitPoll'
def cronValue = 'H/6 * * * *'
job("${jobName}") {
    description(readFileFromWorkspace('jobs/build/resources/buildGateGitPoll/buildGateGitPollDescription.txt'))
    disabled()
    authenticationToken('test123')
    authorization {
        permission('hudson.model.Item.Discover', 'anonymous')
        permission('hudson.model.Item.Read', 'anonymous')
    }
    triggers {
        cron("H/6 * * * *")
    }
    weight(1)
    logRotator (12, 300, -1, 30)
    jdk('java-1.8.0_u102')
    label 'master'
    multiscm {
        git {
            remote {
                name('origin')
                url('${JENKINSUTIL_REPO_SSH}')
                branch('master')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                relativeTargetDirectory('jenkinsutil')
            }
        }
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                branch('master')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                relativeTargetDirectory('Apps')
                wipeOutWorkspace()
            }
        }
    }
    steps {
        shell(readFileFromWorkspace('jobs/build/resources/buildGateGitPoll/scriptForGitPoll.sh'))
    }
    wrappers {
       buildUserVars()
       timestamps()
    }
}
