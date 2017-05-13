#!/bin/bash +x
cd ${WORKSPACE}/jenkinsutil/grepVersion
export PROD_VERSION=`./grepJson.sh`
token=4
echo DATABASE=${token} >> ${WORKSPACE}/deploy.properties
echo PROD_VERSION=${PROD_VERSION} >> ${WORKSPACE}/deploy.properties
