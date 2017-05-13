set +x

rm -rf ${WORKSPACE}/properties_from_deploy.properties
LATEST_INTEGRATION=`jenkinsutil/grepVersion/get_latest_integration.rb`
PRODUCTION_VERSION=`jenkinsutil/grepVersion/get_latest_prod_version.rb`
LAST_DEPLOY_VERSION=`jenkinsutil/grepVersion/get_previous_demo.rb LOCKBOX`

if [ -z $ELIS2_VERSION ];
then
   echo "We are going to deploy the latest integration gate build, $LATEST_INTEGRATION, for you to the Lockbox Demo Server!"
   ELIS2_VERSION=$LATEST_INTEGRATION
 else
       echo "We are going to deploy the Version $ELIS2_VERSION, for you to the Lockbox Demo Server!"
 fi


#LAST_DEPLOY_VERSION="6.1.134.1966"
echo "LAST_DEPLOY_VERSION=${LAST_DEPLOY_VERSION//#}" >> ${WORKSPACE}/properties_from_deploy.properties
echo "PRODUCTION_VERSION=$PRODUCTION_VERSION" >> ${WORKSPACE}/properties_from_deploy.properties
echo "Version=$ELIS2_VERSION" >> ${WORKSPACE}/properties_from_deploy.properties
echo "ELIS2_VERSION=$ELIS2_VERSION" >> ${WORKSPACE}/properties_from_deploy.properties

cat ${WORKSPACE}/properties_from_deploy.properties

cp ${WORKSPACE}/toggles/toggles-demomocklockbox.sql ${WORKSPACE}/toggles.sql
