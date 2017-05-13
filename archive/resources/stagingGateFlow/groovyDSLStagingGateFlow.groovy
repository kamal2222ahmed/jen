out.println 'Nexus ID: ' + params["NEXUS_ID"]
out.println 'Git Branch: Staging'

parallel (
  {
    build("ELIS2-StagingGateBuild", NEXUS_ID: "STG")
  },
  {
    build("ELIS2-StagingGateBuild-testSonarInt", NEXUS_ID: "STG")
  },
  {
    build("DB-Database-Check", NEXUS_ID: "DBSTG", GIT_BRANCH_NAME: "Staging", TARGET_BRANCH: "master", SMOKETEST: true)
  }
)
  