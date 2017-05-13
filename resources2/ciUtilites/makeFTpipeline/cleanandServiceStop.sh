knife node environment_set el2-$ENV-app1 ${ENVIRONMENT}
knife node show el2-$ENV-app1
knife node run_list set el2-$ENV-app1 'role[Deployable], recipe[yum_elis2], recipe[yum_elis2::releases], recipe[java_elis2], recipe[crypto], role[activemq], role[tomcat-nonpm], recipe[javakeystore], recipe[utilities::stop_tomcats], recipe[imagemagick], recipe[elis2_conf::installAll], role[ELIS_internal], role[ELIS_icam-int], role[ELIS_mock], role[ELIS_external], role[ELIS_externalAPI], role[ELIS_icam-ext], role[ELIS_services], role[ELIS_lockbox], role[ELIS_lockboxConnector], role[ELIS_paygovmock], role[ELIS_securitymock], role[mongo_FormDB], role[ELIS_digitalevidence], role[mongo_InternalDocDB], recipe[elis2_conf::installcerts], recipe[elis2_conf::restart_tomcat]'

echo "sleep 2 minutes and wait for runlist to be registered with chef server"
sleep 120

knife node show el2-$ENV-app1
knife search "chef_environment:${ENVIRONMENT} AND role:Deployable"

knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" "sudo rm -rf /var/chef/cache/*; sudo service tomcat7 stop; sudo service mongod stop; sleep 10 ; sudo service activemq-instance-ELIS2-int stop; sudo service activemq-instance-ELIS2-ext stop; sudo rm -rf /opt/apache-activemq-5.12.0/data /data/active-mq-data/*/kahadb/* /data/active-mq-data/*/*.log /data/active-mq-data/*/*.pid /data/active-mq-data/*/*.pid.stop ;  sudo service activemq-instance-ELIS2-int start; sudo service activemq-instance-ELIS2-ext start; sudo service mongod start" -a ipaddress -x chef
knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" "sudo yum clean all --enablerepo=nexus-snapshots --disablerepo='*'" -a ipaddress -x chef -C 10
mongo ${appIP}:27017/FormDB ${WORKSPACE}/jenkinsutil/mongo_scorch/scorch_formdb.js || true
