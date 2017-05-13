#!/bin/bash +x
cd ${WORKSPACE}/jenkinsutil/grepVersion export PROD_VERSION=`./grepJson.sh`
export MASTER_VERSION=`./get_latest_integration.rb`
token=4
echo DATABASE=${token} >> ${WORKSPACE}/deploy.properties
echo PROD_VERSION=${PROD_VERSION} >> ${WORKSPACE}/deploy.properties
echo MASTER_VERSION=${MASTER_VERSION} >> ${WORKSPACE}/deploy.properties
