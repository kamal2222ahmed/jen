job('EXP-DataGuardCheck') {
  description('proof of concept for the DataGuard Check gate')
  jdk('java-1.8.0_u112')
  label('npm-java8-builder')
  authorization {
    permission('hudson.model.Item.Discover', 'anonymous')
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
    permissionAll('sdlassit')
    blocksInheritance()
  }
  logRotator {
    artifactDaysToKeep(30)
    daysToKeep(30)
    numToKeep(10)
  }
  throttleConcurrentBuilds {
    maxTotal(10)
    maxPerNode(1)
  }
  parameters {
    stringParam("GIT_BRANCH_NAME", null, "This is the name of the branch to build from")
  }
  scm {
    git {
      remote {
        name('origin')
        url('${APPS_REPO_SSH}')
        branch('${GIT_BRANCH_NAME}')
        credentials('883c4184-cc90-4238-a37f-08ada3d3fc55')
      }
      extensions {
        relativeTargetDirectory('Apps')
        cloneOptions {
          timeout(25)
        }
      }
    }
  }
  steps {
      shell(readFileFromWorkspace('resources2/ciUtilites/skynetHelperJobs/dataguardCheck.sh'))
  }

  wrappers {
    buildUserVars()
    timestamps()
    buildName('#${ENV,var="GIT_BRANCH_NAME"}-${BUILD_NUMBER}')
    timeout {
        absolute(10)
        failBuild()
        writeDescription('Build failed due to timeout after 10 minutes')
    }
  }
}
