sectionedView('MicroServices') {
    description('<h1><center> MicroServices </center></h1>')
    sections {
        listView {
           name('300DPI')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-Micro.*')
                names('ELIS2-DEMO-Mock-Lockbox-300DPI-Deploy')
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
