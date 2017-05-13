job('Upload Adobe LiveCycle template') {
  description(readFileFromWorkspace('resources/helperJobs/adobeUploadTemplatedescription.txt'))
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
  }
  parameters {
    fileParam("upload", "Please upload a file with the *.xdp file extension" )
  }
  steps {
      shell(readFileFromWorkspace('resources/helperJobs/adobeUploadTemplate.sh'))
  }
}
