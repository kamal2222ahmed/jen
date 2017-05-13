sectionedView('Team-MasterBuildTest') {
    description(readFileFromWorkspace('resources/views/teamMasterBuildtestPipeline.txt'))
    sections {
        listView {
            name('Team-MasterBuildTest Build')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('Test-Team-ELIS2-BuildGateFlow', 'Test-Team-ELIS2-BuildGate-testSonarInt', 'Test-Team-ELIS2-BuildGate')
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
            name('Team-MasterBuildTest Tests')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-Team-MasterBuildTestCheck-Tests')
                regex('ELIS2-Team-TestSuite-.*')
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
            name('Team-MasterBuildTest Helper Jobs')
            width('FULL')
            alignment('CENTER')
            jobs {
		names('ELIS2-Team-FT-Wake-Up-N-Sleep', 'ELIS2-Team-MasterBuildCheck-FT-DeployByVersion', 'ELIS2-Manual-Wake-N-Sleep-Team-FT')
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
