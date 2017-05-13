buildFlowJob('ConsumeFTFlow') {
  description(readFileFromWorkspace('resources2/helperjobs/consumeFTFlow/consumeFTFlow-Description.txt'))
  jdk('java-1.8.0_u102')
  concurrentBuild()
  buildFlow(readFileFromWorkspace('resources2/helperjobs/consumeFTFlow/consumeFTFlow.groovy'))
  label('Build-Deploy-Node-Java8 || Build-Deploy-Node-Java8-awscli')
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
    artifactDaysToKeep(50)
    daysToKeep(20)
    numToKeep(500)
  }
  throttleConcurrentBuilds {
    maxTotal(40)
    maxPerNode(1)
  }
  parameters {
    stringParam("ENVIRONMENT")
  }
  publishers {
      downstreamParameterized {
        trigger ('test-slack') {
          condition('FAILED')
          parameters{
            predefinedProp("CHANNEL", "#ci-pipelinefailure")
            predefinedProp("TITLE", '${ENVIRONMENT} is NOT HEALTHY')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} is NOT HEALTHY')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "danger")
          }
        }
        trigger ('test-slack') {
          condition('SUCCESS')
          parameters{
            predefinedProp("CHANNEL", "#ci-pipeline-fts")
            predefinedProp("TITLE", '${ENVIRONMENT} is healthy')
            predefinedProp("MESSAGE_TEXT", '${ENVIRONMENT} is healthy')
            predefinedProp("USERNAME", "jenkinsFTBot")
            predefinedProp("BUILDURL", '${BUILD_URL}')
            predefinedProp("COLOR", "good")
          }
        }
      }
  }
  wrappers {
    buildUserVars()
  }
}
