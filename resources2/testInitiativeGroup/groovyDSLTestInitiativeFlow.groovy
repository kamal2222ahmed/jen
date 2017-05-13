out.println 'Nexus ID: ' + params["NEXUS_ID"]
out.println 'Git Branch Hash: ' + params["GIT_BRANCH_HASH"]
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME"]
out.println 'Starting The Test Initiative Parallel Flow'


parallel (
  {
    build("ELIS2-TestInitiative-Elis2Services", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
  },
  {
    build("ELIS2-TestInitiative-EverythingElse", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
  }
)
