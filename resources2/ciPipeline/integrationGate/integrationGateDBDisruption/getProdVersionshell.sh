set +x

rm -rf ${WORKSPACE}/deploy.properties
PRODUCTION_VERSION=`jenkinsutil/grepVersion/get_latest_prod_version.rb`
echo "PRODUCTION_VERSION=$PRODUCTION_VERSION" >> ${WORKSPACE}/deploy.properties
