if [ -z $ARTIFACT_VERSION ]; then
   ARTIFACT_VERSION=${ARTIFACT2_VERSION}
fi

export HTTPS_PROXY=
export HTTP_PROXY=
export https_proxy=
export http_proxy=
mkdir ${WORKSPACE}/${ENVIRONMENT}
cd ${WORKSPACE}/${ENVIRONMENT}
wget http://10.103.135.40:8080/nexus/content/groups/public/gov/dhs/uscis/elis2/Resources/${ARTIFACT_VERSION}/Resources-${ARTIFACT_VERSION}.rpm
rpm2cpio Resources-${ARTIFACT_VERSION}.rpm | cpio -idmv
mv ${WORKSPACE}/${ENVIRONMENT}/opt/adobework/uscis_templates/* ${WORKSPACE}/${ENVIRONMENT}
rm -rf ${WORKSPACE}/${ENVIRONMENT}/opt ${WORKSPACE}/${ENVIRONMENT}/Resources-${ARTIFACT_VERSION}.rpm
scp -o StrictHostKeyChecking=no -r ${WORKSPACE}/${ENVIRONMENT} jboss@10.193.129.10:/opt/adobework/uscis_templates/
ssh -o StrictHostKeyChecking=no -t jboss@10.193.129.10 "sudo chown -R jboss: /opt/adobework/uscis_templates/${ENVIRONMENT}; ls -lart /opt/adobework/uscis_templates/${ENVIRONMENT}"
cd ${WORKSPACE}
rm -rf ${WORKSPACE}/${ENVIRONMENT}


knife ssh "chef_environment:${ENVIRONMENT} AND role:Deployable" -C 2 -a ipaddress -x chef "sudo chef-client -j \"http://${NEXUS}/nexus/service/local/artifact/maven/content?r=public&g=gov.dhs.uscis.elis2&a=InternalApp&v=${ARTIFACT_VERSION}&p=json\""
