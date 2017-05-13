out.println 'Attempting Deployment to FT ENV' + params["ENVIRONMENT"] + ' - ' + params["NEXUS_ID"]+ '.' + params["SNAPSHOT_NUMBER"] + '-SNAPSHOT'
out.println 'Git Branch: ' + params["GIT_BRANCH_NAME"]

build ("ELIS2-FT-Wake-Up-N-Sleep", ENVIRONMENT: params["ENVIRONMENT"], STATE: "ON")

retry ( 2 )
{
    build ("ELIS2-FT-DeployByVersion", BRANCH: params["GIT_BRANCH_NAME"], ARTIFACT_VERSION: params["NEXUS_ID"] + "." + params["SNAPSHOT_NUMBER"] + "-SNAPSHOT", ENVIRONMENT:  params["ENVIRONMENT"], TOGGLE: "ALL_ON")
}

out.println 'Starting Test Run'

parallel(
  {
    build ("ELIS2-BGO-TestSuite-Lockbox", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-I131", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-IV", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-DACA", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-N400", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-DigitalEvidence", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
   {
    build ("ELIS2-BGO-TestSuite-N600", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-N565", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
   {
    build ("ELIS2-BGO-TestSuite-N336", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-I90", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-BGO-TestSuite-TPS", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  }
)
