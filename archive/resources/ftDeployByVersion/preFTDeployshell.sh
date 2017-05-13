echo "Cleaning workspace..."
rm -rf ${WORKSPACE}/logs
rm -rf ${WORKSPACE}/conf
rm -rf Database*
rm -rf DataLoader*
rm -f deploy.properties

cd ${WORKSPACE}/jenkinsutil/grepVersion

export PROD_VERSION=`./grepJson.sh`
export MASTER_VERSION=`./get_latest_integration.rb`

mkdir -p ${WORKSPACE}/Database-${ARTIFACT_VERSION}
mkdir -p ${WORKSPACE}/Database-${MASTER_VERSION}
mkdir -p ${WORKSPACE}/DataLoader-${ARTIFACT_VERSION}

if [ -z "$PROD_VERSION" ]; then
  echo "Error occurred determining PROD_VERSION, using 8.1.30.685"
  export PROD_VERSION=8.1.30.685
fi

echo PROD_VERSION=${PROD_VERSION} >> ${WORKSPACE}/deploy.properties
echo MASTER_VERSION=${MASTER_VERSION} >> ${WORKSPACE}/deploy.properties

export dbip=`knife search node "chef_environment:${ENVIRONMENT} AND role:oracle_rdbms" -F json -a ipaddress | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}'`
export tcip=`knife search node "chef_environment:${ENVIRONMENT} AND role:tomcat-nonpm" -F json -a ipaddress | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}'`

echo dbip=${dbip} >> ${WORKSPACE}/deploy.properties
echo "DATABASE IP ADDRESS IS: " ${dbip}
echo tcip=${tcip} >> ${WORKSPACE}/deploy.properties
echo "TOMCAT SERVER IP ADDRESS IS: " ${tcip}
