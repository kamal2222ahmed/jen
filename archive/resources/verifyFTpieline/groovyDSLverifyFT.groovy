out.println 'Attempting Deployment to FT ENV' + params["ENVIRONMENT"] + ' - ' + params["NEXUS_ID"]+ '.' + params["BUILD_VERSION"]

build ("ELIS2-FT-Wake-Up-N-Sleep", ENVIRONMENT: params["ENVIRONMENT"], STATE: "ENABLE")
build ("ELIS2-FT-DeployByVersion", BRANCH: params["BTAR_VERSION"], ARTIFACT_VERSION: params["BTAR_VERSION"], ENVIRONMENT:  params["ENVIRONMENT"], TOGGLE: "ALL_ON")


parallel(
  { build ("ELIS2-VerifyTest-Lockbox", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-I131", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-DACA", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-IV", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-DigitalEvidence", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-I90", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-N600", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-N565", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-N336", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) },
  { build ("ELIS2-VerifyTest-N400", GIT_BRANCH_NAME: params["BTAR_VERSION"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["BUILD_VERSION"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: false) }
  )
