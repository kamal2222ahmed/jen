parallel (
  { build("launchFT-idb1", ENVIRONMENT: params["ENVIRONMENT"], Version: params["Version"], DELETE: params["DELETE"])},
  { build("launchFT-app1", ENVIRONMENT: params["ENVIRONMENT"], Version: params["Version"], DELETE: params["DELETE"])}
)
