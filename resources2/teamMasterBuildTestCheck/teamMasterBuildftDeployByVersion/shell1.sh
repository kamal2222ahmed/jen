#!/bin/bash
echo "Cleaning workspace..."
rm -rf ${WORKSPACE}/logs
rm -rf ${WORKSPACE}/conf
rm -rf Database*
rm -rf DataLoader*
rm -f deploy.properties

mkdir Database-${PROD_VERSION}
mkdir Database-${ARTIFACT_VERSION}
mkdir DataLoader-${ARTIFACT_VERSION}


cd ${WORKSPACE}/jenkinsutil/grepVersion

export PROD_VERSION=`./grepJson.sh`

if [ -z "$PROD_VERSION" ]; then
  echo "Error occurred determining PROD_VERSION, using 8.1.48.12"
  export PROD_VERSION=8.1.48.12
fi

echo PROD_VERSION=${PROD_VERSION} >> ${WORKSPACE}/deploy.properties

export dbip=`knife search node "chef_environment:${ENVIRONMENT} AND role:oracle_rdbms" -F json -a ipaddress | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}'`
export tcip=`knife search node "chef_environment:${ENVIRONMENT} AND role:tomcat-nonpm" -F json -a ipaddress | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}'`

echo dbip=${dbip} >> ${WORKSPACE}/deploy.properties
echo "DATABASE IP ADDRESS IS: " ${dbip}
echo tcip=${tcip} >> ${WORKSPACE}/deploy.properties
echo "TOMCAT SERVER IP ADDRESS IS: " ${tcip}

#extracts team name for database schema


TEAM=`echo ${BRANCH%%-*} | tr '[:upper:]' '[:lower:]' | grep -v '_'`
TEAM2=`echo ${BRANCH%%_*} | tr '[:upper:]' '[:lower:]'| grep -v '-'`

echo "TEAM= $TEAM"
echo "TEAM2= $TEAM2"


if [ -z $TEAM ];
then
   TEAM=$TEAM2
else
  echo "$TEAM"
fi

echo $TEAM

echo TEAM=$TEAM >> ${WORKSPACE}/deploy.properties
