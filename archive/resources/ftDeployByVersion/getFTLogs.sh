set +x
mkdir ${WORKSPACE}/logs
mkdir ${WORKSPACE}/conf

export tcip=`knife search node "chef_environment:${ENVIRONMENT} AND role:tomcat-nonpm" -F json -a ipaddress | grep -o '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}'`

(scp tomcat7@$tcip:/logs/tomcat7/logs/* ${WORKSPACE}/logs/. || true)

(scp tomcat7@$tcip:/logs/tomcat7/MockServices/log/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/efile/log/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/InternalApp/log/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/ServicesApp/log/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/lockboxConnector/log/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/lockboxIntegration/log/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/digitalEvidence/log/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/digitalEvidence/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/logs/tomcat7/ExternalAPI/* ${WORKSPACE}/logs/. || true)
(scp tomcat7@$tcip:/usr/local/tomcat/default/conf/*.properties ${WORKSPACE}/conf/. || true)

