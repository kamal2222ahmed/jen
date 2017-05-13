sectionedView('DEVELOPER UTILITES') {
    description('Developer Helper Jobs')
    sections {
        listView {
            name('Virtual Machine Management')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('AADS-VM-Start', 'AADS-VM-Stop', 'Shutdown-N-Restart-Linux', 'Shutdown-N-Restart-Windows', 'workOnTheWeekend')
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
            name('SonarQube Developer Cockpit Testing')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-BuildGateBuild-SonarScanner99')
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
