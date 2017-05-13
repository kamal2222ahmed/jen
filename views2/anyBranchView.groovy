sectionedView('AnyBranch') {
    description(readFileFromWorkspace('resources2/views2/anyBranchPipelineDescription.txt'))
    sections {
      listView {
          name('ANY BRANCH Entrance')
          width('FULL')
          alignment('CENTER')
          jobs {
              names('ELIS2-anyBranchGateFlow')
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
            name('ANY BRANCH BUILD GATE')
            width('FULL')
            alignment('CENTER')
            jobs {
                names('ELIS2-anyBranchGateBuild', 'ELIS2-anyBranchGate-Tests', 'ELIS2-anyBranchGateBuild-testSonarInt')
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
            name('GATED TESTS')
            width('FULL')
            alignment('CENTER')
            jobs {
                regex('ELIS2-TestSuite-.*')
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
                name('ELIS2-FT-DeployByVersion')
                name('ELIS2-FT-Wake-Up-N-Sleep')
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
