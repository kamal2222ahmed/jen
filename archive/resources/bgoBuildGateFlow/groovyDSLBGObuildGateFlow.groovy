out.println 'Nexus ID: ' + params["NEXUS_ID"]
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME1"]

parallel (
  {
    build("ELIS2-Test-BGO-BuildGateBuild", NEXUS_ID: "BLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME1"]) 
  },
  {
    build("ELIS2-Test-BGO-BuildGate-testSonarInt", NEXUS_ID: "BLD", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME1"])
  }
)
