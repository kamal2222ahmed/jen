sectionedView('FORTIFY') {
    description('<h1 style="text-align:center;"><strong><font size="5" color="red">Fortify Static Code Security Analyzer Pipeline</font><strong></h1>')
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
