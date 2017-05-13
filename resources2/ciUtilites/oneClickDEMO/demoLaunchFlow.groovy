parallel (
  { build("Demo_Environment_Creation_IDB1", ENVIRONMENT: params["ENVIRONMENT"], Version: params["Version"])},
  { build("Demo_Environment_Creation_APP1", ENVIRONMENT: params["ENVIRONMENT"], Version: params["Version"])}
)
