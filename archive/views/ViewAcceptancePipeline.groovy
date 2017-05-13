sectionedView('CI Pipeline - Acceptance Testing') {
    description('<h1><center> ELIS2 CI ACCEPTANCE TESTING PIPELINE  </center></h1>')
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
    }
}
