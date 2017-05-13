parallel (
  {
    retry ( 4 )
    {
      build("AMI_CREATION_JOB", ENVIRONMENT: params["ENVIRONMENT"], RELEASE_CANDIDATE: params["RELEASE_CANDIDATE"])
    }
  }
)
