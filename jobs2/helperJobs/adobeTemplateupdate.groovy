job('Upload Adobe LiveCycle template') {
  description(readFileFromWorkspace('resources2/helperjobs/adobeTemplateUpdate/adobeUploadTemplatedescription.txt'))
  jdk('java-1.8.0_u102')
  label('master')
  authorization {
    permission('hudson.model.Item.Discover', "anonymous")
    permission('hudson.model.Item.Read', 'anonymous')
    permission('hudson.model.Item.Cancel', 'anonymous')
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
    fileParam("upload", "Please upload a file with the *.xdp file extension" )
  }
  steps {
      shell(readFileFromWorkspace('resources2/helperjobs/adobeTemplateUpdate/adobeUploadTemplate.sh'))
  }
}
