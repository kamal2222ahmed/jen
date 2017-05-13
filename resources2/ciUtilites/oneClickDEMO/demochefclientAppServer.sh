knife node run_list set el2-$ENV-app1 'role[Deployable], recipe[yum_elis2], recipe[yum_elis2::releases], recipe[java_elis2], recipe[crypto], role[activemq], role[tomcat-nonpm], recipe[javakeystore], role[ELIS2_internal], role[ELIS2_icam-int], role[ELIS2_mock], role[ELIS2_external], role[ELIS2_externalAPI], role[ELIS2_icam-ext], role[ELIS2_services], role[ELIS2_lockbox], role[ELIS2_lockboxConnector], role[ELIS2_paygovmock], role[ELIS2_securitymock], role[mongo_FormDB], role[ELIS2_lctemplates], role[ELIS2_digitalevidence], role[mongo_InternalDocDB], role[ELIS2_ext-snapshotgen], role[ELIS2_int-snapshotgen]'

echo "sleep 2 minutes and wait for runlist to be registered with chef server"
sleep 120

#deploy ELIS App
knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" -C 1 -a ipaddress -x chef "sudo chef-client -j \"http://${NEXUS}/repository/public/gov/dhs/uscis/elis2/InternalApp/${Version}/InternalApp-${Version}.json\""

knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" -C 1 -a ipaddress -x chef "sudo chef-client -o livecycleserver::update"
