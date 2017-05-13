buildMonitorView ('HotfixMonitor') {
description('Hot Fix Monitor')
filterBuildQueue(filterBuildQueue = true)
filterExecutors(filterExecutors = true)
  jobFilters {
    jobs {
    names('ELIS2-HF-IntegrationGateBuild','ELIS2-HF-TestSuite-DACA','ELIS2-HF-TestSuite-DigitalEvidence','ELIS2-HF-TestSuite-I131','ELIS2-HF-TestSuite-I90','ELIS2-HF-TestSuite-IV','ELIS2-HF-TestSuite-N336','ELIS2-HF-TestSuite-N400','ELIS2-HF-TestSuite-N600','ELIS2-HF-TS-Lockbox','ELIS2-HOTFIX-BuildGate-Tests','ELIS2-HOTFIX-BuildGate-testSonarInt','ELIS2-HOTFIX-BuildGateBuild','ELIS2-HOTFIX-BuildGateFlow')
    }
  }
}
