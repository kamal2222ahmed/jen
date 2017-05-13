out.println 'Nexus ID: ' + params["NEXUS_ID"]
out.println 'Git Branch Hash: ' + params["GIT_BRANCH_HASH"]
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME"]
out.println 'Starting The Test Initiative Elis2Services Parallel Flow'


parallel (
  {
    build("ELIS2-TestInitiative-Elis2Services-Integration2", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
  },
  {
    build("ELIS2-TestInitiative-Elis2Services-Unit", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
  }
)
