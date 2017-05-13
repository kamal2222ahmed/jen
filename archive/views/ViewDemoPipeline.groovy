sectionedView('DEMO ENVIRONMENTS') {
    description(readFileFromWorkspace('resources/views/demoPipelineDescription.txt'))
    sections {
        listView {
            name('DEMO MOCK ENVIRONMENT')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-DEMO-Mock-Deploy', 'ELIS2-DEMO-Mock-ScorchUpdateLoad')
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
            name('DEMO LIVE ENVIRONMENT')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-DEMO-Live-Deploy', 'ELIS2-DEMO-Live-ScorchUpdateLoad')
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
            name('DEMO LOCKBOX ENVIRONMENT')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-DEMO-Mock-Lockbox-Deploy', 'ELIS2-DEMO-Mock-Lockbox-ScorchUpdateLoad')
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
            name('DEMO MYUSCIS ENVIRONMENT')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-DEMO-Mock-MyUSCIS-Deploy', 'ELIS2-DEMO-Mock-MyUSCIS-ScorchUpdateLoad')
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
            name('DEMO MicroSERVICES ENVIRONMENT')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-DEMO-Micro-Deploy', 'ELIS2-DEMO-Micro-ScorchUpdateLoad')
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
            name('HELPER JOBS')
            width('FULL')
            alignment('CENTER')
            jobs {
                name('DEMO-Log-Emailer')
                name('Demo-Property-File-Emailer')
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
