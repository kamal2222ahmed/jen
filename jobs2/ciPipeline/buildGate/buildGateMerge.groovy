
def jobName = 'ELIS2-BuildGate-Merge'
def cronValue = 'H/6 * * * *'
job("${jobName}") {
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
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
    permissionAll('jgarciar')
    blocksInheritance()
  }
    configure { project ->
        def clearOutWorkspace = project / 'buildWrappers' / 'hudson.plugins.ws__cleanup.PreBuildCleanup' {
            deleteDirs(false)
	    cleanupParameter()
	    externalDelete()
        }
    }
    configure { project ->
        def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
            teamDomain('uscis')
            authToken('CxGbmA6sSwX1wUSPX88I96Xp')
            room('#ci-buildgate')
            startNotification(false)
            notifySuccess(true)
            notifyAborted(false)
            notifyNotBuilt(false)
            notifyUnstable(false)
            notifyFailure(true)
            notifyBackToNormal(false)
            notifyRepeatedFailure(false)
            includeTestSummary(false)
            commitInfoChoice('NONE')
            includeCustomMessage(false)
            customMessage()
        }
    }
    description(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateMerge/BuildGateMergeDescription.txt'))
    logRotator (45, 500, -1, 30)
    parameters {
        stringParam('GIT_BRANCH_NAME', '', 'This is the name of the branch to build from')
        stringParam('SNAPSHOT_NUMBER')
        stringParam('GIT_BRANCH_HASH')
    }
    publishers {
        git {
            branch('origin', 'Staging')
            pushMerge(true)
            pushOnlyIfSuccess(true)

        }
    }
    jdk('java-1.8.0_u102')
    label 'Build-Deploy-Node-Java8'
    scm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                branch('${GIT_BRANCH_HASH}')
                credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
            }
            extensions {
                mergeOptions{
                    branch('Staging')
                    getFastForwardMode()
                    remote('origin')
                    strategy('default')
                }
                wipeOutWorkspace()
                relativeTargetDirectory('Apps')
            }
        }
    }
    steps {
        shell(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateMerge/shellForBuildGateMerge.sh'))
    }
    wrappers {
       buildName('#${ENV,var="GIT_BRANCH_NAME"}-${SNAPSHOT_NUMBER}')
       buildUserVars()
       timestamps()
    }
}
