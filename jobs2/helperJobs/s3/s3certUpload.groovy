job('DT-S3-Certificate-Upload') {
  description('<h1 style="text-align:center;"><strong><font size="5" color="red">Use to upload certificates to assigned s3 bucket </font><strong></h1>')
  jdk('java-1.8.0_u102')
  label('s3-upload-node')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'jbfrimpo')
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(100)
  }
  throttleConcurrentBuilds {
    maxTotal(1)
    maxPerNode(1)
  }
  parameters {
    choiceParam('S3_BUCKET', ['el2-dt-microsecrets', 'el2-pt-microsecrets', 'el2-eut-microsecrets', 'el2-sky-microsecrets', 'el2-sky2-microsecrets', 'el2-trn-microsecrets', 'el2-trn2-microsecrets' ], 'Select desired s3 bucket for certificate upload')
    choiceParam('S3_SUBFOLDER', ['internal', 'external', 'ocp', 'tecs', 'docker', 'jenkins', 'micro', 'payment', 'services'], 'Select desired s3 subfolder for certificate upload')
    choiceParam('ENVIRONMENT_TAG', ['DT-DEMO', 'PT', 'EUT', 'SKY', 'SKY2', 'TRN', 'TRN2'], 'Environment for certificate')
    fileParam('Certificate_to_Upload', 'Enter certificate to upload to desired s3 bucket.')
    stringParam('EXPIRATION_DATE_TAG', 'MM/DD/YYYY', 'Enter certificate expiration date for s3 tag.')
    
  }
  steps {
     shell(readFileFromWorkspace('resources2/helperjobs/s3/certificateUpload.sh')) 
  }
  configure { project ->
    def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
      teamDomain('uscis')
      authToken('CxGbmA6sSwX1wUSPX88I96Xp')
      room('#ci-cd-helperjobs')
      startNotification(true)
      notifySuccess(true)
      notifyAborted(false)
      notifyNotBuilt(false)
      notifyUnstable(false)
      notifyFailure(true)
      notifyBackToNormal(true)
      notifyRepeatedFailure(false)
      includeTestSummary(false)
      commitInfoChoice('AUTHORS_AND_TITLES')
      includeCustomMessage(true)
      customMessage('')
   }
 }
  wrappers {
    preBuildCleanup()
    buildUserVars()
    buildName('#${ENV,var="S3_BUCKET"}-${BUILD_NUMBER}')
    timestamps()
  }
}
