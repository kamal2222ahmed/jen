out.println 'Attempting Deployment to FT ENV' + params["GIT_BRANCH_NAME"] + '-' + params["ENVIRONMENT"] + ' - ' + params["NEXUS_ID"]+ '.' + params["SNAPSHOT_NUMBER"] + '-SNAPSHOT'

build ("ELIS2-FT-Wake-Up-N-Sleep", ENVIRONMENT: params["ENVIRONMENT"], STATE: "ON")
retry ( 4 )
{
    build ("ELIS2-FT-DeployByVersion", BRANCH: params["GIT_BRANCH_NAME"], ARTIFACT_VERSION: params["NEXUS_ID"] + "." + params["SNAPSHOT_NUMBER"] + "-SNAPSHOT", ENVIRONMENT:  params["ENVIRONMENT"], TOGGLE: "ALL_ON")
}

out.println 'Starting Test Run'

parallel(
  {
    build ("ELIS2-HF-TS-Lockbox", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-I131", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-IV", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-DACA", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-I90", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-DigitalEvidence", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-N565", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-N600", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-N336", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-N400", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("ELIS2-HF-TS-TPS", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  }
)
