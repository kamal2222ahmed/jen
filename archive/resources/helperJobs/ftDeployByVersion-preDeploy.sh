export HTTPS_PROXY=
export HTTP_PROXY=
export https_proxy=
export http_proxy=

ssh-keygen -R ${tcip}
ssh-keygen -R ${dbip}
ssh -o StrictHostKeyChecking=no ec2-user@${tcip} -i $keyToUse -t "sudo passwd -d chef ; sudo passwd -d tomcat7; sudo passwd -d socscan"
ssh -o StrictHostKeyChecking=no ec2-user@${dbip} -i $keyToUse -t "sudo passwd -d socscan ; sudo passwd -d oracle; sudo passwd -d ec2-user"

knife ssh "chef_environment:${ENVIRONMENT} AND role:activemq" "sudo service tomcat7 stop ; sudo service jboss stop; sudo service activemq-instance-ELIS2-int stop; sudo service activemq-instance-ELIS2-ext stop; sudo rm -rf /opt/apache-activemq-5.12.0/data /data/active-mq-data/ELIS2-ext/kahadb/* /data/active-mq-data/ELIS2-int/kahadb/* /data/active-mq-data/ELIS2-ext/activemq* /data/active-mq-data/ELIS2-int/activemq* /data/digitalEvidence/*  ; sudo rm -rf /data/s3Mock/* ; sudo mkdir -p /data/digitalEvidence/tmp ; sudo chown -R tomcat7:tomcat7 /data/digitalEvidence/tmp/; sudo service activemq-instance-ELIS2-int start; sudo service activemq-instance-ELIS2-ext start ; sudo service mongod stop ; sleep 5 ; sudo service mongod start; sudo service tomcat7 stop ; sudo rm -rf /usr/local/tomcat/default/webapps/metis /usr/local/tomcat/default/webapps/InternalApp /usr/local/tomcat/default/webapps/efile /usr/local/tomcat/default/webapps/ExternalAPI /usr/local/tomcat/default/webapps/ServicesApp /usr/local/tomcat/default/webapps/lockbox /usr/local/tomcat/default/webapps/lockboxConnector /usr/local/tomcat/default/webapps/paygovmock /usr/local/tomcat/default/webapps/ELISMockServices /usr/local/tomcat/default/webapps/DigitalEvidenceWeb; sudo rm -rf /var/spool/rsyslog/* ; sudo rm -rf /var/log/*2016*; sudo rm -rf /var/chef/backup/*; sudo rm -rf /var/log/audit/audit.log.*; sudo yum -y remove DigitalEvidence ELIS2MockData ELISMockServices ExternalAPI InternalApp Metis PayGovMock ServicesApp efile lockbox lockboxConnector securitymock" -a ipaddress -x tomcat7
knife ssh "chef_environment:${ENVIRONMENT} AND role:*tomcat*" "sudo yum clean all --enablerepo=nexus-snapshots --disablerepo='*'" -a ipaddress -x tomcat7 -C 10
knife ssh "chef_environment:${ENVIRONMENT}" "sudo yum clean all --enablerepo=nexus-snapshots --disablerepo='*'" -a ipaddress -x chef -C 10
