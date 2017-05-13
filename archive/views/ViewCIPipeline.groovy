sectionedView('CI-PIPELINE') {
    description(readFileFromWorkspace('resources/views/ciPipelineDescription.txt'))
    sections {
        listView {
            name('PIPELINE ENTRANCE')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-BuildGate-PreMerge', 'ELIS2-BuildGate-GitPoll')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
        listView {
            name('BUILD GATE')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-BuildGateBuild', 'ELIS2-BuildGateFlow', 'ELIS2-BuildGate-Tests', 'ELIS2-BuildGateBuild-testSonarInt', 'ELIS2-BuildGate-Merge')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
        listView {
            name('STAGING GATE')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-StagingGateBuild', 'ELIS2-StagingGateFlow', 'ELIS2-StagingGate-Tests', 'ELIS2-StagingGateBuild-testSonarInt', 'ELIS2-StagingGate-Merge')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
        listView {
            name('INTEGRATION GATE')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-Integration.*')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
        listView {
            name('GATED TESTS')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-TestSuite-.*')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
        listView {
            name('HELPER JOBS')
            width('FULL')
            alignment('CENTER')
            jobs {
                name('Chef-EnvironmentSync')
                name('ELIS2-BuildGate-Merge')
                name('ELIS2-BuildSpecialBranch')
                name('ELIS2-FT-DeployByVersion')
                name('ELIS2-FT-Wake-Up-N-Sleep')
                name('ELIS2-GetChangeLog')
                name('ELIS2-IntegrationGateBuild-InterfaceCheck')
                name('ELIS2-launch-node')
                name('ELIS2-terminate-slave')
                name('updateTagForInterfaceCheck')
                name('Upload Adobe LiveCycle template')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
    }
}
