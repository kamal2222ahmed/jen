out.println 'Nexus ID: ' + params["NEXUS_ID"]
out.println 'Git Branch Hash: ' + params["GIT_BRANCH_HASH"]
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME"]
out.println 'Starting Build Gate Flow'


parallel (
  {
    build("ELIS2-BuildGateBuild", NEXUS_ID: "BLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
  },
  {
    build("ELIS2-BuildGateBuild-testSonarInt", NEXUS_ID: "BLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
  },
  {
    build("DB-Database-Check", NEXUS_ID: "DBBLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], TARGET_BRANCH: "Staging", SMOKETEST: true)
  }
)
build("ELIS2-BuildGate-Merge", SNAPSHOT_NUMBER: build.environment.get("BUILD_NUMBER"), GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
