cp demolive/gradle.properties Apps/gradle.properties
sed -i 's;"elis2data"+dbNum+branchName;"elis2datadevelop";g' Apps/Shared/DataLoader/dataload.gradle
sed -i 's;databaseElis2DataPasswd;"elis2data";g' Apps/Shared/DataLoader/dataload.gradle
cp toggles/toggles-demomyuscis.sql toggles.sql
#Stop Tomcat
knife ssh "chef_environment:DEMOMYUSCIS AND name:el2-DEMOMYUSCIS-app1" -C 1 -a ipaddress -x chef "sudo chef-client -o utilities::stop_tomcats"
knife ssh "chef_environment:DEMOMYUSCIS AND role:activemq*" "sudo service activemq-instance-ELIS2-int stop; sudo service activemq-instance-ELIS2-ext stop; sudo rm -rf /opt/apache-activemq-5.12.0/data /data/active-mq-data/*/kahadb/* /data/active-mq-data/*/*.log /data/active-mq-data/*/*.pid /data/active-mq-data/*/*.pid.stop ;  sudo service activemq-instance-ELIS2-int start; sudo service activemq-instance-ELIS2-ext start" -a ipaddress -x chef