out.println 'MAJOR: ' + params["MAJOR"]
out.println 'MINOR: ' + params["MINOR"] 
out.println 'SPRINT: ' + params["SPRINT"]

parallel (
  { 
    build("ELIS2-HF-IntegrationGateBuild", MAJOR: params["MAJOR"], MINOR: params["MINOR"], SPRINT: params["SPRINT"]) 
  }
)
