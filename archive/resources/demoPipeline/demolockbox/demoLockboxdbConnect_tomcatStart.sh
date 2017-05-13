LD_LIBRARY_PATH="/usr/lib/oracle/11.2/client64/lib/"
db=$( grep "^databaseServer=" gradle.properties | cut -f2 -d'=')
dbport=$(  grep "^databasePort=" gradle.properties | cut -f2 -d'=' )
set +x
/u01/oracle/product/11.2.0.3/dbhome_1/bin/sqlplus "elis2datadevelop/${masterpass}@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${db})(PORT=${dbport}))(CONNECT_DATA=(SID=tapdev1)))" '@toggles.sql'
set -x

#Start Tomcat
knife ssh "chef_environment:DEMOLOCKBOX AND name:el2-DEMOLOCKBOX-app1" -C 1 -a ipaddress -x chef "sudo chef-client -o utilities::start_tomcats"
