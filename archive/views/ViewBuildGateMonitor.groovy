buildMonitorView ('Build Gate Monitor') {
description('Build Gate Monitor')
filterBuildQueue(filterBuildQueue = true)
filterExecutors(filterExecutors = true)
  jobFilters {
    jobs {
    names('ELIS2-BuildGate-Tests','ELIS2-BuildGateBuild','ELIS2-BuildGateBuild-testSonarInt','ELIS2-BuildGateFlow','ELIS2-FT-DeployByVersion')
    }
  }
}
