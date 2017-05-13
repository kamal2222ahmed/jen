#!/bin/bash
cd ${WORKSPACE}/cloudops/amimaker/properties
whoami
export ENV=`echo ${JOB_NAME} | awk -F'[ -]' '{print $1}'`
export https_proxy=http://10.76.225.15:80
export FTP_PROXY=$HTTP_PROXY
export ALL_PROXY=$HTTP_PROXY
export NO_PROXY=localhost,127.0.0.0/8,10.*,172.*,169.254.169.254,el2-gss-chf1.aws.uscis.dhs.gov
export ftp_proxy=$FTP_PROXY
export all_proxy=$ALL_PROXY
export no_proxy=$NO_PROXY

RELEASE_CANDIDATE=`echo ${RELEASE_CANDIDATE} | sed s/-/./`
bash ${WORKSPACE}/cloudops/amimaker/properties/properties.sh -r ${RELEASE_CANDIDATE}
