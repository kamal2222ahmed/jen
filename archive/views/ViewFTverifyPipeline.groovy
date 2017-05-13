sectionedView('VERIFY FT SKYNET') {
    description(readFileFromWorkspace('resources/views/verifyFTpipelineDescription.txt'))
    sections {
        listView {
            name('Verify FT Entrance')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-VERIFY-FT', 'ELIS2-VERIFY-FT-WITHOUTBUILD', 'PULL_FT', 'RETURN_FT')
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
            name('Deploy By Version')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-FT-DeployByVersion', 'Test-ELIS2-FT-DeployByVersion', 'Test-ELIS2-FT-DeployByVersion-NOLIQUIBASE')
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
            name('Verify FT Smoke Tests')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-VerifyTest.*')
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
