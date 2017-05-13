sectionedView('SeedJobs') {
    description(readFileFromWorkspace('resources/views/seedJobViewDescription.txt'))
    sections {
        listView {
            name('Seed Jobs')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('CI-Seed.*')
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
