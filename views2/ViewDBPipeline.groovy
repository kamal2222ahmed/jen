sectionedView('DB-PIPELINE') {
    description(readFileFromWorkspace('resources2/views2/dbPipelineDescription.txt'))
    sections {
        listView {
            name('ENTRANCE')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('DB-Database-Check')
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
            name('DATABASE BUILD')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('DB-PreMerge', 'DB-Deploy', 'DB-intTest', 'DB-Test-Flow')
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
            name('DATABASE SMOKETESTS')
            width('FULL')
            alignment('CENTER')
            jobs {
		names('DB-SmokeTest')
                regex('DB-TestSuite-.*')
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
