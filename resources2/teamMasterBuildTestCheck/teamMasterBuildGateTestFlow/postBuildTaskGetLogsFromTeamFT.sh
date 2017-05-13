echo "getting logs and configuration files from ${ENVIRONMENT}"
set +x
rm -rf logs conf
mkdir logs
mkdir conf
cd logs

export tcip=`knife search node "chef_environment:${ENVIRONMENT} AND role:tomcat-nonpm" -F json -a ipaddress | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}'`

( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/digitalEvidence/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/efile/log/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/ExternalAPI/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/InternalApp/log/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/lockboxConnector/log/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/lockboxIntegration/log/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/MockServices/log/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/ServicesApp/log/* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/logs/catalina.out ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/logs/tomcat7/logs/localhost_access_log* ${WORKSPACE}/logs/ || true )
( scp -o StrictHostKeyChecking=no tomcat7@$tcip:/usr/local/tomcat/default/conf/*.properties ${WORKSPACE}/conf/ || true )
set -x
