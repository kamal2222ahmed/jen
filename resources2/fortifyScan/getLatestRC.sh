LATEST_VERSION=`jenkinsutil/grepVersion/get_latest_integration.rb`
echo BRANCH_TAG=`echo ${LATEST_VERSION}` >> ${WORKSPACE}/rc.properties


echo PROJECT="ELIS2-"`echo ${GRADLE_PROJECT} | cut -d ':' -f 3` >> ${WORKSPACE}/rc.properties
