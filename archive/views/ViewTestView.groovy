sectionedView('1Test') {
    description('<h1><center>Test View</center></h1>')
    sections {
        listView {
            name('GROOVY-Test-jobs')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('TEST.*')
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
