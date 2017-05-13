ssh-keygen -R ${tcip}
ssh -o StrictHostKeyChecking=no -i $keyToUse ec2-user@${tcip} -t "sudo passwd -d chef ; sudo passwd -d tomcat7; sudo passwd -d socscan; sudo sed -i "/el2-nexus/d" /etc/hosts; sudo sed -i "s/10.103.135.40/10.193.129.166/g" /etc/hosts; sudo rm -rf /var/chef/cache/*"

knife ssh "chef_environment:${ENVIRONMENT} AND role:activemq" "sudo service tomcat7 stop ; sleep 10 ; sudo service activemq-instance-ELIS2-int stop; sudo service activemq-instance-ELIS2-ext stop; sudo rm -rf /opt/apache-activemq-5.12.0/data /data/active-mq-data/*/kahadb/* ; sudo rm -rf /data/active-mq-data/*/*.log; sudo rm -rf /data/active-mq-data/*/*.pid /data/active-mq-data/*/*.pid.stop; sudo rm -rf /opt/jboss/server/lc_mysql/tmp/*;  sudo service activemq-instance-ELIS2-int start; sudo service activemq-instance-ELIS2-ext start; sudo rm -rf /data/s3Mock/* ; sudo rm -rf /data/digitalEvidence/*; sudo rm -rf /var/chef/backup/usr/local/tomcat/default/webapps/*" -a ipaddress -x tomcat7
knife ssh "chef_environment:${ENVIRONMENT} AND role:*tomcat*" "sudo yum clean all --enablerepo=nexus-snapshots --disablerepo='*'" -a ipaddress -x tomcat7 -C 10
knife ssh "chef_environment:${ENVIRONMENT}" "sudo yum clean all --enablerepo=nexus-snapshots --disablerepo='*'" -a ipaddress -x chef -C 10