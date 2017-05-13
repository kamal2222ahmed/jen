#!/bin/bash
cd ${WORKSPACE}/chefrepo
knife exec utils/updateFTEnvironment.krb DEMO1CLICK ${ENVIRONMENT}

knife data bag create ${ENVIRONMENT}
knife data bag from file ${ENVIRONMENT} ${WORKSPACE}/chefrepo/data_bags/DEMOMOCK/*

env=`echo ${ENVIRONMENT} | tr '[:upper:]' '[:lower:]'`
if [ `knife node list | grep el2-$env-app1` ]
   then
    knife node delete el2-$env-app1 --yes
fi

if [ `knife client list | grep el2-$env-app1` ]
   then
     knife client delete el2-$env-app1 --yes
fi


#spin up app server
cd ${WORKSPACE}/cloudops/boto-scripts/ft
./el2-ft-launch-env.py el2-ft-app-config.json ${ENVIRONMENT} > ${ENVIRONMENT}.txt

ftenv_ip=`grep $env ${ENVIRONMENT}.txt | cut -d ' ' -f 3`
#juftenv_ip=10.193.129.77

echo appIP=$ftenv_ip >> ${WORKSPACE}/deploy.properties
echo ENV=$env >> ${WORKSPACE}/deploy.properties

while ! nc -z $ftenv_ip 22; do
  echo "Still Launching Application Server $ftenv_ip:22"
  sleep 10 # wait for 10 seconds before check again
done

echo "Server is running..."

sleep 300


#clean up known_hosts file
rm -f /home/tomcat7/.ssh/known_hosts
