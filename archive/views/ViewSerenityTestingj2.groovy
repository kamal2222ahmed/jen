sectionedView('SERENITY TESTING') {
    description('<h1 style="text-align:center;"><strong><font size="5" color="red">Developer Serenity Testing</font><strong></h1>')
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
