sectionedView('Serenity Testing') {
    description('Developer Serenity Testing')
    sections {
        listView {
            name('Google Chrome')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('Serenity-Clone-Chrome', 'Serenity-Pre-Flow-Chrome', 'Serenity-StressTest-Flow-Chrome')
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
