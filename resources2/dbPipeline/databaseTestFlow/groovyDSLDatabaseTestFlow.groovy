out.println 'Nexus ID: ' + params["NEXUS_ID"]
out.println 'Git Branch Hash: ' + params["GIT_BRANCH_HASH"]
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME"]
out.println 'Latest Prod Version: ' + params["PROD_VERSION"]
out.println 'Artifact Version: ' + params["ARTIFACT_VERSION"]
out.println 'Environment: ' +  params["ENVIRONMENT"]
out.println 'Starting DB Test Flow:'


parallel (
  {
    build("DB-intTest", NEXUS_ID: params["NEXUS_ID"], PROD_VERSION: params["PROD_VERSION"], ARTIFACT_VERSION: params["ARTIFACT_VERSION"], GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"])
  },
  {
    retry ( 2 )
    {
      build("DB-Deploy", ENVIRONMENT: params["ENVIRONMENT"], NEXUS_ID: params["NEXUS_ID"], PROD_VERSION: params["PROD_VERSION"], ARTIFACT_VERSION: params["ARTIFACT_VERSION"], GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], SMOKETEST: params["SMOKETEST"])
    }
  }
)
