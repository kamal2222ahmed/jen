sectionedView('DEMO ENVIRONMENTS') {
    description(readFileFromWorkspace('resources2/views2/demoPipelineDescription.txt'))
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
			name('DEMO SITH ENVIRONMENT')
			width('FULL')
			alignment('CENTER')
			jobs {
				names('ELIS2-DEMO-SITH-Deploy', 'ELIS2-DEMO-SITH-ScorchUpdateLoad')
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
        listView {
            name('DEMO ENVIRONMENTS START + SHUTDOWN MANAGEMENT')
            width('FULL')
            alignment('CENTER')
            jobs {
                name('All-Demo-Environment-Start')
                name('All-Demo-Environment-Stop')
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
