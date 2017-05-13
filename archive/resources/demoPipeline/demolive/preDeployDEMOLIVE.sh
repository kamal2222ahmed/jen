set +x

rm -rf ${WORKSPACE}/properties_from_deploy.properties
BTAR_VERSION=`jenkinsutil/grepVersion/get_latest_integration.rb`
PRODUCTION_VERSION=`jenkinsutil/grepVersion/get_latest_prod_version.rb`
LAST_DEPLOY_VERSION=`jenkinsutil/grepVersion/get_previous_demo.rb LIVE`

if [ -z $ELIS2_VERSION ];
then
   echo "We are going to deploy the latest integration gate build for you to the Demo Live Server!"
   LAST_DEPLOY_VERSION=`jenkinsutil/grepVersion/get_previous_demo.rb LOCKBOX`
else
   LAST_DEPLOY_VERSION=$ELIS2_VERSION
   echo "We are going to deploy ELIS2 Version: $ELIS2_VERSION for you to the Demo Live Server"
fi

#LAST_DEPLOY_VERSION=6.1.134.1966
echo "BTAR_VERSION=$BTAR_VERSION" >> ${WORKSPACE}/properties_from_deploy.properties
echo "LAST_DEPLOY_VERSION=$LAST_DEPLOY_VERSION" >> ${WORKSPACE}/properties_from_deploy.properties
echo "PRODUCTION_VERSION=$PRODUCTION_VERSION" >> ${WORKSPACE}/properties_from_deploy.properties
echo "Version=$BTAR_VERSION" >> ${WORKSPACE}/properties_from_deploy.properties

cat ${WORKSPACE}/properties_from_deploy.properties

cp ${WORKSPACE}/toggles/toggles-demolive.sql ${WORKSPACE}/toggles.sql
