#!/bin/bash
cd ${WORKSPACE}/chefrepo

environments/makeFor2FT.sh
knife environment from file environments/FT_TEMPLATE.json
knife exec utils/updateFTEnvironment.krb FT_TEMPLATE ${ENVIRONMENT}

knife data bag create ${ENVIRONMENT}
knife data bag from file ${ENVIRONMENT} ${WORKSPACE}/chefrepo/data_bags/DEMOMOCK/*

env=`echo ${ENVIRONMENT} | tr '[:upper:]' '[:lower:]'`
if [ `knife node list | grep el2-$env-app1` ] && [ ${DELETE} ]
   then
    knife node delete el2-$env-app1 --yes
    knife node delete el2-$env-sg1 --yes
fi

if [ `knife client list | grep el2-$env-app1` ] && [ ${DELETE} ]
   then
     knife client delete el2-$env-app1 --yes
     knife client delete el2-$env-sg1 --yes
fi


#spin up app server
cd ${WORKSPACE}/cloudops/boto-scripts/ft
./el2-ft-launch-env.py el2-ft-app-config.json ${ENVIRONMENT} > ${ENVIRONMENT}.txt

ftenv_ip=`grep $env ${ENVIRONMENT}.txt | cut -d ' ' -f 3`

echo appIP=$ftenv_ip >> ${WORKSPACE}/deploy.properties
echo ENV=$env >> ${WORKSPACE}/deploy.properties

while ! nc -z $ftenv_ip 22; do
  echo "Still Launching Application Server $ftenv_ip:22"
  sleep 10 # wait for 10 seconds before check again
done

echo "Server is running..."

sleep 40
knife node list | grep el2-$env-app1
knife node list | grep el2-$env-app1
knife node list | grep el2-$env-app1
#clean up known_hosts file
rm -f $HOME/.ssh/known_hosts
