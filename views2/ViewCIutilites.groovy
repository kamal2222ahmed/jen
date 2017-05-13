sectionedView('CI-UTILITES') {
    description('SKYNET CI Pipeline Utility Jobs')
    sections {
        listView {
            name('Create Functional Test Environment')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('FT-launch-Flow', 'launchFT-app1', 'launchFT-idb1')
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
            name('Create Demo Environment')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('1 Click - Demo Environment', 'Demo_Environment_Creation_APP1', 'Demo_Environment_Creation_IDB1')
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
            name('System Account Management')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('FT System Account Password Reset', 'Demo MYUSCIS System Account Password Reset', 'Demo Mock System Account Password Reset', 'Demo Lockbox System Account Password Reset', 'Demo Live System Account Password Reset')
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
            name('Helper Jobs')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('Shutdown-N-Restart-Linux', 'Shutdown-N-Restart-Windows', 'workOnTheWeekend', 'Backup-JobConfig', 'ELIS2-GitRepo-Branch-Report', 'Clean-Cache', 'Backup-ChefRepo', 'reese_Nonweekenders-start', 'reese_Nonweekenders-stop')
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
