#!/bin/bash
cd ${WORKSPACE}/cloudops/amimaker
export ENV=`echo ${JOB_NAME} | awk -F'[ -]' '{print $1}'`
export HTTP_PROXY=http://proxy.apps.dhs.gov:80
export https_proxy=http://10.76.225.15:80
export FTP_PROXY=$HTTP_PROXY
export ALL_PROXY=$HTTP_PROXY
export NO_PROXY=localhost,127.0.0.0/8,10.*,172.*,169.254.169.254,el2-gss-chf1.aws.uscis.dhs.gov
export ftp_proxy=$FTP_PROXY
export all_proxy=$ALL_PROXY
export no_proxy=$NO_PROXY
export PACKER_HOME=/usr/local/packer
export PATH=$PACKER_HOME:$PATH

gem install popen4
/opt/chefdk/embedded/bin/ruby ${WORKSPACE}/cloudops/amimaker/amimaker.rb -c gold_base-ami.yml -e ${ENVIRONMENT} -r "${RELEASE_CANDIDATE}"


#| sudo tee output.txt
#AMI_ID=`tail -2 output.txt | grep ami | awk -F ": " '{print $2}'`
#if [ "${AMI_ID}" == "" ]; then
#  exit 1
#fi

#AMI_FILE=latest-ami-id.txt
#AMI_FILE2=`date +"+%Y-%m-%d-%H-%M-%S"`-ami-id.txt
#sudo echo ami_id: $AMI_ID > ${AMI_FILE}
#sudo echo ami_id: $AMI_ID > ${AMI_FILE2}

#NEXUS_TARGET=http://10.103.135.40:8080/nexus/content/repositories/releases/gov/dhs/uscis/elis2/AMIs/Gold/
#curl --request DELETE --write "%{http_code} %{url_effective}\\n" --user jenkins:${NexusAdminPassword}  --output /dev/null --silent ${NEXUS_TARGET}/latest-ami-id.txt

#curl -v -u jenkins:${NexusAdminPassword} --upload-file ${AMI_FILE} ${NEXUS_TARGET}/${AMI_FILE}

#curl -v -u jenkins:${NexusAdminPassword} --upload-file ${AMI_FILE} ${NEXUS_TARGET}/${AMI_FILE2}


#aws ec2 modify-image-attribute --image-id ${AMI_ID} --launch-permission "{\"Add\":[{\"UserId\":\"728675923247\"}]}"


