buildMonitorView ('DbCheckMonitor') {
description('DbCheckMonitor')
filterBuildQueue(filterBuildQueue = true)
filterExecutors(filterExecutors = true)
  jobFilters {
    jobs {
    names('DB-Database-Check','DB-Deploy','DB-intTest','DB-PreMerge','DB-SmokeTest','DB-Test-Flow','DB-TestSuite-DACA','DB-TestSuite-DigitalEvidence','DB-TestSuite-I131','DB-TestSuite-I90','DB-TestSuite-IV','DB-TestSuite-Lockbox','DB-TestSuite-N336','DB-TestSuite-N400','DB-TestSuite-N565','DB-TestSuite-N600')
    }
  }
}
