out.println 'Starting Test Run'

parallel(
  {
    build ("DB-TestSuite-Lockbox", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("DB-TestSuite-I131", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("DB-TestSuite-IV", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("DB-TestSuite-DACA", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("DB-TestSuite-N400", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("DB-TestSuite-DigitalEvidence", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
   {
    build ("DB-TestSuite-N336", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true) 
  },
  {
    build ("DB-TestSuite-N600", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("DB-TestSuite-N565", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  },
  {
    build ("DB-TestSuite-I90", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], GIT_BRANCH_HASH: params["GIT_BRANCH_HASH"], NEXUS_ID: params["NEXUS_ID"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true)
  }
)
