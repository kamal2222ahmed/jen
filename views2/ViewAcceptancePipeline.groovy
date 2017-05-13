sectionedView('ACCEPTANCE TESTING') {
    description('<h1 style="text-align:center;"><strong><font size="5" color="red"> ELIS2 CI ACCEPTANCE TESTING PIPELINE</font><strong></h1>')
    sections {
        listView {
            name('CI Acceptance Testing')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-AcceptanceTesting')
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
        listView {
            name('Security Scans')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-CLM-Release-Scan')
                regex('ELIS2-CLM.*')
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
        listView {
            name('JMeter Performance Testing')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-Lockbox-Performance-Testing')
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
