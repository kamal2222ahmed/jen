sectionedView('Developer Utilites') {
    description('Developer Helper Jobs')
    sections {
        listView {
            name('Virtual Machine Management')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('Shutdown-N-Restart-Linux', 'Shutdown-N-Restart-Windows', 'workOnTheWeekend')
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
