out.println 'Nexus ID: ' + params["NEXUS_ID"]
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME"]
out.println 'Starting any Branch Gate Flow'


parallel (
  {
    build("ELIS2-anyBranchGateBuild", NEXUS_ID: "BLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_NAME"])
  },
  {
    build("ELIS2-anyBranchGateBuild-testSonarInt", NEXUS_ID: "BLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_NAME"])
  },
  {
    build("DB-Database-Check", NEXUS_ID: "DBBLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], TARGET_BRANCH: "Staging", SMOKETEST: true)
  }
)
