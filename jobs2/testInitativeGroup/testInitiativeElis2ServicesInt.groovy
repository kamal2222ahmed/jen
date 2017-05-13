buildFlowJob('ELIS2-TestInitiative-Elis2Services-Integration2') {
	description(readFileFromWorkspace('resources2/ciPipeline/buildGate/buildGateBuildTestSonarInt/buildGateBuild-testSonarIntDescription.txt'))
	jdk('java-1.8.0_u102')
	concurrentBuild()
	label('Build-Deploy-Node-Java8')
	buildNeedsWorkspace(true)
	buildFlow(readFileFromWorkspace('resources2/testInitiativeGroup/groovyDSLTestInitiativeElis2ServicesIntegrationTestFlow.groovy'))
	authorization {
	  permission('hudson.model.Item.Discover', 'anonymous')
	  permission('hudson.model.Item.Read', 'anonymous')
	  permission('hudson.model.Item.Cancel', 'anonymous')
	  permissionAll('rptighe')
	  permissionAll('jchang2')
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
	  permissionAll('jgarciar')
	  permissionAll('rptighe')
	  blocksInheritance()
	}
	logRotator {
	  artifactDaysToKeep(10)
	  daysToKeep(10)
	  numToKeep(1000)
	}
	throttleConcurrentBuilds {
	  maxTotal(40)
	  maxPerNode(1)
	}
	parameters {
	  stringParam("GIT_BRANCH_NAME", null, "This is the name of the branch to build from")
	  stringParam("GIT_BRANCH_HASH")
	  stringParam("NEXUS_ID", "BLD")
	}
	configure { project ->
	  def slack = project / 'publishers' / 'jenkins.plugins.slack.SlackNotifier' {
		teamDomain('uscis')
		authToken('CxGbmA6sSwX1wUSPX88I96Xp')
		room('#elis-testintgroup')
		startNotification(true)
		notifySuccess(true)
		notifyAborted(false)
		notifyNotBuilt(false)
		notifyUnstable(false)
		notifyFailure(true)
		notifyBackToNormal(true)
		notifyRepeatedFailure(false)
		includeTestSummary(false)
		commitInfoChoice('AUTHORS')
		includeCustomMessage(false)
		customMessage()
	  }
	}
}
  