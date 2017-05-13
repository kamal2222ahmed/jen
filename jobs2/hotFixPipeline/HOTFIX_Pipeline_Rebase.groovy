job('ELIS2-HOTFIX-Pipeline_Rebase') {
  description(readFileFromWorkspace('resources2/hotFixPipeline/hotfixRebase/NewDescription.txt'))
  jdk('java-1.8.0_u112')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Build', 'anonymous')
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
  parameters {
    stringParam("GIT_TAG", "8.1.58.293", "Please enter in the version of code on which the fix to be pushed should be based off of. The preferred version of code would be the <a href=\"https://internal-prod-elis2.uscis.dhs.gov/InternalApp/healthCheck/version\" target=\"_top\"> current version that is in PRODUCTION </a>'")
    stringParam("ReasonForNOApproval", "")
    choiceParam('ApprovedByGovernment', ['YES', 'NO'], '<font color "red">Did you receive Government Approval By a USCIS employee to proceed with this hotfix? </font> <p> <font color "red">IF the answer is NO - please enter in a justification in the "ReasonForNOApproval" field </font>')
  }
  configure { project ->
        project / 'properties' / 'hudson.model.ParametersDefinitionProperty' / parameterDefinitions << 'hudson.plugins.validating__string__parameter.ValidatingStringParameterDefinition' {
            name('emailAddress')
            defaultValue("")
            failedValidationMessage('NO EMAIL ADDRESS ENTERED')
            regex('^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})\$')
            description('''
                Please enter your email address
                '''.stripIndent().trim()
            )
        }
    }
  scm {
    git {
      remote {
        url('${APPS_REPO_SSH}')
        branch('CI-Production_Hotfixes')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
    }
  }
  steps {
    shell(readFileFromWorkspace('resources2/hotFixPipeline/hotfixRebase/NewShellForHotFixRebase.txt'))
  }
  publishers {
    git {
      pushOnlyIfSuccess(true)
      forcePush(true)
      branch("origin", "CI-Production_Hotfixes")
      branch("origin", "Release_Hotfixes")
    }
    downstreamParameterized {
      trigger ('test-slack') {
        condition('FAILED')
        parameters{
          predefinedProp("CHANNEL", "#ci-hotfixes")
          predefinedProp("TITLE", 'ALERT: UNAUTHORIZED HOTFIX REBASE INITIATED BY $emailAddress')
          predefinedProp("MESSAGE_TEXT", '$emailAddress attempted a rebase in the intention of creating a HOTFIX from tag $GIT_TAG without JUSTIFICATION OR GOVERNMENT APPROVAL')
          predefinedProp("USERNAME", "jenkinsFTBot")
          predefinedProp("BUILDURL", '${BUILD_URL}')
          predefinedProp("COLOR", "danger")
        }
      }
      trigger ('test-slack') {
        condition('SUCCESS')
        parameters{
          predefinedProp("CHANNEL", "#ci-hotfixes")
          predefinedProp("TITLE", 'ALERT: HOTFIX REBASE INITIATED BY $emailAddress')
          predefinedProp("MESSAGE_TEXT", 'FYI: The Hotfix Branch has been rebased from ELIS Release Version $GIT_TAG by $emailAddress')
          predefinedProp("USERNAME", "jenkinsFTBot")
          predefinedProp("BUILDURL", '${BUILD_URL}')
          predefinedProp("COLOR", "good")
        }
      }
    }
  }
  wrappers {
    timestamps()
    buildName('#${ENV,var="GIT_TAG"}')
  }
}
