#!/bin/bash
rm -f ${WORKSPACE}/deploy.properties

cd ${WORKSPACE}/chefrepo
knife exec utils/updateFTEnvironment.krb FT_TEMPLATE_2 ${ENVIRONMENT}

env=`echo ${ENVIRONMENT} | tr '[:upper:]' '[:lower:]'`
if [ `knife node list | grep el2-$env-idb1` ] && [ ${DELETE} ]
   then
    knife node delete el2-$env-idb1 --yes
fi

if [ `knife client list | grep el2-$env-idb1` ] && [ ${DELETE} ]
   then
     knife client delete el2-$env-idb1 --yes
fi


#spin up db server
cd ${WORKSPACE}/cloudops/boto-scripts/ft
./el2-ft-launch-env.py el2-ft-idb-config.json ${ENVIRONMENT} > ${ENVIRONMENT}.txt

ftenv_ip=`grep $env ${ENVIRONMENT}.txt | cut -d ' ' -f 3`

while ! nc -z $ftenv_ip 22; do
  echo "Still launching database server $ftenv_ip:22"
  sleep 10 # wait for 10 seconds before check again
done

echo "Server is running..."

sleep 300

echo $ftenv_ip
knife node run_list set el2-$env-idb1 'role[oracle_rdbms]'
echo DBServer=$ftenv_ip >> ${WORKSPACE}/deploy.properties
