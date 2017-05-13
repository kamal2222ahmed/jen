parallel (
  {
    build("ELIS2-CLM-InternalApp", RELEASE_CANDIDATE: params["RELEASE_CANDIDATE"])
  },
  {
    build("ELIS2-CLM-ServicesApp", RELEASE_CANDIDATE: params["RELEASE_CANDIDATE"])
  },
  {
    build("ELIS2-CLM-efile", RELEASE_CANDIDATE: params["RELEASE_CANDIDATE"])
  },
  {
    build("ELIS2-CLM-Lockbox", RELEASE_CANDIDATE: params["RELEASE_CANDIDATE"])
  },
  {
    build("ELIS2-CLM-LockboxConnector", RELEASE_CANDIDATE: params["RELEASE_CANDIDATE"])
  }
)
