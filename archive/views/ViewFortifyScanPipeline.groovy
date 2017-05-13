sectionedView('FORTIFY') {
    description('<h1><center> Fortify Static Code Security Analyzer Pipeline  </center></h1>')
    sections {
        listView {
           name('Fortify Scan')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS-Fortify.*')
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
