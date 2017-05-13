build ("Update-Chef-Config", ENVIRONMENT: params["ENVIRONMENT"])

build ("ELIS2-FT-DeployByVersion", BRANCH: params["GIT_BRANCH_NAME"], ARTIFACT_VERSION: params["RELEASE"], ENVIRONMENT:  params["ENVIRONMENT"], TOGGLE: "ALL_ON")

build ("Serenity-Clone-Chrome", GIT_BRANCH_NAME: params["GIT_BRANCH_NAME"], NEXUS_ID: params["NEXUS_ID"], BUILD_VERSION: params["SNAPSHOT_NUMBER"], ENVIRONMENT: params["ENVIRONMENT"], IS_SNAPSHOT: true) 
