#!/bin/bash
export https_proxy=http://10.76.225.15:80
ip_addy=`aws ec2 describe-instances --filters "Name=tag:Name,Values=EL2-${ENVIRONMENT}-IDB1" --output text --query 'Reservations[*].Instances[*].PrivateIpAddress'`
user=elis2datadevelop
pass=elis2data
#sleepTime=5
echo $ip_addy
string="${user}/${pass}@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=${ip_addy})(PORT=1521))(CONNECT_DATA=(SID=TAPDEV1)))"
echo "string=$string"

echo "SELECT 1 FROM dual;exit;" > ${WORKSPACE}/hello.sql

test2=`/u01/oracle/product/11.2.0.3/dbhome_1/bin/sqlplus -l "$string" '@${WORKSPACE}/hello.sql' | grep 'Connected'` 

HEALTHCHECK="PASS"
if [[ "$test2" == *"Connected"* ]]
  then
    printf "oracle instance $ip_addy is healthy\n"
    #Updates to FT Pool
	set +x
	mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute "update environments set state='ON' where name='${ENVIRONMENT}'"
  else
    printf "oracle instance $ip_addy is dead\n"
    HEALTHCHECK="FAIL"
fi

echo "HEALTHCHECK=$HEALTHCHECK" > ${WORKSPACE}/ftstatus.properties
