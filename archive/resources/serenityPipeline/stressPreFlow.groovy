import hudson.model.*;
import hudson.EnvVars;

EnvVars envVars = build.getEnvironment(listener);
dir = new File(envVars.get('WORKSPACE'));
println "**";
println dir.exists()
name = envVars.get('WORKSPACE') + "/deploy.properties";
def job = Hudson.instance.getJob('ELIS2-IntegrationGateBuild')
 

def file1 = new File(name)
file1.createNewFile()

file1 << 'RELEASE=' + job.getLastSuccessfulBuild().getDisplayName()
