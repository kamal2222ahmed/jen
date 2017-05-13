#!/bin/bash
set +x
token=`mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute "CALL consumeFTEnv('master', '${JOB_NAME}/${BUILD_NUMBER}', @env)"`

while [ -z $token ]; do
   echo No functional test environment is available.
   sleep 10
   token=`mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute "CALL consumeFTEnv('master', '${JOB_NAME}/${BUILD_NUMBER}', @env)"`
done

echo $token

rm -f ${WORKSPACE}/env.properties
echo ENVIRONMENT=${token} >> ${WORKSPACE}/env.properties
