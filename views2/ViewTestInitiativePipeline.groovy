sectionedView('Test Initiative Group') {
    description(readFileFromWorkspace('resources2/views2/testInitiativeGroupPipelineViewDescription.txt'))
    sections {
        listView {
            name('Integration Tests In Parallel')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-TestSonarIntFlow','ELIS2-TestInitiative-Elis2Services', 'ELIS2-TestInitiative-EverythingElse')
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
