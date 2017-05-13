out.println 'MAJOR: ' + params["MAJOR"]
out.println 'MINOR: ' + params["MINOR"]
out.println 'SPRINT: ' + params["SPRINT"]

parallel (
  {
    build("ELIS2-IntegrationGateBuild", MAJOR: params["MAJOR"], MINOR: params["MINOR"], SPRINT: params["SPRINT"])
  },
  {
    build("ELIS2-IntegrationGateBuild-testSonarInt", MAJOR: params["MAJOR"], MINOR: params["MINOR"], SPRINT: params["SPRINT"])
  },
  {
    ignore(FAILURE){
      build("ELIS2-IntegrationGateDB-Disruption", BRANCH: "Integration", ELIS_VERSION: params["MAJOR"] + '.' + params["MINOR"] + '.' + params["SPRINT"] + '.' + build.environment.get("BUILD_NUMBER"))
    }
  }
)