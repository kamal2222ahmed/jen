buildFlowJob('ELIS2-VERIFY-FT') {
  description(readFileFromWorkspace('resources2/verifyFTpieline/verifyFTdescription.txt'))
  jdk('java-1.8.0_u102')
  label('Build-Deploy-Node-Java8')
  buildNeedsWorkspace(true)
  buildFlow(readFileFromWorkspace('resources2/verifyFTpieline/groovyDSLverifyFT.groovy'))
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permissionAll('akkausha')
    permissionAll('ambutt')
    permissionAll('caaponte')
    permissionAll('cnwillia')
    permissionAll('fconwumb')
    permissionAll('jbfrimpo')
    permissionAll('jnugorji')
    permissionAll('jsjohnso')
    permissionAll('kchen1')
    permissionAll('nppatel')
    permissionAll('rthandavan')
    permissionAll('rvangar')
    permissionAll('skahmed')
    permissionAll('skkrishn')
    permissionAll('srkhan')
    permissionAll('srkombam')
    permissionAll('tabaig')
    permissionAll('wezewudi')
    permissionAll('wmfowlke')
  }
  logRotator {
    artifactDaysToKeep(15)
    daysToKeep(15)
    numToKeep(15)
  }
  parameters {
    stringParam('ENVIRONMENT')
    stringParam('BTAR_VERSION', '6.1.122.1604')
    stringParam('NEXUS_ID', '6.1.122')
    stringParam('BUILD_VERSION', '1604', '(e.g for Release Candidate 5.1.104.124 this would be "124")')
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-verify-ft-pipeline')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      commitInfoChoice('AUTHORS')
      includeCustomMessage(false)
      customMessage()
    }
  }
  //publishers {
   //downstreamParameterized {
     // trigger('ELIS2-FT-Wake-Up-N-Sleep') {
       // condition('ALWAYS')
        //parameters {
         // predefinedProp('ENVIRONMENT', '${ENVIRONMENT}')
          //predefinedProp('STATE', 'OFF')
        //}
     // }
   // }
 // }
}
