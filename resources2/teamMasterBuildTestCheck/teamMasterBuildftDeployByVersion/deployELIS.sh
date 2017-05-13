mkdir ${WORKSPACE}/${ENVIRONMENT}
cd ${WORKSPACE}/${ENVIRONMENT}
wget http://10.193.129.166:8081/repository/public/gov/dhs/uscis/elis2/Resources/${ARTIFACT_VERSION}/Resources-${ARTIFACT_VERSION}.rpm
rpm2cpio Resources-${ARTIFACT_VERSION}.rpm | cpio -idmv
mv ${WORKSPACE}/${ENVIRONMENT}/opt/adobework/uscis_templates/* ${WORKSPACE}/${ENVIRONMENT}
ssh -o StrictHostKeyChecking=no -i ${keyToUse} ec2-user@${tcip} -t "sudo passwd -d chef ; sudo passwd -d tomcat7; sudo passwd -d socscan"
rm -rf ${WORKSPACE}/${ENVIRONMENT}/opt ${WORKSPACE}/${ENVIRONMENT}/Resources-${ARTIFACT_VERSION}.rpm
scp -oStrictHostKeyChecking=no -r ${WORKSPACE}/${ENVIRONMENT} jboss@10.193.129.10:/opt/adobework/uscis_templates/
ssh -oStrictHostKeyChecking=no -t jboss@10.193.129.10 "sudo chown -R jboss: /opt/adobework/uscis_templates/${ENVIRONMENT}; ls -lart /opt/adobework/uscis_templates/${ENVIRONMENT}"
cd ${WORKSPACE}
rm -rf ${WORKSPACE}/${ENVIRONMENT}

knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" -C 1 -a ipaddress -x chef "sudo service jboss stop; sudo chef-client -j \"http://${NEXUS}/repository/snapshots/gov/dhs/uscis/elis2/InternalApp/${ARTIFACT_VERSION}/InternalApp-${ARTIFACT_VERSION}.json\""
knife ssh "chef_environment:${ENVIRONMENT} AND role:activemq" "sudo service activemq-instance-ELIS2-int restart; sudo service activemq-instance-ELIS2-ext restart" -a ipaddress -x tomcat7
