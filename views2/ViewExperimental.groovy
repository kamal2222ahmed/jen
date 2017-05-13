sectionedView('Experimental Jobs') {
    description('<h1 style="text-align:center;"><strong><font size="5" color="red">Experimental Jobs</font><strong></h1>')
    sections {
        listView {
           name('Experimental Jobs')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('EXP.*')
                names('EXPERIMENTAL_JOBS_TAB')
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
