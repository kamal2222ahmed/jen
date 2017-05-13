
def jobName = 'ELIS2-BuildGate-PreMerge'
job("${jobName}") {
    description(readFileFromWorkspace('jobs/build/resources/buildGatePreMerge/buildGatePreMergeDescription.txt'))
    disabled()
    authorization {
    	permission('hudson.model.Item.Build', 'jgarciar')
	permission('hudson.model.Item.Build', 'kwgyovai')
	permission('hudson.model.Item.Build', 'anonymous')
	permission('hudson.model.Item.Read', 'anonymous')
    }
    logRotator (25, 400, -1, 30)
    throttleConcurrentBuilds {
        maxPerNode(0)
        maxTotal(15)
    }
    concurrentBuild(true)
    parameters {
        stringParam('NEXUS_ID', 'BLD', 'This is the nexus snapshot precursor')
        stringParam('GIT_BRANCH_NAME')
    }
    configure { project ->
        def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
            teamDomain('uscis')
            authToken('CxGbmA6sSwX1wUSPX88I96Xp')
            buildServerUrl('https://elis-jenkins.uscis.dhs.gov/')
            room('#ci-buildgate')
            startNotification(true)
            notifySuccess(true)
            notifyAborted(false)
            notifyNotBuilt(false)
            notifyUnstable(false)
            notifyFailure(true)
            notifyBackToNormal(true)
            notifyRepeatedFailure(false)
            commitInfoChoice('NOTHING')
            includeCustomMessage(true)
            customMessage('${GIT_BRANCH_NAME}')
        }
    }
    publishers {
        git {
            branch('origin', '${GIT_BRANCH_NAME}')
            pushMerge(true)
            pushOnlyIfSuccess(true)

        }
        downstreamParameterized {
            trigger('ELIS2-BuildGateFlow') {
                parameters {
                    currentBuild()
                    predefinedProp('GIT_BRANCH_HASH', '${GIT_COMMIT}')
                }
            }
        }
    }
    jdk('java-1.8.0_u102')
    label 'Build-Deploy-Node-Java8'
    scm {
        git {
            remote {
                name('origin')
                url('${APPS_REPO_SSH}')
                branch('Release_Hotfixes')
                credentials('dd041e59-94e6-4023-843f-5bad8a9b9238')
            }
            extensions {
                mergeOptions{
                    branch('${GIT_BRANCH_NAME}')
                    getFastForwardMode()
                    remote('origin')
                    strategy('default')
                }
                wipeOutWorkspace()
                relativeTargetDirectory('Release Hotfixes')
                cloneOptions{
                    timeout(25)
                }
            }
        }
    }
    steps {
        setBuildResult("SUCCESS")
        shell(readFileFromWorkspace('jobs/build/resources/buildGatePreMerge/shellForPreMerge.sh'))
    }
    wrappers {
       buildName('#${ENV,var="GIT_BRANCH_NAME"}')
       buildUserVars()
       timestamps()
    }
}
