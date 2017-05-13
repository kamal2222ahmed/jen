#!/bin/bash +x
cd ${WORKSPACE}/Apps
echo "git diff origin/${TARGET_BRANCH}..HEAD --name-only ${WORKSPACE}/Apps/Database/oracle/schemas/ | grep -v \"changelog.xml\""
git diff origin/${TARGET_BRANCH}..HEAD --name-only ${WORKSPACE}/Apps/Database/oracle/schemas/ | grep -v "changelog.xml"
exitValue=`echo $?`
echo "exit value is $exitValue"
echo DATABASECHECK=${exitValue} >> ${WORKSPACE}/deploy.properties

cd ${WORKSPACE}/jenkinsutil/grepVersion
export PROD_VERSION=`./grepJson.sh`
echo PROD_VERSION=${PROD_VERSION} >> ${WORKSPACE}/deploy.properties
