#!/bin/bash
TEAM=`echo ${GIT_BRANCH_NAME%%-*} | tr '[:upper:]' '[:lower:]' | grep -v '_'`
TEAM2=`echo ${GIT_BRANCH_NAME%%_*} | tr '[:upper:]' '[:lower:]'| grep -v '-'`

echo "TEAM=$TEAM"
echo "TEAM2=$TEAM2"



if [ -z $TEAM ];
then
   TEAM=$TEAM2
else
  echo "$TEAM"
fi

echo $TEAM

if [ $TEAM == "alliance" ];
then
  TEAM="allianc"
fi



set +x
ftenv="FT$TEAM"
echo $ftenv
token=`mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute "CALL consumeFTMEnv('$ftenv', '${JOB_NAME}/${BUILD_NUMBER}', @env)"`

while [ -z $token ]; do
   echo No functional test environment is available.
   sleep 10
   token=`mysql -D jenkins -u ${MYSQL_USER} -p${MYSQL_CRED} -h ${MYSQL_HOST} --disable-column-name --execute "CALL consumeFTMEnv('$ftenv', '${JOB_NAME}/${BUILD_NUMBER}', @env)"`
done

echo $token

rm -f ${WORKSPACE}/env.properties
echo TEAM=${TEAM} >> ${WORKSPACE}/env.properties
echo ENVIRONMENT=${token} >> ${WORKSPACE}/env.properties
