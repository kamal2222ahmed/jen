out.println 'Attempting Deployment to FT ENV' + params["ENVIRONMENT"] + ' - ' + params["NEXUS_ID"]+ '.' + params["SNAPSHOT_NUMBER"] + '-SNAPSHOT'
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME"]

retry ( 2 )
{
    build ("ELIS2-FT-DeployByVersion", BRANCH: params["GIT_BRANCH_NAME"], ARTIFACT_VERSION: params["NEXUS_ID"] + "." + params["SNAPSHOT_NUMBER"] + "-SNAPSHOT", ENVIRONMENT:  params["ENVIRONMENT"], TOGGLE: "ALL_ON")
}

out.println 'Starting Test Run'

parallel(
  {
    build ("ELIS2-TestSuite-Lockbox", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-TestSuite-I131", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-TestSuite-IV", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-TestSuite-DACA", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-TestSuite-N400", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-TestSuite-DigitalEvidence", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
   {
    build ("ELIS2-TestSuite-N600", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-TestSuite-N565", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
   {
    build ("ELIS2-TestSuite-N336", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-TestSuite-I90", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  }
)
