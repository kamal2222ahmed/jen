if [ -z $ARTIFACT_VERSION ]; then
   ARTIFACT_VERSION=${ARTIFACT2_VERSION}
fi

mkdir ${WORKSPACE}/${ENVIRONMENT}
cd ${WORKSPACE}/${ENVIRONMENT}
wget http://10.193.129.166:8081/repository/public/gov/dhs/uscis/elis2/Resources/${ARTIFACT_VERSION}/Resources-${ARTIFACT_VERSION}.rpm
rpm2cpio Resources-${ARTIFACT_VERSION}.rpm | cpio -idmv
mv ${WORKSPACE}/${ENVIRONMENT}/opt/adobework/uscis_templates/* ${WORKSPACE}/${ENVIRONMENT}
rm -rf ${WORKSPACE}/${ENVIRONMENT}/opt ${WORKSPACE}/${ENVIRONMENT}/Resources-${ARTIFACT_VERSION}.rpm
scp -oStrictHostKeyChecking=no -r ${WORKSPACE}/${ENVIRONMENT} jboss@10.193.129.10:/opt/adobework/uscis_templates/
ssh -oStrictHostKeyChecking=no -t jboss@10.193.129.10 "sudo chown -R jboss: /opt/adobework/uscis_templates/${ENVIRONMENT}; ls -lart /opt/adobework/uscis_templates/${ENVIRONMENT}"
cd ${WORKSPACE}
rm -rf ${WORKSPACE}/${ENVIRONMENT}


knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" -C 2 -a ipaddress -x chef "sudo chef-client -j \"http://${NEXUS}/repository/public/gov/dhs/uscis/elis2/InternalApp/${ARTIFACT_VERSION}/InternalApp-${ARTIFACT_VERSION}.json\""
