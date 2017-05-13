sectionedView('CI-HOTFIX-PIPELINE') {
    description(readFileFromWorkspace('resources2/views2/hotfixPipelineDescription.txt'))
    sections {
        listView {
            name('HOTFIX RE-BASELINE')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-HOTFIX-Pipeline_Rebase')
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
            name('HOTFIX BUILD GATE')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-HOTFIX-Build.*')
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
            name('HOTFIX INTEGRATION GATE')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-HF-Integration.*')
                name('ELIS2-IntegrationGateDB-Disruption')
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
            name('HOTFIX GATED TESTS')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-HF-TS.*')
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
                name('ELIS2-GetChangeLog')
                name('ELIS2-HOTFIX-Tester-BuildGate-testSonarInt')
                name('Upload Adobe LiveCycle template')
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
