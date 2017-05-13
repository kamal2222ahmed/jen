parallel (
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":InternalApp:InternalAppDomain", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":DigitalEvidence:DigitalEvidenceWeb", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Backend:Elis2ServicesLib", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Backend:BenefitRequestProcessModels", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Backend:ServicesApp", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Backend:ExternalAPI", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":FormI90:I90Domain", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":FormI90:I90App", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":FormI90:I90Rules", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":EIP:LockboxConnector", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":EIP:LockboxIntegration", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:Metis", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:Crypto", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:FormRepository", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:Domain", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:Resources", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:RulesLib", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:security", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:Interfaces", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Shared:Libs", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":InternalApp:InternalApp", UPLOAD_FPR: "true", VERSION: "Master")
  },
  {
    build("ELIS-Fortify", GRADLE_PROJECT: ":Backend:Elis2Services", UPLOAD_FPR: "true", VERSION: "Master")
  }
)
