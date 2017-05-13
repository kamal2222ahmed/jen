#!/bin/bash
cd ${WORKSPACE}/cloudops/amimaker
export ENV=`echo ${JOB_NAME} | awk -F'[ -]' '{print $1}'`
export http_proxy=http://proxy.apps.dhs.gov:80
export https_proxy=http://10.76.225.15:80
export FTP_PROXY=$HTTP_PROXY
export ALL_PROXY=$HTTP_PROXY
export NO_PROXY=localhost,127.0.0.0/8,10.*,172.*,169.254.169.254,el2-gss-chf1.aws.uscis.dhs.gov
export ftp_proxy=$FTP_PROXY
export all_proxy=$ALL_PROXY
export no_proxy=$NO_PROXY
export PACKER_HOME=/usr/local/packer
export PATH=$PACKER_HOME:$PATH

VERSION=`echo ${RELEASE_CANDIDATE} | tr '-' '.'`
#ssh -tt ec2-user@el2-gss-nxs1 "sudo -u apache /var/www/sync/releases_sync.sh ${VERSION}"
ssh -oStrictHostKeyChecking=no -tt ec2-user@10.103.136.234 -i ~/.ssh/id_rsa "sudo -u apache /var/www/sync/releases_sync.sh ${VERSION}"

gem install popen4
/opt/chefdk/embedded/bin/ruby ${WORKSPACE}/cloudops/amimaker/amimaker.rb -c tomcat_base-ami_sv.yml -e ${ENVIRONMENT} -r "${RELEASE_CANDIDATE}" | sudo tee output.txt

AMI_ID=`tail -2 output.txt | grep ami | awk -F ": " '{print $2}'`
if [ "${AMI_ID}" == "" ]; then
  exit 1
fi

echo "AMI_ID=$AMI_ID" >> ${WORKSPACE}/amiIdFile.txt

NEXUS_TARGET=http://10.193.129.166:8080/repository/releases/gov/dhs/uscis/elis2/AMIs


if [ "${FORCE}" != "" ]; then
#curl --request DELETE --write "%{http_code} %{url_effective}\\n" --user jenkins:${NexusAdminPassword}  --output /dev/null --silent ${NEXUS_TARGET}/ami-${RELEASE_CANDIDATE}.txt
curl -v -o /dev/null -X DELETE ${NEXUS_TARGET}/ami-${RELEASE_CANDIDATE}_sv.txt --user jenkins:${NexusAdminPassword}
fi

AMI_FILE=ami-${RELEASE_CANDIDATE}.txt

sudo echo ami_id: $AMI_ID > ${AMI_FILE}
#tail -2 output.txt | grep ami | awk -F ": " '{print $2}' | sudo tee AMI-${RELEASE_CANDIDATE}.txt

curl -v -u jenkins:${NexusAdminPassword} --upload-file ${AMI_FILE} ${NEXUS_TARGET}/${AMI_FILE}

ssh -oStrictHostKeyChecking=no -tt ec2-user@10.103.136.234 "sudo -u apache /var/www/sync/releases_sync.sh ${VERSION}"

aws ec2 modify-image-attribute --image-id ${AMI_ID} --launch-permission "{\"Add\":[{\"UserId\":\"728675923247\"}]}"

