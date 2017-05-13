sectionedView('BGO PIPELINE') {
    description(readFileFromWorkspace('resources/views/bgoPipelineDescription.txt'))
    sections {
        listView {
            name('Build Gate Optimization (BGO) Build Flow')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-Test-BGO-BuildGateFlow', 'ELIS2-Test-BGO-BuildGate-testSonarInt', 'ELIS2-Test-BGO-BuildGateBuild','ELIS2-BGO-Test-Flow', 'ELIS2-BGO-DeployByVersion')
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
            name('Build Gate Optimization (BGO) Build Tests')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-BGO-TestSuite.*')
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
